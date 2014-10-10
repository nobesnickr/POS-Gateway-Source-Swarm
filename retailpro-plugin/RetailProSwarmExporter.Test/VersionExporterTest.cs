//-----------------------------------------------------------------------
// <copyright file="VersionExporterTest.cs" company="Sonrisa">
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
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;
    using RetailProSwarmExporter.BusinessLogic;

    /// <summary>
    /// Tests the Version exporter.
    /// </summary>
    [TestClass]
    public class VersionExporterTest
    {        
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   VersionExporter
        /// Initial data, state:
        ///   New RetailProVersion record in db.
        /// Description:
        ///   Tests detecting new RetailProVersion records.
        /// Expected behavior, state:
        ///   The new record is uploaded using the service.
        /// </summary>
        [TestMethod]
        public void RetailProVersion_Exports_New_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange                            
                var appConfig = new Mock<ICommonAppConfiguration>();
                appConfig.Setup(ac => ac.MaxUploadItems).Returns(10);

                var configuration = new Mock<IConfiguration>();
                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.VersionUpload, It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var retailProVersion = new RetailProVersion()
                    {
                        UpdatedDate = DateTime.Now
                    };
                    s.Save(retailProVersion);
                    s.Flush();
                }

                var target = new VersionExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Act                
                target.ExportChangedItems().Wait();

                // Assert                
                service.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.VersionUpload, It.Is<IEnumerable<RetailProVersion>>(s => s.Count() == 1)), Times.Once());
            }
        }

        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   RetailProVersionExporter
        /// Initial data, state:
        ///   Old RetailProVersion record in db.
        /// Description:
        ///   Tests detecting new RetailProVersion records.
        /// Expected behavior, state:
        ///   The old record is not uploaded.
        /// </summary>
        [TestMethod]
        public void RetailProVersion_Does_Not_Export_Old_Item_Test()
        {
            using (var connection = TestHelper.OpenConnection())
            {
                // Arrange        
                var appConfig = new Mock<ICommonAppConfiguration>();
                var configuration = new Mock<IConfiguration>();
                configuration.Setup(c => c.LastModifiedVersionDate).Returns(DateTime.Now);

                var service = new Mock<ISwarmService>();
                service.Setup(svc => svc.UploadAsync("RetailProVersion", It.IsAny<object>())).ExecutesAsync();

                using (var s = TestHelper.SessionFactory.OpenSession(connection))
                {
                    var retailProVersion = new RetailProVersion()
                    {
                        UpdatedDate = DateTime.Now.AddDays(-10)
                    };
                    s.Save(retailProVersion);
                    s.Flush();
                }

                // Act
                var target = new VersionExporter(() => TestHelper.SessionFactory.OpenSession(connection), configuration.Object, service.Object, appConfig.Object);

                // Assert
                target.ExportChangedItems().Wait();
                service.Verify(svc => svc.UploadAsync("version", It.IsAny<object>()), Times.Never());
            }
        }
    }
}
