//-----------------------------------------------------------------------
// <copyright file="StoreExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 23. 16:43</date>
//-----------------------------------------------------------------------

namespace RetailProV8SwarmExporter.Exporter
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;    
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Retail pro exporter, which is responsible for exporting changes for the version table.
    /// </summary>
    [Export(typeof(IEntityExporter))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public sealed class StoreExporter : BaseV8Exporter
    {
        /// <summary>
        /// The configuration, i.e. interface able to provide the last known store information
        /// </summary>
        private IConfiguration configuration;

        /// <summary>
        /// The extractor (normaly RDA2) to retrieve recent data from Retail Pro
        /// </summary>
        private IRetailProExtractor extractor;

        /// <summary>
        /// Mapper that maps RetailPro documents to model entries
        /// </summary>
        private IRetailProModelMapper modelMapper;
        
        /// <summary>
        /// Initializes a new instance of the <see cref="StoreExporter" /> class.
        /// </summary>
        /// <param name="extractor">Retail Pro data is accessed via this extractor</param>
        /// <param name="configuration">The exporter configuration containing the last upload timestamps</param>
        /// <param name="swarmService">Execution will upload data using this Swarm Service</param>
        /// <param name="modelMapper">Mapper mapping RetailPro documents to model objects</param>
        [ImportingConstructor]
        public StoreExporter(IRetailProExtractor extractor, IConfiguration configuration, ISwarmService swarmService, IRetailProModelMapper modelMapper) 
            : base(swarmService, RetailProCommon.Urls.StoreUpload)
        {
            this.extractor = extractor;
            this.configuration = configuration;
            this.modelMapper = modelMapper;
        }

        /// <summary>
        /// Asynchronous task to export changed stores
        /// </summary>
        /// <returns>The async task</returns>        
        public override Task ExportChangedItems()
        {
            return Task.Factory.StartNew(() =>
            {                
                Logger.Info("Store exporting started");
                var data = this.GetRetailProStores();

                if (data != null && data.Count > 0)
                {
                    this.UploadToService(data).Wait();
                    this.configuration.LastModifiedStoreDate = data.Max(store => store.ModifiedDate);
                }
            });
        }

        /// <summary>
        /// Retrieve list of changed stores
        /// </summary>
        /// <returns>The List the <see cref="ExportChangedItems"/> would export</returns>
        private IList<Store> GetRetailProStores()
        {
            DateTime timeFilter = this.configuration.LastModifiedStoreDate;
            using (var invoiceTable = this.extractor.OpenTable("Invoices"))
            {
                // iterate backwards on the table, meaning starting with the last added invoice
                invoiceTable.Forward = false;
                invoiceTable.Open();
                var recentStores = invoiceTable.Items
                                    .TakeWhile(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) > timeFilter)
                                    .Select(row => 
                                        {
                                            Store store;                                            
                                            if (this.modelMapper.TryGetStoreObject(row, this.extractor.SbsNumber, out store))
                                            {
                                                return store;
                                            }
                                            else
                                            {
                                                return null;
                                            }
                                        })
                                    .Distinct()
                                    .Where(store => store != null)                                    
                                    .ToList();

                Logger.Info("Found " + recentStores.Count + " stores to be sent.");
                
                return recentStores                    
                    .ToList();
            }
        }
    }
}
