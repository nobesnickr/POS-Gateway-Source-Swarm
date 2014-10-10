//-----------------------------------------------------------------------
// <copyright file="ParseHelper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 26. 17:05</date>
//-----------------------------------------------------------------------
namespace RetailProV8SwarmExporter
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Static helper class for parsing strings.
    /// </summary>
    public static class ParseHelper
    {
        /// <summary>
        /// The logger
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Parses a long.
        /// </summary>
        /// <param name="s">The string to parse.</param>
        /// <param name="defaultValue">The default value.</param>
        /// <returns>The parsed long.</returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1026:DefaultParametersShouldNotBeUsed", Justification = "Reviewed.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Warn(System.IFormatProvider,System.String,System.String)", Justification = "Reviewed.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1720:IdentifiersShouldNotContainTypeNames", MessageId = "long", Justification = "Reviewed.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "s", Justification = "Reviewed.")]
        public static long ParseLong(string s, long defaultValue = 0)
        {
            var parsed = defaultValue;
            if (!long.TryParse(s, out parsed))
            {
                Logger.Warn(CultureInfo.InvariantCulture, "Failed to parse '{s}' as long", s);
            }

            return parsed;
        }

        /// <summary>
        /// Parses a date time.
        /// </summary>
        /// <param name="s">The string to parse.</param>
        /// <param name="defaultValue">The default value.</param>
        /// <returns>The parsed date time.</returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1026:DefaultParametersShouldNotBeUsed", Justification = "Reviewed.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals as localized parameters", MessageId = "NLog.Logger.Warn(System.IFormatProvider,System.String,System.String)", Justification = "String will be localized")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Naming", "CA1704:IdentifiersShouldBeSpelledCorrectly", MessageId = "s", Justification = "Reviewed.")]
        public static DateTime ParseDateTime(string s, DateTime? defaultValue = null)
        {
            var parsed = defaultValue ?? new DateTime();
            if (!DateTime.TryParse(s, out parsed))
            {
                Logger.Warn(CultureInfo.InvariantCulture, "Failed to parse '{s}' as long", s);
            }

            return parsed;
        }
    }
}
