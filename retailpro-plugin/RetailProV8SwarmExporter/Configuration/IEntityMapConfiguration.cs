//-----------------------------------------------------------------------
// <copyright file="IEntityMapConfiguration.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 26. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.Configuration
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Interface responsible for providing the entity map configuration.
    /// </summary>
    public interface IEntityMapConfiguration
    {
        /// <summary>
        /// Gets the <see cref="Dictionary{RDA2.FieldIDsSystem.String}"/> for the specified entity name.
        /// </summary>
        /// <param name="entityName">Name of the entity.</param>
        /// <returns>The map for the requested entity.</returns>
        Dictionary<RDA2.FieldIDs, string> this[string entityName] { get; }

        /// <summary>
        /// Awaitable task for loading the configuration.
        /// </summary>
        /// <returns>The task for this operation.</returns>
        System.Threading.Tasks.Task LoadConfiguration();
    }
}
