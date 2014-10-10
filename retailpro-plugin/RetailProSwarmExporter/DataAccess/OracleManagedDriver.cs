//-----------------------------------------------------------------------
// <copyright file="OracleManagedDriver.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:12</date>
//-----------------------------------------------------------------------
namespace RetailProSwarmExporter.DataAccess
{
    using System;
    using System.Collections.Generic;
    using System.Data;
    using System.Linq;
    using System.Reflection;
    using System.Text;
    using NHibernate.AdoNet;
    using NHibernate.Driver;
    using NHibernate.Engine.Query;
    using NHibernate.SqlTypes;
    using NHibernate.Util;

    /// <summary>
    /// Reflection based driver for managed oracle data access.
    /// </summary>    
    public sealed class OracleManagedDriver : ReflectionBasedDriver, IEmbeddedBatcherFactoryProvider
    {
        /// <summary>
        /// The driver assembly name
        /// </summary>
        private const string DriverAssemblyName = "Oracle.ManagedDataAccess";

        /// <summary>
        /// The connection type name
        /// </summary>
        private const string ConnectionTypeName = "Oracle.ManagedDataAccess.Client.OracleConnection";

        /// <summary>
        /// The command type name
        /// </summary>
        private const string CommandTypeName = "Oracle.ManagedDataAccess.Client.OracleCommand";

        /// <summary>
        /// The GUID SQL type
        /// </summary>
        private static readonly SqlType GuidSqlType = new SqlType(DbType.Binary, 16);

        /// <summary>
        /// The oracle command bind by name
        /// </summary>
        private readonly PropertyInfo oracleCommandBindByName;

        /// <summary>
        /// The oracle db type
        /// </summary>
        private readonly PropertyInfo oracleDbType;

        /// <summary>
        /// The oracle db type ref cursor
        /// </summary>
        private readonly object oracleDbTypeRefCursor;

        /// <summary>
        /// Initializes a new instance of the <see cref="OracleManagedDriver" /> class.
        /// </summary>
        /// <exception cref="HibernateException">Thrown when the <c>Oracle.DataAccess</c> assembly can not be loaded.</exception>
        public OracleManagedDriver()
            : base(
            "Oracle.ManagedDataAccess.Client",
            DriverAssemblyName,
            ConnectionTypeName,
            CommandTypeName)
        {
            System.Type oracleCommandType = ReflectHelper.TypeFromAssembly("Oracle.ManagedDataAccess.Client.OracleCommand", DriverAssemblyName, false);
            this.oracleCommandBindByName = oracleCommandType.GetProperty("BindByName");

            System.Type parameterType = ReflectHelper.TypeFromAssembly("Oracle.ManagedDataAccess.Client.OracleParameter", DriverAssemblyName, false);
            this.oracleDbType = parameterType.GetProperty("OracleDbType");

            System.Type oracleDbTypeEnum = ReflectHelper.TypeFromAssembly("Oracle.ManagedDataAccess.Client.OracleDbType", DriverAssemblyName, false);
            this.oracleDbTypeRefCursor = System.Enum.Parse(oracleDbTypeEnum, "RefCursor");
        }

        /// <summary>
        /// Does this Driver require the use of a Named Prefix in the SQL statement.
        /// </summary>
        /// <remarks>
        /// For example, SqlClient requires <c>select * from simple where simple_id = @simple_id</c>
        /// If this is false, like with the OleDb provider, then it is assumed that
        /// the <c>?</c> can be a placeholder for the parameter in the SQL statement.
        /// </remarks>
        public override bool UseNamedPrefixInSql
        {
            get { return true; }
        }

        /// <summary>
        /// Does this Driver require the use of the Named Prefix when trying
        /// to reference the Parameter in the Command's Parameter collection.
        /// </summary>
        /// <remarks>
        /// This is really only useful when the UseNamedPrefixInSql == true.  When this is true the
        /// code will look like:
        /// <code>IDbParameter param = cmd.Parameters["@paramName"]</code>
        /// if this is false the code will be
        /// <code>IDbParameter param = cmd.Parameters["paramName"]</code>.
        /// </remarks>
        public override bool UseNamedPrefixInParameter
        {
            get { return true; }
        }

        /// <summary>
        /// The Named Prefix for parameters.
        /// </summary>
        /// <remarks>
        /// Sql Server uses <c>"@"</c> and Oracle uses <c>":"</c>.
        /// </remarks>
        public override string NamedPrefix
        {
            get { return ":"; }
        }

        #region IEmbeddedBatcherFactoryProvider Members

        /// <summary>
        /// Gets the batcher factory class.
        /// </summary>
        /// <value>
        /// The batcher factory class.
        /// </value>
        System.Type IEmbeddedBatcherFactoryProvider.BatcherFactoryClass
        {
            get { return typeof(OracleDataClientBatchingBatcherFactory); }
        }

        #endregion

        /// <summary>
        /// Initializes the given parameter.
        /// </summary>
        /// <param name="dbParam">The db param.</param>
        /// <param name="name">The name.</param>
        /// <param name="sqlType">Type of the SQL.</param>
        /// <remarks>
        /// This adds logic to ensure that a DbType.Boolean parameter is not created since
        /// ODP.NET doesn't support it.
        /// </remarks>
        protected override void InitializeParameter(IDbDataParameter dbParam, string name, SqlType sqlType)
        {            
            // if the parameter coming in contains a boolean then we need to convert it 
            // to another type since ODP.NET doesn't support DbType.Boolean
            switch (sqlType.DbType)
            {
                case DbType.Boolean:
                    base.InitializeParameter(dbParam, name, SqlTypeFactory.Int16);
                    break;
                case DbType.Guid:
                    base.InitializeParameter(dbParam, name, GuidSqlType);
                    break;
                default:
                    base.InitializeParameter(dbParam, name, sqlType);
                    break;
            }
        }

        /// <summary>
        /// Override to make any adjustments to the IDbCommand object.  (e.g., Oracle custom OUT parameter)
        /// Parameters have been bound by this point, so their order can be adjusted too.
        /// This is analogous to the RegisterResultSetOutParameter() function in Hibernate.
        /// </summary>
        /// <param name="command">The command argument.</param>
        protected override void OnBeforePrepare(IDbCommand command)
        {
            base.OnBeforePrepare(command);

            // need to explicitly turn on named parameter binding
            // http://tgaw.wordpress.com/2006/03/03/ora-01722-with-odp-and-command-parameters/
            this.oracleCommandBindByName.SetValue(command, true, null);

            CallableParser.Detail detail = CallableParser.Parse(command.CommandText);

            if (!detail.IsCallable)
            {
                return;
            }

            command.CommandType = CommandType.StoredProcedure;
            command.CommandText = detail.FunctionName;
            this.oracleCommandBindByName.SetValue(command, false, null);

            IDbDataParameter outCursor = command.CreateParameter();
            this.oracleDbType.SetValue(outCursor, this.oracleDbTypeRefCursor, null);

            outCursor.Direction = detail.HasReturn ? ParameterDirection.ReturnValue : ParameterDirection.Output;

            command.Parameters.Insert(0, outCursor);
        }
    }
}
