/*
 * Copyright 2003 - 2011 The eFaps Team
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

import java.util.HashMap;
import java.util.Map;

import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Insert;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.PrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.db.SelectBuilder;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.ui.wicket.util.EFapsKey;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("5bdf1780-6b34-4d7d-a1fd-01de63a71037")
@EFapsRevision("$Rev$")
public abstract class ContactsPicker_Base
{
    /**
     * Executed on validate event to check the information for a new contact.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @return Return containing true if valid
     * @throws EFapsException on error
     */
    public Return validateContact(final Parameter _parameter)
        throws EFapsException
    {
        final Return ret = new Return();
        final String name = _parameter.getParameterValue("name");
        final String taxNumber = _parameter.getParameterValue("taxNumber");
        final StringBuilder warnHtml = validateName4Contact(_parameter, name);
        final StringBuilder errorHtml = validateTaxNumber4Contact(_parameter, taxNumber);

        if (errorHtml.length() == 0) {
            ret.put(ReturnValues.TRUE, true);
        }
        if (warnHtml.length() != 0 || errorHtml.length() != 0) {
            ret.put(ReturnValues.SNIPLETT, warnHtml.append(errorHtml).toString());
        }
        return ret;
    }

    /**
     * Method for return the name of a contact.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @param _html         StringBuilder to append to
     * @param _name String
     * @return StringBuilder with html.
     * @throws EFapsException on error
     */
    protected StringBuilder validateName4Contact(final Parameter _parameter,
                                                 final String _name)
        throws EFapsException
    {
        final StringBuilder html = new StringBuilder();
        final QueryBuilder queryBldr = new QueryBuilder(CIContacts.Contact);
        queryBldr.addWhereAttrEqValue(CIContacts.Contact.Name, _name).setIgnoreCase(true);
        final InstanceQuery query = queryBldr.getQuery();
        if (!query.execute().isEmpty()) {
            html.append("<div style=\"text-align:center;\">")
                 .append(DBProperties.getProperty("org.efaps.esjp.contacts.ContactsPicker.existingContact"))
                 .append("</div>");
        }
        return html;
    }

    /**
     * Method for search the taxNumber of the Contact if exists
     * return SNIPLETT.
     *
     * @param _parameter Parameter as passed from the eFaps API
     * @param _taxNumber TaxNumber.
     * @return html StringBuilder.
     * @throws EFapsException on error.
     */
    protected StringBuilder validateTaxNumber4Contact(final Parameter _parameter,
                                                      final String _taxNumber)
        throws EFapsException
    {
        final StringBuilder html = new StringBuilder();
        final QueryBuilder queryBldr = new QueryBuilder(CIContacts.ClassOrganisation);
        queryBldr.addWhereAttrEqValue(CIContacts.ClassOrganisation.TaxNumber, _taxNumber);
        final InstanceQuery query = queryBldr.getQuery();
        if (!query.execute().isEmpty()) {
            html.append("<div style=\"text-align:center;\">")
                 .append(DBProperties.getProperty("org.efaps.esjp.contacts.ContactsPicker.existingTaxNumber"))
                 .append("</div>");
        }
        return html;
    }

    /**
     * Executed on picker event. Creates a new contact and returns it
     * as map to the calling form picker.
     *
     * @param _parameter    Parameter as passed from the eFaps API
     * @return map for the picker to fill the form
     * @throws EFapsException on error
     */
    public Return picker4NewContact(final Parameter _parameter)
        throws EFapsException
    {
        final Return retVal = new Return();
        final Map<String, String> map = new HashMap<String, String>();
        retVal.put(ReturnValues.VALUES, map);
        final String name = _parameter.getParameterValue("name");
        final Insert insert = new Insert(CIContacts.Contact);
        insert.add(CIContacts.Contact.Name, name);
        insert.execute();

        final Instance contactInst = insert.getInstance();

        if (contactInst != null && contactInst.isValid()) {
            // create classifications
            final Classification classification = (Classification) CIContacts.ClassOrganisation.getType();
            final Insert relInsert1 = new Insert(classification.getClassifyRelationType());
            relInsert1.add(classification.getRelLinkAttributeName(), contactInst.getId());
            relInsert1.add(classification.getRelTypeAttributeName(), classification.getId());
            relInsert1.execute();

            final Insert classInsert1 = new Insert(classification);
            classInsert1.add(classification.getLinkAttributeName(), contactInst.getId());
            classInsert1.add(CIContacts.ClassOrganisation.TaxNumber, _parameter.getParameterValue("taxNumber"));
            classInsert1.execute();

            final Classification classification2 = (Classification) classification.getParentClassification();
            final Insert relInsert2 = new Insert(classification2.getClassifyRelationType());
            relInsert2.add(classification.getRelLinkAttributeName(), contactInst.getId());
            relInsert2.add(classification.getRelTypeAttributeName(), classification2.getId());
            relInsert2.execute();

            final Insert classInsert2 = new Insert(classification2);
            classInsert2.add(classification.getLinkAttributeName(), contactInst.getId());
            classInsert2.execute();

            map.put(EFapsKey.PICKER_VALUE.getKey(), name);
            map.put("contact", contactInst.getOid());
            map.put("contactData", getFieldValue4Contact(_parameter, contactInst));
        }
        return retVal;
    }


    /**
     * Method to get the value for the field directly under the Contact.
     *
     * @param _parameter    Parameter as passed from the eFaps API
     * @param _instance     Instance of the contact
     * @return String for the field
     * @throws EFapsException on error
     */
    public String getFieldValue4Contact(final Parameter _parameter,
                                        final Instance _instance)
        throws EFapsException
    {
        final PrintQuery print = new PrintQuery(_instance);
        final SelectBuilder selTaxNumber = new SelectBuilder().clazz(CIContacts.ClassOrganisation)
            .attribute(CIContacts.ClassOrganisation.TaxNumber);
        final SelectBuilder selIdCard = new SelectBuilder().clazz(CIContacts.ClassPerson)
            .attribute(CIContacts.ClassPerson.IdentityCard);

        print.addSelect(selTaxNumber, selIdCard);
        print.execute();
        final String taxnumber = print.<String> getSelect(selTaxNumber);
        final String idcard = print.<String> getSelect(selIdCard);
        final boolean dni = taxnumber == null || (taxnumber.length() < 1 && idcard != null && idcard.length() > 1);

        final StringBuilder strBldr = new StringBuilder();
        strBldr.append(dni ? DBProperties.getProperty(CIContacts.ClassPerson.getType().getName()
                        + "/" + CIContacts.ClassPerson.IdentityCard.name + ".Label")
                        : DBProperties.getProperty(CIContacts.ClassOrganisation.getType().getName()
                                        + "/" + CIContacts.ClassOrganisation.TaxNumber.name + ".Label"))
                        .append(": ").append(dni ? idcard : taxnumber).append("  -  ");
        return strBldr.toString();
    }
}
