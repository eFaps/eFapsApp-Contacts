/*
 * Copyright © 2003 - 2024 The eFaps Team (-)
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
 */
package org.efaps.esjp.contacts.taxid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Instance;
import org.efaps.db.PrintQuery;
import org.efaps.db.SelectBuilder;
import org.efaps.eql.EQL;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.esjp.ci.CIFormContacts;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.ui.html.HtmlTable;
import org.efaps.update.AppDependency;
import org.efaps.update.util.InstallationException;
import org.efaps.util.EFapsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class TaxIdInfo.
 *
 * @author The eFaps Team
 */
@EFapsUUID("1399e978-ea48-4aff-bb03-a0ca7e10c204")
@EFapsApplication("eFapsApp-Contacts")
public abstract class TaxIdInfo_Base
    extends AbstractCommon
{

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(TaxIdInfo.class);

    /**
     * Gets the tax id info.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @return the tax id info
     * @throws EFapsException on error
     */
    public Return getTaxIdInfoUI(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        String taxId = _parameter.getParameterValue(CIFormContacts.Contacts_TaxIdRequestForm.taxid.name);
        if (StringUtils.isEmpty(taxId)
                        && InstanceUtils.isKindOf(_parameter.getInstance(), CIContacts.ContactAbstract)) {
            final PrintQuery print = new PrintQuery(_parameter.getInstance());
            final SelectBuilder selTaxId = SelectBuilder.get().clazz(CIContacts.ClassOrganisation)
                            .attribute(CIContacts.ClassOrganisation.TaxNumber);
            print.addSelect(selTaxId);
            print.execute();
            taxId = print.getSelect(selTaxId);
        }
        final Request request = new Request();
        final var dto = request.getTaxpayer(taxId);
        ret.put(ReturnValues.SNIPLETT, getSnipplet4Taxpayer(_parameter, dto));
        return ret;
    }

    public CharSequence getSnipplet4Taxpayer(final Parameter _parameter,
                                             final TaxpayerDto _dto)
        throws EFapsException
    {
        CharSequence ret;
        if (_dto != null) {
            final HtmlTable table = new HtmlTable();
            table.table()
                            .tr()
                            .th(getDBProperty("KeyHeader"))
                            .th(getDBProperty("ValueHeader"))
                            .trC()
                            .tr()
                            .td(getDBProperty("taxId"))
                            .td(_dto.getId())
                            .trC()
                            .tr()
                            .td(getDBProperty("name"))
                            .td(_dto.getName())
                            .trC();
            ret = table.toString();
        } else {
            final String taxId = _parameter.getParameterValue(CIFormContacts.Contacts_TaxIdRequestForm.taxid.name);
            ret = getFormatedDBProperty("NotFound", taxId);
        }
        return ret;
    }

    /**
     * Gets the address label.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _ubigeo the ubigeo
     * @return the address label
     * @throws EFapsException on error
     */
    public CharSequence getCityLabel(final Parameter _parameter,
                                     final String _ubigeo)
        throws EFapsException
    {
        final StringBuilder ret = new StringBuilder();
        try {
            if (AppDependency.getAppDependency("eFapsLocalizations-Ubicaciones").isMet()) {
                try {
                    final Class<?> clazz = Class.forName("org.efaps.esjp.ubicaciones.Ubicaciones", false,
                                    EFapsClassLoader.getInstance());
                    final Method method = clazz.getMethod("getAddressLabel", Parameter.class, String.class);
                    ret.append(method.invoke(clazz.getDeclaredConstructor().newInstance(), _parameter, _ubigeo));
                } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                                | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
                    TaxIdInfo_Base.LOG.error("Catched error", e);
                }
            }
        } catch (final InstallationException e) {
            TaxIdInfo_Base.LOG.error("Catched error", e);
            throw new EFapsException("AppDependency", e);
        }
        return ret;
    }

    /**
     * Checks if is not empty.
     *
     * @param _value the value
     * @return true, if is not empty
     */
    protected boolean isNotEmpty(final String _value)
    {
        return StringUtils.isNoneEmpty(_value) && !_value.equals("-");
    }

    public Instance createContactFromTaxpayerDto(final TaxpayerDto _dto, final boolean _isClient)
        throws EFapsException
    {
        Instance ret = null;
        if (_dto != null) {
            ret = EQL.builder().insert(CIContacts.Contact)
                            .set(CIContacts.Contact.Name, _dto.getName())
                            .set(CIContacts.Contact.Status, CIContacts.ContactStatus.Active)
                            .stmt().execute();

            final var classClass = (Classification) CIContacts.Class.getType();
            EQL.builder().insert(classClass.getClassifyRelationType())
                            .set(classClass.getRelLinkAttributeName(), String.valueOf(ret.getId()))
                            .set(classClass.getRelTypeAttributeName(), String.valueOf(classClass.getId()))
                            .stmt().execute();
            EQL.builder().insert(classClass)
                            .set(classClass.getLinkAttributeName(), String.valueOf(ret.getId()))
                            .stmt().execute();

            final var orgClass = (Classification) CIContacts.ClassOrganisation.getType();
            EQL.builder().insert(orgClass.getClassifyRelationType())
                            .set(orgClass.getRelLinkAttributeName(), String.valueOf(ret.getId()))
                            .set(orgClass.getRelTypeAttributeName(), String.valueOf(orgClass.getId()))
                            .stmt().execute();
            EQL.builder().insert(orgClass)
                            .set(orgClass.getLinkAttributeName(), String.valueOf(ret.getId()))
                            .set(CIContacts.ClassOrganisation.TaxNumber, _dto.getId())
                            .stmt().execute();

            final var locationClass = (Classification) CIContacts.ClassLocation.getType();
            EQL.builder().insert(locationClass.getClassifyRelationType())
                            .set(locationClass.getRelLinkAttributeName(), String.valueOf(ret.getId()))
                            .set(locationClass.getRelTypeAttributeName(), String.valueOf(locationClass.getId()))
                            .stmt().execute();
            EQL.builder().insert(locationClass)
                            .set(locationClass.getLinkAttributeName(), String.valueOf(ret.getId()))
                            .set(CIContacts.ClassLocation.LocationAdressStreet, getAdressStreet(_dto))
                            .set(CIContacts.ClassLocation.LocationAdressCity, getAdressCity(_dto))
                            .stmt().execute();

            if (_isClient) {
                final var classClient = (Classification) CIContacts.ClassClient.getType();
                EQL.builder().insert(classClient.getClassifyRelationType())
                                .set(classClient.getRelLinkAttributeName(), String.valueOf(ret.getId()))
                                .set(classClient.getRelTypeAttributeName(), String.valueOf(classClient.getId()))
                                .stmt().execute();

                EQL.builder().insert(classClient)
                                .set(classClient.getLinkAttributeName(), String.valueOf(ret.getId()))
                                .stmt().execute();
            }
        }
        return ret;
    }

    public String getAdressStreet(final TaxpayerDto _dto)
    {
        final var ret = new StringBuilder();
        if (isNotEmpty(_dto.getStreetType())) {
            ret.append(_dto.getStreetType()).append(" ");
        }
        if (_dto.getStreet() != null) {
            ret.append(WordUtils.capitalizeFully(_dto.getStreet())).append(" ");
        }
        if (isNotEmpty(_dto.getStreetNumber())) {
            ret.append("Nro. ").append(_dto.getStreetNumber()).append(" ");
        }
        if (isNotEmpty(_dto.getStreetInterior())) {
            ret.append("Int. ").append(_dto.getStreetInterior()).append(" ");
        }
        if (isNotEmpty(_dto.getApartmentNumber())) {
            ret.append("Dep. ").append(_dto.getApartmentNumber()).append(" ");
        }
        return ret.toString();
    }

    public String getAdressCity(final TaxpayerDto _dto)
    {
        final var ret = new StringBuilder();
        if (isNotEmpty(_dto.getDepartment())) {
            ret.append(_dto.getDepartment()).append(" - ");
        }
        if (isNotEmpty(_dto.getProvince())) {
            ret.append(_dto.getProvince()).append(" - ");
        }
        if (isNotEmpty(_dto.getDistrict())) {
            ret.append(_dto.getDistrict()).append(" - ");
        }
        return ret.toString();
    }

}
