//-----------------------------------------------------------------------
// <copyright file="SwarmServiceTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 14:58</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Test
{
    using System;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.Configuration;
    using RetailProCommon.Service;

    /// <summary>
    /// Test class for the swarm service.
    /// </summary>
    [TestClass]
    public class SwarmServiceTest
    {
        /// <summary>
        /// Tested method:
        ///   UploadAsync
        /// Tested class:
        ///   SwarmService
        /// Initial data, state:
        ///   Null content is given.
        /// Description:
        ///   Tests if the service throws an exception for null content.
        /// Cause of failure:
        ///   Content is null.
        /// </summary>
        [ExpectedException(typeof(ArgumentNullException))]
        [TestMethod]
        public void Null_Content_Throws_Exception()
        {
            var appConfig = new Mock<ICommonAppConfiguration>();
            appConfig.Setup(ac => ac.ServiceBaseUrl).Returns("http://example.com");
            var target = new SwarmService(appConfig.Object);
            target.UploadAsync("url", null).Wait();
        }

        /// <summary>
        /// Tested method:
        ///   SwarmService ctor
        /// Tested class:
        ///   SwarmService
        /// Initial data, state:
        ///   Null appConfig is given.
        /// Description:
        ///   Tests if the service throws an exception for null dependency.
        /// Cause of failure:
        ///   AppConfig is null.
        /// </summary>        
        [ExpectedException(typeof(ArgumentNullException))]
        [TestMethod]
        public void Null_AppConfig_Throws_Exception()
        {
            var target = new SwarmService(null);
        }

        /// <summary>
        /// Tested method:
        ///   GetAsync
        /// Tested class:
        ///   SwarmService
        /// Initial data, state:
        ///   Null url is given.
        /// Description:
        ///   Tests if the service throws an exception for null url.
        /// Cause of failure:
        ///   Url is null
        /// </summary>        
        [ExpectedException(typeof(ArgumentException))]
        [TestMethod]
        public void GetAsnyc_Null_Url_Throws_Exception()
        {
            var appConfig = new Mock<ICommonAppConfiguration>();
            appConfig.Setup(ac => ac.ServiceBaseUrl).Returns("http://example.com");
            var target = new SwarmService(appConfig.Object);
            target.GetAsync<object>(null);
        }

        /// <summary>
        /// Tested method:
        ///   UploadAsync
        /// Tested class:
        ///   SwarmService
        /// Initial data, state:
        ///   Some content is given.
        /// Description:
        ///   Tests that the REST request has SwarmId and Pos-Software headers
        /// Cause of failure:
        ///   Headers are not part of the REST request sent
        /// </summary>
        [TestMethod]
        public void Upload_Has_SwarmId_And_PosSoftware_Headers()
        {
            // Arrange
            const string SwarmId = "sonrisa";
            const string PosSoftware = "retailpro9";

            var appConfig = new Mock<ICommonAppConfiguration>();
            appConfig.Setup(ac => ac.ServiceBaseUrl).Returns("http://example.com");
            appConfig.Setup(ac => ac.SwarmId).Returns(SwarmId);
            appConfig.Setup(ac => ac.PosSoftwareId).Returns(PosSoftware);
            var target = new SwarmService(appConfig.Object);

            // Act
            var request = target.CreateUploadRequest("gateway/service/location", "{\"DataField\":\"Value\"}");

            // Assert
            Assert.AreEqual(SwarmId, request.Headers.Get("SwarmId"));
            Assert.AreEqual(PosSoftware, request.Headers.Get("Pos-Software"));
        }
    }
}
