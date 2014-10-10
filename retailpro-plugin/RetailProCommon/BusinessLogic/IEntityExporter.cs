//-----------------------------------------------------------------------
// <copyright file="IEntityExporter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:32</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using NHibernate;

    /// <summary>
    /// Interface for exporting recently changed items from retail pro.
    /// </summary>    
    public interface IEntityExporter
    {
        /// <summary>
        /// Exports changed items.
        /// Also stores the appropriate last modified date in the related configuration.
        /// </summary>
        /// <returns>The async task for the export operation.</returns>
        Task ExportChangedItems();
    }
}
