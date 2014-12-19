//-----------------------------------------------------------------------
// <copyright file="ICommonAppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 15:22</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Contains memebers for immutable application configuration.
    /// </summary>
    public interface ICommonAppConfiguration
    {
        /// <summary>
        /// Gets the max upload items.
        /// </summary>
        int MaxUploadItems { get; }

        /// <summary>
        /// Gets the service base URL.
        /// </summary>
        string ServiceBaseUrl { get; }

        /// <summary>
        /// Gets the swarm id.
        /// </summary>
        string SwarmId { get; }

        /// <summary>
        /// Gets the name of the configuration directory.
        /// </summary>
        string ConfigDirectoryName { get; }

        /// <summary>
        /// Gets the name of the configuration file.
        /// </summary>
        string ConfigFileName { get; }

        /// <summary>
        /// Gets the Pos software Id used by the gateway to distinguish the different client types
        /// </summary>
        string PosSoftwareId { get; }

        /// <summary>
        /// Gets the SSL security protocol used by SwarmService to set the ServicePointManager
        /// </summary>
        string SecurityProtocol { get; }
    }
}
