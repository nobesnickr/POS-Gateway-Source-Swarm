//-----------------------------------------------------------------------
// <copyright file="InvoiceExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 17:18</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Exporter
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Dynamic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;    
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Exports the invoice data from Retail Pro v8
    /// </summary>
    [Export(typeof(IEntityExporter))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public sealed class InvoiceExporter : BaseV8Exporter
    {
        /// <summary>
        /// The application configuration containing the maximum upload limit
        /// </summary>
        private IV8AppConfiguration localConfig;

        /// <summary>
        /// The application configuration containing the maximum upload limit
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
        /// Initializes a new instance of the <see cref="InvoiceExporter"/> class
        /// InvoiceExporter retrieves data from Retail Pro using the RDA2 interface,
        /// and uploads it to the Swarm Service
        /// </summary>
        /// <param name="extractor">Retail Pro data is accessed via this extractor</param>
        /// <param name="localConfig">Local configuration contains Retail Pro v8's local settings</param>
        /// <param name="configuration">The exporter configuration containing the last upload timestamps</param>
        /// <param name="swarmService">Execution will upload data using this Swarm Service</param>
        /// <param name="modelMapper">Mapper mapping RetailPro documents to model objects</param>
        [ImportingConstructor]
        public InvoiceExporter(IRetailProExtractor extractor, IV8AppConfiguration localConfig, IConfiguration configuration, ISwarmService swarmService, IRetailProModelMapper modelMapper) 
            : base(swarmService, RetailProCommon.Urls.InvoiceUpload)
        {
            this.extractor = extractor;
            this.configuration = configuration;
            this.localConfig = localConfig;
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
                this.UploadRecentInvoices();
            });
        }

        /// <summary>
        /// Retrieve data from RetailPro, starting from the most recent item
        /// and iterating backwards
        /// </summary>
        private void UploadRecentInvoices()
        {
            using (var invoiceTable = this.extractor.OpenTable("Invoices"))
            {
                invoiceTable.Forward = false;
                invoiceTable.Open();

                var storeFilters = this.configuration.LastModifiedInvoiceDate;
                var oldestFilter = this.GetOldestFilter(storeFilters);

                Logger.Info(System.Globalization.CultureInfo.InvariantCulture, "Preparing to extract invoices using {0} as oldestFilter", oldestFilter);

                // Log all entries in the storeFilters array
                if (Logger.IsTraceEnabled)
                {
                    foreach (var entry in storeFilters.Entries)
                    {
                        Logger.Trace("Invoice exporter is using store filter for SBS: {0} and StoreNo: {1} with value: {2}", entry.Key.SbsNumber, entry.Key.StoreNumber, entry.Value);
                    }
                }

                IEnumerable<Invoice> invoices;

                int totalNumberOfInvoices = 0;
                var maxTimestampDict = new DateDictionary();

                using (var customerTable = this.extractor.OpenTable("Customers"))
                {
                    customerTable.Open();
                    customerTable.ActiveIndex = (int)RDA2.IndexIDs.idxCustID;

                    // Select invoices since the new timestamp
                    invoices = invoiceTable.Items
                                    .Where(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) >= this.localConfig.IgnoreEarlierInvoicesFilter)
                                    .TakeWhile(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) > oldestFilter)
                                    .Where(i => i.GetDateTime(RDA2.FieldIDs.fidDocLastEdit) > storeFilters.GetDate(this.extractor.SbsNumber, i.GetString(RDA2.FieldIDs.fidStore)))
                                    .Select(row => 
                                        {
                                            Invoice invoice;
                                            if (this.modelMapper.TryGetInvoiceObject(row, customerTable, this.extractor.SbsNumber, out invoice))
                                            {
                                                return invoice;
                                            } 
                                            else 
                                            {
                                                return null;
                                            }
                                        })
                                    .Where(i => i != null);

                    var tempList = new List<Invoice>();

                    // Iterate through all invoices, upload in chunks.
                    foreach (var item in invoices)
                    {
                        tempList.Add(item);
                        totalNumberOfInvoices++;

                        if (tempList.Count >= this.localConfig.MaxUploadItems)
                        {                            
                            this.UploadInvoicesToService(tempList);
                            tempList.Clear();
                        }

                        // If upload was successfull then store the most recent modified
                        // item for each store. This has to be in a cache, so it doesn't
                        // change the actual date dictionary if upload fails at any point
                        var modifiedDate = ParseHelper.ParseDateTime(item.ModifiedDate);
                        if (modifiedDate > maxTimestampDict.GetDate(item.SbsNo, item.StoreNo))
                        {
                            maxTimestampDict.SetDate(item.SbsNo, item.StoreNo, modifiedDate);
                        }
                    }

                    if (tempList.Count > 0)
                    {                        
                        this.UploadInvoicesToService(tempList);
                    }
                }

                // If we found an invoice for a store which is more 
                // recent then the last known invoice, than its timestamp
                // will be added to the dictionary
                foreach (var entry in maxTimestampDict.Entries)
                {
                    if (storeFilters.GetDate(entry.Key.SbsNumber, entry.Key.StoreNumber) < maxTimestampDict.GetDate(entry.Key.SbsNumber, entry.Key.StoreNumber))
                    {
                        storeFilters.SetDate(entry.Key.SbsNumber, entry.Key.StoreNumber, entry.Value);
                    }
                }

                Logger.Info(System.Globalization.CultureInfo.InvariantCulture, "Finalizing invoice exporting after uploading {0} items", totalNumberOfInvoices);
            }
        }

        /// <summary>
        /// Function that takes a list of invoices and uploads it to the service,
        /// customers, invoice lines, products and invoices separately
        /// </summary>
        /// <param name="invoices">List of invoices</param>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Debug(System.IFormatProvider,System.String,System.Int32)", Justification = "Reviewed.")]
        private void UploadInvoicesToService(IEnumerable<Invoice> invoices)
        {
            Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, "Sending {0} invoices", invoices.Count());
            var invoiceItems = invoices.SelectMany(i => i.InvoiceItems).ToList();
            var data = new
            {
                Customers = invoices.Select(i => i.Customer).Where(c => c != null).Distinct().ToList(),
                Items = invoiceItems,
                Products = invoiceItems.Select(item => (object)item.Product).Distinct().ToList(),
                Invoices = invoices.ToList()
            };
            this.UploadToService(data).Wait();
        }

        /// <summary>
        /// Reads oldest date for date dictionary, but doesn't return a value older then SyncDaysLimit days
        /// </summary>
        /// <param name="storeFilters">DateDictionary containing filter for all stores</param>
        /// <returns>Oldest datefilter, or SyncDaysLimit before the most recent</returns>
        private DateTime GetOldestFilter(DateDictionary storeFilters)
        {
            var oldestFilter = storeFilters.Entries.Count > 0 ? storeFilters.Entries.Values.Min() : storeFilters.DefaultDate;

            var mostRecentFilter = storeFilters.Entries.Count > 0 ? storeFilters.Entries.Values.Max() : storeFilters.DefaultDate;

            // If the oldest entry is "not that old" then return it
            if (oldestFilter.AddDays(this.localConfig.SyncDaysLimit) > mostRecentFilter)
            {
                return oldestFilter;
            } 
            else
            {
                // If the most recent filter is too small to substract SyncDaysLimit from it
                if (mostRecentFilter < DateTime.MinValue.AddDays(this.localConfig.SyncDaysLimit))
                {
                    return DateTime.MinValue;
                }
                else
                {
                    return mostRecentFilter.AddDays(-this.localConfig.SyncDaysLimit);
                }
            }
        }
    }
}
