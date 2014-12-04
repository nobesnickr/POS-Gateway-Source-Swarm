package com.sonrisa.swarm.vend.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Scope(value = "singleton")
@Component("vendIdsDAO")
public class VendIdsDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(VendIdsDAO.class);
	
	private static VendIdsDAO instance = new VendIdsDAO();
	
	private VendIdsDAO(){}
	
	/** JDBC template is required to access the legacy databases's updates table */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public static VendIdsDAO getInstance(){
		return instance;
    }
    
    private static final String INSERT_ID =
			"INSERT INTO vend_ids (account_id, type, uuid) VALUES (?, ?, ?)";
    
	public long storeId(long storeId, String typeOfId, String stringId){
		
		try{
			jdbcTemplate.update(INSERT_ID, 
					new Object[]{ storeId,	typeOfId, stringId });
		}catch(DuplicateKeyException ex){
			// The stringId was previously stored, this is not an issue.
			LOGGER.debug("The String id: "+stringId+"was previously stored.");
		}
		
		Long numericId = getNumericId(stringId);
		
		if (numericId == null){
			LOGGER.warn("Numeric id should not be null.");
		}		
		
		return numericId.longValue();
	}
    
	private static final String SELECT_OUTLET_FOR_REGISTER = 
			"SELECT ls_outlet_id FROM registers "
			+ "WHERE store_id = ? "
			+ "AND ls_register_id = ?";
	
	/**
	 * This function returns the foreign Id of the outlet associated to a concrete register
	 */
	public Long getForeignOutletIdForRegister(Long storeId, Long registerId){
		
		Object[] param = new Object[]{ storeId, registerId };
		
		return jdbcTemplate.queryForObject(SELECT_OUTLET_FOR_REGISTER, param, Long.class);
	}
	
	private static final String SELECT_NUM_ID =
			"SELECT id FROM vend_ids WHERE uuid = ?";
	
	/**
	 * This function receives a uuid (String) and receives the numeric id
	 * associated to it.
	 */
	public Long getNumericId(String stringId) {
		Object[] param = new Object[]{ stringId };
		
		return jdbcTemplate.queryForObject(SELECT_NUM_ID, param, Long.class);
	}
    
}