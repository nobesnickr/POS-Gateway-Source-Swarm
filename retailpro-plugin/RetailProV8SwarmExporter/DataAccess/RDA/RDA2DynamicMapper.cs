//-----------------------------------------------------------------------
// <copyright file="RDA2DynamicMapper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Dynamic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;    
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Static class which maps RetailProDocuments to Customers, Products or Invoices
    /// </summary>
    [Export(typeof(IRetailProModelMapper))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class RDA2DynamicMapper : IRetailProModelMapper
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The map configuration
        /// </summary>
        private readonly Configuration.IEntityMapConfiguration mapConfig;

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2DynamicMapper"/> class.
        /// </summary>
        /// <param name="mapConfig">The map configuration.</param>
        [ImportingConstructor]
        public RDA2DynamicMapper(Configuration.IEntityMapConfiguration mapConfig)
        {
            this.mapConfig = mapConfig;
        }
        
        /// <summary>
        /// Using a Retail Pro document from a table, get InvoiceLine model object
        /// </summary>   
        /// <param name="document">The RDA2 document to extract information from</param>
        /// <param name="customerTable">The customer accessor to access the customer's information</param>
        /// <param name="sbsNumber">The SBS number of to store to be used for this invoice</param>
        /// <returns>The invoice extracted</returns>
        public Invoice GetInvoiceObject(IRetailProDocument document, IRetailProTable customerTable, long sbsNumber)
        {
            if (document == null)
            {
                throw new ArgumentNullException("document");
            }

            if (customerTable == null)
            {
                throw new ArgumentNullException("customerTable");
            }

            Logger.Trace("Fetching invoice for:" + document);

            var invoice = this.GetObjectFromMap<Invoice>(document, "Invoice");
            invoice.SbsNo = sbsNumber;
            invoice.ModifiedDate = document.GetString(RDA2.FieldIDs.fidDocLastEdit);
            invoice.InvoiceSid = document.GetString(RDA2.FieldIDs.fidDocSID);
            invoice.StoreNo = document.GetString(RDA2.FieldIDs.fidStore);

            // Find customer, and force certain attributes to match invoice's
            var customerSID = document.GetString(RDA2.FieldIDs.fidBillToCustID);
            var customerEntry = customerTable.Find(customerSID);

            if (customerEntry != null)
            {
                invoice.Customer = this.GetCustomerObject(customerSID, customerEntry);
            }
            else 
            {
                invoice.Customer = new Model.Customer() 
                {
                    CustSid = customerSID,
                };
            }

            invoice.Customer.SbsNo = invoice.SbsNo;
            invoice.Customer.StoreNo = invoice.StoreNo;
            invoice.InvoiceItems = document.GetNestedTable(RDA2.TDBNestedTables.ntblInvoiceItems)
                .Items
                .Select(i => this.GetInvoiceItemObject(i, invoice))
                .ToList();
            return invoice;
        }

        /// <summary>
        /// Map invoice document to Store
        /// </summary>
        /// <param name="document">Document containing an Invoice</param>
        /// <param name="sbsNumber">Sbs number of the store group</param>
        /// <returns>The correctly mapped store or throws exception</returns>
        public Store GetStoreObject(IRetailProDocument document, long sbsNumber)
        {
            if (document == null)
            {
                throw new ArgumentNullException("document");
            }

            var store = this.GetObjectFromMap<Store>(document, "Store");
            store.StoreNo = document.GetString(RDA2.FieldIDs.fidStore);
            store.ModifiedDate = document.GetDateTime(RDA2.FieldIDs.fidDocLastEdit);
            store.SbsNo = sbsNumber;
            return store;
        }

        /// <summary>
        /// Using a Retail Pro document from a table, get Customer model object
        /// </summary>
        /// <param name="customerSID">The customer sid.</param>
        /// <param name="document">The RDA2 document to extract information from</param>
        /// <returns>
        /// The customer extracted
        /// </returns>
        /// <exception cref="System.ArgumentNullException">If document is null.</exception>
        private Customer GetCustomerObject(string customerSID, IRetailProDocument document)
        {
            Logger.Trace("Fetching customer for:" + document);

            var customer = this.GetObjectFromMap<Customer>(document, "Customer");
            customer.CustSid = customerSID;

            // Sbs and store numbers are set after this method is called.
            return customer;
        }

        /// <summary>
        /// Using a Retail Pro document from a table, get InvoiceLine model object
        /// </summary>   
        /// <param name="document">The RDA2 document to extract information from</param>
        /// <param name="invoice">The invoice that will help fill out data in the invoiceLine</param>
        /// <returns>The InvoiceItem extracted from the document</returns>
        private InvoiceItem GetInvoiceItemObject(IRetailProDocument document, Invoice invoice)
        {
            Logger.Trace("Fetching invoice line for:" + document);            

            var invoiceLineItem = this.GetObjectFromMap<InvoiceItem>(document, "InvoiceItem");
            invoiceLineItem.InvoiceSid = invoice.InvoiceSid;

            invoiceLineItem.ItemPos = (ParseHelper.ParseLong(invoice.InvoiceSid) << 10) + document.Index;
            invoiceLineItem.StoreNo = invoice.StoreNo;
            invoiceLineItem.SbsNo = invoice.SbsNo;            

            var product = this.GetObjectFromMap<Product>(document, "Product");            
            product.SbsNo = invoice.SbsNo;
            product.StoreNo = invoice.StoreNo;
            product.ProductSid = document.GetString(RDA2.FieldIDs.fidItemSID);
            product.Department = this.GetObjectFromMap(document, "Department");

            invoiceLineItem.Product = product;

            return invoiceLineItem;
        }

        /// <summary>
        /// Gets a dynamic, ExpandoObject object from the given doument using the map.
        /// </summary>
        /// <param name="document">The document.</param>
        /// <param name="entityName">Name of the entity.</param>
        /// <returns>The created ExpandoObject.</returns>
        private RetailProCommon.Model.DynamicEntity GetObjectFromMap(IRetailProDocument document, string entityName)
        {
            return this.GetObjectFromMap<RetailProCommon.Model.DynamicEntity>(document, entityName);
        }

        /// <summary>
        /// Gets a dynamic, ExpandoObject object from the given doument using the map.
        /// </summary>
        /// <typeparam name="T">The type of the dynamic object to create</typeparam>
        /// <param name="document">The document.</param>
        /// <param name="entityName">Name of the entity.</param>
        /// <returns>
        /// The created ExpandoObject.
        /// </returns>
        private T GetObjectFromMap<T>(IRetailProDocument document, string entityName) where T : RetailProCommon.Model.DynamicEntity
        {
            var obj = (RetailProCommon.Model.DynamicEntity)Activator.CreateInstance<T>();
            foreach (var map in this.mapConfig[entityName])
            {
                obj[map.Value] = document.GetString(map.Key);
            }

            return (T)obj;
        }
    }
}
