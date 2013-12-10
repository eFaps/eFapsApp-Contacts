/*
 * Copyright 2003 - 2013 The eFaps Team
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
 * Revision:        $Rev$
 * Last Changed:    $Date$
 * Last Changed By: $Author$
 */

package org.efaps.esjp.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.ui.field.Field;
import org.efaps.db.AttributeQuery;
import org.efaps.db.Instance;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.PrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.db.SelectBuilder;
import org.efaps.db.Update;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.esjp.ci.CIERP;
import org.efaps.esjp.ci.CIFormContacts;
import org.efaps.ui.wicket.util.EFapsKey;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("0999190f-d2bb-43e7-a6e6-49fdc5a31b95")
@EFapsRevision("$Rev$")
public abstract class Contacts_Base
{

    /**
     * method for move documents of the contacts a other contacts in case duplicate name.
     * @param _parameter Parameter as passed from the eFaps API.
     * @return new Return
     * @throws EFapsException on error.
     */
    public Return moveDocuments2Contacts(final Parameter _parameter)
        throws EFapsException
    {
        final String fromContacts = _parameter.getParameterValue("fromContacts");
        final String toContacts = _parameter.getParameterValue("toContacts");

        if (fromContacts != null && toContacts != null) {
            final QueryBuilder queryBldr = new QueryBuilder(CIERP.DocumentAbstract);
            queryBldr.addWhereAttrEqValue(CIERP.DocumentAbstract.Contact, Instance.get(fromContacts).getId());
            final MultiPrintQuery multi = queryBldr.getPrint();
            multi.addAttribute(CIERP.DocumentAbstract.ID, CIERP.DocumentAbstract.OID);
            multi.execute();

            final Map<Long, String> ids = new HashMap<Long, String>();
            while (multi.next()) {
                ids.put(multi.<Long>getAttribute(CIERP.DocumentAbstract.ID),
                        multi.<String>getAttribute(CIERP.DocumentAbstract.OID));
            }

            if (!ids.isEmpty()) {
                for (final Entry<Long, String> entry : ids.entrySet()) {
                    final Update update = new Update(entry.getValue());
                    update.add(CIERP.DocumentAbstract.Contact, Instance.get(toContacts).getId());
                    update.execute();
                }
            }
        }

        return new Return();
    }

    /**
     * Method for auto-complete contacts.
     * @param _parameter Parameter as passed from the eFaps API.
     * @return Return with values.
     * @throws EFapsException on error.
     */
    public Return autoComplete4Contact(final Parameter _parameter)
        throws EFapsException
    {
        final String input = (String) _parameter.get(ParameterValues.OTHERS);
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final String minLengthStr = (String) properties.get("MinLength");
        final Integer minLength = minLengthStr == null ? 0 : Integer.valueOf(minLengthStr);

        final Map<String, Map<String, String>> tmpMap = new TreeMap<String, Map<String, String>>();
        if (input.length() > minLength) {
            final String classesStr = (String) properties.get("Classifications");
            String[] classes = new String[0];
            if (classesStr != null) {
                classes = classesStr.split(";");
            }
            final String key = properties.containsKey("Key") ? (String) properties.get("Key") : "OID";

            final QueryBuilder queryBldr = new QueryBuilder(CIContacts.Contact);
            if (classes.length > 0) {
                final Classification[] classTypes = new Classification[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    classTypes[i] = (Classification) Type.get(classes[i]);
                }
                queryBldr.addWhereClassification(classTypes);
            }
            final boolean nameSearch = !Character.isDigit(input.charAt(0));
            if (nameSearch) {
                queryBldr.addWhereAttrMatchValue(CIContacts.Contact.Name, input + "*").setIgnoreCase(true);
                final MultiPrintQuery multi = queryBldr.getPrint();
                multi.addAttribute("Name", key);
                multi.execute();
                while (multi.next()) {
                    final String name = multi.<String>getAttribute("Name");
                    final String keyVal = multi.getAttribute(key).toString();
                    final Map<String, String> map = new HashMap<String, String>();
                    map.put(EFapsKey.AUTOCOMPLETE_KEY.getKey(), keyVal);
                    map.put(EFapsKey.AUTOCOMPLETE_VALUE.getKey(), name);
                    map.put(EFapsKey.AUTOCOMPLETE_CHOICE.getKey(), name);
                    tmpMap.put(name, map);
                }
            } else {
                final Map<Instance,String > inst2tax = new HashMap<Instance,String>();
                final QueryBuilder orgQueryBldr = new QueryBuilder(CIContacts.ClassOrganisation);
                orgQueryBldr.addWhereAttrMatchValue(CIContacts.ClassOrganisation.TaxNumber, input + "*");
                if (classes.length > 0) {
                    final AttributeQuery attrQuery = queryBldr.getAttributeQuery(CIContacts.Contact.ID);
                    orgQueryBldr.addWhereAttrInQuery(CIContacts.ClassOrganisation.ContactLink, attrQuery);
                }
                final MultiPrintQuery multi = orgQueryBldr.getPrint();
                multi.addAttribute(CIContacts.ClassOrganisation.TaxNumber,
                                CIContacts.ClassOrganisation.ContactLink);
                multi.execute();
                while (multi.next()) {
                    inst2tax.put(Instance.get(CIContacts.Contact.getType(),
                                    multi.<Long>getAttribute(CIContacts.ClassOrganisation.ContactLink)),
                                    multi.<String>getAttribute(CIContacts.ClassOrganisation.TaxNumber));
                }

                final QueryBuilder persQueryBldr = new QueryBuilder(CIContacts.ClassPerson);
                persQueryBldr.addWhereAttrMatchValue(CIContacts.ClassPerson.IdentityCard, input + "*");
                if (classes.length > 0) {
                    final AttributeQuery attrQuery = queryBldr.getAttributeQuery(CIContacts.Contact.ID);
                    persQueryBldr.addWhereAttrInQuery(CIContacts.ClassPerson.ContactLink, attrQuery);
                }
                final MultiPrintQuery multi2 = persQueryBldr.getPrint();
                multi2.addAttribute(CIContacts.ClassPerson.IdentityCard, CIContacts.ClassPerson.ContactLink);
                multi2.execute();
                while (multi2.next()) {
                    final String idcard = multi2.<String>getAttribute(CIContacts.ClassPerson.IdentityCard);
                    if (idcard != null) {
                        inst2tax.put(Instance.get(CIContacts.Contact.getType(),
                                        multi2.<Long>getAttribute(CIContacts.ClassPerson.ContactLink)), idcard);
                    }
                }
                final List<Instance> queryList =new ArrayList<Instance>();
                for (final Entry<Instance, String> entry : inst2tax.entrySet()) {
                    if (entry.getKey().isValid()) {
                        queryList.add(entry.getKey());
                    }
                }
                final MultiPrintQuery print = new MultiPrintQuery(queryList);
                print.setEnforceSorted(true);
                print.addAttribute(CIContacts.Contact.Name);
                print.addAttribute(key);
                print.execute();
                while (print.next()) {
                    final Map<String, String> map = new HashMap<String, String>();
                    final String choice = inst2tax.get(print.getCurrentInstance()) + " - "
                                    + print.<String>getAttribute(CIContacts.Contact.Name);
                    map.put(EFapsKey.AUTOCOMPLETE_KEY.getKey(), print.getAttribute(key).toString());
                    map.put(EFapsKey.AUTOCOMPLETE_VALUE.getKey(),
                                    print.<String>getAttribute(CIContacts.Contact.Name));
                    map.put(EFapsKey.AUTOCOMPLETE_CHOICE.getKey(), choice);
                    tmpMap.put(choice, map);
                }

            }
        }
        final Return retVal = new Return();
        list.addAll(tmpMap.values());
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     * Method for update a field of the contact.
     *
     * @param _parameter Parameter as passed from the eFaps API.
     * @return retVal with values of the contact.
     * @throws EFapsException on error.
     */
    public Return updateFields4Contact(final Parameter _parameter)
        throws EFapsException
    {
        final Field field = (Field) _parameter.get(ParameterValues.UIOBJECT);
        final String fieldName = field.getName();
        final String value = _parameter.getParameterValue(fieldName);
        final Instance instance = value.contains(".") ? Instance.get(value)
                        : Instance.get(CIContacts.Contact.getType(), value);

        final Map<?, ?> properties = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);

        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final Map<String, String> map = new HashMap<String, String>();

        final String targetFieldName = properties.containsKey("FieldName") ? (String) properties.get("FieldName")
                        : "contactData";

        if (instance.isValid()) {
            map.put(targetFieldName, getFieldValue4Contact(instance));
        } else {
            map.put(targetFieldName, "????");
        }
        list.add(map);

        final Return retVal = new Return();
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     * Method to get the value for the field directly under the Contact.
     *
     * @param _instance Instacne of the contact
     * @param _esscape escape the script or not
     * @return String for the field
     * @throws EFapsException on error
     */
    public String getFieldValue4Contact(final Instance _instance,
                                        final boolean _escape)
        throws EFapsException
    {
        final StringBuilder strBldr = new StringBuilder();

        if (_instance.isValid()) {
            final PrintQuery print = new PrintQuery(_instance);
            final SelectBuilder selTaxN = SelectBuilder.get().clazz(CIContacts.ClassOrganisation)
                            .attribute(CIContacts.ClassOrganisation.TaxNumber);
            final SelectBuilder selIDC = SelectBuilder.get().clazz(CIContacts.ClassPerson)
                            .attribute(CIContacts.ClassPerson.IdentityCard);
            final SelectBuilder selStreet = SelectBuilder.get().clazz(CIContacts.ClassLocation)
                            .attribute(CIContacts.ClassLocation.LocationAdressStreet);
            print.addSelect(selTaxN, selIDC, selStreet);
            print.execute();
            final String taxnumber = print.<String>getSelect(selTaxN);
            final String idcard = print.<String>getSelect(selIDC);
            final String street = print.<String>getSelect(selStreet);

            final boolean hasDOI = taxnumber == null
                            || (taxnumber.length() < 1 && idcard != null && idcard.length() > 1);

            strBldr.append(hasDOI ? DBProperties.getProperty("Contacts_ClassPerson/IdentityCard.Label")
                            : DBProperties.getProperty("Contacts_ClassOrganisation/TaxNumber.Label"))
                            .append(": ").append(hasDOI ? idcard : taxnumber);

            if (street != null && !street.isEmpty()) {
                strBldr.append("  -  ").append(DBProperties.getProperty("Contacts_ClassLocation.Label"))
                        .append(": ").append(street);
            }
        }
        return _escape ? StringEscapeUtils.escapeEcmaScript(strBldr.toString()) : strBldr.toString();
    }

    public String getFieldValue4Contact(final Instance _instance)
        throws EFapsException
    {
        return getFieldValue4Contact(_instance, true);

    }

    /**
     * Method to check if the instance is of the classification Carrier
     *
     * @param _parameter as passed from eFaps API.
     * @return Return ret.
     * @throws EFapsException on error.
     */
    public Return checkContactsClassCarrier(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final Instance instance = _parameter.getInstance();
        final String clazz = "Sales_Contacts_ClassCarrier";
        final Classification classif = (Classification) Type.get(clazz);

        final QueryBuilder queryBldr = new QueryBuilder(CIContacts.Contact);
        queryBldr.addWhereClassification(classif);
        queryBldr.addWhereAttrEqValue(CIContacts.Contact.ID, instance.getId());
        final MultiPrintQuery multi = queryBldr.getPrint();
        multi.execute();
        if (multi.next()) {
            ret.put(ReturnValues.TRUE, true);
        }

        return ret;
    }

    public Return updateFields4PersonNames(final Parameter _parameter)
    {
        final Return ret = new Return();
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final Map<String, String> map = new HashMap<String, String>();

        final String names = _parameter.getParameterValue(CIFormContacts.Contacts_ClassPersonForm.forename.name);
        final String fLastName = _parameter
                        .getParameterValue(CIFormContacts.Contacts_ClassPersonForm.firstLastName.name);
        final String sLastName = _parameter
                        .getParameterValue(CIFormContacts.Contacts_ClassPersonForm.secondLastName.name);

        final StringBuilder contactName = new StringBuilder();

        if (fLastName != null && !fLastName.isEmpty()) {
            contactName.append(fLastName);
        }
        if (sLastName != null && !sLastName.isEmpty()) {
            contactName.append(contactName.length() > 0 ? " " : "").append(sLastName);
        }

        if (names != null && !names.isEmpty()) {
            contactName.append(contactName.length() > 0 ? ", " : "").append(names);
        }

        map.put(CIFormContacts.Contacts_ContactForm.name.name, contactName.toString());
        list.add(map);

        ret.put(ReturnValues.VALUES, list);
        return ret;
    }
}
