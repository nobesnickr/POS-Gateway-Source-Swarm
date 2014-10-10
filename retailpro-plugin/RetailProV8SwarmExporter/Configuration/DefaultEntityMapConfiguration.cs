//-----------------------------------------------------------------------
// <copyright file="DefaultEntityMapConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 10. 26. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Default entity map configuration.
    /// </summary>
    ////    [Export(typeof(IEntityMapConfiguration))]
    public class DefaultEntityMapConfiguration : IEntityMapConfiguration
    {
        /// <summary>
        /// The entity mapping.
        /// </summary>
        private static Dictionary<string, Dictionary<RDA2.FieldIDs, string>> map = new Dictionary<string, Dictionary<RDA2.FieldIDs, string>>();

        /// <summary>
        /// Initializes static members of the <see cref="DefaultEntityMapConfiguration"/> class.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1810:InitializeReferenceTypeStaticFieldsInline", Justification = "Initializer would be too big.")]
        static DefaultEntityMapConfiguration()
        {
            CreateConsumerMap();
            CreateInvoiceItemMap();
            CreateDepartmentMap();
            CreateProductMap();
            CreateInvoiceMap();
            CreateStoreMap();
        }

        /// <summary>
        /// Gets the <see cref="Dictionary{RDA2.FieldIDsSystem.String}"/> for the specified entity name.
        /// </summary>
        /// <param name="entityName">Name of the entity.</param>
        /// <returns>The map for the requested entity.</returns>
        public Dictionary<RDA2.FieldIDs, string> this[string entityName]
        {
            get { return map[entityName]; }
        }

        /// <summary>
        /// Loads the remote configuration.
        /// </summary>
        /// <returns>The task for this operation.</returns>
        public System.Threading.Tasks.Task LoadConfiguration()
        {
            return System.Threading.Tasks.Task.Factory.StartNew(() => { });
        }

        /// <summary>
        /// Creates the store map.
        /// </summary>
        private static void CreateStoreMap()
        {
            map.Add(
                "Store",
                new Dictionary<RDA2.FieldIDs, string>()
                            {                                                                                  
                            });
        }

        /// <summary>
        /// Creates the invoice map.
        /// </summary>
        private static void CreateInvoiceMap()
        {
            map.Add(
                "Invoice",
                new Dictionary<RDA2.FieldIDs, string>()
                {
                    { RDA2.FieldIDs.fidDocPostDate, "CreatedDate" },
                    { RDA2.FieldIDs.fidInvcNum, "InvoiceNo" },                    
                    { RDA2.FieldIDs.fidTotal, "Total" },                    
                });
        }

        /// <summary>
        /// Creates the product map.
        /// </summary>
        private static void CreateProductMap()
        {
            map.Add(
                "Product",
                new Dictionary<RDA2.FieldIDs, string>()
                {                    
                    { RDA2.FieldIDs.fidScanUPC, "Upc" },
                    { RDA2.FieldIDs.fidALU, "Sku" },                    
                    { RDA2.FieldIDs.fidDesc1, "Desc" },                    
                    { RDA2.FieldIDs.fidDocItmCost, "Price" },                    
                });
        }

        /// <summary>
        /// Creates the department map.
        /// </summary>
        private static void CreateDepartmentMap()
        {
            map.Add(
                "Department",
                new Dictionary<RDA2.FieldIDs, string>()
                {
                    { RDA2.FieldIDs.fidDC, "DepartmentCode" },
                    { RDA2.FieldIDs.fidDeptName, "DepartmentName" },                    
                });
        }

        /// <summary>
        /// Creates the invoice item map.
        /// </summary>
        private static void CreateInvoiceItemMap()
        {
            map.Add(
                "InvoiceItem",
                new Dictionary<RDA2.FieldIDs, string>()
                {
                    { RDA2.FieldIDs.fidItemSID, "ProductSid" },
                    { RDA2.FieldIDs.fidFC1TaxAmt, "TaxAmt" },
                    { RDA2.FieldIDs.fidQTY, "Quantity" },
                });
        }

        /// <summary>
        /// Creates the consumer map.
        /// </summary>
        private static void CreateConsumerMap()
        {
            var customerMap = new Dictionary<RDA2.FieldIDs, string>()
            {
                { RDA2.FieldIDs.fidCustAddr1, "Address1" },
                { RDA2.FieldIDs.fidCustAddr2, "Address2" },
                { RDA2.FieldIDs.fidCustAddr3, "Address3" },                
                { RDA2.FieldIDs.fidBillToEMail, "Email" },
                { RDA2.FieldIDs.fidBillToCustFName, "FirstName" },
                { RDA2.FieldIDs.fidBillToCustLName, "LastName" },
                { RDA2.FieldIDs.fidBillToPhone1, "Phone1" },
                { RDA2.FieldIDs.fidBillToPhone2, "Phone2" },
                { RDA2.FieldIDs.fidCustZIP, "Zip" },                
            };
            map.Add("Customer", customerMap);
        }      
    }
}
