//-----------------------------------------------------------------------
// <copyright file="Product.cs" company="Sonrisa">
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
    /// Product entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    [DataContract]
    public class Product
    {
        /// <summary>
        /// Gets or sets the item sid.
        /// </summary>
        [DataMember(Name = "ProductSid")]
        public string ItemSid { get; set; }

        /// <summary>
        /// Gets or sets the cost.
        /// </summary>
        [DataMember(Name = "Price")]
        public double Cost { get; set; }

        /// <summary>
        /// Gets or sets all the sku associated with this product
        /// </summary>
        public IEnumerable<StockKeepingUnit> SkuList { get; set; }

        /// <summary>
        /// Gets the first sku
        /// </summary>
        [DataMember]
        public string Sku 
        {
            get
            {
                return this.SkuList == null ? string.Empty : this.SkuList.Select(sku => sku.Value).FirstOrDefault();
            }
        }

        /// <summary>
        /// Gets or sets the SBS number.
        /// </summary>
        [DataMember(Name = "SbsNo")]
        public int SbsNumber { get; set; }

        /// <summary>
        /// Gets or sets the store number.
        /// </summary>
        [DataMember(Name = "StoreNo")]
        public string StoreNumber { get; set; }

        /// <summary>
        /// Gets or sets the upd.
        /// </summary>
        [DataMember]
        public string Upc { get; set; }

        /// <summary>
        /// Gets or sets the description1.
        /// </summary>
        [DataMember(Name = "Desc")]
        public string Description1 { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)(7L * this.ItemSid.GetHashCode()) + (int)((17L * this.SbsNumber) + (23L * (this.StoreNumber != null ? this.StoreNumber.GetHashCode() : 0)));
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

            var other = obj as Product;
            return other != null && this.ItemSid == other.ItemSid && this.SbsNumber == other.SbsNumber && this.StoreNumber == other.StoreNumber;
        }
    }

    /// <summary>
    /// Stock keeping unit is the unique idetifier of a sale unit
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class StockKeepingUnit
    {
        /// <summary>
        /// Gets or sets the Sheet id
        /// </summary>
        public int SheetId { get; set; }

        /// <summary>
        /// Gets or sets the item SID
        /// </summary>
        public string ItemSid { get; set; }

        /// <summary>
        /// Gets or sets the value which is the SKU
        /// </summary>
        public string Value { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)(7L * this.ItemSid.GetHashCode()) + (int)(23L * this.SheetId);
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

            var other = obj as StockKeepingUnit;
            return other != null && this.ItemSid == other.ItemSid && this.SheetId == other.SheetId;
        }
    }
}
