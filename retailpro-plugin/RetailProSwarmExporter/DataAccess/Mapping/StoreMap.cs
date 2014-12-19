//-----------------------------------------------------------------------
// <copyright file="StoreMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 26. 11:56</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.DataAccess.Mapping
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using FluentNHibernate.Mapping;
    using RetailProCommon.Model;

    /// <summary>
    /// NHibernate mapping for store entity.
    /// </summary>
    public class StoreMap : ClassMap<Store>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="StoreMap"/> class.
        /// </summary>
        public StoreMap()
        {
            this.Table("STORE_V");
            this.CompositeId()
                .KeyProperty(s => s.SbsNumber, "SBS_NO")
                .KeyProperty(s => s.StoreNumber, "STORE_NO");
            this.Map(s => s.StoreName, "STORE_NAME");
            this.Map(s => s.Location, "ZIP");
            this.Map(s => s.ModifiedDate, "MODIFIED_DATE");
            this.Map(s => s.StoreCode, "STORE_CODE");
            this.Map(s => s.Active, "ACTIVE");
        }
    }
}
