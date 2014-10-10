//-----------------------------------------------------------------------
// <copyright file="VersionExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 15:25</date>
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
    public class VersionExporter : EntityExporterBase
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Initializes a new instance of the <see cref="VersionExporter" /> class.
        /// </summary>
        /// <param name="sessionFactory">The session factory.</param>
        /// <param name="configuration">The configuration.</param>
        /// <param name="swarmService">The swarm service.</param>
        /// <param name="appConfig">The app config.</param>
        [ImportingConstructor]
        public VersionExporter(Func<ISession> sessionFactory, IConfiguration configuration, ISwarmService swarmService, ICommonAppConfiguration appConfig)
            : base(sessionFactory, configuration, swarmService, RetailProCommon.Urls.VersionUpload, appConfig)
        {
        }

        /// <summary>
        /// Gets all changed version entities
        /// </summary>
        /// <returns>
        /// Task to execute
        /// </returns>
        public override Task ExportChangedItems()
        {
            return Task.Factory.StartNew(() =>
            {
                Logger.Trace("Quering version items modified after " + this.Configuration.LastModifiedVersionDate.ToString());

                using (var unitOfWork = this.GetUnitOfWork())
                {
                    var versionItems = unitOfWork.Query<RetailProVersion>()
                        .Where(v => v.UpdatedDate > this.Configuration.LastModifiedVersionDate)
                        .OrderBy(i => i.UpdatedDate)
                        .Take(this.MaxItems)
                        .ToList();

                    Logger.Trace("Got " + versionItems.Count + " new version item(s)");
                    if (versionItems.Count > 0)
                    {
                        base.UploadToService(versionItems);
                        this.Configuration.LastModifiedVersionDate = versionItems.Max(v => v.UpdatedDate);
                    }
                }
            });
        }
    }
}
