//-----------------------------------------------------------------------
// <copyright file="ISwarmService.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 12:27</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Service
{
    using System;
    using System.Threading.Tasks;

    /// <summary>
    /// Interface, which defines a method to upload content to the swarm service.
    /// </summary>
    public interface ISwarmService
    {
        /// <summary>
        /// Uploads the given content to the given url in an async manner.
        /// </summary>
        /// <param name="url">The relative url.</param>
        /// <param name="content">The content to upload.</param>
        /// <returns>The async task.</returns>
       Task UploadAsync(string url, object content);

       /// <summary>
       /// Gets the requested json data deserialized as the given type parameter..
       /// </summary>
       /// <typeparam name="TResult">The type of the result.</typeparam>
       /// <param name="url">The URL to get the json from.</param>
       /// <returns>The deserialized object.</returns>
       /// <exception cref="System.ArgumentException">if url is null or empty.</exception>
       Task<TResult> GetAsync<TResult>(string url);
    }
}
