//-----------------------------------------------------------------------
// <copyright file="ModelMapperExtensions.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 11. 06. 10:57</date>
//---------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using RetailProV8SwarmExporter.Model;

    /// <summary>
    /// Model mapper extensions extensions.
    /// </summary>
    public static class ModelMapperExtensions
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Using a Retail Pro document from a table, get InvoiceLine model object
        /// </summary>
        /// <param name="mapper">The mapper.</param>
        /// <param name="document">The RDA2 document to extract information from</param>
        /// <param name="customerTable">The customer accessor to access the customer's information</param>
        /// <param name="sbsNumber">The SBS number of to store to be used for this invoice</param>
        /// <param name="invoice">Returns the correctly fecthed invoice or null</param>
        /// <returns>
        /// True of sucessful, false otherwise
        /// </returns>
        public static bool TryGetInvoiceObject(this IRetailProModelMapper mapper, IRetailProDocument document, IRetailProTable customerTable, long sbsNumber, out Invoice invoice)
        {
            if (mapper == null)
            {
                throw new ArgumentNullException("mapper");
            }

            try
            {
                invoice = mapper.GetInvoiceObject(document, customerTable, sbsNumber);
                return true;
            }
            catch (RetailProExtractorException e)
            {
                Logger.WarnException("Failed to map Retail Pro document to Invoice: " + document, e);
                invoice = null;
            }

            return false;
        }

        /// <summary>
        /// Map document to Store, handle exception if rises
        /// </summary>
        /// <param name="mapper">The mapper.</param>
        /// <param name="document">Document containing an Invoice</param>
        /// <param name="sbsNumber">Sbs number of the store group</param>
        /// <param name="store">The correctly mapped store or null</param>
        /// <returns>
        /// True if succesfull, false otherwise
        /// </returns>
        public static bool TryGetStoreObject(this IRetailProModelMapper mapper, IRetailProDocument document, long sbsNumber, out Store store)
        {
            if (mapper == null)
            {
                throw new ArgumentNullException("mapper");
            }

            try
            {
                store = mapper.GetStoreObject(document, sbsNumber);
                return true;
            }
            catch (RetailProExtractorException e)
            {
                Logger.WarnException("Extraction problem while trying to extracting store from invoices", e);
                store = null;
            }

            return false;
        }
    }
}
