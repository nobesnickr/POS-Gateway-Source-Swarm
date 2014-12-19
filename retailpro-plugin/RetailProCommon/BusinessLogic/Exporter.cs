//-----------------------------------------------------------------------
// <copyright file="Exporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 12:27</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using NHibernate;
    using RetailProCommon.Service;

    /// <summary>
    /// Class responsible for exporting values from the retail pro database.
    /// </summary>
    [Export(typeof(Exporter))]
    public class Exporter
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The list of entity exporters.
        /// </summary>
        private readonly IEnumerable<IEntityExporter> entityExporters;

        /// <summary>
        /// Initializes a new instance of the <see cref="Exporter" /> class.
        /// </summary>
        /// <param name="entityExporters">The list of available entity eyporters.</param>
        [ImportingConstructor]
        public Exporter([ImportMany] IEnumerable<IEntityExporter> entityExporters)
        {            
            this.entityExporters = entityExporters;                       
        }

        /// <summary>
        /// Runs the exporter asynchronously.
        /// </summary>
        /// <returns>
        /// The awaitable task for the export operations.
        /// </returns>
        public Task RunAsync()
        {
            Logger.Trace("Running export jobs: " + string.Join(",", this.entityExporters.Select(e => e.GetType().Name)));
            var jobs = this.entityExporters.Select(q => q.ExportChangedItems()).ToArray();
            return Task.Factory.ContinueWhenAll(
                jobs, 
                tasks => 
                {                    
                    var exceptions = tasks
                        .Where(t => t.Exception != null)                        
                        .SelectMany(t => t.Exception.Flatten().InnerExceptions)
                        .ToList();
                    exceptions.ForEach(e => Logger.ErrorException("Unexpected error occurred:", e));
                    Logger.Trace(System.Globalization.CultureInfo.InvariantCulture, "Export jobs completed with {0} error(s).", exceptions.Count); 
                }, 
                TaskContinuationOptions.ExecuteSynchronously);
        }

        /// <summary>
        /// Runs the exporter instances synchronously.
        /// </summary>
        public void Run()
        {
            foreach (var entityExporter in this.entityExporters)
            {
                try
                {
                    Logger.Trace("Running exporter job: " + entityExporter.GetType().Name);
                    entityExporter.ExportChangedItems().Wait();
                }
                catch (AggregateException exception)
                {
                    foreach (var innerException in exception.Flatten().InnerExceptions)
                    {
                        Logger.ErrorException("Unexpected error occured:", innerException);
                    }
                }
            }
        }
    }
}
