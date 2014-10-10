//-----------------------------------------------------------------------
// <copyright file="InvoiceItem.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:11</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;
    using System.Runtime.Serialization;
    using System.Text;

    /// <summary>
    /// InvoiceItem entity.
    /// </summary>
    [DataContract]
    public class InvoiceItem
    {
        /// <summary>
        /// Gets the invoice item's unique identifier. 
        /// </summary>
        [DataMember(Name = "ItemPos")]
        public string TruncatedItemIdentifier
        {
            get { return SIDUtil.ShiftAndTruncateSID(this.InvoiceSid, this.ItemPosition); }
            private set { }
        }

        /// <summary>
        /// Gets or sets the item position.
        /// </summary>
        public int ItemPosition { get; set; }

        /// <summary>
        /// Gets or sets the invoice sid.
        /// </summary>
        [DataMember]
        public string InvoiceSid { get; set; }

        /// <summary>
        /// Gets or sets the parent Invoice entity
        /// </summary>
        public Invoice Invoice { get; set; }

        /// <summary>
        /// Gets or sets the item sid.
        /// </summary>
        [DataMember]
        public string ProductSid { get; set; }

        /// <summary>
        /// Gets or sets the quantity.
        /// </summary>
        [DataMember]
        public int Quantity { get; set; }

        /// <summary>
        /// Gets or sets the price.
        /// </summary>
        [DataMember]
        public double Price { get; set; }

        /// <summary>
        /// Gets or sets the price.
        /// </summary>
        [DataMember(Name = "TaxAmt")]
        public double TaxAmount { get; set; }

        /// <summary>
        /// Gets or sets the product.
        /// </summary>
        public Product Product { get; set; }

        /// <summary>
        /// Gets the store number.
        /// </summary>
        [DataMember(Name = "StoreNo")]
        public string StoreNumber
        {
            get { return this.Invoice != null ? this.Invoice.StoreNumber : string.Empty; }
        }

        /// <summary>
        /// Gets the SBS number.
        /// </summary>
        [DataMember(Name = "SbsNo")]
        public int SbsNumber
        {
            get { return this.Invoice != null ? this.Invoice.SbsNumber : 0; }
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
            var other = obj as InvoiceItem;
            if (other == null)
            {
                return false;
            }

            return this.InvoiceSid == other.InvoiceSid && this.ItemPosition == other.ItemPosition;
        }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            var code = this.InvoiceSid + ((long)this.ItemPosition * 13L);
            return code.GetHashCode();
        }
    }
}
