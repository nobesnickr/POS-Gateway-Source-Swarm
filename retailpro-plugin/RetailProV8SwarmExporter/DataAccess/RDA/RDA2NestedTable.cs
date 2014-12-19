//-----------------------------------------------------------------------
// <copyright file="RDA2NestedTable.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 21. 16:20</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Class for accessing nested tables (such as invoice lines)
    /// </summary>
    public sealed class RDA2NestedTable : IRetailProTable
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// For iteration the current index of the position token 
        /// </summary>
        private int index;
        
        /// <summary>
        /// The RDA2 nested document this accessor provides access to
        /// </summary>
        private RDA2.IRdaDocument nestedDocument = null;

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2NestedTable"/> class
        /// RDA2 nested tables are derived from RDA2.IRdaDocuments
        /// </summary>
        /// <param name="nestedDocument">The RDA2 nested document this accessor provides access to</param>
        public RDA2NestedTable(RDA2.IRdaDocument nestedDocument) 
        {
            if (nestedDocument == null)
            {
                throw new ArgumentNullException("nestedDocument");
            }

            this.nestedDocument = nestedDocument;
            this.nestedDocument.SetPosition(0);
        }

        /// <summary>
        /// Gets the current document of the iteration
        /// </summary>
        public IEnumerable<IRetailProDocument> Items
        {
            get
            {
                Logger.Trace("Nested document contains: " + this.nestedDocument.Count + " items");
                while (this.index < this.nestedDocument.Count)
                {
                    this.nestedDocument.SetPosition(this.index);

                    yield return new RDA2Document(this.nestedDocument, this.index);
                    this.index++;
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
            get
            {
                throw new InvalidOperationException("Nested tables don't support Active Index");
            }

            set
            {
                throw new InvalidOperationException("Nested tables don't support Active Index");
            }
        }

        /// <summary>
        /// Gets or sets a value indicating whether the direction of iteration when using the Iterate inherited from IRetailProAccessor
        /// </summary>
        /// <seealso cref="ActiveIndex"/>
        public bool Forward 
        {
            get
            {
                return true;
            }

            set
            {
                if (value == false)
                {
                    throw new InvalidOperationException("Nested tables don't support iterating backwards");
                }
            }
        }

        /// <summary>
        /// Open the table, reset the itartion
        /// </summary>
        public void Open()
        {
            this.index = 0;
            this.nestedDocument.SetPosition(this.index);
        }

        /// <summary>
        /// Close the table, finalize the iteration
        /// </summary>
        public void Close()
        {
        }

        /// <summary>
        /// Disposes the object
        /// </summary>
        public void Dispose()
        {
        }

        /// <summary>
        /// Move to current token position based on a search token
        /// </summary>
        /// <param name="value">The token to search for, e.g. productSID</param>
        /// <returns>
        /// True if the searched item was found, false otherwise
        /// </returns>  
        public IRetailProDocument Find(string value)
        {
            throw new InvalidOperationException("Nested tables don't support searching");
        }
    }
}
