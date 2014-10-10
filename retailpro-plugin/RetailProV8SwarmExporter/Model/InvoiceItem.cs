//-----------------------------------------------------------------------
// <copyright file="InvoiceItem.cs" company="Sonrisa">
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
    /// Invoice item expandable model.
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1710:IdentifiersShouldHaveCorrectSuffix", Justification = "Mainly not used as a dictionary.")]
    public class InvoiceItem : RetailProCommon.Model.DynamicEntity
    {
        /// <summary>
        /// Gets or sets the item position.
        /// </summary>
        public long ItemPos { get; set; }

        /// <summary>
        /// Gets or sets the SBS no.
        /// </summary>
        public long SbsNo { get; set; }

        /// <summary>
        /// Gets or sets the store no.
        /// </summary>
        public string StoreNo { get; set; }

        /// <summary>
        /// Gets or sets the invoice SID of the parent invoice
        /// </summary>
        public string InvoiceSid { get; set; }

        /// <summary>
        /// Gets or sets the product.
        /// </summary>
        [JsonIgnore]
        public Product Product { get; set; }
    }
}
