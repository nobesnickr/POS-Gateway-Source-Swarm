//-----------------------------------------------------------------------
// <copyright file="ExporterTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 15:45</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.Test
{
    using System;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.BusinessLogic;

    /// <summary>
    /// Unit tests for exporter.
    /// </summary>
    [TestClass]
    public class ExporterTest
    {
        /// <summary>
        /// Tests running the exporter.
        /// </summary>        
        [TestMethod]
        public void ExportRunAsyncTest()
        {
            // Arrange
            var entityExporter = new Mock<IEntityExporter>();
            entityExporter.Setup(e => e.ExportChangedItems()).ExecutesAsync();
            var exporter = new Exporter(new IEntityExporter[] { entityExporter.Object });
            
            // Act
            exporter.RunAsync().Wait();

            // Assert
            entityExporter.Verify(e => e.ExportChangedItems(), Times.Once());
        }

        /// <summary>
        /// Tests running the exporter.
        /// </summary>        
        [TestMethod]
        public void ExportRunSyncTest()
        {
            // Arrange
            var entityExporter = new Mock<IEntityExporter>();
            entityExporter.Setup(e => e.ExportChangedItems()).ExecutesAsync();
            var exporter = new Exporter(new IEntityExporter[] { entityExporter.Object });

            // Act
            exporter.Run();

            // Assert
            entityExporter.Verify(e => e.ExportChangedItems(), Times.Once());
        }
    }
}
