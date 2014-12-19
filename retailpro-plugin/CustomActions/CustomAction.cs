//-----------------------------------------------------------------------
// <copyright file="CustomAction.cs" company="Sonrisa">
//     Copyright (c) JitSmart  All rights reserved.
// </copyright>
// <author>tamas-tfs</author>
// <date>2013. 7. 25. 10:55</date>
//-----------------------------------------------------------------------

namespace CustomActions
{
    using System;
using System.Collections.Generic;
using System.Configuration;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Reflection;
using System.Text.RegularExpressions;
using System.Windows.Forms;
using System.Xml;
using DiagnosticLibrary;
using Microsoft.Deployment.WindowsInstaller;
using Microsoft.Win32.TaskScheduler;
using Oracle.ManagedDataAccess.Client;

    /// <summary>
    /// Custom actions for the RetailProSwarmExporter
    /// </summary>
    public class CustomAction
    {
        /// <summary>
        /// Creates a task that runs the given RetailProSwarmExporter
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult ScheduleTask(Session session)
        {
            Debug();
            session.Log("Begin Schedule action");

            try
            {
                using (var taskService = new TaskService())
                {
                    var task = taskService.FindTask(session["TASKNAME"]);
                    
                    if (task != null)
                    { 
                        taskService.RootFolder.DeleteTask(session["TASKNAME"]);
                    }

                    var taskDefinition = taskService.NewTask();
                    taskDefinition.RegistrationInfo.Description = session["TASKDESCRIPTION"];

                    var trigger = new DailyTrigger();
                    trigger.Repetition.Interval = TimeSpan.FromMinutes(5);
                    trigger.Repetition.Duration = TimeSpan.FromDays(3650);
                    trigger.StartBoundary = DateTime.Now;
                    trigger.EndBoundary = DateTime.Today.AddYears(10);
                    trigger.Enabled = true;
                    trigger.Repetition.StopAtDurationEnd = false;

                    taskDefinition.Triggers.Add(trigger);
                    taskDefinition.Actions.Add(new ExecAction(GetExePath(session)));
                    taskService.RootFolder.RegisterTaskDefinition(session["TASKNAME"], taskDefinition);
                }

                session.Log("Task scheduled.");
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                return ActionResult.Failure;
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Removes the scheduled task created by the installer
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult RemoveScheduledTask(Session session)
        {
            Debug();
            session.Log("Begin removing scheduled task");

            try
            {
                using (var taskService = new TaskService())
                {
                    var task = taskService.FindTask(session["TASKNAME"]);

                    if (task != null)
                    {
                        taskService.RootFolder.DeleteTask(session["TASKNAME"]);
                        session.Log("Scheduled task removed.");
                    }
                    else
                    { 
                        session.Log("Scheduled task does not exist.");
                    }
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Removes the "RetailProSwarmExporter" folder and all it's files from AppData
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult RemoveAppDataFilesAndFolder(Session session)
        {
            Debug();            
            try
            {
                var exePath = GetExePath(session);
                var config = ConfigurationManager.OpenExeConfiguration(exePath);
                if (config == null)
                {
                    session.Log("Cannot load config file for: " + exePath);
                    return ActionResult.NotExecuted;
                }

                var configDirectory = config.AppSettings.Settings["configDirectory"];
                if (configDirectory == null)
                {
                    session.Log("configDirectory not found in application configuration");
                    return ActionResult.NotExecuted;
                }
                
                if (string.IsNullOrEmpty(configDirectory.Value))
                {
                    session.Log("Invalid configDirectory value (null or empty).");
                    return ActionResult.NotExecuted;
                }

                var dir = new DirectoryInfo(Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData), configDirectory.Value));
                session.Log("Begin removing config folder and files from: " + dir);
                if (dir.Exists)
                {
                    dir.Delete(true);
                    session.Log("Directory and it's content is deleted.");
                }
                else
                {
                    session.Log("Directory doesn't exists.");
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Removes the installed folder and all it's files
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult DeleteInstalledFiles(Session session)
        {
            Debug();            
            try
            {
                var dir = new DirectoryInfo(session["INSTALLDIR"]);
                session.Log("Begin removing the installed files and folder:" + dir);
                if (dir.Exists)
                {
                    dir.Delete(true);
                    session.Log("Install directory removed.");
                }
                else
                {
                    session.Log("Install directory doesn't exists.");
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                return ActionResult.Failure;
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Queries the oracle's host name and port number using the listener's status info
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult GetLocalDatabaseData(Session session)
        {
            Debug();

            try
            {
                var process = new Process();
                var startInfo = new ProcessStartInfo();
                startInfo.WindowStyle = ProcessWindowStyle.Hidden;
                startInfo.FileName = "cmd.exe";
                startInfo.Arguments = @"/C lsnrctl status";
                startInfo.RedirectStandardOutput = true;
                startInfo.UseShellExecute = false;
                process.StartInfo = startInfo;
                process.Start();
                process.WaitForExit();

                var input = process.StandardOutput.ReadToEnd();
                string regexExpression = @"\(DESCRIPTION=\(ADDRESS=\(PROTOCOL=tcp\)\(HOST=(\S+)\)\(PORT=(\d+)\)\)\)";
                var regex = new Regex(regexExpression);
                var match = regex.Match(input);

                if (match.Success)
                {
                    session["HOST"] = match.Groups[1].Value;
                    session["PORT"] = match.Groups[2].Value;
                }
                else 
                {
                    MessageBox.Show("Failed to query the RetailPro Oracle host name and port. The installer will continue with the default values.", "Warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                MessageBox.Show("Error occured during quering the RetailPro Oracle host name and port. The installer will continue with the default values.", "Warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Runs a diagnostic check for decideing the security protocol
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult GetProtocol(Session session)
        {
            Debug();

            try
            {
                var result = Diagnostic.RunDiagnostic();

                if (result.IsSuccess)
                {
                    session["SECURITYPROTOCOL"] = result.ProtocolName;
                }
                else
                {
                    session.Log(result.LastError.Message);
                    MessageBox.Show("Failed to query the security protocol. The installer will continue with the default value.", "Warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                MessageBox.Show("Failed to query the security protocol. The installer will continue with the default value.", "Warning", MessageBoxButtons.OK, MessageBoxIcon.Warning);
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Tests the database connection with the given data
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult TestConnection(Session session)
        {
            Debug();

            try
            {
                var connetionString = string.Format("Data Source = (DESCRIPTION = (ADDRESS_LIST = (ADDRESS = (PROTOCOL = TCP)(HOST = {0})(PORT = {1}))) (CONNECT_DATA = (SERVICE_NAME = {2}))); User Id = {3}; Password= {4};", session["HOST"], session["PORT"], session["SERVICENAME"], session["DBUSER"], session["DBPASSWORD"]);

                using (var dbConnection = new OracleConnection(connetionString))
                { 
                    dbConnection.Open();

                    using (var command = dbConnection.CreateCommand())
                    {
                        string sql = "SELECT COUNT(*) FROM Invoice_V";
                        command.CommandText = sql;

                        using (var reader = command.ExecuteReader())
                        {
                            reader.Read();

                            session["P.DATABASE_CONNECTION_VALID"] = "1";
                        }
                    }
                }
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                session["P.DATABASE_CONNECTION_VALID"] = "0";
                MessageBox.Show("Can not connect to the RetailPro Oracle database: " + exp.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            
            return ActionResult.Success;
        }

        /// <summary>
        /// Attempt to connect to RDA2 using the intallation parameters
        /// </summary>
        /// <param name="session">installer's session</param>
        /// <returns>the result of the action</returns>
        [CustomAction]
        public static ActionResult TestRDA2Connection(Session session)
        {
            Debug();

            try
            {
                // Get the type object associated with the CLSID.
                Type myType = Type.GetTypeFromCLSID(new Guid("FF217C33-5EED-482A-B233-E11CBEE0D4A7"));

                var instance = Activator.CreateInstance(myType);

                // Arguments of the RDA2.Rda2ServerClass.Connect(...) method
                object[] connectionParams = new object[] { session["RETAILPROPATH"], session["WORKSTATION"], string.Empty, string.Empty };

                // Try to connect to the RDA2 interface, according to the documentation
                // this might take up to 10-20 seconds, especially when connection
                // is initiated to a location accessible through the network
                var result = myType.InvokeMember("Connect", BindingFlags.InvokeMethod, null, instance, connectionParams);

                session["P.RDA2_CONNECTION_VALID"] = "1";
            }
            catch (System.Runtime.InteropServices.COMException exp)
            {
                session.Log(exp.Message);
                SendLog(exp.ToString(), session["SWARMID"], "installer-v8");
                session["P.RDA2_CONNECTION_VALID"] = "0";
                MessageBox.Show("RDA2.dll is not found or not compatible. Please download and register the newest available version from ftp://ftp.retailpro.com/", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            catch (TargetInvocationException exp)
            {
                session.Log(exp.Message);
                SendLog(exp.ToString(), session["SWARMID"], "installer-v8");
                session["P.RDA2_CONNECTION_VALID"] = "0";
                MessageBox.Show("Failed to connect to Retail Pro, please verify your Retail Pro installation path and workstation id", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            catch (Exception exp)
            {
                session.Log(exp.Message);
                SendLog(exp.ToString(), session["SWARMID"], "installer-v8");
                session["P.RDA2_CONNECTION_VALID"] = "0";
                MessageBox.Show("Failed to connect to Retail Pro, please contact your Swarm administrator", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

            return ActionResult.Success;
        }

        /// <summary>
        /// Gets the executable path.
        /// </summary>
        /// <param name="session">The session.</param>
        /// <returns>The path for the installed executable.</returns>
        private static string GetExePath(Session session)
        {
            return session["INSTALLDIR"] + session["TASKEXE"];
        }

        /// <summary>
        /// Send a message to the server's logging endpoint
        /// We don't care about the exception during the send attempt
        /// </summary>
        /// <param name="message">the log message we want to send to the server</param>
        /// <param name="swarmId">the swarm id</param>
        /// <param name="posSoftwareId">the pos software id</param>
        private static void SendLog(string message, string swarmId, string posSoftwareId)
        {
            try
            {
                Utils.SendLog(message, swarmId, posSoftwareId);
            }
            catch (Exception)
            {
            }
        }

        /// <summary>
        /// If not attached launches the debugger.
        /// Breaks the current execution.
        /// </summary>
        private static void Debug()
        {
#if DEBUG
            if (!Debugger.IsAttached)
            {
                Debugger.Launch();
            }

            Debugger.Break();
#endif
        }
    }
}
