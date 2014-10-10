//-----------------------------------------------------------------------
// <copyright file="DateDictionary.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2014. 4. 8. 14:11</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Xml.Serialization;
    using RetailProCommon.Model;

    /// <summary>
    /// Dictionary storing the last cached dates for invoices, stores, etc.
    /// </summary>
    public class DateDictionary
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="DateDictionary"/> class.
        /// </summary>
        public DateDictionary()
        {
            this.Entries = new Dictionary<StoreKey, DateTime>();
        }

        /// <summary>
        /// Gets or sets the dictionary values
        /// </summary>
        public Dictionary<StoreKey, DateTime> Entries { get; set; }

        /// <summary>
        /// Gets or sets the default date returned when store is not found in cache
        /// </summary>
        public DateTime DefaultDate { get; set; }
        
        /// <summary>
        /// Gets or sets the date value for a given store
        /// </summary>
        /// <param name="sbsNo">Store to be cached</param>
        /// <param name="storeNo">Store to be cache</param>
        /// <returns>The store's date value or the DefaultDate if not found</returns>
        public DateTime GetDate(long sbsNo, string storeNo)
        {
            var store = new StoreKey() { SbsNumber = sbsNo, StoreNumber = storeNo };

            if (this.Entries.ContainsKey(store))
            {
                return this.Entries[store];
            }
            else
            {
                return this.DefaultDate;
            }
        }

        /// <summary>
        /// Sets date for given store
        /// </summary>
        /// <param name="sbsNo">Store to be cached</param>
        /// <param name="storeNo">Store to be cache</param>
        /// <param name="value">New value to be used</param>
        public void SetDate(long sbsNo, string storeNo, DateTime value)
        {
            var store = new StoreKey() { SbsNumber = sbsNo, StoreNumber = storeNo };
            this.Entries[store] = value;
        }

        /// <summary>
        /// Key of the dictionary
        /// </summary>
        public class StoreKey
        {
            /// <summary>
            /// Gets or sets the SBS number.
            /// </summary>
            public long SbsNumber { get; set; }

            /// <summary>
            /// Gets or sets the store number.
            /// </summary>
            public string StoreNumber { get; set; }

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
                var other = obj as StoreKey;
                return other != null && this.StoreNumber != null && this.StoreNumber == other.StoreNumber && this.SbsNumber == other.SbsNumber;
            }
        }
    }
}
