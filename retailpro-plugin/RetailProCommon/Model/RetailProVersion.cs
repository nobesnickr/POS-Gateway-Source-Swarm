//-----------------------------------------------------------------------
// <copyright file="RetailProVersion.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 15:21</date>
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
    /// Retail Pro version entity.
    /// </summary>
    [DataContract]
    public class RetailProVersion
    {
        /// <summary>
        /// Gets or sets the component id.
        /// <code>NUMBER(10)</code>
        /// </summary>
        [DataMember]
        public int ComponentId { get; set; }

        /// <summary>
        /// Gets or sets the type of the componenet.
        /// <code>NUMBER(5)</code>
        /// </summary>
        [DataMember]
        public int ComponentType { get; set; }

        /// <summary>
        /// Gets or sets the version.
        /// </summary>
        [DataMember]
        public string Version { get; set; }

        /// <summary>
        /// Gets or sets the updated date.
        /// </summary>
        [DataMember]
        public DateTime UpdatedDate { get; set; }

        /// <summary>
        /// Gets or sets the install date.
        /// </summary>
        [DataMember]
        public DateTime InstallDate { get; set; }

        /// <summary>
        /// Gets or sets the comments.
        /// </summary>
        [DataMember]
        public string Comments { get; set; }
    }
}
