//-----------------------------------------------------------------------
// <copyright file="Product.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 18:29</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Model
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Dynamic product entity
    /// </summary>
    public class Product : RetailProCommon.Model.DynamicEntity
    {
        /// <summary>
        /// Gets or sets the product sid.
        /// </summary>
        public string ProductSid { get; set; }

        /// <summary>
        /// Gets or sets the store No.
        /// </summary>
        public string StoreNo { get; set; }

        /// <summary>
        /// Gets or sets the SBS No.
        /// </summary>
        public long SbsNo { get; set; }

        /// <summary>
        /// Gets or sets the department.
        /// </summary>
        public RetailProCommon.Model.DynamicEntity Department { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            var hash = (this.ProductSid == null ? 0 : this.ProductSid.GetHashCode() * 23L) + (7L * this.SbsNo) + (17L * (this.StoreNo != null ? this.StoreNo.GetHashCode() : 0));
            return (int)hash;
        }

        /// <summary>
        /// Determines whether the specified <see cref="System.Object" />, is equal to this instance.
        /// </summary>
        /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
        /// <returns>
        ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
        /// </returns>
        public override bool Equals(object obj)
        {
            var other = obj as Product;
            if (other == null)
            {
                return false;
            }

            return this.StoreNo != null && this.StoreNo == other.StoreNo
                && this.ProductSid != null && this.ProductSid == other.ProductSid
                && this.SbsNo == other.SbsNo;
        }
    }
}
