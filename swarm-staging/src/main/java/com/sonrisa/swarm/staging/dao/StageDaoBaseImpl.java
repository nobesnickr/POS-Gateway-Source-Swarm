/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.staging.dao;

import hu.sonrisa.backend.dao.BaseJpaDao;
import hu.sonrisa.backend.entity.SonrisaJPAEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.sonrisa.swarm.common.exception.ItemMappingException;
import com.sonrisa.swarm.model.StageBatchInsertable;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.model.staging.retailpro.RetailProAttr;
import com.sonrisa.swarm.staging.dao.jdbc.JdbcTemplateBasedDao;

/**
 * Common base class for the Swarm DAO classes with some basic methods.
 * 
 * @author Béla Szabó
 * 
 */
public abstract class StageDaoBaseImpl<T extends SonrisaJPAEntity<Long>>
        extends BaseJpaDao<Long, T> implements
        JdbcTemplateBasedDao<StageBatchInsertable> {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(StageDaoBaseImpl.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * The SQL insert string. We generate this value dynamically by the
     * {@link RetailProAttr} annotation.
     */
    private Map<Class, String> insertQuery = null;

    public StageDaoBaseImpl(Class<T> clazz) {
        super(clazz);
        insertQuery = new HashMap<Class, String>();
    }

    /**
     * Return list all Ids
     */
    public List<Long> findAllIds() {
        return jdbcTemplate.queryForList("select id from " + getTableName()
                + " order by id asc;", Long.class);
    }

    @Override
    public void create(final List<? extends StageBatchInsertable> entityList) {
        create(entityList, null);
    }

    /**
     * Add correctly annotated StageBatchInsertable objects to the stage tables
     * 
     * @param entityList
     *            The list of the insertable entities
     * @param localStoreId
     *            The local id of the swarm partner
     */
    public void create(final List<? extends StageBatchInsertable> entityList,
            final Long localStoreId) {	
        if (entityList == null || entityList.isEmpty()) {
            return;
        }       
        try{
	        storeEntityList(entityList, localStoreId);
        }catch(Exception exception){
        	LOGGER.error("A entity can not be inserted into the stage table.", exception);
        	LOGGER.error("The entity list will be inserted one by one: {}",entityList);
        	
        	for (StageBatchInsertable entity : entityList){
        		List<StageBatchInsertable> auxList = new ArrayList<StageBatchInsertable>();
        		auxList.add(entity);
        		try{
        			storeEntityList(auxList, localStoreId);
        		}catch(Exception e){
        			LOGGER.error("Entity: "+entity+ "could not be stored due to:", exception);
        		}
        	}
        }
    }

	private void storeEntityList(
			final List<? extends StageBatchInsertable> entityList,
			final Long localStoreId) {
		jdbcTemplate.batchUpdate(getSqlInsert(entityList.get(0).getClass()),
		        new BatchPreparedStatementSetter() {
		            @Override
		            public void setValues(PreparedStatement ps, int i)
		                    throws SQLException {
		                setPreparedStatement(ps, entityList.get(i),
		                        localStoreId);
		            }

		            @Override
		            public int getBatchSize() {
		                return entityList.size();
		            }
		        });
	}

    /**
     * Gets the first class in the inheritance chain (upwards), which
     * is annotated using the StageInsertableType class. This class
     * will be then used to retrieve Stage column information
     * @param entity Base entity's class
     * @return The class annotated with StageInsertableType, e.g. CustomerDTO for ErplyCustomerDTO
     */
    private Class<?> getStageAnnotatedType(Class<?> entity) {
        Class<?> clazz = entity;
        while (clazz.getAnnotation(StageInsertableType.class) == null) {
            if (clazz.getSuperclass() == null) {
                throw new ItemMappingException(
                        "Invalid annotation of class, missing StageInsertableType for:  "
                                + entity.getSimpleName());
            }
            clazz = clazz.getSuperclass();
        }
        return clazz;
    }

    /**
     * Fill the prepared statement with the corresponding values.
     * 
     * @param ps
     * @param invoice
     * @throws SQLException
     */
    protected void setPreparedStatement(PreparedStatement ps, StageBatchInsertable entity, Long localStoreId) throws SQLException {
        try {
            int parameterIndex = 1;

            Class<?> clazz = getStageAnnotatedType(entity.getClass());
            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(StageInsertableAttr.class)) {
                    StageInsertableAttr attr = method
                            .getAnnotation(StageInsertableAttr.class);
                    method.setAccessible(true);
                    
                    setValues(ps, parameterIndex, method.invoke(entity),
                            method.getReturnType(), attr.maxLength());
                    parameterIndex++;
                }
            }

            // if class is annotated, then it's possible to know that a store_id
            // column is present
            // in the table. if there is a store_id column insert the store_id
            // (or null) there
            StageInsertableType classAnnotation = clazz
                    .getAnnotation(StageInsertableType.class);
            if (classAnnotation.storeIdColumnName() != null) {
                setValues(ps, parameterIndex, localStoreId, Long.class, 0);
            }
        } catch (IllegalAccessException ex) {
            throw new ItemMappingException(
                    "An exception occured during parameter setting in the batch update. Item class: "
                            + entity.getClass(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ItemMappingException(
                    "An exception occured during parameter setting in the batch update. Item class: "
                            + entity.getClass(), ex);
        } catch (InvocationTargetException ex) {
            throw new ItemMappingException(
                    "An exception occured during parameter setting in the batch update. Item class: "
                            + entity.getClass(), ex);
        }
    }

    /**
     * Set double but truncate it to a fixed length based on the static
     * variables of StageInsertableType
     */
    private void setFixedDouble(PreparedStatement ps, int parameterIndex,double doubleValue) throws SQLException {
        
        double d = Math.min(doubleValue, Math.pow(10, StageInsertableType.doubleExponent) - 1);
        
        ps.setString(parameterIndex,
                     String.format(Locale.US, "%."+ StageInsertableType.doublePrecision + "f", d));
    }

    /**
     * Set string to prepared statement, but truncate it the the max length
     */
    private void setLimitedString(PreparedStatement ps, int parameterIndex, String s, int maxLength) throws SQLException {
        if (maxLength > 0 && s.length() >= maxLength) {
            LOGGER.debug("Stage value longer than {}", maxLength, s);
            ps.setString(parameterIndex, s.substring(0, maxLength));
        } else {
            ps.setString(parameterIndex, s);
        }
    }

    /**
     * Put values into the {@link PreparedStatement} object
     */
    private void setValues(PreparedStatement ps, int parameterIndex,
            Object value, Class<?> fieldClass, int maxLength)
            throws SQLException {
        // If the value is null put SQL null type
        if (value == null) {
            ps.setNull(parameterIndex, Types.NULL);
            return;
        } else if (fieldClass == Long.class || fieldClass == long.class) {
            ps.setLong(parameterIndex, (Long) value);
            return;
        } else if (fieldClass == Integer.class || fieldClass == int.class) {
            ps.setInt(parameterIndex, (Integer) value);
            return;
        } else if (fieldClass == Double.class || fieldClass == double.class) {
            setFixedDouble(ps, parameterIndex, (Double) value);
            return;
        } else if (fieldClass == java.sql.Timestamp.class) {
            long longValue = ((Timestamp) value).getTime();
            if (longValue > 0) {
                ps.setLong(parameterIndex, longValue);
            } else {
                ps.setNull(parameterIndex, Types.NULL);
            }
        } else if (fieldClass == String.class) {
            setLimitedString(ps, parameterIndex, (String) value, maxLength);
        } else {
            LOGGER.warn("No mapping branch exists for {} with value {} ",
                    fieldClass.getSimpleName(), value.toString());
            ps.setString(parameterIndex, value.toString());
        }
    }

    /**
     * Return the SQL insert query for inserting into the staging tables for a
     * certain kind of POJO (e.g. stage entity or DTO)
     * 
     * @note This method caches the queries and only reads through the
     *       annotations one time
     * @param classOfListItem
     *            The type of the item passed in the list of create
     * @return String of the SQL query
     */
    protected String getSqlInsert(Class<? extends StageBatchInsertable> classOfListItem) {
        if (insertQuery.containsKey(classOfListItem)) {
            return insertQuery.get(classOfListItem);
        } else {
            String insertQueryOfListItem = generateSqlInsertByTheAnnotationField(classOfListItem);
            insertQuery.put(classOfListItem, insertQueryOfListItem);
            return insertQueryOfListItem;
        }
    }

    /**
     * Generates SQL insert query to a certain kind of POJO
     * 
     * @param clazz
     *            The class of the POJO that has the StageInsertableAttr
     *            annotations
     * @return String of the insert query
     */
    private String generateSqlInsertByTheAnnotationField(
            Class<? extends StageBatchInsertable> baseClass) {

        Class<?> clazz = getStageAnnotatedType(baseClass);

        // See if StageInsertableType annotation exists on class
        StageInsertableType classAnnotation = clazz.getAnnotation(StageInsertableType.class);
        String tableName = "";

        // If doesn't exists, use the DAO's default table name
        if (classAnnotation == null) {
            tableName = getTableName();
            // Otherwise use the annotated value
        } else {
            tableName = StageInsertableType.tablePrefix
                    + classAnnotation.dbTableName();
        }

        StringBuilder sqlInsert = new StringBuilder("INSERT INTO " + tableName
                + " (");

        StringBuilder annotatedFields = new StringBuilder();
        StringBuilder annotatedParameters = new StringBuilder();

        // iterates over the declared fields
        // / @warning Inherited fields will be missing

        Method[] methods = clazz.getMethods();

        int index = 0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(StageInsertableAttr.class)) {
                StageInsertableAttr stageInsertableAnnotation = method
                        .getAnnotation(StageInsertableAttr.class);
                if (stageInsertableAnnotation != null) {
                    String dbColumnName = stageInsertableAnnotation.dbColumnName();
                    if (index == 0) {
                        annotatedFields.append(dbColumnName);
                        annotatedParameters.append("?");
                    } else {
                        annotatedFields.append(", ").append(dbColumnName);
                        annotatedParameters.append(", ?");
                    }
                    index++;
                }
            }
        }

        // if class is annotated, then it's possible to know that a store_id
        // column is present in the table. if there is a store_id column insert
        // the store_id (or null) there
        if (classAnnotation != null && classAnnotation.storeIdColumnName() != null) {
            if (index == 0) {
                annotatedFields.append(classAnnotation.storeIdColumnName());
                annotatedParameters.append("?");
            } else {
                annotatedFields.append(", ").append(
                        classAnnotation.storeIdColumnName());
                annotatedParameters.append(", ?");
            }
        }

        sqlInsert.append(annotatedFields);
        sqlInsert.append(") VALUES (");
        sqlInsert.append(annotatedParameters);
        sqlInsert.append(")");
        return sqlInsert.toString();
    }
}
