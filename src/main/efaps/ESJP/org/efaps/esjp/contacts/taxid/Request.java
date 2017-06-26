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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.esjp.contacts.util.Contacts;
import org.efaps.util.EFapsException;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class Request.
 *
 * @author The eFaps Team
 */
@EFapsUUID("9794de15-0c35-41d2-8632-fd8003fd6cc7")
@EFapsApplication("eFapsApp-Contacts")
public class Request
{

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Request.class);

    /**
     * Gets the info.
     *
     * @param _taxId the tax id
     * @return the info
     * @throws EFapsException on error
     */
    public InfoJson getInfoJson(final String _taxId)
        throws EFapsException
    {
        Request.LOG.debug("Request TaxId: {}", _taxId);
        InfoJson ret = null;
        if (Contacts.TAXID_RESTURI.exists()) {
            final ClientConfig clientConfig = new ClientConfig();

            final Client client = ClientBuilder.newClient(clientConfig).register(JacksonFeature.class);

            final WebTarget webTarget = client.target(Contacts.TAXID_RESTURI.get());

            ret = webTarget.path(_taxId).request().accept(MediaType.APPLICATION_JSON_TYPE).get(InfoJson.class);

            Request.LOG.debug("Retrieved TaxIdInfo: {}", ret);
        }
        return ret;
    }
}
