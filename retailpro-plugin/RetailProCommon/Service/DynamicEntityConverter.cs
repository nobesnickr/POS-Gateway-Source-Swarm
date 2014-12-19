//-----------------------------------------------------------------------
// <copyright file="DynamicEntityConverter.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 18:29</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Service
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Reflection;
    using System.Text;
    using Newtonsoft.Json;
    using Newtonsoft.Json.Converters;

    /// <summary>
    /// Converter for writing Expandable objects to json.
    /// </summary>
    public class DynamicEntityConverter : JsonConverter
    {
        /// <summary>
        /// Gets a value indicating whether this <see cref="T:Newtonsoft.Json.JsonConverter" /> can read JSON.
        /// </summary>
        /// <value>
        /// <c>true</c> if this <see cref="T:Newtonsoft.Json.JsonConverter" /> can read JSON; otherwise, <c>false</c>.
        /// </value>
        public override bool CanRead
        {
            get
            {
                return false;
            }
        }

        /// <summary>
        /// Gets a value indicating whether this <see cref="T:Newtonsoft.Json.JsonConverter" /> can write JSON.
        /// </summary>
        /// <value>
        /// <c>true</c> if this <see cref="T:Newtonsoft.Json.JsonConverter" /> can write JSON; otherwise, <c>false</c>.
        /// </value>
        public override bool CanWrite
        {
            get
            {
                return true;
            }
        }

        /// <summary>
        /// Reads the JSON representation of the object.
        /// </summary>
        /// <param name="reader">The <see cref="T:Newtonsoft.Json.JsonReader" /> to read from.</param>
        /// <param name="objectType">Type of the object.</param>
        /// <param name="existingValue">The existing value of object being read.</param>
        /// <param name="serializer">The calling serializer.</param>
        /// <returns>
        /// The object value.
        /// </returns>
        /// <exception cref="System.NotImplementedException">Always, since not implemented.</exception>
        public override object ReadJson(JsonReader reader, Type objectType, object existingValue, JsonSerializer serializer)
        {
            throw new NotImplementedException();
        }

        /// <summary>
        /// Writes the JSON representation of the object.
        /// </summary>
        /// <param name="writer">The <see cref="T:Newtonsoft.Json.JsonWriter" /> to write to.</param>
        /// <param name="value">The value.</param>
        /// <param name="serializer">The calling serializer.</param>
        public override void WriteJson(JsonWriter writer, object value, JsonSerializer serializer)
        {
            var obj = (RetailProCommon.Model.DynamicEntity)value;            
            var type = obj.GetType();
            writer.WriteStartObject();
            foreach (var key in obj.Keys)
            {
                var prop = type.GetProperty(key);
                if (prop == null || prop.GetCustomAttributes(typeof(JsonIgnoreAttribute), true).Count() == 0)
                {
                    string propertyName = key;
                    if (prop != null)
                    {
                        var jsonPropertyAttribute = (JsonPropertyAttribute)prop.GetCustomAttributes(typeof(JsonPropertyAttribute), true).FirstOrDefault();
                        if (jsonPropertyAttribute != null && !string.IsNullOrEmpty(jsonPropertyAttribute.PropertyName))
                        {
                            propertyName = jsonPropertyAttribute.PropertyName;
                        }
                    }

                    writer.WritePropertyName(propertyName);
                    serializer.Serialize(writer, obj[key]);
                }
            }
            
            writer.WriteEnd();
        }

        /// <summary>
        /// Determines whether this instance can convert the specified object type.
        /// </summary>
        /// <param name="objectType">Type of the object.</param>
        /// <returns>
        ///   <c>true</c> if this instance can convert the specified object type; otherwise, <c>false</c>.
        /// </returns>
        public override bool CanConvert(Type objectType)
        {
            return objectType.IsSubclassOf(typeof(RetailProCommon.Model.DynamicEntity));
        }
    }
}
