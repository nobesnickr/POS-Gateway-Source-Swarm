//-----------------------------------------------------------------------
// <copyright file="SerializableDateDictionary.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2014. 04. 08. 16:04</date>
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
    /// Extension of the DateDictionary class with serializable list
    /// </summary>
    [Serializable]
    public class SerializableDateDictionary
    {
        /// <summary>
        /// Gets or sets the entries to be serialized
        /// </summary>
        [XmlArray("EntryList")]
        [XmlArrayItem("ModifiedDate")]
        public List<StoreKeyValuePair> EntryList { get; set; }

        /// <summary>
        /// Gets or sets the DefaultDate
        /// </summary>
        public DateTime DefaultDate { get; set; }

        /// <summary>
        /// Gets or sets the dictionary containing data
        /// </summary>
        [XmlIgnore]
        public DateDictionary Dictionary
        {
            get
            {
                var retVal = new DateDictionary() { DefaultDate = this.DefaultDate };
                retVal.Entries = this.EntryList.ToDictionary(
                    item => new DateDictionary.StoreKey()
                    {
                        StoreNumber = item.StoreNumber,
                        SbsNumber = item.SbsNumber
                    },
                    item => item.Value);

                return retVal;
            }

            set
            {
                this.DefaultDate = value.DefaultDate;
                this.EntryList = value.Entries.Select(e => new StoreKeyValuePair()
                {
                    SbsNumber = e.Key.SbsNumber,
                    StoreNumber = e.Key.StoreNumber,
                    Value = e.Value
                }).ToList();
            }
        }

        /// <summary>
        /// Serialize KeyValuePair holding stores and their dates
        /// </summary>
        [Serializable]
        [XmlType(TypeName = "Store")]
        public struct StoreKeyValuePair
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
            /// Gets or sets the store's date value
            /// </summary>
            public DateTime Value { get; set; }
        }
    }
}
