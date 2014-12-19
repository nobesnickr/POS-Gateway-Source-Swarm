//-----------------------------------------------------------------------
// <copyright file="FileConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 25. 14:33</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.IO;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using System.Xml.Serialization;
using RetailProCommon.Service;

    /// <summary>
    /// Configuration implementation, stores data in a file.
    /// </summary>
    [Export(typeof(IConfiguration))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class FileConfiguration : IConfiguration, IDisposable
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The swarm service
        /// </summary>
        private readonly ISwarmService swarmService;

        /// <summary>
        /// The file path.
        /// </summary>
        private string filePath;

        /// <summary>
        /// The disposed flag.
        /// </summary>
        private bool isDisposed;

        /// <summary>
        /// Stores the last remote configuration version.
        /// </summary>
        private long lastRemoteVersion;

        /// <summary>
        /// Initializes a new instance of the <see cref="FileConfiguration" /> class.
        /// </summary>
        /// <param name="appConfig">The application configuration.</param>
        /// <param name="swarmService">The swarm service.</param>
        [ImportingConstructor]
        public FileConfiguration(ICommonAppConfiguration appConfig, ISwarmService swarmService)
        {
            this.swarmService = swarmService;

            //// Note: Has to update the installer if the folder is changed.
            var dirPath = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), appConfig.ConfigDirectoryName);
            if (!Directory.Exists(dirPath))
            {
                Directory.CreateDirectory(dirPath);                
            }

            this.filePath = Path.Combine(dirPath, appConfig.ConfigFileName);
            this.Load();
        }

        /// <summary>
        /// Finalizes an instance of the <see cref="FileConfiguration"/> class.
        /// </summary>
        ~FileConfiguration()
        {
            this.Dispose(false);
        }        

        /// <summary>
        /// Gets or sets the last modified invoice date.
        /// </summary>
        public DateDictionary LastModifiedInvoiceDate { get; set; }

        /// <summary>
        /// Gets or sets the last modified version date.
        /// </summary>
        public DateTime LastModifiedVersionDate { get; set; }

        /// <summary>
        /// Gets or sets the last modified Store date.
        /// </summary>
        public DateTime LastModifiedStoreDate { get; set; }

        /// <summary>
        /// Loads the remote configurations.
        /// </summary>
        /// <returns>
        /// The async task for the operation.
        /// </returns>
        public Task LoadRemoteConfiguration()
        {
            return Task.Factory.StartNew(() =>
            {                
                var remoteConfig = this.swarmService.GetAsync<RemoteConfiguration>(Urls.RemoteConfig).Result;
                if (remoteConfig.Version > this.lastRemoteVersion)
                {
                    this.lastRemoteVersion = remoteConfig.Version;
                    if (remoteConfig.LastInvoice.HasValue)
                    {
                        this.LastModifiedInvoiceDate.DefaultDate = remoteConfig.LastInvoice.Value;

                        foreach (var key in this.LastModifiedInvoiceDate.Entries.Keys.ToArray())
                        {
                            this.LastModifiedInvoiceDate.Entries[key] = remoteConfig.LastInvoice.Value;
                        }
                    }

                    if (remoteConfig.LastStore.HasValue)
                    {
                        this.LastModifiedStoreDate = remoteConfig.LastStore.Value;
                    }

                    if (remoteConfig.LastVersion.HasValue)
                    {
                        this.LastModifiedVersionDate = remoteConfig.LastVersion.Value;
                    }

                    this.Save();
                }
            });
        }

        /// <summary>
        /// Performs application-defined tasks associated with freeing, releasing, or resetting unmanaged resources.
        /// </summary>
        public void Dispose()
        {
            this.Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// Releases unmanaged and - optionally - managed resources.
        /// </summary>
        /// <param name="disposing"><c>true</c> to release both managed and unmanaged resources; <c>false</c> to release only unmanaged resources.</param>
        protected virtual void Dispose(bool disposing)
        {
            if (!this.isDisposed)
            {
                this.isDisposed = true;
                if (disposing)
                {
                    this.Save();
                }
            }
        }

        /// <summary>
        /// Saves the current configuration asynchronously.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Justification = "Configuration saving should swallow all exceptions.")]
        private void Save()
        {
            try
            {
                var serializer = new XmlSerializer(typeof(SettingsData));
                using (var stream = File.CreateText(this.filePath))
                {
                    var data = new SettingsData()
                    {
                        LastModifiedVersionDate = this.LastModifiedVersionDate,
                        LastModifiedInvoiceDate = new SerializableDateDictionary() { Dictionary = this.LastModifiedInvoiceDate },
                        LastModifiedStoreDate = this.LastModifiedStoreDate,
                        LastRemoteVersion = this.lastRemoteVersion,
                    };

                    serializer.Serialize(stream, data);
                }
            }
            catch (Exception exc)
            {
                // Swallow and log this exception. There is not much we could do.
                Logger.ErrorException("Error when saving file configuration.", exc);
            }
        }

        /// <summary>
        /// Loads the current configuration.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Justification = "Configuration loading should swallow all exceptions.")]
        private void Load()
        {
            const int MonthLimit = 4;

            try
            {
                var serializer = new XmlSerializer(typeof(SettingsData));
                using (var stream = File.OpenRead(this.filePath))
                {
                    var data = (SettingsData)serializer.Deserialize(stream);
                    
                    this.LastModifiedInvoiceDate = data.LastModifiedInvoiceDate.Dictionary;

                    // Default date can't be older than 4 months
                    if (this.LastModifiedInvoiceDate.DefaultDate < DateTime.Now.AddMonths(-MonthLimit))
                    {
                        this.LastModifiedInvoiceDate.DefaultDate = DateTime.Now.AddMonths(-MonthLimit);
                    }

                    this.LastModifiedVersionDate = data.LastModifiedVersionDate;
                    this.LastModifiedStoreDate = data.LastModifiedStoreDate;
                    this.lastRemoteVersion = data.LastRemoteVersion;
                }
            }
            catch (Exception exc)
            {
                // Swallow exception and load default values.
                Logger.ErrorException("Error when loading file configuration. Initializing to default.", exc);
                this.LastModifiedInvoiceDate = new DateDictionary();
                this.LastModifiedInvoiceDate.DefaultDate = DateTime.Now.AddMonths(-MonthLimit);
                this.Save();
            }
        }

        /// <summary>
        /// Settings data stored in the xml.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1034:NestedTypesShouldNotBeVisible", Justification = "Has to be public to be able to serialize.")]
        [Serializable]
        public class SettingsData
        {
            /// <summary>
            /// Gets or sets the last modified invoice date.
            /// </summary>
            public SerializableDateDictionary LastModifiedInvoiceDate { get; set; }

            /// <summary>
            /// Gets or sets the last modified version date.
            /// </summary>
            public DateTime LastModifiedVersionDate { get; set; }

            /// <summary>
            /// Gets or sets the last modified store date.
            /// </summary>
            public DateTime LastModifiedStoreDate { get; set; }

            /// <summary>
            /// Gets or sets the last remote configuration version.
            /// </summary>
            public long LastRemoteVersion { get; set; }
        }
    }
}
