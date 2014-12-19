//-----------------------------------------------------------------------
// <copyright file="StoreExportTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 24. 11:57</date>
//---------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Dynamic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;    
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.DataAccess.RDA;
    using RetailProV8SwarmExporter.Exporter;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Testing the StoreExport class
    /// </summary>
    [TestClass]
    public class StoreExportTest
    {
        /// <summary>
        /// The SBS number
        /// </summary>
        private const int SbsNumber = 42;

        /// <summary>
        /// The mock extractor, initialized in the <see cref="TestInitialize" /> function
        /// </summary>
        private Mock<IRetailProExtractor> extractorMock;

        /// <summary>
        /// The service mock
        /// </summary>
        private Mock<ISwarmService> serviceMock;

        /// <summary>
        /// Initializes the test class.
        /// </summary>
        [TestInitialize]
        public void TestInitialize()
        {
            // Create mock extractor
            this.extractorMock = new Mock<IRetailProExtractor>();
            this.extractorMock.SetupGet(e => e.SbsNumber).Returns(SbsNumber);

            this.serviceMock = new Mock<ISwarmService>();
            this.serviceMock.Setup(svc => svc.UploadAsync(It.IsAny<string>(), It.IsAny<object>())).Returns(Task.Factory.StartNew(() => { }));
        }
        
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   StoreExporter
        /// Initial data, state:
        ///   One new and one old store in DB
        /// Description:
        ///   Tests exporting stores
        /// Expected behavior, state:
        ///   One store found and exported.
        /// </summary>
        [TestMethod]        
        public void ExportChangedItems_Date_Filter_Test()
        {
            #region Arrange
            // Create invoices for stores.
            var invoiceNew = new Mock<IRetailProDocument>();
            invoiceNew.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            var invoiceOld = new Mock<IRetailProDocument>();
            invoiceOld.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now.AddMonths(-2));            

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", invoiceNew, invoiceOld);

            var configuration = new Mock<IConfiguration>();
            configuration.Setup(c => c.LastModifiedStoreDate).Returns(DateTime.Now.AddMonths(-1));

            var mapper = new Mock<IRetailProModelMapper>();
            var mappedStore = new Store(); 
            mappedStore.ModifiedDate = invoiceNew.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit);
            mapper.Setup(m => m.GetStoreObject(invoiceNew.Object, 42)).Returns(mappedStore);

            var target = new StoreExporter(this.extractorMock.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert            
            mapper.Verify(
                m => m.GetStoreObject(
                    It.Is<IRetailProDocument>(doc => doc.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) == invoiceNew.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)),
                    42), 
                Times.Once());
            configuration.VerifySet(c => c.LastModifiedStoreDate, Times.Once());
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.StoreUpload, It.Is<IEnumerable<object>>(data => data.Count() == 1)), Times.Once());
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   StoreExporter
        /// Initial data, state:
        ///   One new store in DB, service throws error.
        /// Description:
        ///   Tests exporting stores
        /// Expected behavior, state:
        ///   LastModifiedStoreDAte will NOT be changed.
        /// </summary>
        [TestMethod]
        public void ExportChangedItems_Date_Not_Changed_When_Service_Error_Test()
        {
            #region Arrange
            // Create invoices for stores.
            var invoiceNew = new Mock<IRetailProDocument>();
            invoiceNew.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", invoiceNew);

            var configuration = new Mock<IConfiguration>();
            configuration.Setup(c => c.LastModifiedStoreDate).Returns(DateTime.Now.AddMonths(-1));

            var mapper = new Mock<IRetailProModelMapper>();
            var mappedStore = new Store();
            mappedStore.ModifiedDate = invoiceNew.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit);
            mapper.Setup(m => m.GetStoreObject(invoiceNew.Object, 42)).Returns(mappedStore);

            this.serviceMock.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.StoreUpload, It.IsAny<object>())).Returns(
                Task.Factory.StartNew(() => { throw new ServiceException(RetailProCommon.Urls.StoreUpload, System.Net.HttpStatusCode.NotFound); }));

            var target = new StoreExporter(this.extractorMock.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            try
            {
                target.ExportChangedItems().Wait();
            }
            catch (AggregateException)
            {
            }

            #region Assert
            configuration.VerifySet(c => c.LastModifiedStoreDate, Times.Never());
            #endregion
        }   
    }
}
