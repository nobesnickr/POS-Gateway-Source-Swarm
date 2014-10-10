//-----------------------------------------------------------------------
// <copyright file="DiagnosticResult.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>tamas-tfs</author>
// <date>2014. 2. 26. 14:02</date>
//-----------------------------------------------------------------------

namespace DiagnosticLibrary
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// The diagnostical result class
    /// </summary>
    public class DiagnosticResult
    {
        /// <summary>
        /// Gets or sets a value indicating whether the diagnostic was succesful
        /// </summary>
        public bool IsSuccess { get; set; }

        /// <summary>
        /// Gets or sets of the found protocol's name
        /// </summary>
        public string ProtocolName { get; set; }

        /// <summary>
        /// Gets or sets of the error which occured during diagnostic
        /// </summary>
        public Exception LastError { get; set; }
    }
}
