//-----------------------------------------------------------------------
// <copyright file="Heartbeat.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 30. 10:29</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.Model;

    /// <summary>
    /// Calls the heartbeat service on a regular basis.
    /// </summary>
    [Export(typeof(IEntityExporter))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class Heartbeat : IEntityExporter
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The swarm service.
        /// </summary>
        private Service.ISwarmService swarmService;

        /// <summary>
        /// The version
        /// </summary>
        private IApplicationVersion version;

        /// <summary>
        /// Initializes a new instance of the <see cref="Heartbeat" /> class.
        /// </summary>
        /// <param name="swarmService">The swarm service.</param>
        /// <param name="version">The version.</param>
        [ImportingConstructor]
        public Heartbeat(Service.ISwarmService swarmService, IApplicationVersion version)
        {
            this.version = version;
            this.swarmService = swarmService;
        }

        /// <summary>
        /// Calls the heartbeat service and transmits the current assembly version.
        /// </summary>
        /// <returns>
        /// The async task for the export operation.
        /// </returns>
        public Task ExportChangedItems()
        {
            Logger.Trace("Transmitting heartbeat. Version: " + this.version.Version);
            return this.swarmService.UploadAsync(Urls.HeartbeatUpload, new Pulse() { Version = this.version.Version });
        }
    }
}
