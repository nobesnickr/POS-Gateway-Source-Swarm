//-----------------------------------------------------------------------
// <copyright file="InvoiceExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:34</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
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
    /// Exports recently changed invoices.
    /// </summary>
    [Export(typeof(IEntityExporter))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class InvoiceExporter : EntityExporterBase
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Initializes a new instance of the <see cref="InvoiceExporter" /> class.
        /// </summary>
        /// <param name="sessionFactory">The session factory.</param>
        /// <param name="configuration">The configuration.</param>
        /// <param name="swarmService">The swarm service.</param>
        /// <param name="appConfig">The app config.</param>
        [ImportingConstructor]
        public InvoiceExporter(Func<ISession> sessionFactory, IConfiguration configuration, ISwarmService swarmService, ICommonAppConfiguration appConfig)
            : base(sessionFactory, configuration, swarmService, RetailProCommon.Urls.InvoiceUpload, appConfig)
        {
        }

        /// <summary>
        /// Export all items modified after LastModifiedInvoiceDate
        /// Also changes the LastModifiedDate to the most recent exported timestamp
        /// </summary>
        /// <returns>Task to execute</returns>
        public override Task ExportChangedItems()
        {
            return Task.Factory.StartNew(() =>
            {
                using (var unitOfWork = this.GetUnitOfWork())
                {
                    var stores = unitOfWork.Query<Store>();
                    foreach (var store in stores)
                    {
                        this.ExportStore(unitOfWork, store);
                    }
                }
            });
        }

        /// <summary>
        /// Export changed invoices for a given store
        /// </summary>
        /// <param name="unitOfWork">Session for accessing data</param>
        /// <param name="store">Store for which invoices are exported</param>
        private void ExportStore(ISession unitOfWork, Store store)
        {
            // TODO: NHibernate datetime comparison seems to be buggy
            var dateFilter = this.Configuration.LastModifiedInvoiceDate.GetDate(store.SbsNumber, store.StoreNumber);
            Logger.Trace("Querying invoices modified after {0} for {1}", dateFilter, store);

            var invoices = unitOfWork.Query<Invoice>()
                           .Fetch(i => i.Customer)
                           .Where(i => i.ModifiedDate > this.Configuration.LastModifiedInvoiceDate.GetDate(store.SbsNumber, store.StoreNumber))
                           .Where(i => i.StoreNumber == store.StoreNumber && i.SbsNumber == store.SbsNumber)
                           .OrderBy(i => i.ModifiedDate);

            Logger.Info("Got " + invoices.Count() + " new invoice(s)");

            DateTime globalMostRecent = dateFilter;

            // Segment data into chunks of this.MaxItems invoices
            for (int i = 0; i < invoices.Count(); i += this.MaxItems)
            {
                var subInvoices = invoices
                                    .Skip(i)
                                    .Take(this.MaxItems)
                                    .ToList();

                // Has to set all product's store number
                foreach (var invc in subInvoices)
                {
                    invc.InvoiceItems.ForEach(item => item.Product.StoreNumber = invc.StoreNumber);
                }

                // Generate data for JSON
                var invoiceItems = subInvoices.SelectMany(invc => invc.InvoiceItems).ToList();
                var data = new
                {
                    Customers = subInvoices.Select(invc => invc.Customer).Where(c => c != null).Distinct(),
                    Items = invoiceItems,
                    Products = invoiceItems.Select(item => item.Product).Distinct(),
                    Invoices = subInvoices
                };

                // Attempt to upload data to the gateway
                this.UploadToService(data);

                // Get most recent from batch
                var currentMostRecent = subInvoices.Select(invc => invc.ModifiedDate).Max();
                if (currentMostRecent > globalMostRecent)
                {
                    globalMostRecent = currentMostRecent;
                }
            }

            // If upload was successfull, update the LastModified date based
            // on the most recent invoice of the subquery
            this.Configuration.LastModifiedInvoiceDate.SetDate(store.SbsNumber, store.StoreNumber, globalMostRecent);
            Logger.Info("Last modified invoice date is set to {0} for {1}/{2}", globalMostRecent, store.SbsNumber, store.StoreNumber);
        }
    }
}
