//-----------------------------------------------------------------------
// <copyright file="HeartbeatTest.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 30. 10:36</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.Test
{
    using System;
    using System.Threading.Tasks;
    using Microsoft.VisualStudio.TestTools.UnitTesting;
    using Moq;
    using RetailProCommon.BusinessLogic;
    using RetailProCommon.Model;
    using RetailProCommon.Service;

    /// <summary>
    /// Test class for the heartbeat business logic.
    /// </summary>
    [TestClass]
    public class HeartbeatTest
    {
        /// <summary>
        /// Tested method:
        ///   ExportChangedItems
        /// Tested class:
        ///   Heartbeat
        /// Initial data, state:
        ///   None
        /// Description:
        ///   Tests uploading the heartbeat data.
        /// Expected behavior, state:
        ///   Json with version information is uploaded.
        /// </summary>
        [TestMethod]
        public void HeartbeatExportTest()
        {
            // Arrange
            var serviceMock = new Mock<ISwarmService>();
            var uploadTask = new Task(() => { });
            var versionMock = new Mock<IApplicationVersion>();
            versionMock.Setup(v => v.Version).Returns("1.0.0.Teszt");
            serviceMock.Setup(svc => svc.UploadAsync(RetailProCommon.Urls.HeartbeatUpload, It.IsAny<Pulse>())).Returns(uploadTask);
            var target = new Heartbeat(serviceMock.Object, versionMock.Object);

            // Act
            var result = target.ExportChangedItems();

            // Assert
            Assert.AreEqual(uploadTask, result);
            serviceMock.Verify(svc => svc.UploadAsync(RetailProCommon.Urls.HeartbeatUpload, It.Is<Pulse>(data => data.Version == "1.0.0.Teszt")), Times.Once());
        }
    }
}
