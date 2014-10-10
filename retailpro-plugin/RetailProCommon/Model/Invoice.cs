//-----------------------------------------------------------------------
// <copyright file="Invoice.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:11</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Runtime.Serialization;
    using System.Text;

    /// <summary>
    /// Invoice entity.
    /// </summary>
    [DataContract]
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class Invoice
    {
        /// <summary>
        /// Gets or sets the invoice sid.
        /// </summary>
        [DataMember]
        public string InvoiceSid { get; set; }

        /// <summary>
        /// Gets or sets the created date.
        /// </summary>
        [DataMember]
        public DateTime CreatedDate { get; set; }
        
        /// <summary>
        /// Gets or sets the store number.
        /// </summary>
        [DataMember(Name = "StoreNo")]
        public string StoreNumber { get; set; }

        /// <summary>
        /// Gets or sets the SBS number.
        /// </summary>
        [DataMember(Name = "SbsNo")]
        public int SbsNumber { get; set; }

        /// <summary>
        /// Gets the customer sid.
        /// </summary>
        [DataMember(Name = "CustSid")]
        public string CustomerSid
        {
            get
            {
                return this.Customer == null ? (string)null : this.Customer.CustomerSid;
            }
        }

        /// <summary>
        /// Gets or sets the invoice number.
        /// </summary>
        [DataMember(Name = "InvoiceNo")]
        public string InvoiceNumber { get; set; }

        /// <summary>
        /// Gets the total.
        /// </summary>
        [DataMember(Name = "Total")]
        public double Total
        {
            get
            {
                return this.InvoiceItems != null ? this.InvoiceItems.Distinct().Select(i => i.Price * i.Quantity).Sum() : 0.0;
            }
        }
        
        /// <summary>
        /// Gets or sets the modified date.
        /// </summary>        
        public DateTime ModifiedDate { get; set; }

        /// <summary>
        /// Gets or sets the invoice items.
        /// </summary>
        public IEnumerable<InvoiceItem> InvoiceItems { get; set; }

        /// <summary>
        /// Gets or sets the customer.
        /// </summary>
        public Customer Customer { get; set; }

        /// <summary>
        /// Gets or sets the invoice type (Sales, Return, etc.)
        /// </summary>
        [DataMember]
        public int ReceiptType { get; set; }

        /// <summary>
        /// Gets or sets the receipt status (Cancelled, Modified, etc.)
        /// </summary>
        [DataMember]
        public int ReceiptStatus { get; set; }

        /// <summary>
        /// Gets or sets the SID of the SO that the receipt references.
        /// </summary>
        [DataMember]
        public string SONumber { get; set; }

        /// <summary>
        /// Gets or sets the tender of the invoice
        /// </summary>
        public IEnumerable<Tender> Tenders { get; set; }

        /// <summary>
        /// Gets the tender code, e.g. Cash, Credit, Split, Debig
        /// </summary>
        [DataMember]
        public int Tender 
        {
            get
            {
                return this.Tenders == null ? -1 : this.Tenders.Select(t => t.TenderNumber).FirstOrDefault();
            }
        }
    }

    /// <summary>
    /// Invoice's tender i.e. the method of payment
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class Tender
    {
        /// <summary>
        /// Gets or sets the unique system ID for the invoice.
        /// </summary>
        public string InvoiceSid { get; set; }

        /// <summary>
        /// Gets or sets the sequential number per Invc_Sid and Tender_Type 
        /// </summary>
        public int TenderNumber { get; set; }

        /// <summary>
        /// Gets or sets the tender type
        /// </summary>
        public int TenderType { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)(7L * this.InvoiceSid.GetHashCode()) + (int)(23L * this.TenderNumber);
        }

        /// <summary>
        /// Determines whether the specified <see cref="System.Object" /> is equal to this instance.
        /// </summary>
        /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
        /// <returns>
        ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
        /// </returns>
        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            var other = obj as Tender;
            return other != null && this.InvoiceSid == other.InvoiceSid && this.TenderNumber == other.TenderNumber;
        }
    }
}
