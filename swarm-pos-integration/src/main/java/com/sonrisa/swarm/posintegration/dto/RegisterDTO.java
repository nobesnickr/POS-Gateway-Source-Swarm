package com.sonrisa.swarm.posintegration.dto;

import java.sql.Timestamp;

import com.sonrisa.swarm.model.staging.annotation.StageInsertableAttr;
import com.sonrisa.swarm.model.staging.annotation.StageInsertableType;
import com.sonrisa.swarm.posintegration.warehouse.DWTransferable;

@StageInsertableType(dbTableName="registers")
public abstract class RegisterDTO implements DWTransferable {

	/** Identifier of the manufacturer in the remote system */
    @StageInsertableAttr(dbColumnName = "ls_register_id")
    public abstract long getRemoteId();
    
    /** Identifier of the an outlet in the remote system */
    @StageInsertableAttr(dbColumnName = "ls_outlet_id")
    public abstract Long getOutletId();

    /** Register's name */
    @StageInsertableAttr(dbColumnName = "name")
    public abstract String getName();
    
	/** Timestamp of the outlet entry in the remote system */
    @StageInsertableAttr(dbColumnName = "last_modified", usedAsTimestamp = true)
	public abstract Timestamp getLastModified();
}
