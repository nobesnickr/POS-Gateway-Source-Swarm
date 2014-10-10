//-----------------------------------------------------------------------
// <copyright file="ServiceException.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 25. 14:02</date>
//-----------------------------------------------------------------------
namespace Diagnostic
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Exception class for errors related to service calls.
    /// </summary>
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2237:MarkISerializableTypesWithSerializable", Justification = "This exception will not be serialized")]
    [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1032:ImplementStandardExceptionConstructors", Justification = "No need for those parameters.")]
    public class ServiceException : Exception
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="ServiceException"/> class.
        /// </summary>
        /// <param name="url">The URL.</param>
        /// <param name="httpStatusCode">The HTTP status code.</param>
        public ServiceException(string url, System.Net.HttpStatusCode httpStatusCode)
            : base(FormatMessage(url, httpStatusCode))
        {
            this.Url = url;
            this.HttpStatusCode = httpStatusCode;
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="ServiceException"/> class.
        /// </summary>
        /// <param name="url">The URL.</param>
        /// <param name="innerException">The inner exception.</param>
        public ServiceException(string url, Exception innerException)
            : base(FormatMessage(url), innerException)
        {
            this.Url = url;
        }

        /// <summary>
        /// Gets the URL.
        /// </summary>
        public string Url { get; private set; }

        /// <summary>
        /// Gets the HTTP status code.
        /// </summary>
        public System.Net.HttpStatusCode HttpStatusCode { get; private set; }

        /// <summary>
        /// Formats the message.
        /// </summary>
        /// <param name="url">The URL.</param>
        /// <param name="httpStatusCode">The HTTP status code.</param>
        /// <returns>The formatted message.</returns>
        private static string FormatMessage(string url, System.Net.HttpStatusCode httpStatusCode)
        {
            return string.Format(System.Globalization.CultureInfo.InvariantCulture, "Recieved {0} when connecting to {1}.", httpStatusCode, url);
        }

        /// <summary>
        /// Formats the message.
        /// </summary>
        /// <param name="url">The URL.</param>        
        /// <returns>The formatted message.</returns>
        private static string FormatMessage(string url)
        {
            return string.Format(System.Globalization.CultureInfo.InvariantCulture, "Error when connecting to {0}.", url);
        }
    }
}
