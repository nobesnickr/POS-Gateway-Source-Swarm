//-----------------------------------------------------------------------
// <copyright file="InvoiceExporterTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 14:01</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Test
{
    using System;
    using System.Collections;
    using System.Collections.Generic;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using NHibernate;
    using NHibernate.Linq;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;
    using RetailProSwarmExporter.BusinessLogic;

    /// <summary>
    /// Tests the Invoice exporter.
    /// </summary>
    [TestClass]
    public class InvoiceExporterTest
    {        
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   New Invoice record in db.
        /// Description:
        ///   Tests detecting new Invoice records.
        /// Expected behavior, state:
        ///   The new record is uploaded using the service.
        /// </summary>
        [TestMethod]        
        public void Invoice_Exports_New_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange                            
                var appConfig = new Mock<ICommonAppConfiguration>();
                appConfig.Setup(ac => ac.MaxUploadItems).Returns(10);

                var configuration = new Mock<IConfiguration>();
                configuration.Setup(c => c.LastModifiedInvoiceDate).Returns(new DateDictionary());
                
                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var store = new Store { StoreNumber = "AAA", SbsNumber = 123 };
                    s.Save(store);

                    var customer = new Customer() { CustomerSid = "sid" };
                    s.Save(customer);
                    var invoice = new Invoice()
                    {
                        InvoiceSid = "ABC123",
                        SbsNumber = store.SbsNumber,
                        StoreNumber = store.StoreNumber,
                        InvoiceNumber = "12",
                        ModifiedDate = DateTime.Now,
                        Customer = customer,                          
                    };
                    s.Save(invoice);
                    s.Flush();
                }

                var target = new InvoiceExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Act                
                target.ExportChangedItems().Wait();

                // Assert                
                service.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(o => ValidateInvoiceCount(o, 1))), Times.Once());
            }
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///   Old Invoice record in db.
        /// Description:
        ///   Tests detecting new Invoice records.
        /// Expected behavior, state:
        ///   The old record is not uploaded.
        /// </summary>
        [TestMethod]
        public void Invoice_Does_Not_Export_Old_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange     
                var appConfig = new Mock<ICommonAppConfiguration>();
                var configuration = new Mock<IConfiguration>();

                configuration.Setup(c => c.LastModifiedInvoiceDate).Returns(new DateDictionary() { DefaultDate = DateTime.Now });

                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync("invoice", It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var invoice = new Invoice()
                    {
                        InvoiceSid = "123",
                        SbsNumber = 41,   
                        StoreNumber = "A10",
                        InvoiceNumber = "12",
                        ModifiedDate = DateTime.Now.AddDays(-10)
                    };
                    s.Save(invoice);
                    s.Flush();
                }

                // Act
                var target = new InvoiceExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Assert
                target.ExportChangedItems();
                service.Verify(svc => svc.UploadAsync("invoice", It.IsAny<object>()), Times.Never());
            }
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///  12 new invoices in DB, MaxUploadItems is set to 2,
        ///  6 invoices have the same modified date, 6 have different
        /// Description:
        ///   Tests detecting number of upload attempts.
        /// Expected behavior, state:
        ///   12/2=6 upload attempts are made, each with 2 invoices
        /// </summary>
        [TestMethod]
        public void Invoice_Exports_More_Than_Upload_Limit()
        {
            const int InvoiceCount = 12;
            const int BatchSize = 2;

            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange                            
                var appConfig = new Mock<ICommonAppConfiguration>();
                appConfig.Setup(ac => ac.MaxUploadItems).Returns(BatchSize);

                var configuration = new Mock<IConfiguration>();
                configuration.SetupProperty(c => c.LastModifiedInvoiceDate, new DateDictionary());

                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var store = new Store { StoreNumber = "AAA", SbsNumber = 123 };
                    s.Save(store);

                    var customer = new Customer() { CustomerSid = "sid" };
                    s.Save(customer);

                    for (int i = 0; i < InvoiceCount; i++)
                    {
                        var invoice = new Invoice()
                        {
                            InvoiceSid = (100 + i).ToString(),
                            SbsNumber = store.SbsNumber,
                            StoreNumber = store.StoreNumber,
                            InvoiceNumber = i.ToString(),
                            ModifiedDate = (i < InvoiceCount / 2) ? DateTime.Now : DateTime.Now.AddDays(-i),
                            Customer = customer
                        };

                        s.Save(invoice);
                    }

                    s.Flush();
                }

                var target = new InvoiceExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Act                
                target.ExportChangedItems().Wait();

                // Assert                
                service.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(o => ValidateInvoiceCount(o, BatchSize))), Times.Exactly(InvoiceCount / BatchSize));
            }
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   InvoiceExporter
        /// Initial data, state:
        ///  There are two stores and respective invoices in the database, before
        ///  and after the last modified date of the configuration.
        /// Description:
        ///   Tests that different modified dates are used for invoices in 
        ///   different stores.
        /// Expected behavior, state:
        ///   Invoices before the store based modified date cached value
        ///   are ignored, others are exported.
        /// </summary>
        [TestMethod]
        public void Invoice_Export_Uses_Different_Modified_Dates()
        {
            DateTime lastLocalExport = DateTime.Now;

            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange                            
                var appConfig = new Mock<ICommonAppConfiguration>();
                appConfig.Setup(ac => ac.MaxUploadItems).Returns(100);

                var configuration = new Mock<IConfiguration>();

                var localStore = new Store() { StoreNumber = "1", SbsNumber = 123, StoreName = "Local" };
                var syncedStore = new Store() { StoreNumber = "2", SbsNumber = 456, StoreName = "Synched store" };

                var initialDict = new DateDictionary();
                initialDict.SetDate(localStore.SbsNumber, localStore.StoreNumber, lastLocalExport);
                initialDict.SetDate(syncedStore.SbsNumber, syncedStore.StoreNumber, lastLocalExport.AddHours(-1));

                configuration.SetupProperty(c => c.LastModifiedInvoiceDate, initialDict);

                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var customer = new Customer() { CustomerSid = "sid" };
                    s.Save(customer);

                    s.Save(localStore);
                    s.Save(syncedStore);

                    // Old invoice
                    s.Save(CreateMockInvoice("200", localStore, customer, lastLocalExport.AddDays(-2)));

                    // Newer invoice for local store
                    s.Save(CreateMockInvoice("300", localStore, customer, lastLocalExport.AddMinutes(-10)));

                    // Old invoice for synched store already should've been exported
                    s.Save(CreateMockInvoice("400", syncedStore, customer, lastLocalExport.AddHours(-5)));

                    // New invoice for local store, generated when synched, should be exported
                    s.Save(CreateMockInvoice("500", syncedStore, customer, lastLocalExport.AddMinutes(-15)));

                    s.Flush();
                }

                var target = new InvoiceExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Act                
                target.ExportChangedItems().Wait();

                // Assert
                service.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.InvoiceUpload, It.Is<object>(o => ValidateInvoiceCount(o, 1))));
            }
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
                StoreNumber = store.StoreNumber,
                SbsNumber = store.SbsNumber,
                ModifiedDate = modifiedDate,
                Customer = customer
            };
        }

        /// <summary>
        /// Validates the invoice count on given data object.
        /// </summary>
        /// <param name="data">The data object.</param>
        /// <param name="count">The expected count.</param>
        /// <returns>True if the counts match.</returns>
        private static bool ValidateInvoiceCount(dynamic data, int count)
        {
            return data.Invoices.Count == count;
        }
    }
}
