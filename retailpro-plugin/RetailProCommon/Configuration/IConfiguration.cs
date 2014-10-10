//-----------------------------------------------------------------------
// <copyright file="IConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 12:07</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Interface for getting and setting configuration values.
    /// </summary>
    public interface IConfiguration
    {
        /// <summary>
        /// Gets or sets the last modified invoice date.
        /// </summary>
        DateDictionary LastModifiedInvoiceDate { get; set; }

        /// <summary>
        /// Gets or sets the last modified version date.
        /// </summary>
        DateTime LastModifiedVersionDate { get; set; }

        /// <summary>
        /// Gets or sets the last modified store date.
        /// </summary>
        DateTime LastModifiedStoreDate { get; set; }

        /// <summary>
        /// Loads the remote configurations.
        /// </summary>
        /// <returns>The async task for the operation.</returns>
        Task LoadRemoteConfiguration();
    }
}
