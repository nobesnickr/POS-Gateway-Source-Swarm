//-----------------------------------------------------------------------
// <copyright file="TestHelper.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 29. 11:53</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Data.SQLite;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using FluentNHibernate.Cfg;
    using FluentNHibernate.Cfg.Db;
    using FluentNHibernate.Conventions.Helpers;
    using NHibernate;
    using NHibernate.Tool.hbm2ddl;

    /// <summary>
    /// Static unit test helper class.
    /// </summary>
    public static class TestHelper
    {
        /// <summary>
        /// The in memory connection string.
        /// </summary>
        private const string InMemoryConnectionStirng = "Data Source=:memory:;Version=3;New=True;";

        /// <summary>
        /// The nhibernate configuration.
        /// </summary>
        private static NHibernate.Cfg.Configuration config;

        /// <summary>
        /// The nhibernate session factory.
        /// </summary>
        private static ISessionFactory sessionFactory;

        /// <summary>
        /// Gets the session factory.
        /// </summary>
        public static ISessionFactory SessionFactory
        {
            get
            {
                return sessionFactory ?? (sessionFactory = Config.BuildSessionFactory());
            }
        }

        /// <summary>
        /// Gets the nhibernate config.
        /// </summary>
        private static NHibernate.Cfg.Configuration Config
        {
            get
            {
                if (config == null)
                {
                    config = Fluently.Configure()
                        .Database(
                          SQLiteConfiguration.Standard.InMemory())
                        .Mappings(m => m
                            .FluentMappings.AddFromAssemblyOf<SwarmExporterProgram>()
                            .Conventions.Add(DefaultLazy.Never())
                            .Conventions.Add(LazyLoad.Never()))
                        .BuildConfiguration();
                }

                return config;
            }
        }

        /// <summary>
        /// Opens a new connection.
        /// </summary>
        /// <returns>The open connection.</returns>
        public static IDbConnection OpenConnection()
        {
            var connection = new SQLiteConnection(InMemoryConnectionStirng);
            connection.Open();
            BuildSchema(connection);
            return connection;
        }

        /// <summary>
        /// Opens and returns a new session.
        /// </summary>
        /// <returns>The session.</returns>
        public static ISession OpenSession()
        {
            var session = SessionFactory.OpenSession();
            BuildSchema(session.Connection);
            return session;
        }

        /// <summary>
        /// Builds the schema.
        /// </summary>
        /// <param name="connection">The connection to use.</param>
        private static void BuildSchema(IDbConnection connection)
        {
            new SchemaExport(Config).Execute(
                false, // Change to true to write DDL script to console
                true,
                false,
                connection,
                null);
        }
    }
}
