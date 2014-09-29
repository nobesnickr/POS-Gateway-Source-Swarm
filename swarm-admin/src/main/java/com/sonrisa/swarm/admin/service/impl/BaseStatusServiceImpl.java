/*
 *   Copyright (c) 2014 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.sonrisa.swarm.admin.service.impl;

import hu.sonrisa.backend.dao.BaseDaoInterface;
import hu.sonrisa.backend.dao.filter.JpaFilter;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.sonrisa.swarm.admin.model.BaseStatusEntity;
import com.sonrisa.swarm.admin.model.StatusEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity;
import com.sonrisa.swarm.admin.model.query.BaseStatusQueryEntity.StoreStatus;
import com.sonrisa.swarm.admin.model.query.StatusQueryEntity;
import com.sonrisa.swarm.admin.service.BaseStatusService;
import com.sonrisa.swarm.admin.service.StatusProcessingService;
import com.sonrisa.swarm.admin.service.exception.InvalidStatusRequestException;
import com.sonrisa.swarm.model.BaseSwarmEntity;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.posintegration.service.ApiService;

/**
 * Base implementation of the {@link BaseStatusService} interface containing 
 * some common methods.
 * 
 * @author Barnabas
 */
public abstract class BaseStatusServiceImpl<E extends BaseSwarmEntity, S extends BaseStatusEntity, Q extends BaseStatusQueryEntity> implements BaseStatusService<S,Q>{

    /**
     * Access to the stores table
     */
    protected BaseDaoInterface<Long, E> sourceDao;
    
    /**
     * Api service to access api id by api name and vice versa
     */
    protected ApiService apiService;

    /**
     * Store status service processing {@link StoreEntity} to {@link StatusEntity}
     */
    private StatusProcessingService<E,S> statusProcessingService;
    
    /**
     * If the query's <code>take</code> exceeds this limit, it will be set to this value
     */
    private Integer globalQueryLimit = 10000;
    
    /**
     * Source of the status service
     */
    private Class<E> sourceEntityType;
    
    /**
     * Initialize using processor
     * 
     * @param statusProcessingService Service used to convert E to S
     * @param sourceDao Store dao to access E
     * @param apiService Api service to access the <code>apis</code> table's contents
     */
    protected BaseStatusServiceImpl(StatusProcessingService<E,S> statusProcessingService, BaseDaoInterface<Long, E> sourceDao, ApiService apiService, Class<E> sourceEntityType){
        this.statusProcessingService = statusProcessingService;
        this.sourceDao = sourceDao;
        this.apiService = apiService;
        this.sourceEntityType = sourceEntityType;
    }
    
    /**
     * Implementable method which translates a REST query and provides its JpaFilter equivalent
     * 
     * @param queryConfig REST query received
     * @return JpaFilter<E> returning the equivalent of the REST query
     */
    protected abstract JpaFilter<E> convertStatusQueryToJpaFilter(Q queryConfig) throws InvalidStatusRequestException ;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<S> getStoreStatuses(Q queryConfig) throws InvalidStatusRequestException {
        
        validateQuery(queryConfig);
        
        JpaFilter<E> jpaFilter = convertStatusQueryToJpaFilter(queryConfig);
        
        return processStores(sourceDao.find(jpaFilter,0,0), queryConfig.getStatus(), queryConfig.getSkip(), queryConfig.getTake());       
    }
    
    /**
     * Helper function to select the API ids from a list for a given query
     * 
     * @param queryConfig Query config containing the apis for which filtering is expected
     * @param apiNames All API names allowed
     * @return Query config's apiIds or all if non specified
     * @throws InvalidStatusRequestException 
     */
    protected List<Long> convertApiNamesToApiIds(Q queryConfig, Set<String> apiNames) throws InvalidStatusRequestException {
      
        List<Long> apiIds = null;

        // Query for either API name
        if (!queryConfig.getApi().isEmpty()) {
            apiIds = new ArrayList<Long>();

            for (String apiName : queryConfig.getApi()) {
                if (!apiNames.contains(apiName)) {
                    throw new InvalidStatusRequestException("API is not associated with the gateway: "
                            + apiName + " allowed values are: " + StringUtils.join(apiNames, ','));
                }
                apiIds.add(apiService.findByName(apiName).getApiId());
            }

            // If non specified use all gateway API
        } else {
            apiIds = new ArrayList<Long>();
            for(String apiName : apiNames){
                apiIds.add(apiService.findByName(apiName).getApiId());
            }
        }
        
        return apiIds;
    }

    
    /**
     * Validates that {@link StatusQueryEntity} is a valid query 
     * 
     * @param queryConfig Query configuration sent to the POS status REST interface
     * @throws InvalidStatusRequestException If invalid
     */
    private void validateQuery(Q queryConfig) throws InvalidStatusRequestException {
        if(queryConfig == null){
            throw new IllegalArgumentException("queryConfig is null");
        }
        
        logger().debug("Verifying query: {}", queryConfig);
        
        // Verify skip
        if(queryConfig.getSkip() < 0){
            throw new InvalidStatusRequestException("Illegal value for skip: " + queryConfig.getSkip());
        }
        
        // Verify take
        if(queryConfig.getTake() < 0){
            throw new InvalidStatusRequestException("Illegal value for take: " + queryConfig.getTake());
        }
        
        // Verify orderBy
        final String orderBy = queryConfig.getOrderBy();
        final List<String> validOrderByColumns = getValidOrderByColumns(sourceEntityType);
        if(!validOrderByColumns.contains(orderBy)){
            throw new InvalidStatusRequestException(
                    "Illegal value for orderBy: " + orderBy + 
                    " allowed values are: " + StringUtils.join(validOrderByColumns, ','));
        }
    }
    
    /**
     * Converts stores to status entities
     * @param stores Source of conversion
     * @param skip Skip first few items
     * @param takeArgument Only take this amount of items from stores
     * @return Processed stores of interval <i>[skip, skip+1, ..., skip+take-1]</i>
     */
    private List<S> processStores(List<E> stores, Set<StoreStatus> statusFiltered, int skip, int takeArgument){

        int skipped = 0;
        int take = Math.min(takeArgument, globalQueryLimit);
        
        logger().debug("Extracting {}..{} from {} stores with status: {}", skip, skip+take-1, stores.size(), statusFiltered);
        
        List<S> statusValues = new ArrayList<S>();
        for(E store : stores){
            S status = statusProcessingService.processStore(store);
            
            if(skipped < skip){
                skipped++;
            } else {

                // Filter by status if necessary
                if(statusFiltered.isEmpty() || statusFiltered.contains(status.getStatus())){
                    statusValues.add(status);
                } else {
                    logger().debug("Skipping {} as it's status {} doesn't match the expected {}", store.getId(), status.getStatus(), statusFiltered);
                }
                
                if(statusValues.size() >= take){
                    break;
                }
            }
        }
        
        return statusValues;
    }

    /**
     * Get valid values for ordering an entity
     * @return List of the name of the columns with annotated getters 
     */
    private static List<String> getValidOrderByColumns(Class<? extends SonrisaJPAEntity<?>> clazz){
        List<String> retVal = new ArrayList<String>();
        
        for(Method method : getJpaColumnGetters(clazz)){
            Class<?> returnType = method.getReturnType();
            
            // No ordering by arrays, e.g. byte[]
            if(returnType != null && !returnType.isArray()){
                Column column = method.getAnnotation(Column.class);
                retVal.add(column.name());                
            }
        }    
        
        return retVal;
    }
    
    /**
     * Translates DB column names (e.g. api_id) to JPA column names
     * to be applicable in JPQL. 
     * 
     * @param dbColumnName
     * @return
     */
    protected static String getJpaFieldName(Class<? extends SonrisaJPAEntity<?>> clazz, String dbColumnName){
        
        for(Method method : getJpaColumnGetters(clazz)){
            Column column = method.getAnnotation(Column.class);
            
            if(dbColumnName.equals(column.name())){
                final String methodName = method.getName();
                // Convert values like getSomethingValue to somethingValue
                return methodName.substring(3,4).toLowerCase() + methodName.substring(4);
            }
        }    
        
        // This exception is thrown when {@link StoreEntity} doesn't 
        // follow the project's entity field naming conventions
        throw new RuntimeException("Failed to resolve " + dbColumnName + " as JPA field for " + clazz.getCanonicalName());
    }
    
    /**
     * Get all getters in a type annotated with {@link Column}
     */
    private static List<Method> getJpaColumnGetters(Class<? extends SonrisaJPAEntity<?>> clazz){
        List<Method> retVal = new ArrayList<Method>();
        
        for(Method method : clazz.getMethods()){

            Column column = method.getAnnotation(Column.class);
            
            // Only getters and if annotated with Column
            if(method.getName().startsWith("get") && column != null){
                retVal.add(method);
            }            
        }    
        
        return retVal;
    }
    
    public void setGlobalQueryLimit(Integer globalQueryLimit) {
        this.globalQueryLimit = globalQueryLimit;
    }

    public void setSourceDao(BaseDaoInterface<Long, E> storeDao) {
        this.sourceDao = storeDao;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void setStatusProcessingService(StatusProcessingService<E,S> storeStatusService) {
        this.statusProcessingService = storeStatusService;
    }    
    
    /**
     * Get sub-class's logger
     * @return
     */
    public abstract Logger logger();
}
