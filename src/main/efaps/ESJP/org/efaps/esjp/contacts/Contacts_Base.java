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
package org.efaps.esjp.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.efaps.admin.datamodel.Classification;
import org.efaps.admin.datamodel.Status;
import org.efaps.admin.datamodel.Type;
import org.efaps.admin.event.Parameter;
import org.efaps.admin.event.Parameter.ParameterValues;
import org.efaps.admin.event.Return;
import org.efaps.admin.event.Return.ReturnValues;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.program.esjp.Listener;
import org.efaps.admin.ui.AbstractCommand;
import org.efaps.admin.ui.AbstractUserInterfaceObject.TargetMode;
import org.efaps.admin.ui.field.Field;
import org.efaps.db.AttributeQuery;
import org.efaps.db.CachedPrintQuery;
import org.efaps.db.Instance;
import org.efaps.db.InstanceQuery;
import org.efaps.db.MultiPrintQuery;
import org.efaps.db.PrintQuery;
import org.efaps.db.QueryBuilder;
import org.efaps.db.SelectBuilder;
import org.efaps.db.Update;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.esjp.ci.CIERP;
import org.efaps.esjp.ci.CIFormContacts;
import org.efaps.esjp.ci.CIMsgContacts;
import org.efaps.esjp.common.AbstractCommon;
import org.efaps.esjp.common.util.InterfaceUtils;
import org.efaps.esjp.contacts.listener.IOnContact;
import org.efaps.esjp.contacts.util.Contacts;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 */
@EFapsUUID("0999190f-d2bb-43e7-a6e6-49fdc5a31b95")
@EFapsApplication("eFapsApp-Contacts")
public abstract class Contacts_Base
    extends AbstractCommon
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

            final Map<Long, String> ids = new HashMap<>();
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
        final List<Map<String, String>> list = new ArrayList<>();

        final String key = containsProperty(_parameter, "Key") ? getProperty(_parameter, "Key") : "OID";

        final QueryBuilder queryBldr = getQueryBldr4AutoComplete(_parameter);
        add2QueryBuilder4BlockGroup(_parameter, queryBldr);

        final boolean nameSearch = !Character.isDigit(input.charAt(0));
        if (nameSearch) {
            queryBldr.addWhereAttrMatchValue(CIContacts.Contact.Name, input + "*").setIgnoreCase(true);
            queryBldr.addOrderByAttributeAsc(CIContacts.Contact.Name);
            InterfaceUtils.addMaxResult2QueryBuilder4AutoComplete(_parameter, queryBldr);

            final List<Instance> instances = queryBldr.getQuery().execute();

            if (Contacts.ACTIVATETRADENAMESEARCH.get()) {
                final QueryBuilder orgAttrQueryBldr = new QueryBuilder(CIContacts.ClassOrganisation);
                orgAttrQueryBldr.addWhereAttrMatchValue(CIContacts.ClassOrganisation.TradeName, input + "*")
                    .setIgnoreCase(true);
                final QueryBuilder subQueryBldr = getQueryBldr4AutoComplete(_parameter);
                subQueryBldr.addWhereAttrInQuery(CIContacts.Contact.ID,
                                orgAttrQueryBldr.getAttributeQuery(CIContacts.ClassOrganisation.ContactLink));

                InterfaceUtils.addMaxResult2QueryBuilder4AutoComplete(_parameter, subQueryBldr);
                final List<Instance> subInstances = subQueryBldr.getQuery().execute();
                for (final Instance instance : subInstances) {
                    if (!instances.contains(instance)) {
                        instances.add(instance);
                    }
                }
            }

            final MultiPrintQuery multi = new MultiPrintQuery(instances);
            multi.addAttribute(CIContacts.Contact.Name);
            multi.addAttribute(key);
            multi.setEnforceSorted(true);
            multi.execute();
            while (multi.next()) {
                final String name = multi.<String>getAttribute(CIContacts.Contact.Name);
                final String keyVal = multi.getAttribute(key).toString();
                final Map<String, String> map = new HashMap<>();
                map.put("eFapsAutoCompleteKEY", keyVal);
                map.put("eFapsAutoCompleteVALUE", name);
                list.add(map);
            }
        } else {
            final QueryBuilder filterAttrQueryBldr = new QueryBuilder(CIContacts.Contact);
            filterAttrQueryBldr.setOr(true);

            final QueryBuilder orgAttrQueryBldr = new QueryBuilder(CIContacts.ClassOrganisation);
            orgAttrQueryBldr.addWhereAttrMatchValue(CIContacts.ClassOrganisation.TaxNumber, input + "*");
            orgAttrQueryBldr.addOrderByAttributeAsc(CIContacts.ClassOrganisation.TaxNumber);
            InterfaceUtils.addMaxResult2QueryBuilder4AutoComplete(_parameter, orgAttrQueryBldr);
            final AttributeQuery orgAttrQuery = orgAttrQueryBldr
                            .getAttributeQuery(CIContacts.ClassOrganisation.ContactLink);

            final QueryBuilder persAttrQueryBldr = new QueryBuilder(CIContacts.ClassPerson);
            persAttrQueryBldr.addWhereAttrMatchValue(CIContacts.ClassPerson.IdentityCard, input + "*");
            persAttrQueryBldr.addOrderByAttributeAsc(CIContacts.ClassPerson.IdentityCard);
            InterfaceUtils.addMaxResult2QueryBuilder4AutoComplete(_parameter, persAttrQueryBldr);
            final AttributeQuery persAttrQuery = persAttrQueryBldr
                            .getAttributeQuery(CIContacts.ClassPerson.ContactLink);

            filterAttrQueryBldr.addWhereAttrInQuery(CIContacts.Contact.ID, orgAttrQuery);
            filterAttrQueryBldr.addWhereAttrInQuery(CIContacts.Contact.ID, persAttrQuery);
            final AttributeQuery filterAttrQuery = filterAttrQueryBldr.getAttributeQuery(CIContacts.Contact.ID);

            queryBldr.addWhereAttrInQuery(CIContacts.Contact.ID, filterAttrQuery);

            final MultiPrintQuery multi = queryBldr.getPrint();

            final SelectBuilder taxSel = SelectBuilder.get().clazz(CIContacts.ClassOrganisation)
                            .attribute(CIContacts.ClassOrganisation.TaxNumber);
            final SelectBuilder doiSel = SelectBuilder.get().clazz(CIContacts.ClassPerson)
                            .attribute(CIContacts.ClassPerson.IdentityCard);
            multi.addSelect(taxSel, doiSel);
            multi.addAttribute(CIContacts.Contact.Name);
            multi.addAttribute(key);
            multi.execute();
            while (multi.next()) {
                final Map<String, String> map = new HashMap<>();
                final String tax = multi.<String>getSelect(taxSel);
                final String doi = multi.<String>getSelect(doiSel);
                final String choice = (StringUtils.isEmpty(tax) ? doi : tax) + " - "
                                + multi.<String>getAttribute(CIContacts.Contact.Name);
                map.put("eFapsAutoCompleteKEY", multi.getAttribute(key).toString());
                map.put("eFapsAutoCompleteVALUE", multi.<String>getAttribute(CIContacts.Contact.Name));
                map.put("eFapsAutoCompleteCHOICE", choice);
                list.add(map);
            }

            Collections.sort(list, (_o1,
             _o2) -> _o1.get("eFapsAutoCompleteCHOICE").compareTo(
                            _o2.get("eFapsAutoCompleteCHOICE")));
        }
        final Return retVal = new Return();
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     * Adds the 2 query builder 4 block group.
     *
     * @param _parameter the parameter
     * @param _queryBldr the query bldr
     * @throws EFapsException the e faps exception
     */
    public void add2QueryBuilder4BlockGroup(final Parameter _parameter,
                                            final QueryBuilder _queryBldr)
        throws EFapsException
    {
        if (Contacts.ACTIVATEBLOCKGROUPS.get()) {
            final QueryBuilder queryBldr = new QueryBuilder(CIContacts.BlockGroup);
            queryBldr.addWhereAttrEqValue(CIContacts.BlockGroup.Status, Status.find(CIContacts.ContactStatus.Active));
            final InstanceQuery query = queryBldr.getCachedQuery("BlockGroup")
                            .setLifespanUnit(TimeUnit.MINUTES).setLifespan(5);
            query.execute();
            final Set<Instance> blockGroupInsts = new HashSet<>();
            while (query.next()) {
                final Properties props = Contacts.getSysConfig()
                                .getObjectAttributeValueAsProperties(query.getCurrentValue());
                String key = null;
                String prefix = _parameter.get(ParameterValues.ACCESSMODE) == null
                                ? null
                                : String.valueOf(_parameter.get(ParameterValues.ACCESSMODE));
                if (containsProperty(_parameter, "Key4BlockGroup")) {
                    key = getProperty(_parameter, "Key4BlockGroup");
                } else if (!"false".equalsIgnoreCase(props.getProperty("CheckCallCMD", "true"))) {
                    final AbstractCommand cmd = (AbstractCommand) _parameter.get(ParameterValues.CALL_CMD);
                    if (cmd != null && cmd.getTargetCreateType() != null) {
                        key = cmd.getTargetCreateType().getName();
                        if (prefix == null) {
                            prefix = TargetMode.CREATE.name();
                        }
                    }
                }
                if (key == null && InstanceUtils.isValid(_parameter.getInstance())) {
                    key = _parameter.getInstance().getType().getName();
                }
                if (key == null && InstanceUtils.isValid(_parameter.getCallInstance())) {
                    key = _parameter.getCallInstance().getType().getName();
                }
                if (key != null) {
                    if (props.containsKey(prefix + "." + key)) {
                        if (BooleanUtils.toBoolean(
                                        props.getProperty(_parameter.get(ParameterValues.ACCESSMODE) + "." + key))) {
                            blockGroupInsts.add(query.getCurrentValue());
                        }
                    } else if (props.containsKey(key)) {
                        if (BooleanUtils.toBoolean(props.getProperty(key))) {
                            blockGroupInsts.add(query.getCurrentValue());
                        }
                    }
                }
            }
            if (!blockGroupInsts.isEmpty()) {
                final QueryBuilder attrQueryBldr = new QueryBuilder(CIContacts.BlockGroup2Contact);
                attrQueryBldr.addWhereAttrEqValue(CIContacts.BlockGroup2Contact.FromLink, blockGroupInsts.toArray());
                _queryBldr.addWhereAttrNotInQuery(CIContacts.ContactAbstract.ID,
                                attrQueryBldr.getAttributeQuery(CIContacts.BlockGroup2Contact.ToLink));
            }
        }
    }

    /**
     * @param _parameter Parameter as passed from the eFaps API.
     * @return QueryBuilder used for Autocomplete
     * @throws EFapsException on error.
     */
    protected QueryBuilder getQueryBldr4AutoComplete(final Parameter _parameter)
        throws EFapsException
    {
        QueryBuilder ret = getQueryBldrFromProperties(_parameter);
        // if the QueryBuilder was not set via the Properties, set defaults
        if (ret == null) {
            ret = new QueryBuilder(CIContacts.Contact);
            final Map<Integer, String> classes = analyseProperty(_parameter, "Classification");
            if (!classes.isEmpty()) {
                final List<Classification> classTypes = new ArrayList<>();
                for (final String clazz : classes.values()) {
                    classTypes.add((Classification) Type.get(clazz));
                }
                ret.addWhereClassification(classTypes.toArray(new Classification[classTypes.size()]));
            }
            ret.addWhereAttrEqValue(CIContacts.Contact.Status, CIContacts.ContactStatus.Active);
        }
        return ret;
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

        final List<Map<String, Object>> list = new ArrayList<>();
        final Map<String, Object> map = new HashMap<>();

        final String targetFieldName;
        if (containsProperty(_parameter, "FieldName")) {
            targetFieldName = getProperty(_parameter, "FieldName");
        } else {
            targetFieldName = "contactData";
        }

        if (instance.isValid()) {
            map.put(targetFieldName, getFieldValue4Contact(instance));
        } else {
            map.put(targetFieldName, "????");
        }
        add2UpdateMap4Contact(_parameter, instance,  map);
        list.add(map);

        final Return retVal = new Return();
        retVal.put(ReturnValues.VALUES, list);
        return retVal;
    }

    /**
     * @param _parameter Parameter as passed by the eFaps API
     * @param _contactInstance instance of the contact
     * @param _map  map to be added to
     * @throws EFapsException on error
     */
    protected void add2UpdateMap4Contact(final Parameter _parameter,
                                         final Instance _contactInstance,
                                         final Map<String, Object> _map)
        throws EFapsException
    {
        for (final IOnContact listener : Listener.get().<IOnContact>invoke(IOnContact.class)) {
            listener.add2UpdateMap4Contact(_parameter, _contactInstance, _map);
        }
    }

    /**
     * Method to get the value for the field directly under the Contact.
     *
     * @param _instance Instacne of the contact
     * @param _escape the escape
     * @return String for the field
     * @throws EFapsException on error
     */
    public String getFieldValue4Contact(final Instance _instance,
                                        final boolean _escape)
        throws EFapsException
    {
        String ret = null;
        if (_instance.isValid()) {
            final PrintQuery print = new PrintQuery(_instance);
            print.addMsgPhrase(CIMsgContacts.ContactInfoMsgPhrase);
            print.execute();
            ret = print.getMsgPhrase(CIMsgContacts.ContactInfoMsgPhrase);
        }
        return ret == null ? "" : _escape ? StringEscapeUtils.escapeEcmaScript(ret) : ret;
    }

    /**
     * Gets the field value4 contact.
     *
     * @param _instance the instance
     * @return the field value4 contact
     * @throws EFapsException on error
     */
    public String getFieldValue4Contact(final Instance _instance)
        throws EFapsException
    {
        return getFieldValue4Contact(_instance, true);
    }

    /**
     * Method to check if the instance is of the classification Carrier.
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

    /**
     * Update fields4 person names.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @return the return
     */
    public Return updateFields4PersonNames(final Parameter _parameter)
    {
        final Return ret = new Return();
        final List<Map<String, String>> list = new ArrayList<>();
        final Map<String, String> map = new HashMap<>();

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

    /**
     * Checks if is foreign.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _contactInst the contact inst
     * @return true, if is foreign
     * @throws EFapsException on error
     */
    protected static boolean isForeign(final Parameter _parameter,
                                       final Instance _contactInst)
        throws EFapsException
    {
        final SelectBuilder selCountryInst = SelectBuilder.get().clazz(CIContacts.ClassLocation).linkto(
                        CIContacts.ClassLocation.LocationCountryLink).instance();
        final PrintQuery print = CachedPrintQuery.get4Request(_contactInst);
        print.addSelect(selCountryInst);
        print.executeWithoutAccessCheck();

        final Instance countryInst = print.getSelect(selCountryInst);
        final Instance homeCountyrInst =  Contacts.HOMECOUNTRY.get();
        return InstanceUtils.isValid(homeCountyrInst) && InstanceUtils.isValid(countryInst)
                        && !homeCountyrInst.equals(countryInst);
    }
}
