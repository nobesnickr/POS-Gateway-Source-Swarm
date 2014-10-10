//-----------------------------------------------------------------------
// <copyright file="IRetailProDocument.cs" company="Sonrisa">
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
    using System.Text;

    /// <summary>
    /// Interface carrying functionality of a IRDADocument, e.g. opening table, closing table,
    /// setting active index, searching within table
    /// </summary>
    public interface IRetailProDocument
    {
        /// <summary>
        /// Gets the index of the document within the table
        /// </summary>
        int Index { get; }

        /// <summary>
        /// Reads the current data item's fields
        /// </summary>
        /// <param name="fieldId">Specifies which field is requested</param>
        /// <returns>String value of the field</returns>
        string GetString(RDA2.FieldIDs fieldId);

        /// <summary>
        /// Reads the current data item's specified field and converts it to double 
        /// </summary>
        /// <param name="fieldId">The RDA2 fid property</param>
        /// <param name="defaultValue">the Default value if the parse fails</param>
        /// <returns>Double value of the field</returns>
        double GetDouble(RDA2.FieldIDs fieldId, double defaultValue);

        /// <summary>
        /// Reads the current data item's specified field and converts it to Datetime
        /// </summary>
        /// <param name="fieldId">The RDA2 fid property</param>
        /// <returns>DateTime value of the field</returns>
        DateTime GetDateTime(RDA2.FieldIDs fieldId);

        /// <summary>
        /// Returns a nested table embeded in this table
        /// </summary>
        /// <param name="tableId">The RDA2 nestedTableID property for this accessor</param>
        /// <returns>The accessor for the nested table, normally a wrapper for an RDA2 Document</returns>
        IRetailProTable GetNestedTable(RDA2.TDBNestedTables tableId);
    }
}
