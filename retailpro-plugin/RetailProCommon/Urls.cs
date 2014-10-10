//-----------------------------------------------------------------------
// <copyright file="Urls.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 29. 11:58</date>
//-----------------------------------------------------------------------
namespace RetailProCommon
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Static class, which contains all used urls.
    /// </summary>
    public static class Urls
    {
        /// <summary>
        /// Url constants
        /// </summary>
        public const string
            ConfigPrefix    = "config/",
            UploadPrefix    = "items/",
            InvoiceUpload   = UploadPrefix + "invoice",
            StoreUpload     = UploadPrefix + "store",
            HeartbeatUpload = UploadPrefix + "heartbeat",
            VersionUpload   = UploadPrefix + "version",
            Mapping         = ConfigPrefix + "mapping",
            RemoteConfig    = ConfigPrefix + "lastdate",            
            LogConfig       = ConfigPrefix + "log",
            LogUpload       = "log";
    }
}
