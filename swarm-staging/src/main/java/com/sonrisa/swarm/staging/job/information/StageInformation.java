package com.sonrisa.swarm.staging.job.information;

import hu.sonrisa.backend.dao.filter.ProviderJpaFilter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.newrelic.api.agent.NewRelic;
import com.sonrisa.swarm.model.staging.BaseStageEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.OutletStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.model.staging.RegisterStage;
import com.sonrisa.swarm.staging.service.BaseStagingService;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.OutletStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;
import com.sonrisa.swarm.staging.service.RegisterStagingService;

public class StageInformation implements Tasklet {

    @Autowired
    private CategoryStagingService categoryStagingService;  
    
    @Autowired
    private CustomerStagingService customerStagingService;  
    
    @Autowired
    private InvoiceLineStagingService invoiceLineStagingService;
    
    @Autowired
    private InvoiceStagingService invoiceStagingService;  
    
    @Autowired
    private ManufacturerStagingService manufacturerStagingService;  
    
    @Autowired
    private OutletStagingService outletStagingService;  
    
    @Autowired
    private ProductStagingService productStagingService;  

    @Autowired
    private RegisterStagingService registerStagingService;
    
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		
		long categoryCount 		= countStaging(categoryStagingService, CategoryStage.class);
		long customerCount 		= countStaging(customerStagingService, CustomerStage.class);
		long invoiceLineCount 	= countStaging(invoiceLineStagingService, InvoiceLineStage.class);
		long invoiceCount 		= countStaging(invoiceStagingService, InvoiceStage.class);
		long manufacturerCount 	= countStaging(manufacturerStagingService, ManufacturerStage.class);
		long outletLineCount 	= countStaging(outletStagingService, OutletStage.class);
		long productCount 		= countStaging(productStagingService, ProductStage.class);
		long registerCount 		= countStaging(registerStagingService, RegisterStage.class);
		long total = categoryCount+customerCount+invoiceLineCount+invoiceCount+manufacturerCount
				+outletLineCount+productCount+registerCount;
		
		NewRelic.recordMetric("Custom/Staging/Categories", categoryCount);
		NewRelic.recordMetric("Custom/Staging/Customers", customerCount);
		NewRelic.recordMetric("Custom/Staging/Invoice_Lines", invoiceLineCount);
		NewRelic.recordMetric("Custom/Staging/Invoices", invoiceCount);
		NewRelic.recordMetric("Custom/Staging/Manufacturers", manufacturerCount);
		NewRelic.recordMetric("Custom/Staging/Outlets", outletLineCount);
		NewRelic.recordMetric("Custom/Staging/Products", productCount);
		NewRelic.recordMetric("Custom/Staging/Registers", registerCount);
		NewRelic.recordMetric("Custom/Staging/Total", total);
		
		return RepeatStatus.FINISHED;
	}

	private <T extends BaseStageEntity> long countStaging
												(BaseStagingService<T> stagingService, 	Class<T> stageEntityClass) {
		ProviderJpaFilter<T> filter = SimpleFilter.of(stageEntityClass);
		return stagingService.count(filter);
	}
}
