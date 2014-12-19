//-----------------------------------------------------------------------
// <copyright file="FileConfigurationTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 26. 9:58</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Test
{
    using System;
    using System.IO;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;

    /// <summary>
    /// Test class for the swarm service.
    /// </summary>
    [TestClass]
    public class FileConfigurationTest
    {
        /// <summary>
        /// The application configuration mock.
        /// </summary>
        private static Mock<ICommonAppConfiguration> appConfigMock;

        /// <summary>
        /// Initializes the test class.
        /// </summary>
        /// <param name="context">The context.</param>
        [ClassInitialize]
        public static void ClassInitialize(TestContext context)
        {
            appConfigMock = new Mock<ICommonAppConfiguration>();
            appConfigMock.Setup(c => c.ConfigDirectoryName).Returns("Unittest");
            appConfigMock.Setup(c => c.ConfigFileName).Returns("Settings.xml");            
            DeleteTestSettings();
        }

        /// <summary>
        /// Cleans up after each tests.
        /// </summary>
        [TestCleanup]
        public void TestCleanup()
        {
            DeleteTestSettings();
        }

        /// <summary>
        /// Tested method:
        ///   Dispose
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   FileConfiguration is filled with some test data
        /// Description:
        ///   FileConfiguration instance is disposed and then read back
        /// Expected behavior, state:
        ///   The content was persisted
        /// </summary>
        [TestMethod]
        public void FileConfiguration_Persistence_Test()
        {
            #region Arrange
            var baseTime = DateTime.Now;
            var store = new Store()
            {
                StoreNumber = "ABC",
                SbsNumber = 12
            };

            var swarmServiceMock = new Mock<ISwarmService>();
            var first = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);

            first.LastModifiedStoreDate = baseTime;
            first.LastModifiedVersionDate = baseTime.AddDays(-1);
            first.LastModifiedInvoiceDate = new DateDictionary() { DefaultDate = baseTime };
            first.LastModifiedInvoiceDate.SetDate(store.SbsNumber, store.StoreNumber, baseTime.AddMinutes(-10));
            #endregion

            // Act
            first.Dispose();
            var second = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);

            #region Assert
            Assert.AreEqual(baseTime, second.LastModifiedStoreDate);
            Assert.AreEqual(baseTime.AddDays(-1), second.LastModifiedVersionDate);

            Assert.AreEqual(baseTime, second.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreEqual(baseTime.AddMinutes(-10), second.LastModifiedInvoiceDate.GetDate(store.SbsNumber, store.StoreNumber));
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   FileConfiguration.ctor
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   Version 1.5.0.917's SettingsV8.xml is placed in the AppData folder
        /// Description:
        ///   FileConfiguration is read
        /// Expected behavior, state:
        ///   Since certain fields are missing they'll instansiate with default values
        /// </summary>
        [TestMethod]
        public void FileConfiguration_Loading_With_Incorrect_Format_Test()
        {
            // Arrange
            var swarmServiceMock = new Mock<ISwarmService>();

            // Copy SettingsV8.xml to app data folder
            var destination = Path.Combine(GetConfigurationDirPath(), appConfigMock.Object.ConfigFileName);
            File.Copy(@"..\..\TestData\V8-1.5.0.917\Settings.xml", destination);

            // Act
            var fileConfiguration = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);

            #region Assert
            Assert.AreNotEqual(DateTime.MinValue, fileConfiguration.LastModifiedStoreDate);
            Assert.AreNotEqual(DateTime.MinValue, fileConfiguration.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreNotEqual(DateTime.MinValue, fileConfiguration.LastModifiedInvoiceDate.GetDate(1, "ABC"), "Not existing store should give default value");
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   FileConfiguration.ctor
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   Lajolla's SettingsV8.xml is placed in the AppData folder
        /// Description:
        ///   FileConfiguration is read
        /// Expected behavior, state:
        ///   Date dictionary is filled with appropriate values
        /// </summary>
        [TestMethod]
        public void FileConfiguration_Loading_With_Production_Data()
        {
            // Arrange
            var swarmServiceMock = new Mock<ISwarmService>();

            // Copy SettingsV8.xml to app data folder
            var destination = Path.Combine(GetConfigurationDirPath(), appConfigMock.Object.ConfigFileName);
            File.Copy(@"..\..\TestData\V8-1.7.1.0-Lajolla\Settings.xml", destination);

            // Act
            var fileConfiguration = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);

            #region Assert
            Assert.AreEqual(Convert.ToDateTime("2014-10-21T21:05:30"), fileConfiguration.LastModifiedInvoiceDate.GetDate(0, "GVM"));
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   LoadRemoteConfiguration
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   None
        /// Description:
        ///   Tests overwriting file configuration with remote values if the version is greater.
        /// Expected behavior, state:
        ///   The remote values will be used.
        /// </summary>
        [TestMethod]        
        public void LoadRemoteConfigurations_Test()
        {
            #region Arrange
            var random = new Random();
            var swarmServiceMock = new Mock<ISwarmService>();
            var newDate = DateTime.Now.AddDays(-random.Next(1000) - 1);
            var remoteConfig = new RemoteConfiguration()
            {
                Version = 1,
                LastVersion = newDate,
                LastInvoice = newDate,
                LastStore = newDate
            };

            swarmServiceMock.Setup(svc => svc.GetAsync<RemoteConfiguration>(It.IsAny<string>()))
                .Returns(Task.Factory.StartNew<RemoteConfiguration>(() => remoteConfig));            

            var target = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);

            Store store = new Store() { SbsNumber = 123, StoreNumber = "ABC" };
            target.LastModifiedInvoiceDate = new DateDictionary() { DefaultDate = DateTime.Now };
            target.LastModifiedInvoiceDate.SetDate(store.SbsNumber, store.StoreNumber, DateTime.Now);
            
            target.LastModifiedStoreDate = DateTime.Now;
            target.LastModifiedVersionDate = DateTime.Now;
            #endregion

            // Act                        
            target.LoadRemoteConfiguration().Wait();

            #region Assert
            Assert.AreEqual(remoteConfig.LastInvoice, target.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreEqual(remoteConfig.LastInvoice, target.LastModifiedInvoiceDate.GetDate(store.SbsNumber, store.StoreNumber));
            Assert.AreEqual(remoteConfig.LastStore, target.LastModifiedStoreDate);
            Assert.AreEqual(remoteConfig.LastVersion, target.LastModifiedVersionDate);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   LoadRemoteConfigurations
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   None
        /// Description:
        ///   Tests if the file configuration is not overwritten with remote values if the version is equal.
        /// Expected behavior, state:
        ///   The remote values will NOT be used.
        /// </summary>
        [TestMethod]
        public void LoadRemoteConfiguration_Older_Version_Test()
        {
            #region Arrange
            var random = new Random();
            var swarmServiceMock = new Mock<ISwarmService>();
            var remoteConfig = new RemoteConfiguration()
            {
                Version = 0,
                LastVersion = DateTime.Now.AddDays(-random.Next(1000)),
                LastInvoice = DateTime.Now.AddDays(-random.Next(1000)),
                LastStore = DateTime.Now.AddDays(-random.Next(1000)),
            };

            swarmServiceMock.Setup(svc => svc.GetAsync<RemoteConfiguration>(It.IsAny<string>()))
                .Returns(Task.Factory.StartNew<RemoteConfiguration>(() => remoteConfig));

            var target = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);
            #endregion

            // Act                        
            target.LoadRemoteConfiguration().Wait();

            #region Assert
            Assert.AreNotEqual(remoteConfig.LastInvoice, target.LastModifiedInvoiceDate);
            Assert.AreNotEqual(remoteConfig.LastStore, target.LastModifiedStoreDate);
            Assert.AreNotEqual(remoteConfig.LastVersion, target.LastModifiedVersionDate);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   LoadRemoteConfiguration
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   Null datetimes will be returned.
        /// Description:
        ///   Tests overwriting file configuration with remote values if the version is greater.
        /// Expected behavior, state:
        ///   Null remote values will NOT be used.
        /// </summary>
        [TestMethod]
        public void LoadRemoteConfigurations_Nulls_Not_Used_Test()
        {
            #region Arrange
            var random = new Random();
            var swarmServiceMock = new Mock<ISwarmService>();
            var remoteConfig = new RemoteConfiguration()
            {
                Version = 1,
            };

            swarmServiceMock.Setup(svc => svc.GetAsync<RemoteConfiguration>(It.IsAny<string>()))
                .Returns(Task.Factory.StartNew<RemoteConfiguration>(() => remoteConfig));

            var target = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);
            var lastInvoiceDefault = target.LastModifiedInvoiceDate.DefaultDate;
            var lastStore = target.LastModifiedStoreDate;
            var lastVersion = target.LastModifiedVersionDate;
            #endregion

            // Act                        
            target.LoadRemoteConfiguration().Wait();

            #region Assert
            Assert.AreEqual(lastInvoiceDefault, target.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreEqual(lastStore, target.LastModifiedStoreDate);
            Assert.AreEqual(lastVersion, target.LastModifiedVersionDate);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   LoadRemoteConfiguration
        /// Tested class:
        ///   FileConfiguration
        /// Initial data, state:
        ///   Future datetimes will be returned.
        /// Description:
        ///   Tests overwriting file configuration with remote values if the version is greater.
        /// Expected behavior, state:
        ///   Future remote values will BE used.
        /// </summary>
        [TestMethod]
        public void LoadRemoteConfigurations_Future_Dates_Are_Used_Test()
        {
            #region Arrange
            var swarmServiceMock = new Mock<ISwarmService>();
            var target = new FileConfiguration(appConfigMock.Object, swarmServiceMock.Object);            
            
            var remoteConfig = new RemoteConfiguration()
            {
                Version = 1,
                LastVersion = target.LastModifiedVersionDate.AddMinutes(1),
                LastStore = target.LastModifiedStoreDate.AddMinutes(1),
                LastInvoice = target.LastModifiedInvoiceDate.DefaultDate.AddMinutes(1)
            };

            swarmServiceMock.Setup(svc => svc.GetAsync<RemoteConfiguration>(It.IsAny<string>()))
                .Returns(Task.Factory.StartNew<RemoteConfiguration>(() => remoteConfig));
            
            var lastInvoiceDefault = target.LastModifiedInvoiceDate.DefaultDate;
            var lastStore = target.LastModifiedStoreDate;
            var lastVersion = target.LastModifiedVersionDate;
            #endregion

            // Act                        
            target.LoadRemoteConfiguration().Wait();

            #region Assert
            Assert.AreNotEqual(lastInvoiceDefault, target.LastModifiedInvoiceDate.DefaultDate);
            Assert.AreNotEqual(lastStore, target.LastModifiedStoreDate);
            Assert.AreNotEqual(lastVersion, target.LastModifiedVersionDate);
            #endregion
        }

        /// <summary>
        /// Deletes the test settings file.
        /// </summary>
        private static void DeleteTestSettings()
        {
            var dirPath = GetConfigurationDirPath();
            if (Directory.Exists(dirPath))
            {
                var path = Path.Combine(dirPath, appConfigMock.Object.ConfigFileName);
                if (File.Exists(path))
                {
                    File.Delete(path);
                }
            }
        }

        /// <summary>
        /// Returns the path for AppData folder used for testing
        /// </summary>
        /// <returns>The directory's absolute path</returns>
        private static string GetConfigurationDirPath()
        {
            return Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), appConfigMock.Object.ConfigDirectoryName);
        }
    }
}
