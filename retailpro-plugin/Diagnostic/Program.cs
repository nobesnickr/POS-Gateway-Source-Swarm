//-----------------------------------------------------------------------
// <copyright file="Program.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>tamas-tfs</author>
// <date>2014. 2. 25. 14:02</date>
//-----------------------------------------------------------------------

namespace Diagnostic
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Net;
    using System.Text;
    using System.Threading.Tasks;

    public class Program
    {
        /// <summary>
        /// Resources attempted
        /// </summary>
        private static readonly string[] BaseUrl = 
        { 
            //"https://ajax.googleapis.com/ajax/services/search/",
            "https://pos-gateway-dev.swarm-mobile.com/swarm/api/", 
            "http://pos-gateway-dev.swarm-mobile.com/swarm/api/" 
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
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        static void Main(string[] args)
        {
            // Each URL with each protocol
            foreach (var url in BaseUrl)
            {
                foreach (var protocol in Protocols)
                {
                    if (StepExecute(url, protocol))
                    {
                        Console.Write("Success " + protocol.ToString() + "!");
                        return;
                    }
                }
            }

            Console.Write("Error " + "No luck" + "!");
        }

        /// <summary>
        /// Executes one stage
        /// </summary>
        /// <param name="url">URL of the stage</param>
        /// <param name="protocol">Protocol of the stage</param>
        private static bool StepExecute(string url, SecurityProtocolType protocol)
        {
            try
            {
                SwarmService service = new SwarmService(new Uri(url), protocol);

                if (url.StartsWith("https://ajax.googleapis.com"))
                {
                    service.GetAsync<string>("web?v=1.0&q=Swarm").Wait();
                }
                else
                {
                    service.GetAsync<Dictionary<string, string>>("version").Wait();

                    service.UploadAsync("log", "DIAGNOSTICS ENTRY");
                }

                return true;
            }
            catch (Exception e)
            {
                Logger.ErrorException("Failure", e);
                return false;
            }
        }

    }
}
