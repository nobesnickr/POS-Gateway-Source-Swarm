//-----------------------------------------------------------------------
// <copyright file="ProductMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:22</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.DataAccess.Mapping
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Text;
    using FluentNHibernate.Mapping;
    using RetailProCommon.Model;

    /// <summary>
    /// Fluent NHibernate map for the Product entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class ProductMap : ClassMap<Product>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ProductMap"/> class.
        /// </summary>
        public ProductMap()
        {
            this.Table("inventory_v");
            this.Id(p => p.ItemSid, "ITEM_SID");
            this.Map(p => p.Cost, "COST");
            this.Map(p => p.SbsNumber, "SBS_NO");
            this.Map(p => p.Upc, "UPC");
            this.Map(p => p.Description1, "DESCRIPTION1");

            this.HasMany(p => p.SkuList)
                .Inverse()
                .KeyColumn("ITEM_SID");
        }
    }

        /// <summary>
    /// Fluent NHibernate map for the Product entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class StockKeepingUnitMap : ClassMap<StockKeepingUnit>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="StockKeepingUnitMap"/> class.
        /// </summary>
        public StockKeepingUnitMap()
        {
            this.Table("PI_SCAN_GOOD_V");
            this.CompositeId()
                .KeyProperty(sku => sku.ItemSid, "ITEM_SID")
                .KeyProperty(sku => sku.SheetId, "SHEET_ID");
            this.Map(sku => sku.Value, "SKU");
        }
    }
}
