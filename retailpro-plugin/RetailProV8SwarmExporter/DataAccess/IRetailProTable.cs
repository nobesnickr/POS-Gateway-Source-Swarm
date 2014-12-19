//-----------------------------------------------------------------------
// <copyright file="IRetailProTable.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 9. 13. 11:43</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Runtime.InteropServices;
    using System.Text;

    /// <summary>
    /// Interface carrying functionality of a IRDATable, e.g. opening table, closing table,
    /// setting active index, searching within table
    /// </summary>
    public interface IRetailProTable : IDisposable
    {
        /// <summary>
        /// Gets or sets the indexing field of the accessor, items
        /// will be ordered accordingly to this field
        /// </summary>
        /// <seealso cref="Forward"/>
        int ActiveIndex { get; set; }

        /// <summary>
        /// Gets or sets a value indicating whether the direction of iteration when using the Iterate inherited from IRetailProAccessor
        /// </summary>
        /// <seealso cref="ActiveIndex"/>
        bool Forward { get; set; }

        /// <summary>
        /// Gets the enumerator the access data
        /// </summary>
        IEnumerable<IRetailProDocument> Items { get; }

        /// <summary>
        /// Open the table, reset the itartion
        /// </summary>
        void Open();

        /// <summary>
        /// Close the table, finalize the iteration
        /// </summary>
        void Close();

        /// <summary>
        /// Move to current token position based on a search token
        /// </summary>
        /// <param name="value">The token to search for, e.g. productSID</param>
        /// <returns>
        /// True if the searched item was found, false otherwise
        /// </returns>  
        IRetailProDocument Find(string value);
    }
}
