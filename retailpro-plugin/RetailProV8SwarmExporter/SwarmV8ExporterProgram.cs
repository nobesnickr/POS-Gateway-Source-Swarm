//-----------------------------------------------------------------------
// <copyright file="SwarmV8ExporterProgram.cs" company="Sonrisa">
//     Copyright (c) JitSmart  All rights reserved.
// </copyright>
// <author>Csabi</author>
// <date>2013. 7. 24. 10:55</date>
//-----------------------------------------------------------------------

namespace RetailProV8SwarmExporter
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.ComponentModel.Composition.Hosting;
    using System.Configuration;
    using System.Linq;
    using System.Text;
    using System.Threading;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;
    using RetailProV8SwarmExporter.Exporter;
    
    /// <summary>
    /// Main program.
    /// </summary>
    public sealed class SwarmV8ExporterProgram
    {
        /// <summary>
        /// Unique application guid.
        /// </summary>
        private const string AppGuid = "fcf90916-af01-4d1c-8d7d-33f1e08c5cb3";

        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();
        
        /// <summary>
        /// Prevents a default instance of the <see cref="SwarmV8ExporterProgram"/> class from being created.
        /// </summary>
        private SwarmV8ExporterProgram()
        {
        }

        /// <summary>
        /// Main entry point for the exporter application.
        /// </summary>        
        /// <param name="args">The arguments.</param>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA1801:ReviewUnusedParameters", MessageId = "args", Justification = "Used in release mode.")]
        private static void Main(string[] args)
        {            
#if DEBUG
            if (!System.Diagnostics.Debugger.IsAttached)
            {
                // If we are in debug mode and the debugger is not attached wait until the dev can attach the debugger
                System.Windows.MessageBox.Show("You can attach debugger now.");
            }
#else
            if (!System.Diagnostics.Debugger.IsAttached && args.Length > 0 && args[0].ToUpperInvariant().Contains("DEBUGBREAK"))
            {
                // provide the possibility for attaching a debugger in release mode also.
                System.Windows.MessageBox.Show("You can attach debugger now.");
            }
#endif

            // Prevent multiple application instances using a mutex.
            using (var mutex = new Mutex(false, "Global\\" + AppGuid))
            {
                if (!mutex.WaitOne(0, false))
                {
                    return;
                }

                RunExporter();
                Logger.Info("Finished exporting.");
            }
        }

        /// <summary>
        /// Runs the exporter.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Justification = "Has to log all exceptions.")]
        private static void RunExporter()
        {
            try
            {
                using (var catalog = new AggregateCatalog(new DirectoryCatalog(".", "*.dll"), new DirectoryCatalog(".", "*.exe")))
                using (var container = new CompositionContainer(catalog))
                {
                    container.GetExportedValue<RetailProCommon.Logging.RemoteLogLevelConfiguration>().ApplyRemoteLogLevels().Wait();

                    RetailProCommon.Logging.SwarmServiceTarget.SetService(
                        container.GetExportedValue<ISwarmService>());                    
                 
                    // Log info
                    var config = container.GetExportedValue<RetailProV8SwarmExporter.Configuration.IV8AppConfiguration>();
                    Logger.Info("Exporting data for {0} from {1} WS {2}", config.SwarmId, config.InstallationPath, config.AuthWorkstation);

                    // load remote configurations paralell
                    System.Threading.Tasks.Task.WaitAll(
                        container.GetExportedValue<RetailProCommon.Configuration.IConfiguration>().LoadRemoteConfiguration(),
                        container.GetExportedValue<IEntityMapConfiguration>().LoadConfiguration());

                    // Get extractor and open connection
                    var extractor = container.GetExportedValue<RetailProV8SwarmExporter.DataAccess.IRetailProExtractor>();
                    extractor.Connect();

                    var exporter = container.GetExportedValue<RetailProCommon.BusinessLogic.Exporter>();
                    exporter.Run();
                }
            }
            catch (AggregateException exceptions)
            {
                var flat = exceptions.Flatten();
                foreach (var exc in flat.InnerExceptions)
                {
                    Logger.ErrorException("Error during export:", exc);
                }
            }
            catch (Exception exception)
            {
                Logger.ErrorException("Unexpected error:", exception);
            }

            NLog.LogManager.Flush(TimeSpan.FromSeconds(30));
        }
    }
}
