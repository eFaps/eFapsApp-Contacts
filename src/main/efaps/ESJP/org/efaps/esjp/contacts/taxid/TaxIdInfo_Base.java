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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.text.WordUtils;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsClassLoader;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.ci.CIFormContacts;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.contacts.taxid.InfoJson.SubAddress;
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
        final String taxId = _parameter.getParameterValue(CIFormContacts.Contacts_TaxIdRequestForm.taxid.name);
        final Request request = new Request();
        final InfoJson infoJson = request.getInfoJson(taxId);
        ret.put(ReturnValues.SNIPLETT, getSnipplet4InfoJson(_parameter, infoJson));
        return ret;
    }

    /**
     * Gets the snipplet for info json.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _infoJson the info json
     * @return the snipplet for info json
     * @throws EFapsException on error
     */
    public CharSequence getSnipplet4InfoJson(final Parameter _parameter,
                                             final InfoJson _infoJson)
        throws EFapsException
    {
        final HtmlTable table = new HtmlTable();
        table.table()
            .tr()
                .th(getDBProperty("KeyHeader"))
                .th(getDBProperty("ValueHeader"))
            .trC()
            .tr()
                .td(getDBProperty("retrieved"))
                .td(_infoJson.getRetrieved())
            .trC()
            .tr()
                .td(getDBProperty("taxId"))
                .td(_infoJson.getTaxId())
            .trC()
            .tr()
                .td(getDBProperty("name"))
                .td(_infoJson.getName())
            .trC()
            .tr()
                .td(getDBProperty("comercialName"))
                .td(_infoJson.getComercialName())
            .trC()
            .tr()
                .td(getDBProperty("addressStreetType"))
                .td(_infoJson.getAddressStreetType())
            .trC()
            .tr()
                .td(getDBProperty("addressStreet"))
                .td(_infoJson.getAddressStreet())
            .trC()
            .tr()
                .td(getDBProperty("addressNumber"))
                .td(_infoJson.getAddressNumber())
            .trC();

        for (final SubAddress subAddress : _infoJson.getSubAddress()) {
            table.tr()
                .td(getDBProperty("subAddressType"))
                .td(subAddress.getType())
            .trC()
            .tr()
                .td(getDBProperty("subAddressValue"))
                .td(subAddress.getValue())
            .trC();
        }
        table.tr()
                .td(getDBProperty("addressUbigeo"))
                .td(_infoJson.getAddressUbigeo())
            .trC()
            .tr()
                .td(getDBProperty("addressReference"))
                .td(_infoJson.getAddressReference())
            .trC()
            .tr()
                .td(getDBProperty("active"))
                .td(_infoJson.getActive())
            .trC()
            .tr()
                .td(getDBProperty("verified"))
                .td(_infoJson.getVerified())
            .trC()
            .tr()
                .td("")
            .trC()
            .tr()
                .td(getDBProperty("cityLabel"))
                .td(getCityLabel(_parameter, _infoJson.getAddressUbigeo()).toString())
            .trC()
            .tr()
                .td(getDBProperty("addressLabel"))
                .td(getAddressLabel(_parameter, _infoJson).toString())
                .trC()
            .tableC();
        return table.toString();
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
                    ret.append(method.invoke(clazz.newInstance(), _parameter, _ubigeo));
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
     * Gets the address label.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _infoJson the info json
     * @return the address label
     * @throws EFapsException on error
     */
    public CharSequence getAddressLabel(final Parameter _parameter,
                                        final InfoJson _infoJson)
        throws EFapsException
    {
        final StringBuilder ret = new StringBuilder();
        ret.append(WordUtils.capitalizeFully(_infoJson.getAddressStreetType()))
            .append(" ")
            .append(WordUtils.capitalizeFully(_infoJson.getAddressStreet()))
            .append(" ")
            .append(_infoJson.getAddressNumber());

        for (final SubAddress subAddress : _infoJson.getSubAddress()) {
            ret.append(" ");
            switch (subAddress.getType()) {
                case "INTERNAL":
                    ret.append("Int.");
                    break;
                case "DEPARTMENT":
                    ret.append("dep.");
                    break;
                case "BLOCK":
                    ret.append("Mz.");
                    break;
                case "KILOMETER":
                    ret.append("km.");
                    break;
                case "LOT":
                    ret.append("Lote");
                    break;
                default:
                    break;
            }
            ret.append(" ")
                .append(subAddress.getValue());
        }
        return ret;
    }
}
