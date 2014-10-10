//-----------------------------------------------------------------------
// <copyright file="Customer.cs" company="Sonrisa">
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
    using RetailProCommon;

    /// <summary>
    /// Model for an expandable customer entity
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1710:IdentifiersShouldHaveCorrectSuffix", Justification = "Mainly not used as a dictionary.")]
    public class Customer : RetailProCommon.Model.DynamicEntity
    {
        /// <summary>
        /// Gets or sets the customer sid.
        /// </summary>
        public string CustSid { get; set; }

        /// <summary>
        /// Gets or sets the SBS no.
        /// </summary>
        public long SbsNo { get; set; }

        /// <summary>
        /// Gets or sets the store no.
        /// </summary>
        public string StoreNo { get; set; }

        /// <summary>
        /// Determines whether the specified <see cref="System.Object" />, is equal to this instance.
        /// </summary>
        /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
        /// <returns>
        ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
        /// </returns>
        public override bool Equals(object obj)
        {
            var other = obj as Customer;
            if (other == null)
            {
                return false;
            }

            return this.StoreNo != null && this.CustSid != null && this.SbsNo == other.SbsNo && this.StoreNo == other.StoreNo && this.CustSid == other.CustSid;
        }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            var hash = 
                ((this.CustSid == null ? 0 : this.CustSid.GetHashCode()) * 3L)
                + (this.SbsNo.GetHashCode() * 5L)
                + ((this.StoreNo == null ? 0 : this.StoreNo.GetHashCode()) * 7L);
            return (int)hash;
        }
    }
}
