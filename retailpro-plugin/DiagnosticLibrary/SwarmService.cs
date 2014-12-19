//-----------------------------------------------------------------------
// <copyright file="SwarmService.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:45</date>
//-----------------------------------------------------------------------
namespace DiagnosticLibrary
{
    using System;
    using System.Collections.Generic;
    using System.IO.Compression;
    using System.Linq;
    using System.Net;
    using System.Text;
    using System.Threading.Tasks;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>
    /// <see cref="ISwarmService"/> implementation, which is able to upload the content to a REST service.
    /// </summary>
    public class SwarmService
    {
        /// <summary>
        /// Service specific constants.
        /// </summary>
        private const string
            JsonMediaType = "application/json",
            SwarmIdKey = "SwarmId",
            PosSoftwareIdKey = "Pos-Software",
            UploadRequestMethod = "PUT";

        /// <summary>
        /// Serialization settings
        /// </summary>
        private static readonly JsonSerializerSettings SerializerSettings;

        /// <summary>
        /// The server's base address.
        /// </summary>
        private readonly Uri baseAddress;

        /// <summary>
        /// The client's swarm id.
        /// </summary>
        private readonly string swarmId;

        /// <summary>
        /// The client's POS data source's id.
        /// </summary>
        private readonly string posSoftwareId;

        /// <summary>
        /// Security protocol used
        /// </summary>
        private SecurityProtocolType securityProtocol;
        
        /// <summary>
        /// Initializes static members of the <see cref="SwarmService"/> class.
        /// </summary>
        static SwarmService()
        {
            SerializerSettings = new JsonSerializerSettings()
            {
                NullValueHandling = NullValueHandling.Ignore,
            };

            // Allow every self signed certificate for now.
            ServicePointManager.ServerCertificateValidationCallback = (sender, certificate, chain, sslPolicyErrors) =>
            {
                return true;
            };
        }

        /// <summary>
        /// Initializes a new instance of the <see cref="SwarmService" /> class.
        /// </summary>
        /// <param name="baseAddress">Base address used</param>
        /// <param name="securityProtocol">Security protocol used</param>
        /// <param name="swarmId">swarm id, default value "diagnostics"</param>
        /// <param name="posSoftwareId">pos software id, default vaule is "installer"</param>
        public SwarmService(Uri baseAddress, SecurityProtocolType securityProtocol, string swarmId = "diagnostics", string posSoftwareId = "installer")
        {
            this.swarmId = swarmId;
            this.posSoftwareId = posSoftwareId;
            this.baseAddress = baseAddress;
            this.securityProtocol = securityProtocol;
        }

        /// <summary>
        /// Gets the DebugInfo used for logging the state of the entity
        /// </summary>
        public string DebugInfo
        {
            get
            {
                return "{SwarmService:" + this.baseAddress + "," + this.securityProtocol + "}";
            }
        }

        /// <summary>
        /// Uploads the given content to the given url in an async manner.
        /// </summary>
        /// <param name="url">The relative url.</param>
        /// <param name="content">The content to upload.</param>
        /// <returns>The async task.</returns>
        public Task UploadAsync(string url, object content)
        {
            if (content == null)
            {
                throw new ArgumentNullException("content");
            }

            return Task.Factory.StartNew(() =>
            {
                var json = JsonConvert.SerializeObject(content, Formatting.None, SerializerSettings);
                HttpWebResponse response = null;
                try
                {
                    ServicePointManager.SecurityProtocol = this.securityProtocol;

                    var request = this.CreateUploadRequest(url, json, compress: false);

                    response = (HttpWebResponse)request.GetResponse();
                    response.Close();
                    if (response.StatusCode != HttpStatusCode.OK && response.StatusCode != HttpStatusCode.Created && response.StatusCode != HttpStatusCode.Accepted)
                    {
                        throw new ServiceException(url, response.StatusCode);
                    }
                }
                catch (ProtocolViolationException protocolException)
                {
                    throw new ServiceException(url, protocolException);
                }
                catch (WebException webException)
                {
                    throw new ServiceException(url, webException);
                }
            });
        }

        /// <summary>
        /// Gets the requested json data deserialized as the given type parameter.
        /// </summary>
        /// <typeparam name="TResult">The type of the result.</typeparam>
        /// <param name="url">The URL to get the json from.</param>
        /// <returns>
        /// The deserialized object.
        /// </returns>
        /// <exception cref="System.ArgumentException">if url is null or empty.</exception>
        public Task<string> GetAsync<TResult>(string url)
        {
            if (string.IsNullOrEmpty(url))
            {
                throw new ArgumentException("url");
            }

            return Task.Factory.StartNew<string>(() =>
            {
                HttpWebResponse response = null;
                try
                {
                    ServicePointManager.SecurityProtocol = this.securityProtocol;

                    var request = (HttpWebRequest)HttpWebRequest.Create(new Uri(this.baseAddress, url).ToString());
                    request.Headers.Add(SwarmIdKey, this.swarmId);
                    request.Headers.Add(PosSoftwareIdKey, this.posSoftwareId);

                    response = (HttpWebResponse)request.GetResponse();

                    if (response.StatusCode != HttpStatusCode.OK)
                    {
                        throw new ServiceException(url, response.StatusCode);
                    }

                    string content = null;
                    using (var reader = new System.IO.StreamReader(response.GetResponseStream()))
                    {
                        content = reader.ReadToEnd();
                    }

                    if (!content.StartsWith("{"))
                    {
                        throw new WebException("Received content is not a json.");
                    }

                    return content;
                }
                catch (ProtocolViolationException protocolException)
                {
                    throw new ServiceException(url, protocolException);
                }
                catch (WebException webException)
                {
                    throw new ServiceException(url, webException);
                }
            });
        }

        /// <summary>
        /// Creates the upload request.
        /// </summary>
        /// <param name="url">The relative URL.</param>
        /// <param name="jsonContent">Content of the json.</param>
        /// <param name="compress">if set to <c>true</c> the json stream is sent using gzip compression.</param>
        /// <returns>
        /// The created request.
        /// </returns>
        public HttpWebRequest CreateUploadRequest(string url, string jsonContent, bool compress = false)
        {
            var request = (HttpWebRequest)WebRequest.Create(new Uri(this.baseAddress, url).ToString());

            request.Method = UploadRequestMethod;
            request.Headers.Add(SwarmIdKey, this.swarmId);
            request.Headers.Add(PosSoftwareIdKey, this.posSoftwareId);
            request.ContentType = JsonMediaType;

            var bytes = Encoding.UTF8.GetBytes(jsonContent);

            if (compress)
            {
                request.AutomaticDecompression = DecompressionMethods.GZip;
                request.ServicePoint.Expect100Continue = false;
                request.KeepAlive = false;
                request.Headers.Add("Content-Encoding", "gzip");
                using (var memoryStream = new System.IO.MemoryStream())
                {
                    using (var zipStream = new GZipStream(memoryStream, CompressionMode.Compress))
                    {
                        zipStream.Write(bytes, 0, bytes.Length);
                    }

                    bytes = memoryStream.ToArray();
                }
            }

            request.ContentLength = bytes.Length;
            using (var requestStream = request.GetRequestStream())
            {
                requestStream.Write(bytes, 0, bytes.Length);
            }

            return request;
        }
    }
}
