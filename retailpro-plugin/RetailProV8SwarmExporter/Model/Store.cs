//-----------------------------------------------------------------------
// <copyright file="Store.cs" company="Sonrisa">
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
    /// Extensible store model.
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1710:IdentifiersShouldHaveCorrectSuffix", Justification = "Mainly not used as a dictionary.")]
    public class Store : RetailProCommon.Model.DynamicEntity
    {
        /// <summary>
        /// Gets or sets the store No.
        /// </summary>
        public string StoreNo { get; set; }

        /// <summary>
        /// Gets or sets the SBS No.
        /// </summary>
        public long SbsNo { get; set; }

        /// <summary>
        /// Gets or sets the modified date.
        /// </summary>
        public DateTime ModifiedDate { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)((7L * this.SbsNo) + (17L * (this.StoreNo != null ? this.StoreNo.GetHashCode() : 0)));
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
            var other = obj as Store;
            return other != null && this.StoreNo != null && this.StoreNo == other.StoreNo && this.SbsNo == other.SbsNo;
        }
    }
}
