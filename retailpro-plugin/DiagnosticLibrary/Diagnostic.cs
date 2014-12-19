//-----------------------------------------------------------------------
// <copyright file="Diagnostic.cs" company="Sonrisa">
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
    using System.Net;
    using System.Text;

    /// <summary>
    /// The Diagnostic class 
    /// </summary>
    public class Diagnostic
    {
        /// <summary>
        /// Resources attempted
        /// </summary>
        private static readonly string[] BaseUrl = 
        { 
            "https://pos-gateway.swarm-mobile.com/swarm/api/", 
            /* "http://pos-gateway-dev.swarm-mobile.com/swarm/api/" */
        };

        /// <summary>
        /// The security protocols in a desired order
        /// </summary>
        private static readonly SecurityProtocolType[] Protocols = 
        {
            SecurityProtocolType.Ssl3,
            SecurityProtocolType.Tls
        };

        /// <summary>
        /// Runs the diagnostic. Tries to figure out the best security protocol.
        /// </summary>
        /// <returns>the result of the diagnostic</returns>
        public static DiagnosticResult RunDiagnostic()
        {
            DiagnosticResult result = null;

            // Each URL with each protocol
            foreach (var url in BaseUrl)
            {
                foreach (var protocol in Protocols)
                {
                    result = StepExecute(url, protocol);

                    if (result.IsSuccess)
                    {
                        return result;
                    }
                }
            }

            return result;
        }

        /// <summary>
        /// Executes one stage
        /// </summary>
        /// <param name="url">URL of the stage</param>
        /// <param name="protocol">Protocol of the stage</param>
        /// <returns>the result of the diagnostic</returns>
        private static DiagnosticResult StepExecute(string url, SecurityProtocolType protocol)
        {
            var result = new DiagnosticResult() { IsSuccess = false, LastError = null, ProtocolName = string.Empty };
            
            try
            {
                SwarmService service = new SwarmService(new Uri(url), protocol);

                service.GetAsync<Dictionary<string, string>>("version").Wait();
                service.UploadAsync("log", "DIAGNOSTICS ENTRY").Wait();

                result.IsSuccess = true;
                result.ProtocolName = protocol.ToString();
            }
            catch (Exception e)
            {
                result.IsSuccess = false;
                result.LastError = e;
            }

            return result;
        }
    }
}
