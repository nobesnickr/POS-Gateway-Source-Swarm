package com.sonrisa.swarm.job.loader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import hu.sonrisa.backend.dao.filter.FilterParameter;
import hu.sonrisa.backend.dao.filter.SimpleFilter;

import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.sonrisa.swarm.BaseBatchTest;
import com.sonrisa.swarm.legacy.dao.CategoryDao;
import com.sonrisa.swarm.legacy.dao.CustomerDao;
import com.sonrisa.swarm.legacy.dao.InvoiceDao;
import com.sonrisa.swarm.legacy.dao.InvoiceLineDao;
import com.sonrisa.swarm.legacy.dao.ManufacturerDao;
import com.sonrisa.swarm.legacy.dao.ProductDao;
import com.sonrisa.swarm.legacy.service.CategoryService;
import com.sonrisa.swarm.legacy.service.CustomerService;
import com.sonrisa.swarm.legacy.service.InvoiceLineService;
import com.sonrisa.swarm.legacy.service.InvoiceService;
import com.sonrisa.swarm.legacy.service.ManufacturerService;
import com.sonrisa.swarm.legacy.service.ProductService;
import com.sonrisa.swarm.model.legacy.CategoryEntity;
import com.sonrisa.swarm.model.legacy.CustomerEntity;
import com.sonrisa.swarm.model.legacy.InvoiceEntity;
import com.sonrisa.swarm.model.legacy.InvoiceLineEntity;
import com.sonrisa.swarm.model.legacy.ManufacturerEntity;
import com.sonrisa.swarm.model.legacy.ProductEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;
import com.sonrisa.swarm.model.staging.CustomerStage;
import com.sonrisa.swarm.model.staging.InvoiceLineStage;
import com.sonrisa.swarm.model.staging.InvoiceStage;
import com.sonrisa.swarm.model.staging.ManufacturerStage;
import com.sonrisa.swarm.model.staging.ProductStage;
import com.sonrisa.swarm.staging.service.CategoryStagingService;
import com.sonrisa.swarm.staging.service.CustomerStagingService;
import com.sonrisa.swarm.staging.service.InvoiceLineStagingService;
import com.sonrisa.swarm.staging.service.InvoiceStagingService;
import com.sonrisa.swarm.staging.service.ManufacturerStagingService;
import com.sonrisa.swarm.staging.service.ProductStagingService;

/**
 * Superclass for classes testing the loading mechanism 
 * from staging to legacy.
 * 
 * @author Barna
 */
public abstract class BaseLoaderTest extends BaseBatchTest {
    @Autowired
    @Qualifier("loaderJobTestUtil")    
    private JobLauncherTestUtils jobLauncherTestUtils;
    
    @Autowired
    protected InvoiceService invoiceService;
    @Autowired
    protected InvoiceLineService invoiceLineService;
    @Autowired
    protected InvoiceStagingService invoiceStagingService;
    @Autowired
    protected InvoiceLineStagingService invoiceLineStagingService;
    @Autowired
    protected InvoiceDao invoiceDao;
    @Autowired
    protected InvoiceLineDao invoiceLineDao;
    
    @Autowired
    protected CustomerStagingService customerStagingService;
    @Autowired
    protected CustomerService customerService;
    @Autowired
    protected CustomerDao customerDao;
    
    @Autowired
    protected ProductStagingService productStagingService;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected ProductDao productDao;
    
    @Autowired
    protected CategoryStagingService categoryStagingService;
    @Autowired
    protected CategoryService categoryService;
    @Autowired
    protected CategoryDao categoryDao;
    
    @Autowired
    protected ManufacturerStagingService manufacturerStagingService;
    @Autowired
    protected ManufacturerService manufacturerService;
    @Autowired
    protected ManufacturerDao manufacturerDao;
    
    /**
     * Assert that there are no customers in staging table or anywhere else
     */
    protected void assertNoCustomers(){
        assertEquals(0,  customerStagingService.findAllIds().size());
        assertEquals(0, customerDao.findAll().size());       
    }
     
     /**
      * Asserts that a staging invoices and its lines have been moved to the legacy DB properly.
      * 
      * @param stgInvoice
      * @param line 
      */
     protected InvoiceEntity assertInvoice(Long storeId, InvoiceStage stgInvoice, ProductStage[] stgProducts, InvoiceLineStage[] stgLines) {
         
         final String foreignId = stgInvoice.getLsInvoiceId();
         SimpleFilter filter = new SimpleFilter(InvoiceEntity.class,
                 new FilterParameter("lsInvoiceId", Long.valueOf(foreignId)),
                 new FilterParameter("store.id", storeId));
         final InvoiceEntity invoice = invoiceService.findSingle(filter);
         assertNotNull("Invoice with this foreign ID:" + foreignId + " and storeId: " + storeId + " does not exist in the invoices table.", invoice);
         // Steve: "It's a legacy thing from lightspeed imports. It should always be true"
         assertInvoiceEquals(stgInvoice, invoice);
         assertTrue(invoice.getCompleted());
         
         
         if (stgProducts != null && stgLines != null){
             // assert lines
             assertEquals("This test needs as many products as lines.", stgProducts.length, stgLines.length);
             SimpleFilter lineFilter = new SimpleFilter(InvoiceLineEntity.class, 
                     new FilterParameter("store.id", storeId),
                     new FilterParameter("invoice.id", invoice.getId()));
             
             final List<InvoiceLineEntity> lines = invoiceLineService.find(lineFilter, 0, 0);
             assertEquals("Number of the invoice line does not equal with the expected value. "
                     + "Foreign invoiceId: " + foreignId, stgLines.length, lines.size());
             for (int i = 0; i<stgLines.length; i++){
                 final InvoiceLineStage lineToAssert = stgLines[i];
                 final ProductStage stgProdForThisLine = stgProducts[i];
                 boolean hit = false;

                 for (InvoiceLineEntity line : lines){
                     if (lineToAssert.getLsLineId().equals(line.getLsLineId().toString())){
                         assertInvoiceLineEqual(lineToAssert, stgProdForThisLine, line);
                         hit = true;
                     }
                 }
                 assertTrue(hit);
             }
         }
         return invoice;
     }
     
     /**
      * Asserts that the staging products have been moved to the legacy DB properly.
      * 
      * @param stgProducts 
      */
     protected void assertProducts(ProductStage... stgProducts){
         // we expect that the job moved the product to the data warehouse
         final List<ProductEntity> prods = productDao.findAll();
         assertEquals("The number of the moved products does not equal with the expected value.", stgProducts.length, prods.size());
         
         for (ProductStage stProd : stgProducts){
             final Long lsProdId = Long.valueOf(stProd.getLsProductId());
             // finds products by lsProductId
             SimpleFilter filter = new SimpleFilter(ProductEntity.class, 
                     new FilterParameter("lsProductId", lsProdId));
             final ProductEntity prod = productService.findSingle(filter);
             assertNotNull("Product with this foreign ID:"+lsProdId+" does not exist in the products table.", prod);
             assertProductEquals(stProd, prod);
         }    
     }
     
     /**
      * Assert that one particular customer has been movedto the legacy DB properly
      * @param stgEntity
      */
     protected void assertSingleCustomer(CustomerStage stgEntity){
         final Long foreignId = Long.valueOf(stgEntity.getLsCustomerId());
         // finds products by lsProductId
         SimpleFilter filter = new SimpleFilter(CustomerEntity.class, 
                 new FilterParameter("lsCustomerId", foreignId));
         final CustomerEntity entity = customerService.findSingle(filter);
         assertNotNull("Customer with this foreign ID:"+foreignId+" does not exist in the customers table.", entity);
         assertCustomerEquals(stgEntity, entity);
     }
     
     /**
      * Asserts that the staging customers have been moved to the legacy DB properly.
      * 
      * @param stgEntities 
      */
     protected void assertCustomers(CustomerStage... stgEntities){
         // we expect that the job moved the customers to the data warehouse
         final List<CustomerEntity> entities = customerDao.findAll();
         assertEquals("The number of the moved customers does not equal with the expected value.", stgEntities.length, entities.size());
         
         for (CustomerStage stgEntity : stgEntities){
             assertSingleCustomer(stgEntity);
         }    
     }    
     
     /**
      * Assert that the staging categories have been moved to the legacy DB properly
      * @param stgCategories
      */
     protected void assertCategory(CategoryStage... stgCategories){
         for(CategoryStage category : stgCategories){
             final Long foreignId = Long.valueOf(category.getLsCategoryId());
             SimpleFilter<CategoryEntity> filter = new SimpleFilter<CategoryEntity>(CategoryEntity.class, new FilterParameter("lsCategoryId", foreignId));
             
             final CategoryEntity entity = categoryService.findSingle(filter);
             assertNotNull("Category with this foreign ID:"+foreignId+" does not exist in the category table.", entity);
             assertCategoryEqual(category, entity);
         }
     }
     
     /**
      * Assert that the staging manufacturers have been moved to the legacy DB properly
      * @param stgManufacturers
      */
     protected void assertManufacturer(ManufacturerStage... stgManufacturers){
         for(ManufacturerStage manufacturer : stgManufacturers){
             final Long foreignId = Long.valueOf(manufacturer.getManufacturerId());
             
             @SuppressWarnings("unchecked")
             SimpleFilter filter = new SimpleFilter(ManufacturerEntity.class, 
                     new FilterParameter("manufacturerId", foreignId));
             
             final ManufacturerEntity entity = manufacturerService.findSingle(filter);
             assertNotNull("Manufacturer with this foreign ID:"+foreignId+" does not exist in the category table.", entity);
             
             assertManufacturerEqual(manufacturer, entity);
         }
     }

     /**
      * Executes the whole loader job, and asserts that the job executed
      */
     protected void launchJob() {
         try {           
             JobExecution jobResult = jobLauncherTestUtils.launchJob();
             assertEquals(ExitStatus.COMPLETED, jobResult.getExitStatus());
         } catch (Exception ex) {
             fail(ex.getMessage());
         }
     }

     /**
      * Executes a single step of the loader job, and asserts that it was completed
      */
     protected void stepExecute(final String step) {
         JobExecution jobExecution = jobLauncherTestUtils.launchStep(step);
         assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
     }
}
