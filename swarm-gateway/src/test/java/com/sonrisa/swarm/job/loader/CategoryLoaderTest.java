package com.sonrisa.swarm.job.loader;

import org.junit.Test;

import com.sonrisa.swarm.mock.MockTestData;
import com.sonrisa.swarm.model.legacy.StoreEntity;
import com.sonrisa.swarm.model.staging.CategoryStage;

/**
 * Test cases for the {@link CategoryStage} class, when loading staging to legacy
 *
 * @author Barna
 */
public class CategoryLoaderTest extends BaseLoaderTest {

    /**
     * Testing that if a category has a parent category, after two job
     * cycles, they are both written into the legacy DB.
     */
    @Test
    public void testParentCategoryField(){
        // creates a store 
        StoreEntity strEntity = MockTestData.mockStoreEntity("myStore");
        storeService.save(strEntity);
        final Long storeId = strEntity.getId();

        // create staging category entries
        CategoryStage subCategory = MockTestData.mockCategoryStage("123", storeId);
        CategoryStage parentCategory = MockTestData.mockCategoryStage("456", storeId);
        CategoryStage neighbourCategory = MockTestData.mockCategoryStage("789", storeId);
        CategoryStage parentsNeighbourCategory = MockTestData.mockCategoryStage("1000", storeId);
        
        subCategory.setLsParentCategoryId(parentCategory.getLsCategoryId());
        subCategory.setLsLeftCategoryId(neighbourCategory.getLsCategoryId());
        parentCategory.setLsRightCategoryId(parentsNeighbourCategory.getLsCategoryId());
        
        categoryStagingService.save(subCategory);
        categoryStagingService.save(parentCategory);
        categoryStagingService.save(neighbourCategory);
        categoryStagingService.save(parentsNeighbourCategory);
        
        // executes the whole loader job
        launchJob();

        assertCategory(subCategory, parentCategory);
        
        
    }
}
