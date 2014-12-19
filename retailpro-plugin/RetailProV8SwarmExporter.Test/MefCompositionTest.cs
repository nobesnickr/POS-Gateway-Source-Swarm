//-----------------------------------------------------------------------
// <copyright file="MefCompositionTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 26. 16:00</date>
//----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.ComponentModel.Composition;
    using System.ComponentModel.Composition.Hosting;
    using System.Linq;
    using System.Reflection;
    using Microsoft.VisualStudio.TestTools.UnitTesting;

    /// <summary>
    /// Test class for MEF tests.
    /// </summary>
    [TestClass]
    public class MefCompositionTest
    {
        /// <summary>
        ///   Tests if all exported classes can be instantiated using mef.        
        /// </summary>
        [TestMethod]
        public void CompositionTest()
        {
            // Arrange
            var assemblies = new Assembly[]
            {
                typeof(SwarmV8ExporterProgram).Assembly
            };

            // Query export definitions from the given assemblies.
            var exportedTypes = assemblies
                .SelectMany(a => a.GetTypes())
                .SelectMany(t => t.GetCustomAttributes(typeof(ExportAttribute), false).OfType<ExportAttribute>()
                    .Select(ea => ea.ContractType))
                .ToList();

            using (var catalog = new AggregateCatalog(
                new AssemblyCatalog(typeof(SwarmV8ExporterProgram).Assembly),
                new AssemblyCatalog(typeof(RetailProCommon.Configuration.ICommonAppConfiguration).Assembly)))
            using (var container = new CompositionContainer(catalog))
            {
                foreach (var exportedType in exportedTypes)
                {
                    // Act
                    var contractName = AttributedModelServices.GetContractName(exportedType);
                    var exports = container.GetExportedValues<object>(contractName);

                    // Assert
                    Assert.IsTrue(exports.Count() > 0, "Could not get exported value for contract: " + contractName + " Type: " + exportedType.FullName);
                }
            }
        }
    }
}
