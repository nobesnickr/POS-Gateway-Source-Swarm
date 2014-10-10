//-----------------------------------------------------------------------
// <copyright file="TestHelper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 10:57</date>
//---------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using Moq;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Static helper for unit tests.
    /// </summary>
    public static class TestHelper
    {
        /// <summary>
        /// Setups the Items properti for the given table in the given extractor with the given documents..
        /// </summary>
        /// <param name="extractor">The extractor.</param>
        /// <param name="table">The table.</param>
        /// <param name="documents">The documents.</param>
        public static void SetupItems(this Mock<IRetailProExtractor> extractor, string table, params Mock<IRetailProDocument>[] documents)
        {
            extractor.Setup(e => e.OpenTable(table).Items).Returns(documents.Select(d => d.Object));
        }

        /// <summary>
        /// Sets up the given document property.
        /// </summary>
        /// <param name="document">The document.</param>
        /// <param name="field">The field.</param>
        /// <param name="value">The value.</param>
        /// <returns>The given document.</returns>
        public static Mock<IRetailProDocument> DocumentProp(this Mock<IRetailProDocument> document, RDA2.FieldIDs field, string value)
        {
            document.Setup(d => d.GetString(field)).Returns(value);
            return document;
        }

        /// <summary>
        /// Sets up the given document property.
        /// </summary>
        /// <param name="document">The document.</param>
        /// <param name="field">The field.</param>
        /// <param name="value">The value.</param>
        /// <returns>The given document.</returns>
        public static Mock<IRetailProDocument> DocumentProp(this Mock<IRetailProDocument> document, RDA2.FieldIDs field, DateTime value)
        {
            document.Setup(d => d.GetDateTime(field)).Returns(value);
            return document;
        }
    }
}
