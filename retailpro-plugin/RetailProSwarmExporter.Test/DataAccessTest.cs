//-----------------------------------------------------------------------
// <copyright file="DataAccessTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 26. 12:18</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Test
{
    using System;
    using FluentNHibernate.Cfg;
    using FluentNHibernate.Cfg.Db;
    using FluentNHibernate.Conventions.Helpers;
    using FluentNHibernate.Testing;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using NHibernate;
    using NHibernate.Tool.hbm2ddl;
    using RetailProCommon.Model;
    using RetailProSwarmExporter.DataAccess;

    /// <summary>
    /// Test class for the data access layer.
    /// </summary>
    [TestClass]
    public class DataAccessTest
    {
        /// <summary>
        /// The open database session.
        /// </summary>
        private static ISession session;

        /// <summary>
        /// Initializes the test class.
        /// </summary>
        /// <param name="context">The test context.</param>
        [ClassInitialize]
        public static void TestInitialize(TestContext context)
        {
            session = TestHelper.OpenSession();
        }

        /// <summary>
        /// Executes class cleanup.
        /// </summary>
        [ClassCleanup]
        public static void ClassCleanup()
        {
            session.Close();
        }

        /// <summary>
        /// Tests the store mapping configuration.
        /// </summary>
        [TestMethod]
        public void StorePersistanceTest()
        {
            new PersistenceSpecification<Store>(session)
                .CheckProperty(p => p.SbsNumber, 42)
                .CheckProperty(p => p.StoreName, "42")
                .CheckProperty(p => p.ModifiedDate, DateTime.Today)
                .CheckProperty(p => p.StoreNumber, "42L")
                .VerifyTheMappings();
        }

        /// <summary>
        /// Tests the version mapping configuration.
        /// </summary>
        [TestMethod]
        public void RetailProVersionPersistanceTest()
        {
            new PersistenceSpecification<RetailProVersion>(session)
                .CheckProperty(v => v.Comments, "comment")                
                .CheckProperty(v => v.ComponentType, 45)
                .CheckProperty(v => v.InstallDate, DateTime.Today)
                .CheckProperty(v => v.UpdatedDate, DateTime.Today)
                .CheckProperty(v => v.Version, "12.34.56.78")
                .VerifyTheMappings();
        }

        /// <summary>
        /// Tests the customer mapping configuration.
        /// </summary>
        [TestMethod]        
        public void CustomerPersistanceTest()
        {
            new PersistenceSpecification<Customer>(session)
                .CheckProperty(c => c.CustomerSid, "sid")
                .CheckProperty(c => c.Email, "email")
                .CheckProperty(c => c.FirstName, "Luke")
                .CheckProperty(c => c.LastName, "Skywalker")
                .CheckProperty(c => c.SbsNumber, 12)
                .CheckProperty(c => c.StoreNumber, "23L")
                .VerifyTheMappings();
        }

        /// <summary>
        /// Tests the product mapping configuration.
        /// </summary>
        /// <remarks>
        /// Test is ignored until vendor can be dehydrated.
        /// </remarks>
        [TestMethod]
        [Ignore]
        public void ProductPersistanceTest()
        {   
            new PersistenceSpecification<Product>(session)                
                .CheckProperty(p => p.Cost, 123.45)
                .CheckProperty(p => p.Description1, "desc")
                .CheckProperty(p => p.ItemSid, "1")
                .CheckProperty(p => p.Upc, "upc")
                .VerifyTheMappings();
        }

        /// <summary>
        /// Tests the invoice item mapping configuration.
        /// </summary>
        /// <remarks>
        /// Test is ignored until vendor can be dehydrated.
        /// </remarks>
        [TestMethod]
        [Ignore]        
        public void InvoiceItemPersistanceTest()
        {
            var product = new Product()
            {
                ItemSid = "1234",
            };

            new PersistenceSpecification<InvoiceItem>(session)
                .CheckProperty(i => i.InvoiceSid, "12")
                .CheckProperty(i => i.ItemPosition, 12)
                .CheckProperty(i => i.Price, 12.34)
                .CheckProperty(i => i.ProductSid, "23")
                .CheckProperty(i => i.Quantity, 42)
                .CheckProperty(i => i.TaxAmount, 10)    
                .CheckReference(i => i.Product, product)
                .CheckReference(i => i.ProductSid, "1234")
                .VerifyTheMappings();
        }
    }
}
