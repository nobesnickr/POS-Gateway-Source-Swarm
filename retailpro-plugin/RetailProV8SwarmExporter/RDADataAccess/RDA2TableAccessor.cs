//-----------------------------------------------------------------------
// <copyright file="RDA2TableAccessor.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.RDADataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProV8SwarmExporter.Configuration;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Abstract class capable of accessing and iterating through data in Retail Pro using the RDA2 COM library
    /// </summary>
    public class RDA2TableAccessor : RDA2Accessor, IRetailProIndexedAccessor
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Will use Next()/Prior() if true/false for iteration
        /// </summary>
        private bool forward = false;

        /// <summary>
        /// Indicates whether table has no more items to be iterating over
        /// </summary>
        private bool last = true;

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2TableAccessor"/> class
        /// Indexed tables of RDA2 derive from the RDA2.IRdaTable interface
        /// </summary>
        /// <param name="table">The RDA2 table this accessor provides access to</param>
        /// <param name="localConfiguration">Local configuration containing the ignore filter</param>
        public RDA2TableAccessor(RDA2.IRdaTable table, ILocalConfiguration localConfiguration)
        {
            this.Table = table;
            this.LocalConfiguration = localConfiguration;
        }

        /// <summary>
        /// Gets the current position of the iteration, may be -1 for "Unknown"
        /// Would provide the current item's index in the iteration, but should not be used
        /// </summary>
        public override int Index
        {
            get { throw new NotSupportedException("Shouldn't rely on the Index property of tables with changing order"); }
        }

        /// <summary>
        /// Gets a value indicating whether if there are no more items to iterate over
        /// </summary>
        public override bool Last
        {
            get { return this.last; }
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
        public bool Forward
        {
            get { return this.forward; }
            set { this.forward = value; }
        }

        /// <summary>
        /// Gets or sets the indexed tables of RDA2 derive from the RDA2.IRdaTable interface
        /// </summary>
        protected RDA2.IRdaTable Table { get; set; }

        /// <summary>
        /// Gets or sets the local configuration containing the ignore filter
        /// </summary>
        private ILocalConfiguration LocalConfiguration { get; set; }

        /// <summary>
        /// Open the table, reset the itartion
        /// </summary>
        public void Open()
        {
            this.Table.Open();
            if (this.forward)
            {
                this.Table.First();
            }
            else
            {
                this.Table.Last();
            }

            this.last = false;
            this.CurrentDocument = this.Table.Document();

            Logger.Trace("Opened table {0}", this.Table.Name);
        }

        /// <summary>
        /// Close the table, finalize the iteration
        /// </summary>
        public void Close()
        {
            if (this.CurrentDocument != null)
            {
                this.Table.Close();
            }
            
            this.last = true;
            this.CurrentDocument = null;

            Logger.Trace("Closing table {0}", this.Table.Name);
        }

        /// <summary>
        /// Iterating using the ActiveIndex and the Forward property
        /// </summary>
        public override void Iterate()
        {
            if (this.forward)
            {
                this.Table.Next();
                while (this.Table.Eof)
                {
                    if (this.Table.History != null)
                    {
                        try
                        {
                            int month = this.Table.History.Month;
                            int year = this.Table.History.Year;
                            this.Close();
                            if (++month > 12)
                            {
                                month = 1;
                                year = year + 1;
                            }

                            if (year > DateTime.Now.Year || (year == DateTime.Now.Year && month > DateTime.Now.Month))
                            {
                                Logger.Trace("Iteration reached to future: {0}/{1}", year, month);
                                return;
                            }
                            else
                            {
                                this.Table.History.SetMonthYear(month, year);
                                this.Open();
                            }
                        }
                        catch (Exception e)
                        {
                            Logger.Trace("Failed to open table {0} ({1}/{2}) iterating forward: {3}", this.Table.Name, this.Table.History.Year, this.Table.History.Month, e.Message);
                            this.Close();
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
                            int month = this.Table.History.Month;
                            int year = this.Table.History.Year;
                            this.Close();
                            if (--month < 1)
                            {
                                month = 12;
                                year = year - 1;
                            }

                            if (year < 1900)
                            {
                                return;
                            }

                            if (year > this.LocalConfiguration.IgnoreEarlierInvoicesFilter.Year || (year == this.LocalConfiguration.IgnoreEarlierInvoicesFilter.Year && month >= this.LocalConfiguration.IgnoreEarlierInvoicesFilter.Month))
                            {
                                this.Table.History.SetMonthYear(month, year);
                                this.Open();
                            }
                            else
                            {
                                Logger.Trace("Iteration reached to ignore limit {0}/{1}", year, month);
                                return;
                            }
                        }
                        catch (Exception e)
                        {
                            Logger.Trace("Failed to open table {0} ({1}/{2}) iterating backwards: {3} -> {4}", this.Table.Name, this.Table.History.Year, this.Table.History.Month, e.Message, e.StackTrace);
                            this.Close();
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Move to current token position based on a search token
        /// </summary>
        /// <param name="value">The token to search for, e.g. productSID</param>
        /// <returns>
        /// True if the searched item was found, false otherwise
        /// </returns>  
        public bool Find(string value)
        {
            /* Quating documentation:
             * In general, ExactMatch should be set to False when using this method. The actual structure of the
             * RPro index keys makes it difficult to construct a FindValue that will exactly match an RPro key.
             */
            return this.Table.Find(value, false);
        }

        /// <summary>
        /// Returns a nested table embeded in this table
        /// </summary>
        /// <param name="tableId">The RDA2 nestedTableID property for this accessor</param>
        /// <returns>The accessor for the nested table, normally a wrapper for an RDA2 Document</returns>
        public IRetailProAccessor GetNestedTable(int tableId)
        {
            return new RDA2NestedTableAccessor(this.CurrentDocument.get_NestedDocByID(tableId));
        }      
    }
}
