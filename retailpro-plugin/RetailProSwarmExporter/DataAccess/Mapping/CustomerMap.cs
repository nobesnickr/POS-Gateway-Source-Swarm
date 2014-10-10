//-----------------------------------------------------------------------
// <copyright file="CustomerMap.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:22</date>
//-----------------------------------------------------------------------

namespace RetailProSwarmExporter.DataAccess.Mapping
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Text;
    using FluentNHibernate.Mapping;
    using RetailProCommon.Model;

    /// <summary>
    /// Fluent NHibernate map for the Customer entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class CustomerMap : ClassMap<Customer>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="CustomerMap"/> class.
        /// </summary>
        public CustomerMap()
        {
            this.Table("Customer_V");
            this.Id(customer => customer.CustomerSid, "CUST_SID");
            this.Map(customer => customer.FirstName, "FIRST_NAME");
            this.Map(customer => customer.LastName, "LAST_NAME");
            this.Map(customer => customer.StoreNumber, "STORE_NO");
            this.Map(customer => customer.SbsNumber, "SBS_NO");
            this.Map(customer => customer.Email, "EMAIL_ADDR");

            this.HasMany(customer => customer.Addresses)
                .Inverse()
                .KeyColumn("CUST_SID");
        }
    }

    /// <summary>
    /// Fluent NHibernate map for the Address entity.
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class AddressMap : ClassMap<Address>
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="AddressMap"/> class.
        /// </summary>
        public AddressMap()
        {
            this.Table("CUST_ADDRESS_V");
            this.CompositeId()
                .KeyProperty(address => address.CustomerSid, "CUST_SID")
                .KeyProperty(address => address.AddressNumber, "ADDR_NO");
            this.Map(address => address.Phone1, "PHONE1");
            this.Map(address => address.Phone2, "PHONE2");
            this.Map(address => address.Zip, "ZIP");
            this.Map(address => address.Address1, "Address1");
            this.Map(address => address.Address2, "Address2");
            this.Map(address => address.Address3, "Address3");     
        }
    }
}
