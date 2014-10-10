//-----------------------------------------------------------------------
// <copyright file="IApplicationVersion.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 18. 10:29</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Interface for returning the application version.
    /// </summary>
    public interface IApplicationVersion
    {
        /// <summary>
        /// Gets the application version.
        /// </summary>
        string Version { get; }
    }
}
