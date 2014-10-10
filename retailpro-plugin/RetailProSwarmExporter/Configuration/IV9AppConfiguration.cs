//-----------------------------------------------------------------------
// <copyright file="IV9AppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 19. 9:20</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using RetailProCommon.Configuration;

    /// <summary>
    /// Interface for V9 specific application configuration.
    /// </summary>
    internal interface IV9AppConfiguration : ICommonAppConfiguration
    {
        /// <summary>
        /// Gets the oracle database connection string.
        /// </summary>
        string ConnectionString { get; }
    }
}
