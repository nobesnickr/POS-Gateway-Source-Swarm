//-----------------------------------------------------------------------
// <copyright file="IV8AppConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 28. 15:00</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProCommon.Configuration;

    /// <summary>
    /// Contains configuration information for the local installation of a V8 exporter.
    /// </summary>
    public interface IV8AppConfiguration : ICommonAppConfiguration
    {
        /// <summary>
        /// Gets the Retail Pro V8 installation directory
        /// </summary>
        string InstallationPath { get; }

        /// <summary>
        /// Gets the authorization for V8 needs a WorkStation id (1..99)
        /// </summary>
        int AuthWorkstation { get; }

        /// <summary>
        /// Gets the amount of time (minutes) between running deep exports
        /// </summary>
        int DeepExportIntervalMinutes { get; }

        /// <summary>
        /// Gets a value indicating whether deep invoice exporting is executed
        /// It is not executed if value is falls
        /// </summary>
        bool DeepExportingEnabled { get; }

        /// <summary>
        /// Gets a timestamp indicating that invoices earlier
        /// than this time should be ignored
        /// </summary>
        DateTime IgnoreEarlierInvoicesFilter { get; }

        /// <summary>
        /// Gets the maximum size of the date dictionary in days
        /// </summary>
        int SyncDaysLimit { get; }
    }
}
