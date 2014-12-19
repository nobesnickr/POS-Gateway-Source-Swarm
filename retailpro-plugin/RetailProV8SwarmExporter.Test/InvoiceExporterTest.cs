//-----------------------------------------------------------------------
// <copyright file="InvoiceExporterTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 22. 16:33</date>
//----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.Dynamic;
    using System.Linq;
    using System.Reflection;
    using System.Runtime.Serialization;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.Configuration;    
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.DataAccess.RDA;
    using RetailProV8SwarmExporter.Exporter;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Class devoted to test that InvoiceExporter class retrieves information
    /// from Retail Pro v8 (by using a Mock and not RDA2)
    /// </summary>
    [TestClass]
    public class InvoiceExporterTest
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
        ///   InvoiceExporter
        /// Initial data, state:
        ///   One new and one old invoice in DB
        /// Description:
        ///   Tests exporting invoices
        /// Expected behavior, state:
        ///   One invoice found and exported.
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

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(100);

            var configuration = new Mock<IConfiguration>();
            var dateDictionary = new DateDictionary() { DefaultDate = DateTime.Now.AddMonths(-1) };
            configuration.SetupProperty(c => c.LastModifiedInvoiceDate, dateDictionary);

            var mapper = new Mock<IRetailProModelMapper>();
            mapper.Setup(m => m.GetInvoiceObject(invoiceNew.Object, It.IsAny<IRetailProTable>(), It.IsAny<long>()))
                .Returns(CreateMockInvoice("12345", new Store() { SbsNo = SbsNumber, StoreNo = "ABC" }, new Customer(), DateTime.Now));

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert            
            mapper.Verify(
                m => m.GetInvoiceObject(
                    It.Is<IRetailProDocument>(doc => doc.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) == invoiceNew.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)),
                    It.IsAny<IRetailProTable>(),
                    SbsNumber),
                Times.Once());
            
            Assert.AreEqual(1, dateDictionary.Entries.Count, "No entry was created for the store");
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => VerifyUploadedInvoiceCount(data, 1))), Times.Once());
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Date configuration was reset to DateTime.Min
        /// Description:
        ///   Tests exporting invoices
        /// Expected behavior, state:
        ///   Invoice is found and exported
        /// </summary>
        [TestMethod]
        public void ExportChangedItems_With_MinDate_As_Date()
        {
            #region Arrange
            // Create invoices for stores.
            var invoice = new Mock<IRetailProDocument>();
            invoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", invoice);

            // Setup Appconfig to use a 2 days as sync limit
            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(100);
            appConfig.Setup(c => c.SyncDaysLimit).Returns(2);

            // Create FileConfiguration with DefaultDate as 0001-01-01 and
            // a single entry with 0001-01-01 too
            var configuration = new Mock<IConfiguration>();
            var dateDictionary = new DateDictionary() { DefaultDate = DateTime.MinValue };
            dateDictionary.SetDate(SbsNumber, "DEF", DateTime.MinValue);

            configuration.SetupProperty(c => c.LastModifiedInvoiceDate, dateDictionary);

            var mapper = new Mock<IRetailProModelMapper>();
            mapper.Setup(m => m.GetInvoiceObject(invoice.Object, It.IsAny<IRetailProTable>(), It.IsAny<long>()))
                .Returns(CreateMockInvoice("12345", new Store() { SbsNo = SbsNumber, StoreNo = "ABC" }, new Customer(), DateTime.Now));

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => VerifyUploadedInvoiceCount(data, 1))), Times.Once());
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Two new invoices in DB, MaxUploadItems set to 1
        /// Description:
        ///   Tests exporting invoices
        /// Expected behavior, state:
        ///   Two upload process will start.
        /// </summary>
        [TestMethod]
        public void ExportChangedItems_Batch_Test()
        {
            #region Arrange
            // Create invoices for stores.
            var invoice = new Mock<IRetailProDocument>();
            invoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            var invoice2 = new Mock<IRetailProDocument>();
            invoice2.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", invoice, invoice2);

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(1);

            var configuration = new Mock<IConfiguration>();
            configuration.Setup(c => c.LastModifiedInvoiceDate).Returns(new DateDictionary() { DefaultDate = DateTime.Now.AddMonths(-1) });

            var mapper = new Mock<IRetailProModelMapper>();
            var mappedInvoice = new Invoice();            
            mappedInvoice.InvoiceItems = Enumerable.Empty<InvoiceItem>();
            mappedInvoice.Customer = new Customer();
            mappedInvoice.ModifiedDate = invoice.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit).ToString();
            mapper.Setup(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), 42)).Returns(mappedInvoice);

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => VerifyUploadedInvoiceCount(data, 1))), Times.Exactly(2));
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Two new invoices in DB, MaxUploadItems set to 1, upload fails.
        /// Description:
        ///   Tests exporting invoices
        /// Expected behavior, state:
        ///   Timestamp will be unchanged.
        /// </summary>
        [TestMethod]
        public void ExportChangedItems_Timestamp_Unchanged_If_Upload_Fails_Test()
        {
            #region Arrange
            
            // Create invoices for stores.
            var invoice = new Mock<IRetailProDocument>();
            invoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            // Store's invoice
            var store = new Store()
            {
                SbsNo = SbsNumber,
                StoreNo = "ABC"
            };

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", invoice);

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(1);

            var timestamp = DateTime.Now.AddMonths(-1);
            var configuration = new Mock<IConfiguration>();
            configuration.SetupProperty(c => c.LastModifiedInvoiceDate, new DateDictionary() { DefaultDate = timestamp });

            var mapper = new Mock<IRetailProModelMapper>();
            mapper.Setup(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), It.IsAny<long>()))
                .Returns(CreateMockInvoice("858585", store, new Customer(), DateTime.Now));

            // Service always fails so we can test what happens when its unavailable
            this.serviceMock.Setup(svc => svc.UploadAsync(It.IsAny<string>(), It.IsAny<object>())).Returns(Task.Factory.StartNew(() => { throw new ServiceException(RetailProCommon.Urls.InvoiceUpload, System.Net.HttpStatusCode.BadRequest); }));

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            try
            {
                target.ExportChangedItems().Wait();
                Assert.Fail("ServiceException should have been thrown.");
            }
            catch (AggregateException)
            {
            }

            #region Assert          
            Assert.AreEqual(timestamp, configuration.Object.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreEqual(timestamp, configuration.Object.LastModifiedInvoiceDate.GetDate(store.SbsNo, store.StoreNo));
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Two invoices in DB, first one is a "phantom" record, all of its fields are zero
        /// Description:
        ///   Tests exporting invoices
        /// Expected behavior, state:
        ///   The latter invoice is extracted and the phantom record is skipped
        /// </summary>
        [TestMethod]
        public void ExportChangedItems_Phantom_Records_Are_Skipped()
        {
            #region Arrange

            // Create invoices for stores.
            var phantomInvoice = new Mock<IRetailProDocument>();
            phantomInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.MinValue);

            var realInvoice = new Mock<IRetailProDocument>();
            realInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", phantomInvoice, realInvoice);

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(1);
            appConfig.Setup(c => c.IgnoreEarlierInvoicesFilter).Returns(DateTime.Now.AddMonths(-4));

            var configuration = new Mock<IConfiguration>();
            configuration.Setup(c => c.LastModifiedInvoiceDate).Returns(new DateDictionary() { DefaultDate = DateTime.Now.AddMonths(-1) });

            // Setup mapper for the realInvoice object
            var mapper = new Mock<IRetailProModelMapper>();
            var mappedInvoice = new Invoice();
            mappedInvoice.InvoiceItems = Enumerable.Empty<InvoiceItem>();
            mappedInvoice.Customer = new Customer();
            mappedInvoice.ModifiedDate = realInvoice.Object.GetDateTime(RDA2.FieldIDs.fidDocLastEdit).ToString();
            mapper.Setup(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), 42)).Returns(mappedInvoice);

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => VerifyUploadedInvoiceCount(data, 1))), Times.Exactly(1));
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => !VerifyUploadInvoiceHasNoPhantom(data))), Times.Never());
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Two separate stores are in DB with different last modified date
        /// Description:
        ///   Tests that different time filters are used for different stores.
        /// Expected behavior, state:
        ///   No unnecessary invoices are extracted, before the oldest timestamp
        ///   All invoices are exported, even the ones older than the most recent timestamp
        /// </summary>
        [TestMethod]
        public void Export_Multiple_Stores()
        {
            #region Arrange
            Store localStore = new Store() { SbsNo = 123, StoreNo = "ABC" };
            Store syncedStore = new Store() { SbsNo = 456, StoreNo = "DEF" };

            // Create invoices for stores.
            var oldInvoice = new Mock<IRetailProDocument>();
            oldInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now.AddDays(-1));
            oldInvoice.Setup(i => i.GetString(RDA2.FieldIDs.fidStore)).Returns(localStore.StoreNo);

            // The assumption is that some invoices arrive with a delay for synched stores,
            // so even the the local store's modified date is DateTime.Now, the synched store's 
            // invoice from an hour ago has just arrived in the system
            var recentInvoice = new Mock<IRetailProDocument>();
            recentInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(DateTime.Now.AddHours(-1));
            recentInvoice.Setup(i => i.GetString(RDA2.FieldIDs.fidStore)).Returns(syncedStore.StoreNo);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", new Mock<IRetailProDocument>[] { recentInvoice, oldInvoice });

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(1);
            appConfig.Setup(c => c.SyncDaysLimit).Returns(5);

            var initialDict = new DateDictionary() { DefaultDate = DateTime.Now.AddMonths(-4) };
            initialDict.SetDate(localStore.SbsNo, localStore.StoreNo, DateTime.Now);
            initialDict.SetDate(syncedStore.SbsNo, syncedStore.StoreNo, DateTime.Now.AddDays(-1));

            var configuration = new Mock<IConfiguration>();
            configuration.SetupProperty(c => c.LastModifiedInvoiceDate, initialDict);

            var mapper = new Mock<IRetailProModelMapper>();
            mapper.Setup(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), It.IsAny<long>()))
                .Returns((IRetailProDocument doc, IRetailProTable table, long sbsNumber) => new Invoice()
                {
                    SbsNo = sbsNumber,
                    StoreNo = doc.GetString(RDA2.FieldIDs.fidStore),
                    InvoiceItems = Enumerable.Empty<InvoiceItem>(),
                    Customer = new Customer(),
                    ModifiedDate = doc.GetDateTime(RDA2.FieldIDs.fidDocLastEdit).ToString()
                });

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert
            this.serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(data => VerifyUploadedInvoiceCount(data, 1))), Times.Exactly(1));
            #endregion
        }
        
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Two separate stores are in DB on with really old last modified date
        /// Description:
        ///   Tests that data file is not read back to the oldest last modified date
        /// Expected behavior, state:
        ///   Exporter halts reading the data file over SyncDaysLimit
        /// </summary>
        [TestMethod]
        public void Export_Inactive_Store()
        {
            #region Arrange
            Store activeStore = new Store() { SbsNo = 123, StoreNo = "ABC" };
            Store inactiveStore = new Store() { SbsNo = 456, StoreNo = "DEF" };

            var oldTimestamp = DateTime.Now.AddDays(-20);
            var recentTimestamp = DateTime.Now.AddHours(-1);

            // Create invoices for stores.
            var oldInvoice = new Mock<IRetailProDocument>();
            oldInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(oldTimestamp);
            oldInvoice.Setup(i => i.GetString(RDA2.FieldIDs.fidStore)).Returns(inactiveStore.StoreNo);

            // The assumption is that some invoices arrive with a delay for synched stores,
            // so even the the local store's modified date is DateTime.Now, the synched store's 
            // invoice from an hour ago has just arrived in the system
            var recentInvoice = new Mock<IRetailProDocument>();
            recentInvoice.Setup(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit)).Returns(recentTimestamp);
            recentInvoice.Setup(i => i.GetString(RDA2.FieldIDs.fidStore)).Returns(activeStore.StoreNo);

            // Extractor and mapper                         
            this.extractorMock.SetupItems("Invoices", new Mock<IRetailProDocument>[] { recentInvoice, oldInvoice });

            var appConfig = new Mock<IV8AppConfiguration>();
            appConfig.Setup(c => c.MaxUploadItems).Returns(1);
            appConfig.Setup(c => c.SyncDaysLimit).Returns(5);

            var initialDict = new DateDictionary() { DefaultDate = DateTime.Now.AddMonths(-4) };
            initialDict.SetDate(activeStore.SbsNo, activeStore.StoreNo, recentTimestamp.AddMinutes(-5));
            initialDict.SetDate(inactiveStore.SbsNo, inactiveStore.StoreNo, oldTimestamp);

            var configuration = new Mock<IConfiguration>();
            configuration.SetupProperty(c => c.LastModifiedInvoiceDate, initialDict);

            var mapper = new Mock<IRetailProModelMapper>();
            mapper.Setup(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), It.IsAny<long>()))
                .Returns((IRetailProDocument doc, IRetailProTable table, long sbsNumber) => new Invoice()
                {
                    SbsNo = sbsNumber,
                    StoreNo = doc.GetString(RDA2.FieldIDs.fidStore),
                    InvoiceItems = Enumerable.Empty<InvoiceItem>(),
                    Customer = new Customer(),
                    ModifiedDate = doc.GetDateTime(RDA2.FieldIDs.fidDocLastEdit).ToString()
                });

            var target = new InvoiceExporter(this.extractorMock.Object, appConfig.Object, configuration.Object, this.serviceMock.Object, mapper.Object);
            #endregion

            // Act
            target.ExportChangedItems().Wait();

            #region Assert
            mapper.Verify(m => m.GetInvoiceObject(It.IsAny<IRetailProDocument>(), It.IsAny<IRetailProTable>(), It.IsAny<long>()), Times.Exactly(1), "Only the recent invoice should be inspected");
            #endregion
        }

        /// <summary>
        /// Generates a mock invoice
        /// </summary>
        /// <param name="invoiceSid">Invoice's id</param>
        /// <param name="store">Store entity used to set SBS number and Store number</param>
        /// <param name="customer">Invoice's customer</param>
        /// <param name="modifiedDate">Invoices modified date</param>
        /// <returns>The created mock invoice</returns>
        private static Invoice CreateMockInvoice(string invoiceSid, Store store, Customer customer, DateTime modifiedDate)
        {
            return new Invoice()
            {
                InvoiceSid = invoiceSid,
                StoreNo = store.StoreNo,
                SbsNo = store.SbsNo,
                ModifiedDate = modifiedDate.ToString(),
                Customer = customer,
                InvoiceItems = Enumerable.Empty<InvoiceItem>()
            };
        }

        /// <summary>
        /// Verifies the uploaded invoice count.
        /// </summary>
        /// <param name="data">The upload data.</param>
        /// <param name="invoiceCount">The invoice count.</param>
        /// <returns>True if counts match; false otherwise.</returns>
        private static bool VerifyUploadedInvoiceCount(object data, int invoiceCount)
        {
            var prop = data.GetType().GetProperty("Invoices");
            var invoices = (IEnumerable<Invoice>)prop.GetValue(data, null);
            return invoices.Count() == invoiceCount;
        }

        /// <summary>
        /// Verifies the uploaded invoice has no phantom invoice
        /// </summary>
        /// <param name="data">The upload data.</param>
        /// <returns>True if there are no phantom invoices</returns>
        private static bool VerifyUploadInvoiceHasNoPhantom(object data)
        {
            var prop = data.GetType().GetProperty("Invoices");
            var invoices = (IEnumerable<Invoice>)prop.GetValue(data, null);

            return invoices
                    .Where(i => i.ModifiedDate == DateTime.MinValue.ToString())
                    .Count() == 0;
        }
    } 
}
