//-----------------------------------------------------------------------
// <copyright file="ModelMapperExtensionsTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 11. 06. 10:57</date>
//---------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Unit tests for model mapper extensions.
    /// </summary>
    [TestClass]
    public class ModelMapperExtensionsTest
    {
        /// <summary>
        /// Tested method:
        ///   TryGetInvoiceObject
        /// Tested class:
        ///   ModelMapperExtensions
        /// Initial data, state:
        ///   Mapper returns invoice
        /// Description:
        ///   Tests getting invoice.
        /// Expected behavior, state:
        ///   Invoice is returned
        /// </summary>
        [TestMethod]        
        public void TryGetInvoiceObject_Test()
        {
            #region Arrange
            var mapper = new Mock<IRetailProModelMapper>();
            var document = new Mock<IRetailProDocument>();
            var customerTable = new Mock<IRetailProTable>();
            var invoice = new Model.Invoice();
            mapper.Setup(m => m.GetInvoiceObject(document.Object, customerTable.Object, 42)).Returns(invoice);
            #endregion

            // Act
            Model.Invoice invoiceResult = null;
            var result = mapper.Object.TryGetInvoiceObject(document.Object, customerTable.Object, 42, out invoiceResult);

            #region Assert
            Assert.IsTrue(result);
            Assert.AreEqual(invoice, invoiceResult);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   TryGetInvoiceObject
        /// Tested class:
        ///   ModelMapperExtensions
        /// Initial data, state:
        ///   Mapper throws exception.
        /// Description:
        ///   Tests getting invoice.
        /// Expected behavior, state:
        ///   False is returned
        /// </summary>
        [TestMethod]
        public void TryGetInvoiceObject_Exception_Test()
        {
            #region Arrange
            var mapper = new Mock<IRetailProModelMapper>();
            var document = new Mock<IRetailProDocument>();
            var customerTable = new Mock<IRetailProTable>();
            var invoice = new Model.Invoice();
            mapper.Setup(m => m.GetInvoiceObject(document.Object, customerTable.Object, 42)).Throws(new RetailProExtractorException(string.Empty));
            #endregion

            // Act
            Model.Invoice invoiceResult = null;
            var result = mapper.Object.TryGetInvoiceObject(document.Object, customerTable.Object, 42, out invoiceResult);

            #region Assert
            Assert.IsFalse(result);
            Assert.IsNull(invoiceResult);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   TryGetStoreObject
        /// Tested class:
        ///   ModelMapperExtensions
        /// Initial data, state:
        ///   Mapper returns Store
        /// Description:
        ///   Tests getting Store.
        /// Expected behavior, state:
        ///   Store is returned
        /// </summary>
        [TestMethod]
        public void TryGetStoreObject_Test()
        {
            #region Arrange
            var mapper = new Mock<IRetailProModelMapper>();
            var document = new Mock<IRetailProDocument>();            
            var store = new Model.Store();
            mapper.Setup(m => m.GetStoreObject(document.Object, 42)).Returns(store);
            #endregion

            // Act
            Model.Store storeResult = null;
            var result = mapper.Object.TryGetStoreObject(document.Object, 42, out storeResult);

            #region Assert
            Assert.IsTrue(result);
            Assert.AreEqual(store, storeResult);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   TryGetStoreObject
        /// Tested class:
        ///   ModelMapperExtensions
        /// Initial data, state:
        ///   Mapper throws exception.
        /// Description:
        ///   Tests getting Store.
        /// Expected behavior, state:
        ///   False is returned
        /// </summary>
        [TestMethod]
        public void TryGetStoreObject_Exception_Test()
        {
            #region Arrange
            var mapper = new Mock<IRetailProModelMapper>();
            var document = new Mock<IRetailProDocument>();
            var store = new Model.Store();
            mapper.Setup(m => m.GetStoreObject(document.Object, 42)).Throws(new RetailProExtractorException(string.Empty));
            #endregion

            // Act
            Model.Store storeResult = null;
            var result = mapper.Object.TryGetStoreObject(document.Object, 42, out storeResult);

            #region Assert
            Assert.IsFalse(result);
            Assert.IsNull(storeResult);
            #endregion
        }
    }
}
