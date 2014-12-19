//-----------------------------------------------------------------------
// <copyright file="DynamicEntity.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 10. 27. 19:11</date>
//-----------------------------------------------------------------------
namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Reflection;
    using System.Text;

    /// <summary>
    /// A dynamically mapped entity.
    /// </summary>
    public class DynamicEntity
    {
        /// <summary>
        /// The names of properties in this class.        
        /// </summary>
        private static readonly IEnumerable<string> BasePropertyNames = typeof(DynamicEntity).GetProperties().Select(p => p.Name).ToList();

        /// <summary>
        /// The dictionary of dynamic properties
        /// </summary>
        private Dictionary<string, object> properties = new Dictionary<string, object>();

        /// <summary>
        /// The list of properties declared by the instance
        /// </summary>
        private Dictionary<string, PropertyInfo> propertyInfos;

        /// <summary>
        /// Gets all keys.
        /// </summary>
        public IEnumerable<string> Keys
        {
            get
            {
                this.EnsureProperties();
                return this.properties.Keys.Concat(this.propertyInfos.Select(p => p.Key));
            }
        }

        /// <summary>
        /// Gets or sets the <see cref="System.String"/> with the specified property name.
        /// </summary>
        /// <param name="propertyName">Name of the property.</param>
        /// <returns>The value associated with the given property.</returns>
        public object this[string propertyName]
        {
            get
            {
                this.EnsureProperties();
                if (this.propertyInfos.ContainsKey(propertyName))
                {
                    return this.propertyInfos[propertyName].GetValue(this, null);
                }

                return this.properties[propertyName];
            }

            set
            {
                this.EnsureProperties();
                if (this.propertyInfos.ContainsKey(propertyName))
                {
                    this.propertyInfos[propertyName].SetValue(this, value, null);
                }
                else
                {
                    this.properties[propertyName] = value;
                }
            }
        }

        /// <summary>
        /// Ensures the properties array is filled.
        /// </summary>
        private void EnsureProperties()
        {
            if (this.propertyInfos == null)
            {               
                this.propertyInfos =
                    this.GetType()
                        .GetProperties()
                        .Where(p => !BasePropertyNames.Contains(p.Name))
                        .ToDictionary(p => p.Name);
            }
        }
    }
}
