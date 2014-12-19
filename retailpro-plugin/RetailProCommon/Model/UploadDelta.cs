//-----------------------------------------------------------------------
// <copyright file="UploadDelta.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 27. 14:00</date>
//----------------------------------------------------------------------
namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Container containing a small segment of Retail Pro 
    /// store data which will be sent to the Swarm Server 
    /// joint together by formatting an object of this
    /// instance to a JSON object
    /// </summary>
    public class UploadDelta
    {
        /// <summary>
        /// Gets or sets the list of customers for the upload item
        /// </summary>
        public IEnumerable<Customer> Customers { get; set; }

        /// <summary>
        /// Gets or sets the list of invoices for the upload item
        /// </summary>
        public IEnumerable<Invoice> Invoices { get; set; }

        /// <summary>
        /// Gets or sets the list of invoice lines for the upload item
        /// </summary>
        public IEnumerable<InvoiceItem> Items { get; set; }

        /// <summary>
        /// Gets or sets the list of products for the upload item
        /// </summary>
        public IEnumerable<Product> Products { get; set; }
    }
}
