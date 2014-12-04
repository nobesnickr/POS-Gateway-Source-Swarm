package com.sonrisa.swarm.vend.dto;

import java.sql.Timestamp;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sonrisa.swarm.posintegration.dto.InvoiceDTO;
import com.sonrisa.swarm.posintegration.exception.ExternalExtractorException;
import com.sonrisa.swarm.posintegration.extractor.ExternalDTO;
import com.sonrisa.swarm.posintegration.extractor.annotation.ExternalField;
import com.sonrisa.swarm.vend.api.util.TimeConversionUtil;

/**
 * Representation of a Vend order's batch item
 */
public class VendInvoiceDTO extends InvoiceDTO {
	private static final Logger LOGGER = LoggerFactory.getLogger(VendInvoiceDTO.class);


    /**
     * The id field
     */
    private long id;
    
    /**
     * The id field as a uuid by the vend API
     */
    private String stringId;
    
    /**
     * The saleNumber field
     */
    private String saleNumber;
    
    /**
     * The total field
     */
    private double total;
    
    /**
     * Creation date of the invoice
     */
    private Timestamp createdDate;
    
    /**
     * Update timestamp 
     */
    private Timestamp updatedDate;
    
    /**
     * Customer's id. 
     * Only available for detailed requests
     */
    private Long customerId;
    
    /**
     * Customer's id as a uuid returned by the vend API
     */
    private String stringCustomerId;
    
    /**
     * Value indicating whether DTO is finished (lines, etc.)
     */
    private boolean detailed;

    private String uuidRegisterId;
    
    private Long registerId;
    
    private Long outletId;
    
    private Integer completed;

    @Override
    public long getRemoteId() {
        return id;
    }

    @Override
    public Long getCustomerId() {
        return customerId;
    }

    @Override
    public String getInvoiceNumber() {
        return saleNumber;
    }

    @Override
    public double getTotal() {
        return total;
    }

    @Override
    public Timestamp getInvoiceTimestamp() {
        return createdDate;
    }
    
    @Override
    public Timestamp getLastModified(){
        return updatedDate;
    }

    @Override
    public Integer getLinesProcessed(){
        return detailed ? 1 : 0;
    }

    @Override
	public Long getRegisterId() {
		return registerId;
	}

    @Override
	public Long getOutletId() {
		return outletId;
	}

    @Override
    public Integer getCompleted(){
		return completed;
    }
    
    @ExternalField(value="id", required=true)
    public void setId(String stringId) {
        this.setStringId(stringId);
    }
      
    public void setId(long id) {
        this.id = id;
    }

    @ExternalField(value="invoice_number")
    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }
    

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    /**
     * Total from the batch, note that <strong>this is field is missing</strong> in the detailed page
     * @throws ExternalExtractorException 
     */
    @ExternalField(value="totals")
    public void setTotal(ExternalDTO dto) throws ExternalExtractorException {
		if(dto != null){
			total = dto.getDouble("total_payment");
		}else{
			LOGGER.warn("totals field not found in JSON file");
		}
    }    
    
    @ExternalField(value="sale_date")
    public void setCreatedDate(String createdDateString) throws ParseException {
        this.createdDate = TimeConversionUtil.stringToDate(createdDateString);
    }

    @ExternalField(value="sale_date")
    public void setUpdatedDate(String updatedDateString) throws ParseException {
        this.updatedDate = TimeConversionUtil.stringToDate(updatedDateString);
    }
    @ExternalField(value="customer_id")
    public void setStringCustomerId(String stringCustomerId) {
        this.stringCustomerId = stringCustomerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

	public String getStringId() {
		return stringId;
	}

	public void setStringId(String stringId) {
		this.stringId = stringId;
	}

	public String getStringCustomerId() {
		return stringCustomerId;
	}


	public String getUuidRegisterId() {
		return uuidRegisterId;
	}

	@ExternalField(value="register_id")
	public void setUuidRegisterId(String uuidRegisterId) {
		this.uuidRegisterId = uuidRegisterId;
	}

	public void setRegisterId(Long registerId) {
		this.registerId = registerId;
	}
	
	@ExternalField(value="status")
	public void setCompleted(String status) {
		this.completed = ("CLOSED".equalsIgnoreCase(status) ||
						 "LAYBY".equalsIgnoreCase(status) ||
						 "LAYBY_CLOSED".equalsIgnoreCase(status))?
								 new Integer(1):
								 new Integer(0);
	}
}
