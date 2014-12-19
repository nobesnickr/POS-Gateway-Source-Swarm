//-----------------------------------------------------------------------
// <copyright file="SwarmExporterProgram.cs" company="Sonrisa">
//     Copyright (c) JitSmart  All rights reserved.
// </copyright>
// <author>Csabi</author>
// <date>2013. 7. 24. 10:55</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.ComponentModel.Composition.Hosting;
    using System.Configuration;
    using System.Linq;
    using System.Text;
    using System.Threading;
    using NHibernate.Linq;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Configuration;
    using RetailProCommon.Model;
    using RetailProCommon.Service;

    /// <summary>
    /// Main program.
    /// </summary>
    public sealed class SwarmExporterProgram
    {
        /// <summary>
        /// Unique application guid.
        /// </summary>
        private const string AppGuid = "784F667A-A265-40C4-923E-AD3FACEC8EFD";

        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Prevents a default instance of the <see cref="SwarmExporterProgram"/> class from being created.
        /// </summary>
        private SwarmExporterProgram()
        {
        }

        /// <summary>
        /// Main entry point for the exporter application.
        /// </summary>
        /// <param name="args">The arguments.</param>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA1801:ReviewUnusedParameters", MessageId = "args", Justification = "Used only in release mode.")]
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
            }
        }

        /// <summary>
        /// Runs the exporter.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Justification = "Exception is logged, and swallowed.")]
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

                    var config = container.GetExportedValue<RetailProSwarmExporter.Configuration.IV9AppConfiguration>();
                    Logger.Info("Exporting data for {0} using {1}", config.SwarmId, config.ConnectionString);

                    // load remote configuration
                    container.GetExportedValue<RetailProCommon.Configuration.IConfiguration>().LoadRemoteConfiguration().Wait();

                    container.ComposeExportedValue<Func<NHibernate.ISession>>(DataAccess.NHibernateHelper.OpenSession);
                    var exporter = container.GetExportedValue<Exporter>();
                    exporter.RunAsync().Wait();
                }
            }
            catch (AggregateException exceptions)
            {
                var flat = exceptions.Flatten();
                foreach (var exc in flat.InnerExceptions)
                {
                    Logger.ErrorException("Error during export.", exc);
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
