//-----------------------------------------------------------------------
// <copyright file="RemoteEntityMapConfigurationTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 26. 16:00</date>
//----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.ComponentModel.Composition.Hosting;
    using System.Linq;
    using System.Reflection;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;

    /// <summary>
    /// Test class for MEF tests.
    /// </summary>
    [TestClass]
    public class RemoteEntityMapConfigurationTest
    {        
        /// <summary>
        /// Tested method:
        ///   LoadConfiguration
        /// Tested class:
        ///   RemoteEntityMapConfiguration
        /// Initial data, state:
        ///   Service provides the map.
        /// Description:
        ///   Tests downloading the map.
        /// Expected behavior, state:
        ///   The map is downloaded.
        /// </summary>
        [TestMethod]        
        public void LoadConfiguration_Test()
        {
            #region Arrange
            var entityMap = new Dictionary<RDA2.FieldIDs, string>() { { RDA2.FieldIDs.fidAdjstCmp, "prop" } };
            var mapping = new Dictionary<string, Dictionary<RDA2.FieldIDs, string>>()
            {
                { "entity", entityMap },
            };

            var serviceMock = new Mock<ISwarmService>();
            serviceMock.Setup(s => s.GetAsync<Dictionary<string, Dictionary<RDA2.FieldIDs, string>>>(RetailProCommon.Urls.Mapping))
                .Returns(System.Threading.Tasks.Task.Factory.StartNew(() => { return mapping; }));

            var target = new RemoteEntityMapConfiguration(serviceMock.Object);            
            #endregion

            // Act            
            target.LoadConfiguration().Wait();

            #region Assert
            Assert.AreEqual(entityMap, target["entity"]);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   LoadConfiguration
        /// Tested class:
        ///   RemoteEntityMapConfiguration
        /// Initial data, state:
        ///   Service provides the map.
        /// Description:
        ///   Tests downloading the map without calling LoadConfiguration.
        /// Expected behavior, state:
        ///   The map is downloaded automatically.
        /// </summary>
        [TestMethod]
        public void Auto_LoadConfiguration_Test()
        {
            #region Arrange
            var entityMap = new Dictionary<RDA2.FieldIDs, string>() { { RDA2.FieldIDs.fidAdjstCmp, "prop" } };
            var mapping = new Dictionary<string, Dictionary<RDA2.FieldIDs, string>>()
            {
                { "entity", entityMap },
            };

            var serviceMock = new Mock<ISwarmService>();
            serviceMock.Setup(s => s.GetAsync<Dictionary<string, Dictionary<RDA2.FieldIDs, string>>>(RetailProCommon.Urls.Mapping))
                .Returns(System.Threading.Tasks.Task.Factory.StartNew(() => { return mapping; }));

            var target = new RemoteEntityMapConfiguration(serviceMock.Object);
            #endregion

            // Act            
            var mapResult = target["entity"];

            #region Assert
            serviceMock.Verify(svc => svc.GetAsync<Dictionary<string, Dictionary<RDA2.FieldIDs, string>>>(RetailProCommon.Urls.Mapping), Times.Once());
            #endregion
        }  
    }
}
