/*
 * Copyright 2003 - 2024 The eFaps Team
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
package org.efaps.esjp.contacts.graphql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Instance;
import org.efaps.db.stmt.selection.Evaluator;
import org.efaps.eql.EQL;
import org.efaps.eql.builder.Print;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.esjp.db.InstanceUtils;
import org.efaps.esjp.graphql.BaseDataFetcher;
import org.efaps.graphql.definition.ObjectDef;
import org.efaps.util.EFapsException;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNamedSchemaElement;
import graphql.schema.GraphQLNamedType;

;@EFapsUUID("484b0053-6204-419e-873b-40b1d25ffa35")
@EFapsApplication("eFapsApp-Contacts")
public abstract class ContactDataFetcher_Base
    extends BaseDataFetcher
{

    @Override
    public Object get(final DataFetchingEnvironment environment)
        throws Exception
    {
        final Map<String, Object> source = environment.getSource();
        Instance contactInst = null;
        if (source != null) {
            final var contactObject = source.get(environment.getField().getName());
            if (contactObject instanceof Instance) {
                contactInst = (Instance) contactObject;
            } else if (contactObject instanceof String) {
                contactInst = Instance.get((String) contactObject);
            }
        }
        final String graphTypeName;
        if (environment.getFieldType() instanceof GraphQLList) {
            graphTypeName = ((GraphQLNamedType) ((GraphQLList) environment.getFieldType()).getWrappedType()).getName();
        } else {
            graphTypeName = ((GraphQLNamedType) environment.getFieldType()).getName();
        }
        final Optional<ObjectDef> objectDefOpt = environment.getGraphQlContext().getOrEmpty(graphTypeName);
        final var keyMapping = new HashMap<String, String>();
        if (objectDefOpt.isPresent()) {
            final var objectDef = objectDefOpt.get();
            final String[] words = { "idTypeKey", "idTypeName", "idNumber" };
            for (final var child : environment.getFieldType().getChildren()) {
                final var fieldDef = objectDef.getFields().get(((GraphQLNamedSchemaElement) child).getName());
                if (fieldDef != null && StringUtils.isNotEmpty(fieldDef.getSelect())
                                && Arrays.stream(words).anyMatch(fieldDef.getSelect()::equals)) {
                    keyMapping.put(fieldDef.getSelect(), fieldDef.getName());
                }
            }
        }
        Object data = null;
        if (InstanceUtils.isType(contactInst, CIContacts.Contact)) {
            final var print = EQL.builder()
                            .print(contactInst);
            addSelects(print);
            final var evaluator = print.evaluate();
            data = getValueMap(keyMapping, evaluator);
        } else {
            final List<Map<String, String>> values = new ArrayList<>();
            final var print = EQL.builder()
                            .print()
                            .query(CIContacts.Contact)
                            .select();
            addSelects(print);
            final var evaluator = print.evaluate();
            while (evaluator.next()) {
                values.add(getValueMap(keyMapping, evaluator));
            }
            data = values;
        }
        return DataFetcherResult.newResult()
                        .data(data)
                        .build();
    }

    public Map<String, String> getValueMap(final Map<String, String> keyMapping,
                                           final Evaluator evaluator)
        throws EFapsException
    {
        final Map<String, String> map = new HashMap<>();
        map.put("name", evaluator.get(CIContacts.Contact.Name));
        final String taxNumber = evaluator.get("taxNumber");
        if (taxNumber != null) {
            map.put(keyMapping.containsKey("idTypeKey") ? keyMapping.get("idTypeKey") : "idTypeKey", "06");
            map.put(keyMapping.containsKey("idTypeName") ? keyMapping.get("idTypeName") : "idTypeName", "RUC");
            map.put(keyMapping.containsKey("idNumber") ? keyMapping.get("idNumber") : "idNumber", taxNumber);
        } else {
            map.put(keyMapping.containsKey("idTypeKey") ? keyMapping.get("idTypeKey") : "idTypeKey",
                            evaluator.get("doiTypeValue"));
            map.put(keyMapping.containsKey("idTypeName") ? keyMapping.get("idTypeName") : "idTypeName",
                            evaluator.get("doiTypeDescr"));
            map.put(keyMapping.containsKey("idNumber") ? keyMapping.get("idNumber") : "idNumber",
                            evaluator.get("identityCard"));
        }
        return map;
    }

    public void addSelects(final Print print)
    {
        print.attribute(CIContacts.Contact.Name)
                        .clazz(CIContacts.ClassOrganisation).attribute(CIContacts.ClassOrganisation.TaxNumber)
                        .as("taxNumber")
                        .clazz(CIContacts.ClassPerson).attribute(CIContacts.ClassPerson.IdentityCard)
                        .as("identityCard")
                        .clazz(CIContacts.ClassPerson).linkto(CIContacts.ClassPerson.DOITypeLink)
                        .attribute(CIContacts.AttributeDefinitionDOIType.Value).as("doiTypeValue")
                        .clazz(CIContacts.ClassPerson).linkto(CIContacts.ClassPerson.DOITypeLink)
                        .attribute(CIContacts.AttributeDefinitionDOIType.Description).as("doiTypeDescr");
    }
}
