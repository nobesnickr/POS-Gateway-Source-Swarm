//-----------------------------------------------------------------------
// <copyright file="Invoice.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 11:11</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Model
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Newtonsoft.Json;
    using RetailProCommon;

    /// <summary>
    /// Invoice model class.
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1710:IdentifiersShouldHaveCorrectSuffix", Justification = "Mainly not used as a dictionary.")]
    public class Invoice : RetailProCommon.Model.DynamicEntity
    {
        /// <summary>
        /// Gets or sets the invoice sid.
        /// </summary>
        public string InvoiceSid { get; set; }

        /// <summary> 
        /// Gets or sets the SBS no.
        /// </summary>
        public long SbsNo { get; set; }

        /// <summary>
        /// Gets or sets the store no.
        /// </summary>
        public string StoreNo { get; set; }

        /// <summary>
        /// Gets or sets the modified date.
        /// </summary>
        public string ModifiedDate { get; set; }

        /// <summary>
        /// Gets the customer sid.
        /// </summary>
        public string CustSid
        {
            get
            {
                return this.Customer == null ? (string)null : this.Customer.CustSid;
            }
        }

        /// <summary>
        /// Gets or sets the invoice items.
        /// </summary>
        [JsonIgnore]
        public IEnumerable<InvoiceItem> InvoiceItems { get; set; }

        /// <summary>
        /// Gets or sets the customer.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly", Justification = "Set by mapper.")]
        [JsonIgnore]
        public Customer Customer { get; set; }              
    }
}
