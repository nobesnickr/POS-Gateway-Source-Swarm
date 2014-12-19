//-----------------------------------------------------------------------
// <copyright file="SwarmServiceTarget.cs" company="Sonrisa">
//     Copyright (c) JitSmart  All rights reserved.
// </copyright>
// <author>Csabi</author>
// <date>2013. 11. 04. 10:55</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Logging
{
    using System;
    using System.Collections.Generic;
    using System.ComponentModel.Composition;
    using System.Linq;
    using System.Text;
    using NLog.Targets;
    using RetailProCommon.Service;

    /// <summary>
    /// NLog target, which transmits logs to the swarm service.
    /// </summary>
    [Target("SwarmService")]
    [Export(typeof(SwarmServiceTarget))]
    public class SwarmServiceTarget : TargetWithLayout
    {
        /// <summary>
        /// The swarm service
        /// </summary>
        private static ISwarmService swarmService;

        /// <summary>
        /// Registers the definition.
        /// </summary>
        public static void RegisterDefinition()
        {
            NLog.Config.ConfigurationItemFactory.Default.Targets.RegisterDefinition("SwarmService", typeof(SwarmServiceTarget));                                    
        }

        /// <summary>
        /// Sets the service.
        /// </summary>
        /// <param name="service">The service.</param>
        public static void SetService(ISwarmService service)
        {
            swarmService = service;
        }

        /// <summary>
        /// Writes logging event to the log target.
        /// classes.
        /// </summary>
        /// <param name="logEvent">Logging event to be written out.</param>
        protected override void Write(NLog.LogEventInfo logEvent)
        {
            if (swarmService == null)
            {
                return;
            }

            var message = this.Layout.Render(logEvent);
            try
            {
                swarmService.UploadAsync(Urls.LogUpload, message).Wait();
            }
            catch (Exception)
            {
            }
        }        
    }
}
