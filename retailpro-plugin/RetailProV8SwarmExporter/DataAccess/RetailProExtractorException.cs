// <copyright file="RetailProExtractorException.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2013. 8. 16. 16:09</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;

    /// <summary>
    /// Exception thrown when a problem occurs while trying the extract data from Retail Pro v8
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2237:MarkISerializableTypesWithSerializable", Justification = "This exception will not be serialized")]
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1032:ImplementStandardExceptionConstructors", Justification = "No need for those parameters.")]
    public class RetailProExtractorException : Exception
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="RetailProExtractorException"/> class
        /// Exception thrown with given error indication message
        /// </summary>
        /// <param name="message">The message explaining the error that occured during the extraction</param>
        public RetailProExtractorException(string message) : base(message)
        {
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="RetailProExtractorException"/> class
        /// Exception thrown with given error indication message
        /// </summary>
        /// <param name="message">The message explaining the error that occured during the extraction</param>
        /// <param name="sourceException">The source exception that triggered this exception to be thrown, e.g. an RDA2Exception</param>
        public RetailProExtractorException(string message, Exception sourceException)
            : base(message, sourceException)
        {
        }
    }
}
