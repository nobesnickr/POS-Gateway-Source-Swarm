//-----------------------------------------------------------------------
// <copyright file="V9AppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 19. 9:23</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Configuration;
    using System.Linq;
    using System.Text;
    using RetailProCommon.Configuration;

    /// <summary>
    /// Application configuration class for Retail Pro V9.
    /// </summary>
    [Export(typeof(IV9AppConfiguration))]
    [Export(typeof(ICommonAppConfiguration))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    internal class V9AppConfiguration : CommonAppConfiguration, IV9AppConfiguration
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="V9AppConfiguration"/> class.
        /// </summary>
        public V9AppConfiguration()
        {
            this.ConnectionString = ConfigurationManager.ConnectionStrings["retailProReport"].ConnectionString;
        }

        /// <summary>
        /// Gets the connection string.
        /// </summary>
        public string ConnectionString { get; private set; }
    }
}
