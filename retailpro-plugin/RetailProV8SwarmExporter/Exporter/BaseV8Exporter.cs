//-----------------------------------------------------------------------
// <copyright file="BaseV8Exporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Exporter
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Base class for classes capable of exporting data from Retail Pro v8
    /// </summary>
    public abstract class BaseV8Exporter : IEntityExporter
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Security", "CA2104:DoNotDeclareReadOnlyMutableReferenceTypes", Justification = "Reviewed.")]
        protected static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The service used to send data to Swarm
        /// </summary>
        private readonly ISwarmService swarmService;

        /// <summary>
        /// REST URL to upload the current exported item to
        /// </summary>
        private readonly string uploadUrl;

        /// <summary>
        /// Initializes a new instance of the <see cref="BaseV8Exporter"/> class
        /// </summary>
        /// <param name="service">Service to be used for uploading date</param>
        /// <param name="uploadUrl">Url to be used for uploading date. The url will be prefixed with "items/" string.</param>
        protected BaseV8Exporter(ISwarmService service, string uploadUrl)
        {
            this.swarmService = service;
            this.uploadUrl = uploadUrl;
        }                
        
        /// <summary>
        /// Main executes function for an exporter, uploads recently 
        /// changed items to the swarm service
        /// </summary>
        /// <returns>The async task</returns>
        public abstract Task ExportChangedItems();

        /// <summary>
        /// Function triggered if uploading the items was succesful
        /// </summary>
        /// <param name="data">The data that will sent to the Swarm Server as a JSON</param>
        /// <returns>The async task</returns>
        protected Task UploadToService(object data)
        {
            return Task.Factory.StartNew(() =>
            {
                Logger.Info("Uploading data to " + this.uploadUrl + ".");
                this.swarmService.UploadAsync(this.uploadUrl, data).Wait();
                Logger.Info("Upload success to " + this.uploadUrl + ".");
            });
        }
    }
}
