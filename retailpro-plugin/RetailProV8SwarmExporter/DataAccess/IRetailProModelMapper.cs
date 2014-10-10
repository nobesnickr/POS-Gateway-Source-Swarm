//-----------------------------------------------------------------------
// <copyright file="IRetailProModelMapper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 9. 24. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;    
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Class which maps RetailProDocuments to Customers, Products or Invoices
    /// </summary>
    public interface IRetailProModelMapper
    {
        /// <summary>
        /// Using a Retail Pro document from a table, get InvoiceLine model object
        /// </summary>   
        /// <param name="document">The RDA2 document to extract information from</param>
        /// <param name="customerTable">The customer accessor to access the customer's information</param>
        /// <param name="sbsNumber">The SBS number of to store to be used for this invoice</param>
        /// <returns>The invoice extracted</returns>
        Invoice GetInvoiceObject(IRetailProDocument document, IRetailProTable customerTable, long sbsNumber);

        /// <summary>
        /// Map invoice document to Store
        /// </summary>
        /// <param name="document">Document containing an Invoice</param>
        /// <param name="sbsNumber">Sbs number of the store group</param>
        /// <returns>The correctly mapped store or throws exception</returns>
        Store GetStoreObject(IRetailProDocument document, long sbsNumber);
    }
}
