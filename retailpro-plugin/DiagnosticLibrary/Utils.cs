//-----------------------------------------------------------------------
// <copyright file="Utils.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>tamas-tfs</author>
// <date>2014. 3. 18. 11:45</date>
//-----------------------------------------------------------------------

namespace DiagnosticLibrary
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;

    /// <summary>
    /// The Utils class 
    /// </summary>
    public class Utils
    {
        /// <summary>
        /// Send a message to the server's logging endpoint
        /// </summary>
        /// <param name="logMessage">the log message we want to send to the server</param>
        /// <param name="swarmId">the swarm id</param>
        /// <param name="posSoftwareId">the pos software id</param>
        public static void SendLog(string logMessage, string swarmId, string posSoftwareId)
        {
            SwarmService service = new SwarmService(new Uri("https://pos-gateway.swarm-mobile.com/swarm/api/"), System.Net.SecurityProtocolType.Ssl3, swarmId, posSoftwareId);

            service.UploadAsync("log", logMessage).Wait();
        }
    }
}
