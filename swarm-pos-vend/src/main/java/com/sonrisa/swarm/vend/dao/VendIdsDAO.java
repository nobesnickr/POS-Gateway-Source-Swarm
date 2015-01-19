package com.sonrisa.swarm.vend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    
	public long storeId(final long storeId, final String typeOfId, final String stringId){
		Long numericId = null;
		try{
			numericId = getNumericId(stringId);
		}catch(EmptyResultDataAccessException e){
			// empty result is a good result
		}
		
		if (numericId == null){
			numericId = generateNumericId(storeId, typeOfId, stringId);
		}
		
		if (numericId == null){
			LOGGER.warn("Numeric id should not be null.");
		}		
		
		return numericId.longValue();
	}

	private Long generateNumericId(final long storeId, final String typeOfId,
			final String stringId) {
		Long numericId;
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final PreparedStatementCreator psc = getPrepareStamentCreator(storeId, typeOfId, stringId);
		
		try{
			jdbcTemplate.update(psc, keyHolder);
		}catch(DuplicateKeyException ex){
			// The stringId was previously stored, this is not an issue.
			LOGGER.debug("The String id: {} was previously stored.", stringId);
		}		
		numericId = (Long) keyHolder.getKey();
		return numericId;
	}

	private PreparedStatementCreator getPrepareStamentCreator(
			final long storeId, final String typeOfId, final String stringId) {
		return new PreparedStatementCreator() {
	      @Override
	      public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
	        final PreparedStatement ps = connection.prepareStatement(INSERT_ID,
	            Statement.RETURN_GENERATED_KEYS);
	        ps.setLong(1, storeId);
	        ps.setString(2, typeOfId);
	        ps.setString(3, stringId);
	        return ps;
	      }
	    };
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