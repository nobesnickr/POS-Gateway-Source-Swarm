package com.sonrisa.swarm.model.staging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

/**
 * This entity represents a record in the staging_registers table.
 */
@Entity
@Table(name = "staging_registers")
@StageInsertableType(dbTableName = "registers", storeIdColumnName = "store_id")
public class RegisterStage extends BaseStageEntity{
	private static final long serialVersionUID = 6527205714164973166L;
	
    /** Private key in the staging_outlets table */
    private Long id;
    
    /** Swarm id of the instance */
    private String swarmId;
    
    /** Store id for the instance */
    private Long storeId;    
    
	private Long lsOutletId;
	private Long lsRegisterId;
    private String name;
    private String lastModified;
	
    
    @StageInsertableAttr(dbColumnName="swarm_id")
    @Column(name = "swarm_id")
    @Override
	public String getSwarmId() {
		return swarmId;
	}

	@Override
	public void setSwarmId(String swarmId) {
		this.swarmId = swarmId;
	}

	@StageInsertableAttr(dbColumnName="store_id")
    @Column(name = "store_id")
    @Override
	public Long getStoreId() {
		return this.storeId;
	}

	@Override
	public String getLsStoreNo() {
		return null;
	}

	@Override
	public String getLsSbsNo() {
		return null;
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Override
	public Long getId() {
		return this.id;
	}
    
    @StageInsertableAttr(dbColumnName="ls_outlet_id")
    @Column(name = "ls_outlet_id")
    public Long getLsOutletId() {
		return lsOutletId;
	}

    @StageInsertableAttr(dbColumnName="name")
    @Column(name = "name") 
	public String getName() {
		return name;
	}

    @StageInsertableAttr(dbColumnName="last_modified")
    @Column(name = "last_modified")
	public String getLastModified() {
		return lastModified;
	}

	public void setLsOutletId(Long outletId) {
		this.lsOutletId = outletId;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	@StageInsertableAttr(dbColumnName="ls_register_id")
    @Column(name = "ls_register_id")
	public Long getLsRegisterId() {
		return lsRegisterId;
	}

	public void setLsRegisterId(Long lsRegisterId) {
		this.lsRegisterId = lsRegisterId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
