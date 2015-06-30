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

package org.efaps.esjp.contacts.util;

import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id$
 */
@EFapsUUID("34d740a6-a9de-4f88-b1e9-06c16ecad3ef")
@EFapsApplication("eFapsApp-Contacts")
public interface ContactsSettings
{

    /**
     * OID for a Link.<br/>
     * Default Currency for the Form like Invoice etc..
     */
    String BASE = "org.efaps.contacts.";

    /**
     * Boolean (true/false).<br/>
     * Activate the ContactGroup UserInterface.
     */
    String ACTIVATECONTACTGROUPS = ContactsSettings.BASE + "ActivateContactGroups";

    /**
     * Boolean (true/false).<br/>
     * Activate the SubContacts UserInterface.
     */
    String ACTIVATESUBCONTACTS = ContactsSettings.BASE + "ActivateSubContacts";

    /**
     * Boolean (true/false).<br/>
     * Activate the Financial Information Classification.
     */
    String CLASSFINANCIALACTIVATE = ContactsSettings.BASE + "ActivateClassFinancialInformation";

    /**
     * Boolean (true/false).<br/>
     * Activate the search in tradename for autocopmlete.
     */
    String ACTIVATETRADENAMESEARCH = ContactsSettings.BASE + "ActivateTradeNameSearch";
}
