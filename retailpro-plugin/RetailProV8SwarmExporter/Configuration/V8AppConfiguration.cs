//-----------------------------------------------------------------------
// <copyright file="V8AppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 28. 15:09</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Configuration;
    using System.Globalization;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.Configuration;

    /// <summary>
    /// Contains configuration immutable configuration information about the Retail Pro v8 application
    /// </summary>
    [Export(typeof(IV8AppConfiguration))]
    [Export(typeof(ICommonAppConfiguration))]
    [PartCreationPolicy(CreationPolicy.Shared)]
    public class V8AppConfiguration : CommonAppConfiguration, IV8AppConfiguration
    {
        /// <summary>
        /// Formatting of dates in the App.config file
        /// </summary>
        private const string DateFormat = "yyyy-MM-dd";

        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Initializes a new instance of the <see cref="V8AppConfiguration"/> class
        /// </summary>
        public V8AppConfiguration()
        {
            // Setup workstation of Retail Pro
            int workstation = 0;
            if (!int.TryParse(ConfigurationManager.AppSettings["workstation"], out workstation))
            {
                workstation = 1;
            }

            this.AuthWorkstation = workstation;

            // Setup deep exporting interval or no deep exporting if missing or 0
            int interval = 0;
            if (!int.TryParse(ConfigurationManager.AppSettings["deepExportInterval"], out interval))
            {
                interval = 0;
            }

            this.DeepExportingEnabled = interval > 0;
            this.DeepExportIntervalMinutes = interval;

            // Setup ignoring of invoices older than a certain age
            DateTime ignoreFilter = DateTime.Now;
            if (!DateTime.TryParseExact(ConfigurationManager.AppSettings["importingDataSince"], DateFormat, CultureInfo.InvariantCulture, DateTimeStyles.None, out ignoreFilter))
            {
                Logger.Warn("Failed to read ignoreFilter: " + ConfigurationManager.AppSettings["importingDataSince"]);
                ignoreFilter.AddMonths(-1);
            }

            this.IgnoreEarlierInvoicesFilter = ignoreFilter;

            int syncLimit = 0;
            if (!int.TryParse(ConfigurationManager.AppSettings["syncDaysLimit"], out syncLimit))
            {
                // Default is one day
                Logger.Warn("Failed to read syncDateLimit: " + ConfigurationManager.AppSettings["syncDaysLimit"]);
                syncLimit = 1;
            }

            this.SyncDaysLimit = syncLimit;

            // Setup string based preferences
            this.InstallationPath = ConfigurationManager.AppSettings["installationPath"];
        }

        /// <summary>
        /// Gets the Retail Pro V8 installation directory
        /// </summary>
        public string InstallationPath { get; private set; }

        /// <summary>
        /// Gets the authorization for V8 needs a WorkStation id (1..99)
        /// </summary>
        public int AuthWorkstation { get; private set; }

        /// <summary>
        /// Gets the amount of time (minutes) between running deep exports
        /// </summary>
        public int DeepExportIntervalMinutes { get; private set; }

        /// <summary>
        /// Gets a value indicating whether deep invoice exporting is executed
        /// It is not executed if value is false
        /// </summary>
        public bool DeepExportingEnabled { get; private set; }

        /// <summary>
        /// Gets a timestamp indicating that invoices earlier
        /// than this time should be ignored
        /// </summary>
        public DateTime IgnoreEarlierInvoicesFilter { get; private set; }

        /// <summary>
        /// Gets the maximum size of the date dictionary in days
        /// </summary>
        public int SyncDaysLimit { get; private set; }
    }
}
