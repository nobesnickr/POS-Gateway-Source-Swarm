//-----------------------------------------------------------------------
// <copyright file="InvoiceMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:25</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.DataAccess.Mapping
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Text;
    using FluentNHibernate.Mapping;
    using RetailProCommon.Model;

    /// <summary>
    /// Fluent NHibernate map for the Invoice entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class InvoiceMap : ClassMap<Invoice>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="InvoiceMap"/> class.
        /// </summary>
        public InvoiceMap()
        {
            this.Table("Invoice_V");
            this.Id(invc => invc.InvoiceSid, "INVC_SID").GeneratedBy.Assigned();
            this.Map(invc => invc.ModifiedDate, "MODIFIED_DATE");
            this.Map(invc => invc.CreatedDate, "CREATED_DATE");
            this.Map(invc => invc.StoreNumber, "STORE_NO");
            this.Map(invc => invc.SbsNumber, "SBS_NO");
            this.Map(invc => invc.InvoiceNumber, "INVC_NO");
            this.Map(invc => invc.ReceiptType, "INVC_TYPE");
            this.Map(invc => invc.ReceiptStatus, "STATUS");
            this.Map(invc => invc.SONumber, "SO_SID");
            this.HasMany<InvoiceItem>(i => i.InvoiceItems)
                .Inverse()
                .KeyColumn("INVC_SID");

            this.References<Customer>(i => i.Customer, "CUST_SID");

            this.HasMany<Tender>(i => i.Tenders)
                .Inverse()
                .KeyColumn("INVC_SID");
        }
    }
    
    /// <summary>
    /// Fluent NHibernate map for the Tender entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class TenderMap : ClassMap<Tender>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="TenderMap"/> class.
        /// </summary>
        public TenderMap()
        {
            this.Table("INVC_TENDER_V");
            this.CompositeId()
                .KeyProperty(tender => tender.InvoiceSid, "INVC_SID")
                .KeyProperty(tender => tender.TenderNumber, "TENDER_NO");
            this.Map(tender => tender.TenderType, "TENDER_TYPE");
        }
    }
}
