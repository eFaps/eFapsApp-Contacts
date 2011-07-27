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
 * Revision:        $Rev: 1 $
 * Last Changed:    $Date: 2011-06-13 15:23:17 -0500 (lun, 13 jun 2011) $
 * Last Changed By: $Author: Luis Estrada $
 */

package org.efaps.esjp.contacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.efaps.admin.dbproperty.DBProperties;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Context;
import org.efaps.db.Instance;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.PrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.db.Update;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.util.EFapsException;

/**
 * This class must be replaced for customization, therefore it is left empty.
 * Functional description can be found in the related "<code>_base</code>"
 * class.
 *
 * @author The eFaps Team
 * @version $Id: MoveObject_Base.java 1 2011-06-13 20:23:17Z Luis Estrada $
 */
@EFapsUUID("3dbb9818-1816-4944-be87-132bb1e67242")
@EFapsRevision("$Rev: 1 $")
public class MoveObject_Base
{

    /**
     * Used by the AutoCompleteField used in the select contact.
     *
     * @param _parameter Parameter as passed from the eFaps API.
     * @return map list for auto-complete.
     * @throws EFapsException on error.
     */
    public Return autoComplete4Contact(final Parameter _parameter)
        throws EFapsException
    {
        final String input = (String) _parameter.get(ParameterValues.OTHERS);
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final QueryBuilder queryBldr = new QueryBuilder(CIContacts.Contact);
        queryBldr.addWhereAttrMatchValue(CIContacts.Contact.Name, input + "*").setIgnoreCase(true);
        final MultiPrintQuery multi = queryBldr.getPrint();
        multi.addAttribute(CIContacts.Contact.OID, CIContacts.Contact.Name);
        multi.execute();
        while (multi.next()) {
            final String name = multi.<String>getAttribute(CIContacts.Contact.Name);
            final String oid = multi.<String>getAttribute(CIContacts.Contact.OID);
            final Map<String, String> map = new HashMap<String, String>();
            map.put("eFapsAutoCompleteKEY", oid);
            map.put("eFapsAutoCompleteVALUE", name);
            map.put("eFapsAutoCompleteCHOICE", name);
            list.add(map);
        }
        final Return retVal = new Return();
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     * Method to get the value for the field directly under the Contact.
     *
     * @param _instance Instacne of the contact
     * @return String for the field
     * @throws EFapsException on error
     */
    protected String getFieldValue4Contact(final Instance _instance)
        throws EFapsException
    {
        final PrintQuery print = new PrintQuery(_instance);
        print.addSelect("class[Sales_Contacts_ClassClient].attribute[BillingAdressStreet]");
        print.addSelect("class[Contacts_ClassOrganisation].attribute[TaxNumber]");
        print.addSelect("class[Contacts_ClassPerson].attribute[IdentityCard]");
        print.addSelect("class[Contacts_ClassLocation].attribute[LocationAdressStreet]");
        print.execute();
        final String taxnumber = print.<String>getSelect("class[Contacts_ClassOrganisation].attribute[TaxNumber]");
        final String idcard = print.<String>getSelect("class[Contacts_ClassPerson].attribute[IdentityCard]");
        final boolean dni = taxnumber == null || (taxnumber.length() < 1 && idcard != null && idcard.length() > 1);
        final String street = print.getSelect("class[Sales_Contacts_ClassClient].attribute[BillingAdressStreet]");
        final String locStreet = print.getSelect("class[Contacts_ClassLocation].attribute[LocationAdressStreet]");

        final StringBuilder strBldr = new StringBuilder();
        strBldr.append(dni ? DBProperties.getProperty("Contacts_ClassPerson/IdentityCard.Label")
                        : DBProperties.getProperty("Contacts_ClassOrganisation/TaxNumber.Label"))
                        .append(": ").append(dni ? idcard : taxnumber).append("  -  ")
                        .append(DBProperties.getProperty("Sales_Contacts_ClassClient/BillingAdressStreet.Label"))
                        .append(": ")
                        .append(street.length() > 0 ? street : locStreet);
        return strBldr.toString();
    }

    /**
     * @param _parameter Parameter as passeb by the eFaps API
     * @return update map
     * @throws EFapsException on error
     */
    public Return updateFields4Contact(final Parameter _parameter)
        throws EFapsException
    {
        final Instance instance = Instance.get(_parameter.getParameterValue("contact"));
        final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        final Map<String, String> map = new HashMap<String, String>();
        if (instance.getId() > 0) {
            map.put("contactData", getFieldValue4Contact(instance));
        } else {
            map.put("contactData", "????");
        }
        list.add(map);
        final Return retVal = new Return();
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     *
     * @param _parameter Parameter as passed Efaps API
     * @return Return
     * @throws EFapsException on error
     */
    public Return moveObject2Contact(final Parameter _parameter)
        throws EFapsException
    {
        final String contact = _parameter.getParameterValue("contact");
        final Map<?,?> map = (Map<?, ?>) _parameter.get(ParameterValues.PROPERTIES);
        final String attrChange = (String)map.get("AttributeName");
        final Instance instContact = Instance.get(contact);

        String[] oids = new String[0];
        if (Context.getThreadContext().containsSessionAttribute("storeOIDS")
                        && Context.getThreadContext().getSessionAttribute("storeOIDS") != null) {
            oids = (String[]) Context.getThreadContext().getSessionAttribute("storeOIDS");
            Context.getThreadContext().setSessionAttribute("storeOIDS", null);
        }

        if (oids.length > 0 && instContact.isValid()) {

            for (final String oid : oids) {

                final Update updateObject = new Update(oid);
                updateObject.add(attrChange, instContact.getId());
                updateObject.execute();

            }

        }

        return new Return();

    }

}
