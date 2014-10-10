//-----------------------------------------------------------------------
// <copyright file="Store.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 26. 11:52</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Runtime.Serialization;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Store entity.
    /// </summary>
    [DataContract]
    public class Store
    {
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
        /// Gets or sets the name of the store.
        /// </summary>
        [DataMember]
        public string StoreName { get; set; }

        /// <summary>
        /// Gets or sets the store code
        /// </summary>
        [DataMember]
        public string StoreCode { get; set; }

        /// <summary>
        /// Gets or sets the modified date.
        /// </summary>
        public DateTime ModifiedDate { get; set; }

        /// <summary>
        /// Gets or sets the location of the Store
        /// </summary>
        [DataMember(Name = "Notes")]
        public string Location { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether a store is active
        /// </summary>
        [DataMember]
        public bool Active { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)((7L * this.SbsNumber) + (17L * (this.StoreNumber != null ? this.StoreNumber.GetHashCode() : 0)));
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
            return other != null && this.StoreNumber == other.StoreNumber && this.SbsNumber == other.SbsNumber;
        }
    }
}
