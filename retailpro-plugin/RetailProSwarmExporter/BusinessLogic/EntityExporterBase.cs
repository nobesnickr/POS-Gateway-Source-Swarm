//-----------------------------------------------------------------------
// <copyright file="EntityExporterBase.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 16:51</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using NHibernate;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;
    using RetailProCommon.Service;

    /// <summary>
    /// Abstract base class for entity exporters.
    /// </summary>
    public abstract class EntityExporterBase : IEntityExporter
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The session factory
        /// </summary>
        private readonly Func<ISession> sessionFactory;

        /// <summary>
        /// The swarm service
        /// </summary>
        private readonly ISwarmService swarmService;

        /// <summary>
        /// The upload URL.
        /// </summary>
        private readonly string uploadUrl;

        /// <summary>
        /// Initializes a new instance of the <see cref="EntityExporterBase" /> class.
        /// </summary>
        /// <param name="sessionFactory">The session factory.</param>
        /// <param name="configuration">The configuration.</param>
        /// <param name="swarmService">The swarm service.</param>
        /// <param name="uploadUrl">The upload URL.</param>
        /// <param name="appConfig">The app config.</param>
        protected EntityExporterBase(Func<ISession> sessionFactory, IConfiguration configuration, ISwarmService swarmService, string uploadUrl, ICommonAppConfiguration appConfig)
        {            
            this.Configuration = configuration;
            this.sessionFactory = sessionFactory;
            this.swarmService = swarmService;
            this.uploadUrl = uploadUrl;
            this.MaxItems = appConfig.MaxUploadItems;
        }

        /// <summary>
        /// Gets the max items to query from the database.
        /// </summary>
        protected int MaxItems { get; private set; }

        /// <summary>
        /// Gets the configuration.
        /// </summary>
        protected IConfiguration Configuration { get; private set; }

        /// <summary>
        /// Exports changed items.
        /// Also stores the appropriate last modified date in the related configuration.
        /// </summary>
        /// <returns>The async task for the export operation.</returns>
        public abstract Task ExportChangedItems();

        /// <summary>
        /// Gets the session for executing queries
        /// </summary>
        /// <returns>Session to work in</returns>
        protected ISession GetUnitOfWork()
        {
            return this.sessionFactory();
        }

        /// <summary>
        /// Uploads data to the gateway
        /// </summary>
        /// <param name="data">Data to upload</param>
        protected void UploadToService(object data)
        {
            if (data != null)
            {
                Logger.Trace("Uploading data to " + this.uploadUrl + ".");
                this.swarmService.UploadAsync(this.uploadUrl, data).Wait();
                Logger.Trace("Upload success to " + this.uploadUrl + ".");
            }
        }
    }
}
