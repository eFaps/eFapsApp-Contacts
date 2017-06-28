/*
 * Copyright 2003 - 2017 The eFaps Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.efaps.esjp.contacts.taxid;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class InfoJson.
 *
 * @author The eFaps Team
 */

@EFapsUUID("ab836a48-10d1-4876-b825-9e96ed2d2ebc")
@EFapsApplication("eFapsApp-Contacts")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoJson
{
    /** The tax id. */
    @JsonProperty("taxId")
    private String taxId;

    /** The retrieved. */
    @JsonProperty("retrieved")
    private String retrieved;

    /** The name. */
    @JsonProperty("name")
    private String name;

    /** The address street type. */
    @JsonProperty("addressStreetType")
    private String addressStreetType;

    /** The address street. */
    @JsonProperty("addressStreet")
    private String addressStreet;

    /** The address number. */
    @JsonProperty("addressNumber")
    private String addressNumber;

    /** The address ubigeo. */
    @JsonProperty("addressUbigeo")
    private String addressUbigeo;

    /** The address reference. */
    @JsonProperty("addressReference")
    private String addressReference;

    /** The active. */
    @JsonProperty("active")
    private String active;

    /** The verified. */
    @JsonProperty("verified")
    private String verified;

    /** The comercial name. */
    @JsonProperty("comercialName")
    private String comercialName;

    @JsonProperty("addressZoneType")
    private String addressZoneType;

    @JsonProperty("addressZoneNumber")
    private String addressZoneNumber;

    /** The sub address. */
    @JsonProperty("subAddress")
    private List<SubAddress> subAddress = null;

    /**
     * Gets the tax id.
     *
     * @return the tax id
     */
    @JsonProperty("taxId")
    public String getTaxId()
    {
        return this.taxId;
    }

    /**
     * Sets the tax id.
     *
     * @param taxId the new tax id
     */
    @JsonProperty("taxId")
    public void setTaxId(final String taxId)
    {
        this.taxId = taxId;
    }

    /**
     * Gets the sub address.
     *
     * @return the sub address
     */
    @JsonProperty("subAddress")
    public List<SubAddress> getSubAddress()
    {
        return this.subAddress;
    }

    /**
     * Sets the sub address.
     *
     * @param subAddress the new sub address
     */
    @JsonProperty("subAddress")
    public void setSubAddress(final List<SubAddress> subAddress)
    {
        this.subAddress = subAddress;
    }

    /**
     * Gets the retrieved.
     *
     * @return the retrieved
     */
    @JsonProperty("retrieved")
    public String getRetrieved()
    {
        return this.retrieved;
    }

    /**
     * Sets the retrieved.
     *
     * @param retrieved the new retrieved
     */
    @JsonProperty("retrieved")
    public void setRetrieved(final String retrieved)
    {
        this.retrieved = retrieved;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @JsonProperty("name")
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    @JsonProperty("name")
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Gets the address street type.
     *
     * @return the address street type
     */
    @JsonProperty("addressStreetType")
    public String getAddressStreetType()
    {
        return this.addressStreetType;
    }

    /**
     * Sets the address street type.
     *
     * @param addressStreetType the new address street type
     */
    @JsonProperty("addressStreetType")
    public void setAddressStreetType(final String addressStreetType)
    {
        this.addressStreetType = addressStreetType;
    }

    /**
     * Gets the address street.
     *
     * @return the address street
     */
    @JsonProperty("addressStreet")
    public String getAddressStreet()
    {
        return this.addressStreet;
    }

    /**
     * Sets the address street.
     *
     * @param addressStreet the new address street
     */
    @JsonProperty("addressStreet")
    public void setAddressStreet(final String addressStreet)
    {
        this.addressStreet = addressStreet;
    }

    /**
     * Gets the address number.
     *
     * @return the address number
     */
    @JsonProperty("addressNumber")
    public String getAddressNumber()
    {
        return this.addressNumber;
    }

    /**
     * Sets the address number.
     *
     * @param addressNumber the new address number
     */
    @JsonProperty("addressNumber")
    public void setAddressNumber(final String addressNumber)
    {
        this.addressNumber = addressNumber;
    }

    /**
     * Gets the address ubigeo.
     *
     * @return the address ubigeo
     */
    @JsonProperty("addressUbigeo")
    public String getAddressUbigeo()
    {
        return this.addressUbigeo;
    }

    /**
     * Sets the address ubigeo.
     *
     * @param addressUbigeo the new address ubigeo
     */
    @JsonProperty("addressUbigeo")
    public void setAddressUbigeo(final String addressUbigeo)
    {
        this.addressUbigeo = addressUbigeo;
    }

    /**
     * Gets the address reference.
     *
     * @return the address reference
     */
    @JsonProperty("addressReference")
    public String getAddressReference()
    {
        return this.addressReference;
    }

    /**
     * Sets the address reference.
     *
     * @param addressReference the new address reference
     */
    @JsonProperty("addressReference")
    public void setAddressReference(final String addressReference)
    {
        this.addressReference = addressReference;
    }

    /**
     * Gets the active.
     *
     * @return the active
     */
    @JsonProperty("active")
    public String getActive()
    {
        return this.active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    @JsonProperty("active")
    public void setActive(final String active)
    {
        this.active = active;
    }

    /**
     * Gets the verified.
     *
     * @return the verified
     */
    @JsonProperty("verified")
    public String getVerified()
    {
        return this.verified;
    }

    /**
     * Sets the verified.
     *
     * @param verified the new verified
     */
    @JsonProperty("verified")
    public void setVerified(final String verified)
    {
        this.verified = verified;
    }

    /**
     * Gets the comercial name.
     *
     * @return the comercial name
     */
    @JsonProperty("comercialName")
    public String getComercialName()
    {
        return this.comercialName;
    }

    /**
     * Sets the comercial name.
     *
     * @param comercialName the new comercial name
     */
    @JsonProperty("comercialName")
    public void setComercialName(final String comercialName)
    {
        this.comercialName = comercialName;
    }

    /**
     * Getter method for the instance variable {@link #addressZoneType}.
     *
     * @return value of instance variable {@link #addressZoneType}
     */
    @JsonProperty("addressZoneType")
    public String getAddressZoneType()
    {
        return this.addressZoneType;
    }

    /**
     * Setter method for instance variable {@link #addressZoneType}.
     *
     * @param _addressZoneType value for instance variable {@link #addressZoneType}
     */
    @JsonProperty("addressZoneType")
    public void setAddressZoneType(final String _addressZoneType)
    {
        this.addressZoneType = _addressZoneType;
    }

    /**
     * Getter method for the instance variable {@link #addressZoneNumber}.
     *
     * @return value of instance variable {@link #addressZoneNumber}
     */
    @JsonProperty("addressZoneNumber")
    public String getAddressZoneNumber()
    {
        return this.addressZoneNumber;
    }

    /**
     * Setter method for instance variable {@link #addressZoneNumber}.
     *
     * @param _addressZoneNumber value for instance variable {@link #addressZoneNumber}
     */
    @JsonProperty("addressZoneNumber")
    public void setAddressZoneNumber(final String _addressZoneNumber)
    {
        this.addressZoneNumber = _addressZoneNumber;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * The Class SubAddress.
     *
     */
    public static class SubAddress
    {

        /** The type. */
        @JsonProperty("type")
        private String type;

        /** The value. */
        @JsonProperty("value")
        private String value;

        /**
         * Gets the type.
         *
         * @return the type
         */
        @JsonProperty("type")
        public String getType()
        {
            return this.type;
        }

        /**
         * Sets the type.
         *
         * @param type the new type
         */
        @JsonProperty("type")
        public void setType(final String type)
        {
            this.type = type;
        }

        /**
         * Gets the value.
         *
         * @return the value
         */
        @JsonProperty("value")
        public String getValue()
        {
            return this.value;
        }

        /**
         * Sets the value.
         *
         * @param value the new value
         */
        @JsonProperty("value")
        public void setValue(final String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
