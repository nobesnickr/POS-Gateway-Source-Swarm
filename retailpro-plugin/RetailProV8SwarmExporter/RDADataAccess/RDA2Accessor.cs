//-----------------------------------------------------------------------
// <copyright file="RDA2Accessor.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 16:20</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.RDADataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Abstract base class responsible for accessing data within RDA2, 
    /// by providing a tool to open and iterate over entires in RDA2 tables
    /// </summary>
    public abstract class RDA2Accessor : IRetailProAccessor
    {
        /// <summary>
        /// Gets or sets the document of the RDA2 interface for this particular instance and this particular state
        /// </summary>
        public RDA2.IRdaDocument CurrentDocument { get; protected set; }

        /// <summary>
        /// Gets the current position of the iteration, may be -1 for "Unknown"
        /// </summary>
        public abstract int Index { get; }

        /// <summary>
        /// Gets a value indicating whether there are any more items to iterate over
        /// False if there are none
        /// </summary>
        public abstract bool Last { get; }

        /// <summary>
        /// Iterates the accessor to the next data item
        /// </summary>
        public abstract void Iterate();

        /// <summary>
        /// Reads the current data item's fields
        /// </summary>
        /// <param name="fieldId">Specifies which field is requested</param>
        /// <returns>String value of the field</returns>
        public string GetString(int fieldId)
        {
            RDA2.IRdaField field = this.CurrentDocument.FieldByID(fieldId);
            if (field == null || field.Value == null)
            {
                return string.Empty;
            }

            return field.Value.ToString();
        }

        /// <summary>
        /// Reads the current data item's specified field and converts it to Datetime
        /// </summary>
        /// <param name="fieldId">The RDA2 fid property</param>
        /// <returns>DateTime value of the field</returns>
        public DateTime GetDateTime(int fieldId)
        {
            DateTime retval;
            RDA2.IRdaField field = this.CurrentDocument.FieldByID(fieldId);
            if (field == null || field.Value == null)
            {
                throw new RDA2Exception(string.Format("Datetime property not found"));
            }

            string stringValue = field.Value.ToString();
            if (!DateTime.TryParse(stringValue, out retval))
            {
                throw new RDA2Exception(string.Format("Failed to read {0} as datetime", stringValue));
            }

            return retval;
        }

        /// <summary>
        /// Debug tool for printing all fields and values for the currentDocument
        /// </summary>
        /// <returns>The list of all field names and their values for the current item</returns>
        public override string ToString()
        {
            string retval = string.Empty;
            RDA2.IRdaCollection c = this.CurrentDocument.AllFieldNames;
            foreach (string s in c)
            {
                if (this.CurrentDocument.FieldByName(s).Value == null)
                {
                    retval += "[" + s + ": NULL]";
                }
                else
                {
                    retval += "[" + s + ": " + this.CurrentDocument.FieldByName(s).Value.ToString() + "]";
                }
            }

            return retval;
        }
    }
}
