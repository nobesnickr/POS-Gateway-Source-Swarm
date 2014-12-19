//-----------------------------------------------------------------------
// <copyright file="RDA2Table.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Runtime.InteropServices;
    using System.Text;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// RDA2Table provides access to the Retail Pro database using RDA2 functions
    /// </summary>
    public class RDA2Table : IRetailProTable
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Value indicating whether object is disposed
        /// </summary>
        private bool isDisposed = false;

        /// <summary>
        /// Gets or sets the local configuration containing the ignore filter
        /// </summary>
        private IV8AppConfiguration localConfiguration;

        /// <summary>
        /// The current document of the table
        /// </summary>
        private RDA2.IRdaDocument currentDocument;
        
        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Table"/> class
        /// Indexed tables of RDA2 derive from the RDA2.IRdaTable interface
        /// </summary>
        /// <param name="table">The RDA2 table this accessor provides access to</param>
        /// <param name="localConfiguration">Local configuration to access date limitation data</param>
        /// <param name="name">Name of the table used for better readability of the logs</param>
        public RDA2Table(RDA2.IRdaTable table, IV8AppConfiguration localConfiguration, string name)
        {
            this.Table = table;
            this.localConfiguration = localConfiguration;
            this.Name = name;
        }

        /// <summary>
        /// Gets a value indicating whether if there are no more items to iterate over
        /// </summary>
        public bool Last { get; private set; }

        /// <summary>
        /// Gets the current document of the iteration
        /// </summary>
        public IEnumerable<IRetailProDocument> Items
        {
            get
            {
                int index = 0;
                while (!this.Last)
                {
                    var document = new RDA2Document(this.currentDocument, index++);
                    Logger.Trace(System.Globalization.CultureInfo.InvariantCulture, "Iteration over {0} is at {1}: {2}", this.Name, index, document);
                    yield return document;
                    this.Iterate();
                }
            }
        }

        /// <summary>
        /// Gets or sets the indexing field of the accessor, items
        /// will be ordered accordingly to this field
        /// </summary>
        /// <seealso cref="Forward"/>
        public int ActiveIndex
        {
            get { return this.Table.ActiveIndexID; }
            set { this.Table.ActiveIndexID = value; }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the direction of iteration when using the Iterate inherited from IRetailProAccessor
        /// </summary>
        /// <seealso cref="ActiveIndex"/>
        public bool Forward { get; set; }

        /// <summary>
        /// Gets the indexed tables of RDA2 derive from the RDA2.IRdaTable interface
        /// </summary>
        protected RDA2.IRdaTable Table { get; private set; }

        /// <summary>
        /// Gets or sets the name of the table used for better readability of the code
        /// </summary>
        private string Name { get; set; }

        /// <summary>
        /// Close the table, finalize the iteration
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Info(System.IFormatProvider,System.String,System.String)", Justification = "Reviewed.")]
        public void Close()
        {
            this.Table.Close();
            this.Last = true;
            this.currentDocument = null;

            Logger.Info(System.Globalization.CultureInfo.InvariantCulture, "Closing table {0}", this.Name);
        }

        /// <summary>
        /// Opens table for the first time
        /// </summary>
        public void Open()
        {
            // If needed override table history
            if (this.localConfiguration.OverrideTableHistory)
            {
                this.DoOverrideTableHistory();
            }

            this.ReOpen();
        }

        /// <summary>
        /// Move to current token position based on a search token
        /// </summary>
        /// <param name="value">The token to search for, e.g. productSID</param>
        /// <returns>
        /// Null of not found, the item otherwise
        /// </returns>  
        public IRetailProDocument Find(string value)
        {
            /* Quoting documentation:
             * In general, ExactMatch should be set to False when using this method. The actual structure of the
             * RPro index keys makes it difficult to construct a FindValue that will exactly match an RPro key.
             */
            if (!this.Table.Find(value, false))
            {
                return null;
            }

            return new RDA2Document(this.currentDocument);
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
                    this.Close();
                }
            }
        }

        /// <summary>
        /// Open the table, reset the iteration
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Debug(System.IFormatProvider,System.String,System.String)", Justification = "Reviewed.")]
        private void ReOpen()
        {
            Logger.Info("Preparing to open table {0}", this.Name);

            this.Table.Open();
            this.Last = false;

            if (this.Forward)
            {
                this.Table.First();
                if (this.Table.Eof)
                {
                    Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, "Opening table and iterating to first element for {0}", this.Name);
                    this.Iterate();
                }
            }
            else
            {
                this.Table.Last();
                if (this.Table.Bof)
                {
                    Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, "Opening table and iterating to last element for {0}", this.Name);
                    this.Iterate();
                }
            }

            this.currentDocument = this.Table.Document();

            Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, "Opened table {0}", this.Name);
        }

        /// <summary>
        /// Iterating using the ActiveIndex and the Forward property
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2202:Do not dispose objects multiple times", Justification = "The table will be opened in again.")]
        private void Iterate()
        {
            if (this.Forward)
            {
                this.Table.Next();
                while (this.Table.Eof)
                {
                    if (this.Table.History != null)
                    {
                        try
                        {
                            int month = this.Table.History.Month + 1;
                            int year = this.Table.History.Year;
                            this.Close();
                            if (month > 12)
                            {
                                month = 1;
                                year = year + 1;
                            }

                            if (year > DateTime.Now.Year || (year == DateTime.Now.Year && month > DateTime.Now.Month))
                            {
                                Logger.Debug("Iteration reached the future: {0}/{1} for {2}", year, month, this.Name);
                                return;
                            }
                            else
                            {
                                Logger.Trace("Setting Table history to {0}/{1} for {2}", year, month, this.Name);
                                this.Table.History.SetMonthYear(month, year);
                                this.ReOpen();
                            }
                        }
                        catch (COMException e)
                        {
                            Logger.WarnException(
                                string.Format(System.Globalization.CultureInfo.InvariantCulture, "Failed to open table {0} ({1}/{2}) iterating forward", this.Table.Name, this.Table.History.Year, this.Table.History.Month),
                                e);
                        }
                    }
                }
            }
            else
            {
                this.Table.Prior();
                while (this.Table.Bof)
                {
                    if (this.Table.History != null)
                    {
                        try
                        {
                            int month = this.Table.History.Month - 1;
                            int year = this.Table.History.Year;
                            this.Close();
                            if (month < 1)
                            {
                                month = 12;
                                year = year - 1;
                            }

                            if (year < 1900)
                            {
                                return;
                            }

                            if (year > this.localConfiguration.IgnoreEarlierInvoicesFilter.Year || (year == this.localConfiguration.IgnoreEarlierInvoicesFilter.Year && month >= this.localConfiguration.IgnoreEarlierInvoicesFilter.Month))
                            {
                                Logger.Trace("Opening new data file for {0}/{1} for {2}", year, month, this.Name);
                                this.Table.History.SetMonthYear(month, year);
                                this.ReOpen();
                            }
                            else
                            {
                                Logger.Debug("Iteration reached the ignore limit {0}/{1}", year, month);
                                return;
                            }
                        }
                        catch (COMException e)
                        {
                            Logger.WarnException(
                                string.Format(System.Globalization.CultureInfo.InvariantCulture, "Failed to open table {0} ({1}/{2}) iterating backwards", this.Table.Name, this.Table.History.Year, this.Table.History.Month),
                                e);
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Override history value to the assumed values
        /// </summary>
        private void DoOverrideTableHistory()
        {
            if (this.Table == null)
            {
                Logger.Warn("Table is null");
                return;
            }

            if (this.Table.History == null)
            {
                Logger.Warn("No history for {0}", this.Table.Name);
                return;
            }

            if (this.Forward)
            {
                var newValue = this.localConfiguration.IgnoreEarlierInvoicesFilter;

                // By default the Table.History should point to an old date, 
                // but this can be forcefully overridden
                const string Message = "Overriding table history value from {0}/{1} to {2}/{3} while iterating forward";
                Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, Message, this.Table.History.Year, this.Table.History.Month, newValue.Year, newValue.Month);

                this.Table.History.SetMonthYear(newValue.Month, newValue.Year);
            }
            else
            {
                // By default the Table.History should point to the current date, 
                // but this can be forcefully overridden
                const string Message = "Overriding table history value from {0}/{1} to {2}/{3} while iterating backwards";
                Logger.Debug(System.Globalization.CultureInfo.InvariantCulture, Message, this.Table.History.Year, this.Table.History.Month, DateTime.Now.Year, DateTime.Now.Month);

                this.Table.History.SetMonthYear(DateTime.Now.Month, DateTime.Now.Year);
            }
        }
    }
}
