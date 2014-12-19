//-----------------------------------------------------------------------
// <copyright file="Customer.cs" company="Sonrisa">
//     Copyright (c) Sonrisa  All rights reserved.
// </copyright>
// <author>csaba-tfs</author>
// <date>2013. 7. 24. 11:11</date>
//-----------------------------------------------------------------------

namespace RetailProCommon.Model
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Linq;
    using System.Runtime.Serialization;
    using System.Text;

    /// <summary>
    /// Customer entity.
    /// </summary>
    [DataContract]
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class Customer
    {
        /// <summary>
        /// Gets or sets the customer sid.
        /// </summary>
        [DataMember(Name = "CustSid")]
        public string CustomerSid { get; set; }

        /// <summary>
        /// Gets or sets the first name.
        /// </summary>
        [DataMember(Name = "FirstName")]
        public string FirstName { get; set; }

        /// <summary>
        /// Gets or sets the Last name.
        /// </summary>
        [DataMember(Name = "LastName")]
        public string LastName { get; set; }

        /// <summary>
        /// Gets or sets the store number.
        /// </summary>
        [DataMember(Name = "StoreNo")]
        public string StoreNumber { get; set; }

        /// <summary>
        /// Gets or sets the SBS number.
        /// <code>NUMBER(5)</code>
        /// </summary>
        [DataMember(Name = "SbsNo")]
        public int SbsNumber { get; set; }

        /// <summary>
        /// Gets or sets the email.
        /// </summary>
        [DataMember]
        public string Email { get; set; }

        /// <summary>
        /// Gets or sets the customer's address
        /// </summary>
        public IEnumerable<Address> Addresses { get; set; }

        /// <summary>
        /// Gets the main address of the customer
        /// </summary>
        public Address MainAddress
        {
            get
            {
                if (this.Addresses == null || this.Addresses.Count() == 0)
                {
                    return new Address();
                }
                else
                {
                    return this.Addresses.OrderBy(a => a.AddressNumber).First();
                }
            }
        }

        /// <summary>
        /// Gets the address1.
        /// </summary>
        [DataMember(Name = "Address1")]
        public string Address1
        {
            get
            {
                return this.MainAddress.Address1;
            }
        }

        /// <summary>
        /// Gets the address2.
        /// </summary>
        [DataMember(Name = "Address2")]
        public string Address2
        {
            get
            {
                return this.MainAddress.Address2;
            }
        }

        /// <summary>
        /// Gets the address3.
        /// </summary>
        [DataMember(Name = "Address3")]
        public string Address3
        {
            get
            {
                return this.MainAddress.Address3;
            }
        }

        /// <summary>
        /// Gets the ZIP code.
        /// </summary>
        [DataMember(Name = "Zip")]
        public string Zip
        {
            get
            {
                return this.MainAddress.Zip;
            }
        }

        /// <summary>
        /// Gets the phone number.
        /// </summary>
        [DataMember(Name = "Phone")]
        public string Phone
        {
            get
            {
                return this.MainAddress.Phone1 ?? this.MainAddress.Phone2;
            }
        }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)(7L * this.CustomerSid.GetHashCode()) + (int)((17L * this.SbsNumber) + (23L * (this.StoreNumber != null ? this.StoreNumber.GetHashCode() : 0)));
        }

        /// <summary>
        /// Determines whether the specified <see cref="System.Object" /> is equal to this instance.
        /// </summary>
        /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
        /// <returns>
        ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
        /// </returns>
        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            var other = obj as Customer;
            return other != null && this.CustomerSid == other.CustomerSid && this.SbsNumber == other.SbsNumber && this.StoreNumber == other.StoreNumber;
        }
    }

    /// <summary>
    /// Customer's address
    /// </summary>
    [SuppressMessage("Microsoft.StyleCop.CSharp.MaintainabilityRules", "SA1402:FileMayOnlyContainASingleClass", Justification = "Reviewed")]
    public class Address
    {
        /// <summary>
        /// Gets or sets the customer sid.
        /// </summary>
        public string CustomerSid { get; set; }

        /// <summary>
        /// Gets or sets the customer address number
        /// </summary>
        public int AddressNumber { get; set; }

        /// <summary>
        /// Gets or sets the address1.
        /// </summary>
        public string Address1 { get; set; }

        /// <summary>
        /// Gets or sets the address2.
        /// </summary>
        public string Address2 { get; set; }

        /// <summary>
        /// Gets or sets the address3.
        /// </summary>
        public string Address3 { get; set; }

        /// <summary>
        /// Gets or sets the ZIP.
        /// </summary>
        public string Zip { get; set; }

        /// <summary>
        /// Gets or sets the phone2.
        /// </summary>
        public string Phone2 { get; set; }

        /// <summary>
        /// Gets or sets the phone1.
        /// </summary>
        public string Phone1 { get; set; }

        /// <summary>
        /// Returns a hash code for this instance.
        /// </summary>
        /// <returns>
        /// A hash code for this instance, suitable for use in hashing algorithms and data structures like a hash table. 
        /// </returns>
        public override int GetHashCode()
        {
            return (int)(7L * this.CustomerSid.GetHashCode()) + (int)(23L * this.AddressNumber);
        }

        /// <summary>
        /// Determines whether the specified <see cref="System.Object" /> is equal to this instance.
        /// </summary>
        /// <param name="obj">The <see cref="System.Object" /> to compare with this instance.</param>
        /// <returns>
        ///   <c>true</c> if the specified <see cref="System.Object" /> is equal to this instance; otherwise, <c>false</c>.
        /// </returns>
        public override bool Equals(object obj)
        {
            if (obj == null)
            {
                return false;
            }

            var other = obj as Address;
            return other != null && this.CustomerSid == other.CustomerSid && this.AddressNumber == other.AddressNumber;
        }
    }
}
