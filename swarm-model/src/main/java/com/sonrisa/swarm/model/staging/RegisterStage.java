package com.sonrisa.swarm.model.staging;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;

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
    
	private Long outletId;
	private Long registerId;
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
    public Long getOutletId() {
		return outletId;
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

	public void setOutletId(Long outletId) {
		this.outletId = outletId;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}

	@StageInsertableAttr(dbColumnName="ls_register_id")
    @Column(name = "ls_register_id")
	public Long getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Long registerId) {
		this.registerId = registerId;
	}

	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
