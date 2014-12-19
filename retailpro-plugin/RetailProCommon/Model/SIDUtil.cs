//-----------------------------------------------------------------------
// <copyright file="SIDUtil.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2014. 9. 15. 15:05</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Globalization;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// Class with some utility functions for managing SID values
    /// </summary>
    public class SIDUtil
    {
        /// <summary>
        /// Takes a 64-bit SID value, shifts out (loosing high bits) and adds the addedValue to the lower bits
        /// </summary>
        /// <param name="value">SID value</param>
        /// <param name="addedValue">Value to be added</param>
        /// <returns>64-bit SID value as a string</returns>
        public static string ShiftAndTruncateSID(string value, int addedValue)
        {
            var decimalValue = decimal.Parse(value, CultureInfo.InvariantCulture);

            // Original code was: <code>return (this.InvoiceSid << 10) + this.ItemPosition;</code>
            var computedValue = (decimalValue * (1 << 10)) + addedValue;

            return (computedValue % (1L << 63)).ToString();
        }
    }
}
