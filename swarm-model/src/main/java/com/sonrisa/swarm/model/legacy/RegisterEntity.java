package com.sonrisa.swarm.model.legacy;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = RegisterEntity.TABLE_NAME)
public class RegisterEntity  extends BaseLegacyEntity {

	private static final long serialVersionUID = 4463055729150027425L;

	public static final String TABLE_NAME = "registers";
	/** Private key in the staging_registersts table */
    private Long id;
	private Long registerId;
    private Long outletId;
    private String name;
    private String uuid;
    private OutletEntity outlet;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "register_id")
    @Override    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id")
    @Override
    public StoreEntity getStore() {
        return super.getStore();
    }
    
	

	@Column(name = "last_modified")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLastModified() {
        return new Date();
    }
	
	@Column(name = "ls_register_id")
    public Long getRegisterId() {
		return registerId;
	}

	public void setRegisterId(Long registerId) {
		this.registerId = registerId;
	}

	@Column(name = "ls_outlet_id")
	public Long getOutletId() {
		return outletId;
	}

	public void setOutletId(Long outletId) {
		this.outletId = outletId;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "uuid")
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setLastModified(Date lastModified){}

	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "outlet_id")
	public OutletEntity getOutlet() {
		return outlet;
	}

	public void setOutlet(OutletEntity outlet) {
		this.outlet = outlet;
	}
}
