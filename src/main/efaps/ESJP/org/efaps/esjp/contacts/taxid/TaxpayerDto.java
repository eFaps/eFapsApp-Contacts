/*
 * Copyright 2003 - 2020 The eFaps Team
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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = TaxpayerDto.Builder.class)
@EFapsUUID("59fb46c8-6d36-4f93-a1ab-885761a03dbb")
@EFapsApplication("eFapsApp-Contacts")
public class TaxpayerDto
{

    private final String id;
    private final String name;
    private final String state;
    private final String homeState;
    private final String ubigeo;
    private final String streetType;
    private final String street;
    private final String zoneCode;
    private final String zoneType;
    private final String streetNumber;
    private final String streetInterior;
    private final String streetBatch;
    private final String apartmentNumber;
    private final String block;
    private final String kilometer;
    private final String department;
    private final String province;
    private final String district;
    private final String address;

    private TaxpayerDto(final Builder _builder)
    {
        id = _builder.id;
        name = _builder.name;
        state = _builder.state;
        homeState = _builder.homeState;
        ubigeo = _builder.ubigeo;
        streetType = _builder.streetType;
        street = _builder.street;
        zoneCode = _builder.zoneCode;
        zoneType = _builder.zoneType;
        streetNumber = _builder.streetNumber;
        streetInterior = _builder.streetInterior;
        streetBatch = _builder.streetBatch;
        apartmentNumber = _builder.apartmentNumber;
        block = _builder.block;
        kilometer = _builder.kilometer;
        department = _builder.department;
        province = _builder.province;
        district = _builder.district;
        address = _builder.address;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getState()
    {
        return state;
    }

    public String getHomeState()
    {
        return homeState;
    }

    public String getUbigeo()
    {
        return ubigeo;
    }

    public String getStreetType()
    {
        return streetType;
    }

    public String getStreet()
    {
        return street;
    }

    public String getZoneCode()
    {
        return zoneCode;
    }

    public String getZoneType()
    {
        return zoneType;
    }

    public String getStreetNumber()
    {
        return streetNumber;
    }

    public String getStreetInterior()
    {
        return streetInterior;
    }

    public String getStreetBatch()
    {
        return streetBatch;
    }

    public String getApartmentNumber()
    {
        return apartmentNumber;
    }

    public String getBlock()
    {
        return block;
    }

    public String getKilometer()
    {
        return kilometer;
    }

    public String getDepartment()
    {
        return department;
    }

    public String getProvince()
    {
        return province;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getAddress()
    {
        return address;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {

        private String id;
        private String name;
        private String state;
        private String homeState;
        private String ubigeo;
        private String streetType;
        private String street;
        private String zoneCode;
        private String zoneType;
        private String streetNumber;
        private String streetInterior;
        private String streetBatch;
        private String apartmentNumber;
        private String block;
        private String kilometer;
        private String department;
        private String province;
        private String district;
        private String address;

        public Builder withId(final String _id)
        {
            id = _id;
            return this;
        }

        public Builder withName(final String _name)
        {
            name = _name;
            return this;
        }

        public Builder withState(final String _state)
        {
            state = _state;
            return this;
        }

        public Builder withHomeState(final String _homeState)
        {
            homeState = _homeState;
            return this;
        }

        public Builder withUbigeo(final String _ubigeo)
        {
            ubigeo = _ubigeo;
            return this;
        }

        public Builder withStreetType(final String _streetType)
        {
            streetType = _streetType;
            return this;
        }

        public Builder withStreet(final String _street)
        {
            street = _street;
            return this;
        }

        public Builder withZoneCode(final String _zoneCode)
        {
            zoneCode = _zoneCode;
            return this;
        }

        public Builder withZoneType(final String _zoneType)
        {
            zoneType = _zoneType;
            return this;
        }

        public Builder withStreetNumber(final String _streetNumber)
        {
            streetNumber = _streetNumber;
            return this;
        }

        public Builder withStreetInterior(final String _streetInterior)
        {
            streetInterior = _streetInterior;
            return this;
        }

        public Builder withStreetBatch(final String _streetBatch)
        {
            streetBatch = _streetBatch;
            return this;
        }

        public Builder withApartmentNumber(final String _apartmentNumber)
        {
            apartmentNumber = _apartmentNumber;
            return this;
        }

        public Builder withBlock(final String _block)
        {
            block = _block;
            return this;
        }

        public Builder withKilometer(final String _kilometer)
        {
            kilometer = _kilometer;
            return this;
        }

        public Builder withDepartment(final String _department)
        {
            department = _department;
            return this;
        }

        public Builder withProvince(final String _province)
        {
            province = _province;
            return this;
        }

        public Builder withDistrict(final String _district)
        {
            district = _district;
            return this;
        }

        public Builder withAddress(final String _address)
        {
            address = _address;
            return this;
        }

        public TaxpayerDto build()
        {
            return new TaxpayerDto(this);
        }
    }
}
