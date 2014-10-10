//-----------------------------------------------------------------------
// <copyright file="IRetailProExtractor.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 16:29</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Interface for classes capable of extracting data from Retail Pro v8
    /// </summary>
    public interface IRetailProExtractor : IDisposable
    {
        /// <summary>
        /// Gets the SBS number of the Retail Pro installation
        /// </summary>
        long SbsNumber { get; }

        /// <summary>
        /// Opens on of the Retail Pro tables, as all top level tables are indexed, these return
        /// as IRetailProIndexedAccessors
        /// </summary>
        /// <param name="tableName">Tables are identified by their names, e.g. "Invoices", "Items", "Customers"</param>
        /// <returns>Accessor for the table by the given name</returns>
        IRetailProTable OpenTable(string tableName);

        /// <summary>
        /// Initializes the connection
        /// </summary>
        void Connect();
    }
}
