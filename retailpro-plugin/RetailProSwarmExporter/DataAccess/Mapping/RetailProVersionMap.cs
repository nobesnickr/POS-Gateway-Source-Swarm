//-----------------------------------------------------------------------
// <copyright file="RetailProVersionMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 15:22</date>
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
    /// Map for RetailProVersion.
    /// </summary>
    public class RetailProVersionMap : ClassMap<RetailProVersion>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="RetailProVersionMap"/> class.
        /// </summary>
        public RetailProVersionMap()
        {
            this.Table("VERSION_V");
            this.Id(v => v.ComponentId, "COMPONENT_ID");
            this.Map(v => v.ComponentType, "COMPONENT_TYPE");
            this.Map(v => v.UpdatedDate, "UPDATED_DATE");
            this.Map(v => v.InstallDate, "INSTALL_DATE");
            this.Map(v => v.Version);
            this.Map(v => v.Comments, "COMMENTS");
        }
    }
}
