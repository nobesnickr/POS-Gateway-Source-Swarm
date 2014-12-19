//-----------------------------------------------------------------------
// <copyright file="RemoteEntityMapConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 26. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Configuration;
    using System.Linq;
    using System.Text;
    using RetailProCommon.Service;

    /// <summary>
    /// Entity map configuration, which is loaded from the remote swarm server.
    /// </summary>
    [Export(typeof(IEntityMapConfiguration))]
    public class RemoteEntityMapConfiguration : IEntityMapConfiguration
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The entity mapping.
        /// </summary>
        private Dictionary<string, Dictionary<RDA2.FieldIDs, string>> map;

        /// <summary>
        /// The swarm service.
        /// </summary>
        private ISwarmService service;

        /// <summary>
        /// Initializes a new instance of the <see cref="RemoteEntityMapConfiguration"/> class.
        /// </summary>
        /// <param name="service">The service.</param>
        [ImportingConstructor]
        public RemoteEntityMapConfiguration(ISwarmService service)
        {
            this.service = service;
        }

        /// <summary>
        /// Gets the <see cref="Dictionary{RDA2.FieldIDsSystem.String}"/> for the specified entity name.
        /// If the configuration is not yet loaded, it will bo loaded automatically.
        /// </summary>
        /// <param name="entityName">Name of the entity.</param>
        /// <returns>The map for the requested entity.</returns>
        public Dictionary<RDA2.FieldIDs, string> this[string entityName]
        {
            get 
            {
                if (this.map == null)
                {
                    this.LoadConfiguration().Wait();
                }

                return this.map[entityName];
            }
        }

        /// <summary>
        /// Awaitable task for loading the configuration.
        /// </summary>
        /// <returns>The task for this operation.</returns>
        public System.Threading.Tasks.Task LoadConfiguration()
        {
            return System.Threading.Tasks.Task.Factory.StartNew(() =>
                {
                    Logger.Trace("Downloading remote entity map");   
                    this.map = this.service.GetAsync<Dictionary<string, Dictionary<RDA2.FieldIDs, string>>>(RetailProCommon.Urls.Mapping).Result;
                    Logger.Trace("Remote entity map downloaded.");
                });
        }
    }
}
