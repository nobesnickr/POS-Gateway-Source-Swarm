//-----------------------------------------------------------------------
// <copyright file="RDA2DynamicMapperTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 29. 16:00</date>
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
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.DataAccess.RDA;
    using RetailProV8SwarmExporter.Exporter;

    /// <summary>
    /// Test class for MEF tests.
    /// </summary>
    [TestClass]
    public class RDA2DynamicMapperTest
    {
        /// <summary>
        /// Tested method:
        ///   GetStoreObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Map in mapping.
        /// Description:
        ///   Tests getting a store object.
        /// Expected behavior, state:
        ///   Store object is returned.
        /// </summary>
        [TestMethod]        
        public void GetStoreObject_Test()
        {
            #region Arrange
            var map = new Dictionary<RDA2.FieldIDs, string>()
            {
                { RDA2.FieldIDs.fidAdjstCmp, "Prop" }
            };

            var mapMock = new Mock<IEntityMapConfiguration>();
            mapMock.Setup(m => m["Store"]).Returns(map);

            var modified = new DateTime();
            var storeMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidStore, "storeNo")
                .DocumentProp(RDA2.FieldIDs.fidDocLastEdit, modified)
                .DocumentProp(RDA2.FieldIDs.fidAdjstCmp, "PropValue");

            var target = new RDA2DynamicMapper(mapMock.Object);
            #endregion

            // Act
            var result = target.GetStoreObject(storeMock.Object, 42);

            #region Assert
            Assert.AreEqual(42, result.SbsNo);
            Assert.AreEqual("storeNo", result.StoreNo);
            Assert.AreEqual(modified, result.ModifiedDate);
            Assert.AreEqual("PropValue", result["Prop"]);
            #endregion
        }

        /// <summary>
        /// Tested method:
        /// GetInvoiceObject
        /// Tested class:
        /// RDA2DynamicMapper
        /// Initial data, state:
        /// Map in mapping.
        /// Description:
        /// Tests getting an invoice object.
        /// Expected behavior, state:
        /// Invoice object is returned.
        /// </summary>
        [TestMethod]        
        public void GetInvoiceObject_Test()
        {
            #region Arrange
            var invoiceMap = new Dictionary<RDA2.FieldIDs, string>()
            {
                { RDA2.FieldIDs.fidAdjstCmp, "Prop" }
            };

            var customerMap = new Dictionary<RDA2.FieldIDs, string>()
            {
                { RDA2.FieldIDs.fidBillToAddr1, "Addr1" }
            };

            var mapMock = new Mock<IEntityMapConfiguration>();
            mapMock.Setup(m => m["Invoice"]).Returns(invoiceMap);
            mapMock.Setup(m => m["Customer"]).Returns(customerMap);

            var modified = new DateTime();
            var invoiceMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidStore, "storeNo")
                .DocumentProp(RDA2.FieldIDs.fidAdjstCmp, "PropValue")
                .DocumentProp(RDA2.FieldIDs.fidDocLastEdit, modified.ToString())
                .DocumentProp(RDA2.FieldIDs.fidDocSID, "InvoiceSid")
                .DocumentProp(RDA2.FieldIDs.fidBillToCustID, "CustSid");
            invoiceMock.Setup(s => s.GetNestedTable(RDA2.TDBNestedTables.ntblInvoiceItems).Items).Returns(Enumerable.Empty<IRetailProDocument>());

            var customerMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToAddr1, "Address1");

            var customerTable = new Mock<IRetailProTable>();
            customerTable.Setup(c => c.Find("CustSid")).Returns(customerMock.Object);

            var target = new RDA2DynamicMapper(mapMock.Object);
            #endregion

            // Act
            var result = target.GetInvoiceObject(invoiceMock.Object, customerTable.Object, 42);

            #region Assert
            // Assert invoice data
            Assert.AreEqual(42, result.SbsNo);
            Assert.AreEqual("storeNo", result.StoreNo);
            Assert.AreEqual(modified.ToString(), result.ModifiedDate);
            Assert.AreEqual("InvoiceSid", result.InvoiceSid);
            Assert.AreEqual("PropValue", result["Prop"]);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   GetCustomerObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Map in mapping.
        /// Description:
        ///   Tests getting an invoice object.
        /// Expected behavior, state:
        ///   Invoice object with specified customer data is returned.
        /// </summary>
        [TestMethod]
        public void GetCustomerObject_Test()
        {
            #region Arrange
            var invoiceMap = new Dictionary<RDA2.FieldIDs, string>();
            var customerMap = new Dictionary<RDA2.FieldIDs, string>()
            {
                { RDA2.FieldIDs.fidBillToAddr1, "Addr1" }
            };

            var mapMock = new Mock<IEntityMapConfiguration>();
            mapMock.Setup(m => m["Invoice"]).Returns(invoiceMap);
            mapMock.Setup(m => m["Customer"]).Returns(customerMap);

            var invoiceMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToCustID, "CustSid")
                .DocumentProp(RDA2.FieldIDs.fidStore, "storeNo");
            invoiceMock.Setup(s => s.GetNestedTable(RDA2.TDBNestedTables.ntblInvoiceItems).Items).Returns(Enumerable.Empty<IRetailProDocument>());            

            var customerMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToAddr1, "Address1");

            var customerTable = new Mock<IRetailProTable>();
            customerTable.Setup(c => c.Find("CustSid")).Returns(customerMock.Object);

            var target = new RDA2DynamicMapper(mapMock.Object);
            #endregion

            // Act
            var result = target.GetInvoiceObject(invoiceMock.Object, customerTable.Object, 42);

            #region Assert
            // Assert customer data
            Assert.AreEqual("CustSid", result.Customer.CustSid);
            Assert.AreEqual("storeNo", result.Customer.StoreNo);
            Assert.AreEqual("Address1", result.Customer["Addr1"]);
            Assert.AreEqual(42, result.Customer.SbsNo);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   GetCustomerObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Map in mapping.
        /// Description:
        ///   Tests getting an invoice object.
        /// Expected behavior, state:
        ///   Invoice object with specified customer data is returned.
        /// </summary>
        [TestMethod]
        public void GetCustomerObject_Null_Customer_Test()
        {
            #region Arrange
            var invoiceMap = new Dictionary<RDA2.FieldIDs, string>();            

            var mapMock = new Mock<IEntityMapConfiguration>();
            mapMock.Setup(m => m["Invoice"]).Returns(invoiceMap);            

            var invoiceMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToCustID, "CustSid")
                .DocumentProp(RDA2.FieldIDs.fidStore, "storeNo");
            invoiceMock.Setup(s => s.GetNestedTable(RDA2.TDBNestedTables.ntblInvoiceItems).Items).Returns(Enumerable.Empty<IRetailProDocument>());

            var customerMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToAddr1, "Address1");

            var customerTable = new Mock<IRetailProTable>();            

            var target = new RDA2DynamicMapper(mapMock.Object);
            #endregion

            // Act
            var result = target.GetInvoiceObject(invoiceMock.Object, customerTable.Object, 42);

            #region Assert
            // Assert customer data
            Assert.AreEqual("CustSid", result.Customer.CustSid);
            Assert.AreEqual("storeNo", result.Customer.StoreNo);            
            Assert.AreEqual(42, result.Customer.SbsNo);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   GetInvoiceItemObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Map in mapping.
        /// Description:
        ///   Tests getting an invoice item object.
        /// Expected behavior, state:
        ///   Invoice object with invoice item is returned.
        /// </summary>
        [TestMethod]
        public void GetInvoiceItemObject_Test()
        {
            #region Arrange
            var emptyMap = new Dictionary<RDA2.FieldIDs, string>();            

            var mapMock = new Mock<IEntityMapConfiguration>();
            mapMock.Setup(m => m["Invoice"]).Returns(emptyMap);
            mapMock.Setup(m => m["InvoiceItem"]).Returns(emptyMap);
            mapMock.Setup(m => m["Product"]).Returns(emptyMap);
            mapMock.Setup(m => m["Department"]).Returns(emptyMap);

            var invoiceItemMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidItemSID, "ProductSid");
            invoiceItemMock.Setup(it => it.Index).Returns(3);

            var invoiceMock = new Mock<IRetailProDocument>()
                .DocumentProp(RDA2.FieldIDs.fidBillToCustID, "CustSid")
                .DocumentProp(RDA2.FieldIDs.fidStore, "storeNo")
                .DocumentProp(RDA2.FieldIDs.fidDocSID, "2");
            
            invoiceMock.Setup(s => s.GetNestedTable(RDA2.TDBNestedTables.ntblInvoiceItems).Items).Returns(new IRetailProDocument[] { invoiceItemMock.Object });            

            var customerTable = new Mock<IRetailProTable>();
            var target = new RDA2DynamicMapper(mapMock.Object);
            #endregion

            // Act
            var result = target.GetInvoiceObject(invoiceMock.Object, customerTable.Object, 42);

            #region Assert
            var item = result.InvoiceItems.ElementAt(0);           
            Assert.AreEqual("storeNo", item.StoreNo);
            Assert.AreEqual(2051, item.ItemPos);
            Assert.AreEqual(42, item.SbsNo);
            Assert.AreEqual(42, item.Product.SbsNo);
            Assert.AreEqual("storeNo", item.Product.StoreNo);
            Assert.AreEqual("ProductSid", item.Product.ProductSid);
            Assert.IsTrue(item.Product.Department.GetType() == typeof(RetailProCommon.Model.DynamicEntity));
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   GetInvoiceObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Document is null.
        /// Description:
        ///   Tests throwing exceptions when bad input arguments are provided.
        /// Cause of failure:
        ///   Document is null
        /// </summary>
        [ExpectedException(typeof(ArgumentNullException))]
        [TestMethod]        
        public void GetInvoiceObject_Document_ArgumentNullException_Test()
        {
            #region Arrange
            var target = new RDA2DynamicMapper(new Mock<IEntityMapConfiguration>().Object);
            #endregion

            // Act
            target.GetInvoiceObject(null, new Mock<IRetailProTable>().Object, 12);
        }

        /// <summary>
        /// Tested method:
        ///   GetInvoiceObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   CustomerTable is null.
        /// Description:
        ///   Tests throwing exceptions when bad input arguments are provided.
        /// Cause of failure:
        ///   CustomerTable is null
        /// </summary>
        [ExpectedException(typeof(ArgumentNullException))]
        [TestMethod]
        public void GetInvoiceObject_CustomerTable_ArgumentNullException_Test()
        {
            #region Arrange
            var target = new RDA2DynamicMapper(new Mock<IEntityMapConfiguration>().Object);
            #endregion

            // Act
            target.GetInvoiceObject(new Mock<IRetailProDocument>().Object, null, 12);
        }

        /// <summary>
        /// Tested method:
        ///   GetStoreObject
        /// Tested class:
        ///   RDA2DynamicMapper
        /// Initial data, state:
        ///   Document is null.
        /// Description:
        ///   Tests throwing exceptions when bad input arguments are provided.
        /// Cause of failure:
        ///   Document is null
        /// </summary>
        [ExpectedException(typeof(ArgumentNullException))]
        [TestMethod]
        public void GetStoreObject_Document_ArgumentNullException_Test()
        {
            #region Arrange
            var target = new RDA2DynamicMapper(new Mock<IEntityMapConfiguration>().Object);
            #endregion

            // Act
            target.GetStoreObject(null, 12);
        }
    }
}
