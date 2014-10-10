//-----------------------------------------------------------------------
// <copyright file="MoqExtensions.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 16:46</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.Test
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using Moq.Language.Flow;

    /// <summary>
    /// Static extension class for the Moq framework.
    /// </summary>
    public static class MoqExtensions
    {
        /// <summary>
        /// Specifies the value to return as a Task.
        /// </summary>
        /// <typeparam name="TMock">The type of the mock.</typeparam>
        /// <typeparam name="TResult">The type of the result.</typeparam>
        /// <param name="setup">The setup object.</param>
        /// <param name="value">The value to return.</param>
        /// <returns>The result setup object.</returns>
        ////[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "That's the Moq api.")]
        ////[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1011:ConsiderPassingBaseTypesAsParameters", Justification = "This operation is for ISetup only.")]
        ////public static IReturnsResult<TMock> ReturnsAsync<TMock, TResult>(
        ////    this ISetup<TMock, Task<TResult>> setup, TResult value)
        ////    where TMock : class
        ////{
        ////    if (setup == null)
        ////    {
        ////        throw new ArgumentNullException("setup");
        ////    }
        ////
        ////    return setup.Returns(Task.FromResult(value));
        ////}

        /// <summary>
        /// Sets up the async operation to throw the given exception. 
        /// </summary>
        /// <typeparam name="TMock">The type of the mock.</typeparam>
        /// <typeparam name="TResult">The type of the result.</typeparam>
        /// <param name="setup">The setup object.</param>
        /// <param name="exception">The exception to throw.</param>
        /// <returns>The result setup object.</returns>
        ////[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "That's the Moq api.")]
        ////[System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1011:ConsiderPassingBaseTypesAsParameters", Justification = "This operation is for ISetup only.")]
        ////public static IReturnsResult<TMock> ThrowsAsync<TMock, TResult>(
        ////    this ISetup<TMock, Task<TResult>> setup, 
        ////    Exception exception)
        ////        where TMock : class
        ////{
        ////    if (setup == null)
        ////    {
        ////        throw new ArgumentNullException("setup");
        ////    }
        ////
        ////    return setup.Returns(new TaskFactory().StartNew<TResult>(() => { throw exception; }));
        ////}

        /// <summary>
        /// Sets up a void async operation to execute successfuly. 
        /// </summary>
        /// <typeparam name="TMock">The type of the mock.</typeparam>        
        /// <param name="setup">The setup object.</param>        
        /// <returns>The result setup object.</returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "That's the Moq api.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1011:ConsiderPassingBaseTypesAsParameters", Justification = "This operation is for ISetup only.")]
        public static IReturnsResult<TMock> ExecutesAsync<TMock>(
            this ISetup<TMock, Task> setup)
                where TMock : class
        {
            if (setup == null)
            {
                throw new ArgumentNullException("setup");
            }

            return setup.Returns(Task.Factory.StartNew(() => { }));
        }

        /// <summary>
        /// Sets up the async operation to throw the given exception. 
        /// </summary>
        /// <typeparam name="TMock">The type of the mock.</typeparam>        
        /// <param name="setup">The setup object.</param>
        /// <param name="exception">The exception to throw.</param>
        /// <returns>The result setup object.</returns>
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "That's the Moq api.")]
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Design", "CA1011:ConsiderPassingBaseTypesAsParameters", Justification = "This operation is for ISetup only.")]
        public static IReturnsResult<TMock> ThrowsAsync<TMock>(
            this ISetup<TMock, Task> setup,
            Exception exception)
                where TMock : class
        {
            if (setup == null)
            {
                throw new ArgumentNullException("setup");
            }

            return setup.Returns(Task.Factory.StartNew(() => { throw exception; }));
        }
    }
}
