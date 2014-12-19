//-----------------------------------------------------------------------
// <copyright file="InvoiceItemMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:22</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.DataAccess.Mapping
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using FluentNHibernate.Mapping;
    using RetailProCommon.Model;

    /// <summary>
    /// Fluent NHibernate map for the InvoiceItem entity.
    /// </summary>
    public class InvoiceItemMap : ClassMap<InvoiceItem>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="InvoiceItemMap"/> class.
        /// </summary>
        public InvoiceItemMap()
        {
            this.Table("INVC_ITEM_V");            
            this.CompositeId()
                .KeyProperty(item => item.InvoiceSid, "INVC_SID")
                .KeyProperty(item => item.ItemPosition, "ITEM_POS");
            this.Map(item => item.ProductSid, "ITEM_SID");
            this.Map(item => item.Quantity, "QTY");
            this.Map(item => item.Price, "PRICE");
            this.Map(item => item.TaxAmount, "TAX_AMT");            
            this.References(item => item.Product, "ITEM_SID");
            this.References(item => item.Invoice, "INVC_SID");
        }
    }
}
