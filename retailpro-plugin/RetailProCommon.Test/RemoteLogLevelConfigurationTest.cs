//-----------------------------------------------------------------------
// <copyright file="RemoteLogLevelConfigurationTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 26. 9:58</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Test
{
    using System;
    using System.Collections.Generic;
    using System.IO;
    using System.Linq;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.Configuration;
    using RetailProCommon.Logging;
    using RetailProCommon.Service;

    /// <summary>
    /// Test class for the swarm service.
    /// </summary>
    [TestClass]
    public class RemoteLogLevelConfigurationTest
    {
        /// <summary>
        /// Tested method:
        ///   ApplyRemoteLogLevels
        /// Tested class:
        ///   RemoteLogLevelConfiguration
        /// Initial data, state:
        ///   No remote config.
        /// Description:
        ///   Tests applying remote config.
        /// Expected behavior, state:
        ///   No changes.
        /// </summary>
        [TestMethod]        
        public void ApplyRemoteLogLevels_Test()
        {
            #region Arrange            
            var serviceMock = new Mock<ISwarmService>();
            serviceMock.Setup(svc => svc.GetAsync<Dictionary<string, string>>(Urls.LogConfig)).Returns(
                Task.Factory.StartNew<Dictionary<string, string>>(
                    () => { return new Dictionary<string, string>(); }));

            var target = new RemoteLogLevelConfiguration(serviceMock.Object);
            #endregion

            // Act
            target.ApplyRemoteLogLevels().Wait();
        }

        /// <summary>
        /// Tested method:
        ///   ApplyRemoteLogLevels
        /// Tested class:
        ///   RemoteLogLevelConfiguration
        /// Initial data, state:
        ///   Service exception thrown.
        /// Description:
        ///   Tests applying remote config.
        /// Expected behavior, state:
        ///   No changes.
        /// </summary>
        [TestMethod]
        public void ApplyRemoteLogLevels_Service_Exception_Test()
        {
            #region Arrange
            var serviceMock = new Mock<ISwarmService>();
            serviceMock.Setup(svc => svc.GetAsync<Dictionary<string, string>>(Urls.LogConfig)).Returns(
                Task.Factory.StartNew<Dictionary<string, string>>(
                    () => { throw new ServiceException(Urls.LogConfig, System.Net.HttpStatusCode.NotFound); }));

            var target = new RemoteLogLevelConfiguration(serviceMock.Object);
            #endregion

            // Act
            target.ApplyRemoteLogLevels().Wait();
        }
    }
}
