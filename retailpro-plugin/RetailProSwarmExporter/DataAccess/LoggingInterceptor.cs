//-----------------------------------------------------------------------
// <copyright file="LoggingInterceptor.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:17</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using NHibernate;

    /// <summary>
    /// Interceptor which logs the intercepted SQL queries
    /// </summary>
    public class LoggingInterceptor : EmptyInterceptor, IInterceptor
    {
        /// <summary>
        /// Reference to the logger.
        /// </summary>
        private static readonly NLog.Logger Logger = NLog.LogManager.GetCurrentClassLogger();

        /// <summary>
        /// Prepare SQL statement
        /// </summary>
        /// <param name="sql">Prepared SQL statement</param>
        /// <returns>The SQL statement received as argument</returns>
        NHibernate.SqlCommand.SqlString IInterceptor.OnPrepareStatement(NHibernate.SqlCommand.SqlString sql)
        {
            Logger.Trace("Executing: {0}", sql);
            return sql;
        }
    }
}
