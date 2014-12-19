//-----------------------------------------------------------------------
// <copyright file="DeepInvoiceExportTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 27. 14:00</date>
//----------------------------------------------------------------------

namespace RetailProV8SwarmExporter.Test
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using RetailProCommon.Model;
  
    /// <summary>
    /// Test class for the RetailProCommon.Model package's classes
    /// </summary>
    [TestClass]
    public class ModelTest
    {

        /// <summary>
        /// Test that customers are equal only if CustomerSID, SbsNumber and StoreNumber is the same
        /// </summary>
        [TestMethod]
        public void TestCustomerEquals()
        {
            Customer baseCustomer = new Customer()
            {
                CustomerSid = "123456789",
                StoreNumber = "987654",
                SbsNumber = 7777777777L
            };

            Customer sameCustomer = new Customer()
            {
                CustomerSid = "123456789",
                StoreNumber = "987654",
                SbsNumber = 7777777777L
            };

            Customer sameCustomerInDifferentStore = new Customer()
            {
                CustomerSid = "123456789",
                StoreNumber = "1234567",
                SbsNumber = 6666666L
            };

            Assert.AreEqual(baseCustomer, sameCustomer);
            Assert.AreNotEqual(baseCustomer, sameCustomerInDifferentStore);
        }

        /// <summary>
        /// Test that products are equal only if ItemSID, SbsNumber and StoreNumber is the same
        /// </summary>
        [TestMethod]
        public void TestProductEquals()
        {
            Product baseProduct = new Product()
            {
                ItemSid = 123321123321L,
                SbsNumber = 22222222L,
                StoreNumber = "ABCD"
            };

            Product sameProduct = new Product()
            {
                ItemSid = 123321123321L,
                SbsNumber = 22222222L,
                StoreNumber = "ABCD"
            };

            Product sameProductFromDifferentStore = new Product()
            {
                ItemSid = 123321123321L,
                SbsNumber = 22222222L,
                StoreNumber = "QWERTY"
            };

            Assert.AreEqual(baseProduct, sameProduct);
            Assert.AreNotEqual(baseProduct, sameProductFromDifferentStore);
        }
    }
}
