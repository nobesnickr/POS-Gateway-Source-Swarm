//-----------------------------------------------------------------------
// <copyright file="RemoteConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 25. 9:33</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Used to download configuration items from the server
    /// </summary>
    public class RemoteConfiguration
    {
        /// <summary>
        /// Gets or sets the version of this configuration item.
        /// </summary>
        public long Version { get; set; }

        /// <summary>
        /// Gets or sets the last modified invoice date.
        /// </summary>
        public DateTime? LastInvoice { get; set; }

        /// <summary>
        /// Gets or sets the last modified version date.
        /// </summary>
        public DateTime? LastVersion { get; set; }

        /// <summary>
        /// Gets or sets the last modified store date.
        /// </summary>
        public DateTime? LastStore { get; set; }
    }
}
