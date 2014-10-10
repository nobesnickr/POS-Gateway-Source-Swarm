//-----------------------------------------------------------------------
// <copyright file="RDA2Document.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 9. 21. 16:20</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Class responsible for accessing data within RDA2, 
    /// by providing a tool to open and iterate over entires in RDA2 tables
    /// </summary>
    public class RDA2Document : IRetailProDocument
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Document"/> class.
        /// Document index is set to 0 by default.
        /// </summary>
        /// <param name="document">The document that will provide data for the class</param>
        public RDA2Document(RDA2.IRdaDocument document)
            : this(document, 0)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Document"/> class
        /// </summary>
        /// <param name="document">The document that will provide data for the class</param>
        /// <param name="documentIndex">The index of the RDA2 document</param>
        public RDA2Document(RDA2.IRdaDocument document, int documentIndex)
        {
            if (document == null)
            {
                throw new ArgumentNullException("document");
            }

            this.CurrentDocument = document;
            this.Index = documentIndex;
        }

        /// <summary>
        /// Gets the index of the document within the table, normally an Internal Retail Pro property used when calling SetPosition
        /// </summary>
        public int Index { get; private set; }

        /// <summary>
        /// Gets the document of the RDA2 interface for this particular instance and this particular state
        /// </summary>
        protected RDA2.IRdaDocument CurrentDocument { get; private set; }

        /// <summary>
        /// Reads the current data item's fields
        /// </summary>
        /// <param name="fieldId">Specifies which field is requested</param>
        /// <returns>String value of the field</returns>
        public string GetString(RDA2.FieldIDs fieldId)
        {
            RDA2.IRdaField field = this.CurrentDocument.FieldByID((int)fieldId);
            if (field == null || field.Value == null)
            {
                return string.Empty;
            }

            return field.Value.ToString();
        }

        /// <summary>
        /// Reads the current data item's specified field and converts it to double 
        /// </summary>
        /// <param name="fieldId">The RDA2 fid property</param>
        /// <param name="defaultValue">the Default value if the parse fails</param>
        /// <returns>Double value of the field</returns>
        public double GetDouble(RDA2.FieldIDs fieldId, double defaultValue)
        {
            double result;
            string value = this.GetString(fieldId);
            if (!double.TryParse(value, out result))
            {
                Logger.Warn("Failed to parse Double! field: {0} value: {1}", fieldId, value);
                result = defaultValue;
            }

            return result;
        }

        /// <summary>
        /// Reads the current data item's specified field and converts it to Datetime
        /// </summary>
        /// <param name="fieldId">The RDA2 fid property</param>
        /// <returns>DateTime value of the field</returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA2204:Literals should be spelled correctly", MessageId = "DateTime", Justification = "Spelled correctly.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Warn(System.IFormatProvider,System.String,System.String)", Justification = "Reviewed.")]
        public DateTime GetDateTime(RDA2.FieldIDs fieldId)
        {
            DateTime retval;
            RDA2.IRdaField field = this.CurrentDocument.FieldByID((int)fieldId);
            if (field == null || field.Value == null)
            {
                Logger.Warn(
                    System.Globalization.CultureInfo.InvariantCulture, 
                    "Failed to parse DateTime property {0} as it is NULL", 
                    fieldId.ToString());
            }

            string stringValue = field.Value.ToString();
            if (!DateTime.TryParse(stringValue, out retval))
            {
                Logger.Warn(System.Globalization.CultureInfo.InvariantCulture, "Failed to parse DateTime property {0} as it is {1}", fieldId.ToString(), stringValue);
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

        /// <summary>
        /// Returns a nested table embeded in this table
        /// </summary>
        /// <param name="tableId">The RDA2 nestedTableID property for this accessor</param>
        /// <returns>The accessor for the nested table, normally a wrapper for an RDA2 Document</returns>
        public IRetailProTable GetNestedTable(RDA2.TDBNestedTables tableId)
        {
            return new RDA2NestedTable(this.CurrentDocument.get_NestedDocByID((int)tableId));
        }  
    }
}
