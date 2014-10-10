//-----------------------------------------------------------------------
// <copyright file="NHibernateHelper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:17</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.DataAccess
{
    using System.Configuration;
    using FluentNHibernate.Cfg;
    using FluentNHibernate.Cfg.Db;
    using FluentNHibernate.Conventions.Helpers;
    using NHibernate;

    /// <summary>
    /// Static class responsible for NHIbernate initialization.
    /// </summary>
    public static class NHibernateHelper
    {
        /// <summary>
        /// The session factory
        /// </summary>
        private static ISessionFactory sessionFactory;

        /// <summary>
        /// Initializes static members of the <see cref="NHibernateHelper"/> class.
        /// </summary>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Performance", "CA1810:InitializeReferenceTypeStaticFieldsInline", Justification = "Too long.")]
        static NHibernateHelper()
        {
            var connectionString = ConfigurationManager.ConnectionStrings["retailProReport"].ConnectionString;
            var fluentConfig = Fluently.Configure()
                .Database(
                  OracleClientConfiguration.Oracle10
                    .ConnectionString(connectionString)
                    .Dialect<NHibernate.Dialect.Oracle10gDialect>()
                    .Driver<OracleManagedDriver>())
                .Mappings(m => m
                    .FluentMappings.AddFromAssemblyOf<SwarmExporterProgram>()
                    .Conventions.Add(DefaultLazy.Never())
                    .Conventions.Add(LazyLoad.Never()));
            
            //// var config = fluentConfig.BuildConfiguration();
            sessionFactory = fluentConfig.BuildSessionFactory();
        }

        /// <summary>
        /// Gets the session factory.
        /// </summary>
        public static ISessionFactory SessionFactory
        {
            get
            {
                return sessionFactory;
            }
        }

        /// <summary>
        /// Opens the session.
        /// </summary>
        /// <returns>The open session.</returns>
        public static ISession OpenSession()
        {
            return SessionFactory.OpenSession(new LoggingInterceptor());
        }
    }
}