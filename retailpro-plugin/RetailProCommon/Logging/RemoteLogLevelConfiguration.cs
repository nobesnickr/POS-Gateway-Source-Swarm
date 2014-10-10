//-----------------------------------------------------------------------
// <copyright file="RemoteLogLevelConfiguration.cs" company="Sonrisa">
//     Copyright (c) JitSmart  All rights reserved.
// </copyright>
// <author>Csabi</author>
// <date>2013. 11. 04. 10:55</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Logging
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using NLog;
    using RetailProCommon.Service;

    /// <summary>
    /// Class responsible for applying remote log level configurations.
    /// </summary>
    [Export(typeof(RemoteLogLevelConfiguration))]
    public class RemoteLogLevelConfiguration
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly Logger CurrentLogger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The log levels
        /// </summary>
        private static readonly List<LogLevel> LogLevels = new List<LogLevel>()
            {
                LogLevel.Trace,
                LogLevel.Debug,
                LogLevel.Info,
                LogLevel.Warn,
                LogLevel.Error,
                LogLevel.Fatal,                
            };

        /// <summary>
        /// The service
        /// </summary>
        private ISwarmService service;

        /// <summary>
        /// Initializes a new instance of the <see cref="RemoteLogLevelConfiguration"/> class.
        /// </summary>
        /// <param name="service">The service.</param>
        [ImportingConstructor]
        public RemoteLogLevelConfiguration(ISwarmService service)
        {
            this.service = service;
        }

        /// <summary>
        /// Downloads remote logging levels and applies them.
        /// </summary>
        /// <returns>The async task for the operation.</returns>
        public Task ApplyRemoteLogLevels()
        {
            return Task.Factory.StartNew(() =>
            {
                try
                {
                    ////var remoteConfig = new Dictionary<string, string>() { { "Network", "Trace" } }; // This is just for testing.
                    var remoteConfig = this.service.GetAsync<Dictionary<string, string>>(Urls.LogConfig).Result;
                    if (remoteConfig == null)
                    {
                        return;
                    }

                    //// The remote config dictionary stores min log levels for log targets. For example {"Network": "Warn", "File": "Info" }
                    foreach (var config in remoteConfig)
                    {
                        // Select all rules with the specified remote target
                        var rules = LogManager.Configuration.LoggingRules.Where(r => r.Targets.Select(t => t.Name.ToUpperInvariant()).Contains(config.Key.ToUpperInvariant()));
                        foreach (var rule in rules)
                        {
                            // Set all levels in all affected rules to desired value.
                            // the loglevel comparison works, because the < and > operators are overloaded.
                            var specifiedMinLevel = LogLevel.FromString(config.Value);
                            foreach (var level in LogLevels)
                            {
                                if (level < specifiedMinLevel)
                                {
                                    rule.DisableLoggingForLevel(level);
                                }
                                else
                                {
                                    rule.EnableLoggingForLevel(level);
                                }
                            }
                        }
                    }

                    LogManager.ReconfigExistingLoggers();
                    CurrentLogger.Debug("Configured remote logging levels.");
                }
                catch (Exception exc)
                {
                    CurrentLogger.InfoException("Could not configure remote logging levels.", exc);
                }
            });
        }
    }
}
