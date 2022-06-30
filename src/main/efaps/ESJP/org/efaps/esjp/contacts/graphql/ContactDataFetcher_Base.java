/*
 * Copyright 2003 - 2022 The eFaps Team
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

import java.util.HashMap;
import java.util.Map;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Instance;
import org.efaps.eql.EQL;
import org.efaps.esjp.ci.CIContacts;
import org.efaps.esjp.db.InstanceUtils;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

@EFapsUUID("484b0053-6204-419e-873b-40b1d25ffa35")
@EFapsApplication("eFapsApp-Contacts")
public abstract class ContactDataFetcher_Base
    implements DataFetcher<Object>
{

    @Override
    public Object get(final DataFetchingEnvironment _environment)
        throws Exception
    {
        final Map<String, Object> source = _environment.getSource();
        final var contactObject = source.get(_environment.getField().getName());
        Instance contactInst = null;
        if (contactObject instanceof Instance) {
            contactInst = (Instance) contactObject;
        } else if (contactObject instanceof String) {
            contactInst = Instance.get((String) contactObject);
        }
        final Map<String, String> data = new HashMap<>();
        if (InstanceUtils.isType(contactInst, CIContacts.Contact)) {
           final var evaluator = EQL.builder()
               .print(contactInst)
               .attribute(CIContacts.Contact.Name)
               .clazz(CIContacts.ClassOrganisation).attribute(CIContacts.ClassOrganisation.TaxNumber).as("taxNumber")
               .clazz(CIContacts.ClassPerson).attribute(CIContacts.ClassPerson.IdentityCard).as("identityCard")
               .clazz(CIContacts.ClassPerson).linkto(CIContacts.ClassPerson.DOITypeLink)
                   .attribute(CIContacts.AttributeDefinitionDOIType.Value).as("doiTypeValue")
               .clazz(CIContacts.ClassPerson).linkto(CIContacts.ClassPerson.DOITypeLink)
                   .attribute(CIContacts.AttributeDefinitionDOIType.Description).as("doiTypeDescr")
               .evaluate();
           data.put("name", evaluator.get(CIContacts.Contact.Name));
           final String taxNumber = evaluator.get("taxNumber");
           if (taxNumber != null) {
               data.put("idTypeKey", "07");
               data.put("idTypeName", "RUC");
               data.put("idNumber", taxNumber);
           } else {
               data.put("idTypeKey", evaluator.get("doiTypeValue"));
               data.put("idTypeName",  evaluator.get("doiTypeDescr"));
               data.put("idNumber", evaluator.get("identityCard"));
           }
        }
        return DataFetcherResult.newResult()
                        .data(data)
                        .build();
    }
}
