//-----------------------------------------------------------------------
// <copyright file="CommonAppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 15:24</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Configuration;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.Configuration;

    /// <summary>
    /// Returns immutable application configuration from the app.config file.
    /// </summary>
    public class CommonAppConfiguration : ICommonAppConfiguration
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="CommonAppConfiguration"/> class.
        /// </summary>
        public CommonAppConfiguration()
        {
            int maxUploadItems = 0;
            if (!int.TryParse(ConfigurationManager.AppSettings["maxUploadItems"], out maxUploadItems))
            {
                maxUploadItems = 100;
            }

            this.MaxUploadItems = maxUploadItems;
            this.ServiceBaseUrl = ConfigurationManager.AppSettings["serviceBaseUrl"];
            this.SwarmId = ConfigurationManager.AppSettings["swarmId"];
            this.ConfigDirectoryName = ConfigurationManager.AppSettings["configDirectory"];
            this.ConfigFileName = ConfigurationManager.AppSettings["configFile"];
            this.PosSoftwareId = ConfigurationManager.AppSettings["posSoftwareId"];
            this.SecurityProtocol = ConfigurationManager.AppSettings["securityProtocol"];
        }

        /// <summary>
        /// Gets the max upload items.
        /// </summary>
        public int MaxUploadItems { get; private set; }

        /// <summary>
        /// Gets the service base URL.
        /// </summary>
        public string ServiceBaseUrl { get; private set; }

        /// <summary>
        /// Gets the swarm id.
        /// </summary>
        public string SwarmId { get; private set; }

        /// <summary>
        /// Gets the name of the configuration directory.
        /// </summary>
        public string ConfigDirectoryName { get; private set; }

        /// <summary>
        /// Gets the name of the configuration file.
        /// </summary>
        public string ConfigFileName { get; private set; }

        /// <summary>
        /// Gets the Pos software Id used by the gateway to distinguish the different client types
        /// </summary>
        public string PosSoftwareId { get; private set; }

        /// <summary>
        /// Gets the SSL security protocol used by SwarmService to set the ServicePointManager
        /// </summary>
        public string SecurityProtocol { get; private set; }
    }
}
