//-----------------------------------------------------------------------
// <copyright file="SIDUtilTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>barna-tfs</author>
// <date>2014. 9. 15. 15:05</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Test
{
    using System;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using RetailProCommon.Model;

    /// <summary>
    /// Unit test for the SID utility class.
    /// </summary>
    [TestClass]
    public class SIDUtilTest
    {
        /// <summary>
        /// Tested method:
        ///   ShiftAndTruncateSID
        /// Tested class:
        ///   SIDUtil
        /// Description:
        ///   Tests that new shift implementation matches Retail Pro 8 implementation
        /// Cause of failure:
        ///   Return value doesn't match expected
        /// </summary>
        [TestMethod]
        public void SIDLegacyBehavior_Test()
        {
            #region Arrange
            long invoiceSid = 123456;
            int itemPos = 7;

            long legacyValue = (invoiceSid << 10) + itemPos;
            #endregion

            // Act
            var computedValue = SIDUtil.ShiftAndTruncateSID(invoiceSid.ToString(), itemPos);

            #region Assert
            Assert.AreEqual(legacyValue.ToString(), computedValue);
            #endregion
        }

        /// <summary>
        /// Tested method:
        ///   ShiftAndTruncateSID
        /// Tested class:
        ///   SIDUtil
        /// Description:
        ///   Testing what happens when the SID overflows with actual production values
        /// Cause of failure:
        ///   Return value doesn't match the value in the production database
        /// </summary>
        [TestMethod]
        public void ProductSIDOverflow_Test()
        {
            #region Arrange

            // These are actual values for invoice #32424 of Frye Boston
            long invoiceSid = -2837267432163238796L;
            int itemPos = 1;
            #endregion

            // Act
            var computedValue = SIDUtil.ShiftAndTruncateSID(invoiceSid.ToString(), itemPos);

            #region Assert
            Assert.AreEqual("-9223030962756923391", computedValue);
            #endregion
        }
    }
}
