//-----------------------------------------------------------------------
// <copyright file="EntryAssemblyAppVersion.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 9. 18. 10:29</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.BusinessLogic
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Gets the application version from the assembly version.
    /// </summary>
    [Export(typeof(IApplicationVersion))]
    [PartCreationPolicy(System.ComponentModel.Composition.CreationPolicy.Shared)]
    public class EntryAssemblyAppVersion : IApplicationVersion
    {
        /// <summary>
        /// Reference to the logger
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// The application version
        /// </summary>
        private string version;

        /// <summary>
        /// Gets the application version.
        /// </summary>
        public string Version 
        {
            get
            {
                if (this.version == null)
                {
                    try
                    {
                        this.version = System.Reflection.Assembly.GetEntryAssembly().GetName().Version.ToString();
                    }
                    catch (Exception exc)
                    {
                        Logger.ErrorException("Unable to get version using entry assembly", exc);
                        this.version = "Unable to get version using entry assembly";
                    }
                }

                return this.version;
            }
        }
    }
}
