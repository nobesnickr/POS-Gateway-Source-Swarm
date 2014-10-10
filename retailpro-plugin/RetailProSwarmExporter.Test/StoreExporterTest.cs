//-----------------------------------------------------------------------
// <copyright file="StoreExporterTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 11:41</date>
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
    using RetailProCommon;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;
    using RetailProSwarmExporter.BusinessLogic;

    /// <summary>
    /// Tests the store exporter.
    /// </summary>
    [TestClass]
    public class StoreExporterTest
    {        
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   StoreExporter
        /// Initial data, state:
        ///   New store record in db.
        /// Description:
        ///   Tests detecting new store records.
        /// Expected behavior, state:
        ///   The new record is uploaded using the service.
        /// </summary>
        [TestMethod]
        public void Store_Exports_New_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange                            
                var appConfig = new Mock<ICommonAppConfiguration>();
                appConfig.Setup(ac => ac.MaxUploadItems).Returns(10);
                var configuration = new Mock<IConfiguration>();
                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync(Urls.StoreUpload, It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var store = new Store()
                    {
                        SbsNumber = 41,
                        StoreName = "Name",
                        StoreNumber = "12",
                        ModifiedDate = DateTime.Now
                    };
                    s.Save(store);
                    s.Flush();
                }

                var target = new StoreExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Act                
                target.ExportChangedItems().Wait();

                // Assert                
                service.Verify(svc => svc.UploadAsync(Urls.StoreUpload, It.Is<IEnumerable<Store>>(s => s.Count() == 1)), Times.Once());
            }
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   StoreExporter
        /// Initial data, state:
        ///   Old store record in db.
        /// Description:
        ///   Tests detecting new store records.
        /// Expected behavior, state:
        ///   The old record is not uploaded.
        /// </summary>
        [TestMethod]
        public void Store_Does_Not_Export_Old_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange            
                var configuration = new Mock<IConfiguration>();
                configuration.Setup(c => c.LastModifiedStoreDate).Returns(DateTime.Now);
                var appConfig = new Mock<ICommonAppConfiguration>();
                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync("store", It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var store = new Store()
                    {
                        SbsNumber = 41,
                        StoreName = "Old",
                        StoreNumber = "12",
                        ModifiedDate = DateTime.Now.AddDays(-10)
                    };
                    s.Save(store);
                    s.Flush();
                }

                // Act
                var target = new StoreExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Assert
                target.ExportChangedItems().Wait();
                service.Verify(svc => svc.UploadAsync("store", It.IsAny<object>()), Times.Never());
            }
        }
    }
}
