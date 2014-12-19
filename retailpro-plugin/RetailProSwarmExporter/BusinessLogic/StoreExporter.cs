//-----------------------------------------------------------------------
// <copyright file="StoreExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 26. 11:58</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using NHibernate;
    using NHibernate.Linq;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;

    /// <summary>
    /// Retail pro exporter, which is responsible for exporting changes for the version table.
    /// </summary>
    [Export(typeof(IEntityExporter))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class StoreExporter : EntityExporterBase
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Initializes a new instance of the <see cref="StoreExporter" /> class.
        /// </summary>
        /// <param name="sessionFactory">The session factory.</param>
        /// <param name="configuration">The configuration.</param>
        /// <param name="swarmService">The swarm service.</param>
        /// <param name="appConfig">The app config.</param>
        [ImportingConstructor]
        public StoreExporter(Func<ISession> sessionFactory, IConfiguration configuration, ISwarmService swarmService, ICommonAppConfiguration appConfig)
            : base(sessionFactory, configuration, swarmService, RetailProCommon.Urls.StoreUpload, appConfig)
        {
        }

        /// <summary>
        /// Exports all changed stores
        /// Also changes the LastModifiedStoreDate to the most recent exported store's ModifiedDate
        /// </summary>
        /// <returns>
        /// Task to execute
        /// </returns>
        public override Task ExportChangedItems()
        {
            return Task.Factory.StartNew(() =>
            {
                Logger.Trace("Querying stores modified after " + this.Configuration.LastModifiedStoreDate.ToString());

                using (var unitOfWork = this.GetUnitOfWork()) 
                { 
                    var stores = unitOfWork.Query<Store>()
                        .Where(s => s.ModifiedDate >= this.Configuration.LastModifiedStoreDate)
                        .OrderBy(s => s.ModifiedDate)
                        .Take(this.MaxItems)
                        .ToList();

                    Logger.Trace("Got " + stores.Count + " new store(s)");
                    if (stores.Count > 0)
                    {
                        base.UploadToService(stores);
                        this.Configuration.LastModifiedStoreDate = stores.Max(s => s.ModifiedDate);
                    }
                }
            });
        }
    }
}
