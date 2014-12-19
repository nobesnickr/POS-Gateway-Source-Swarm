// <copyright file="RDA2Exception.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 16. 16:09</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess.RDA
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using RetailProV8SwarmExporter.DataAccess;

    /// <summary>
    /// Exception thrown when a problem occurs while accessing the RDA2 interface
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1032:ImplementStandardExceptionConstructors", Justification = "Exceptions without message parameter are not allowed.")]
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2237:MarkISerializableTypesWithSerializable", Justification = "This exception will not be serialized.")]
    public class RDA2Exception : RetailProExtractorException
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Exception"/> class
        /// Exception thrown with given error indication message
        /// </summary>
        /// <param name="message">The message explaining the error that occured during the extraction</param>
        public RDA2Exception(string message) : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="RDA2Exception"/> class
        /// Exception thrown with given error indication message
        /// </summary>
        /// <param name="message">The message explaining the error that occured during the extraction</param>
        /// <param name="innerException">The source exception that triggered this exception to be thrown, e.g. an RDA2Exception</param>
        public RDA2Exception(string message, Exception innerException)
            : base(message, innerException)
        {
        }
    }
}
