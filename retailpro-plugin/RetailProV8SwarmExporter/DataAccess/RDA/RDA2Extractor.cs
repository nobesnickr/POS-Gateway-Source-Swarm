//-----------------------------------------------------------------------
// <copyright file="RDA2Extractor.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 16. 14:08</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Runtime.InteropServices;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;
    
    /// <summary>
    /// Extractor for Retail Pro v8 using the RDA2 (RetailPro Data Access) interface
    /// </summary>
    [Export(typeof(IRetailProExtractor))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class RDA2Extractor : IRetailProExtractor, IDisposable
    {
        /// <summary>
        /// Indicates whether object is disposed
        /// </summary>
        private bool isDisposed = false;

        /// <summary>
        /// The RDA2 interface
        /// </summary>
        private RDA2.IRdaDB rpro = null;

        /// <summary>
        /// Gets or sets the local configuration containing the Retail Pro installation and work station
        /// </summary>
        private IV8AppConfiguration localConfiguration;

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Extractor"/> class
        /// An RDA2 Extractor represents a connection to the Retail Pro installation,
        /// by providing access to it's data tables and properties
        /// </summary>
        /// <param name="localConfig">Local configuration containing the Retail Pro installation and work station</param>
        [ImportingConstructor]
        public RDA2Extractor(IV8AppConfiguration localConfig)
        {
            this.localConfiguration = localConfig;
        }

        /// <summary>
        /// Gets the SBS (Store Group) identifier extracted from RDA's Preferences
        /// </summary>
        public long SbsNumber { get; private set; }

        /// <summary>
        /// Initializes the RDA2 connection
        /// </summary>
        public void Connect()
        {
            string path = this.localConfiguration.InstallationPath;
            int workStation = this.localConfiguration.AuthWorkstation;

            try
            {
                if (this.rpro != null)
                {
                    this.Disconnect();
                }

                this.rpro = new RDA2.Rda2ServerClass();
                this.rpro.Connect(path, workStation, string.Empty, string.Empty);

                // Save SBS number
                var prefs = this.rpro.Preferences;

                long sbs;
                if (!long.TryParse(prefs.selectSingleNode("//@SBS_NO").text, out sbs))
                {
                    sbs = 0L;
                }

                this.SbsNumber = sbs;
            }
            catch (COMException ce)
            {
                throw new RDA2Exception("Connection to RDA2 interface failed", ce);
            }
        }
        
        /// <summary>
        /// Disconnect the RDA2 connector
        /// </summary>
        public void Disconnect()
        {
            if (this.rpro != null)
            {
                this.rpro.Disconnect();
                this.rpro = null;
            }
        }

        /// <summary>
        /// Open table using the RDA2 interface
        /// </summary>
        /// <param name="tableName">Tables are identified by their names, e.g. "Invoices", "Items", "Customers"</param>
        /// <returns>Accessor for the table by the given name</returns>
        public IRetailProTable OpenTable(string tableName)
        {
            try
            {
                RDA2.IRdaTable tableInstance = this.rpro.CreateTableByName(tableName);
                if (tableInstance == null)
                {
                    throw new RDA2Exception("Failed to open table: " + tableName);
                }

                return new RDA2Table(tableInstance, this.localConfiguration, tableName);
            }
            catch (ExternalException e)
            {
                throw new RDA2Exception("Interop exception occurred while trying to open table: " + tableName, e);
            }
        }

        /// <summary>
        /// Close the table, finalize the iteration
        /// </summary>
        public void Dispose()
        {
            this.Dispose(true);
            GC.SuppressFinalize(this);
        }

        /// <summary>
        /// Dispose of the managed resource by closing the table
        /// </summary>
        /// <param name="disposing">True if disposing</param>
        protected virtual void Dispose(bool disposing)
        {
            if (!this.isDisposed)
            {
                if (disposing)
                {
                    this.Disconnect();
                }
            }
        }
    }
}
