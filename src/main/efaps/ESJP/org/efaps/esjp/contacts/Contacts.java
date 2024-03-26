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

import org.efaps.admin.event.Parameter;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;

/**
 * This class must be replaced for customization, therefore it is left empty.
 * Functional description can be found in the related "<code>_base</code>"
 * class.
 *
 * @author The eFaps Team
 */
@EFapsUUID("2fa82934-00bf-4828-994c-b6cc41672614")
@EFapsApplication("eFapsApp-Contacts")
public class Contacts
    extends Contacts_Base
{

    /**
     * Checks if is foreign.
     *
     * @param _parameter Parameter as passed by the eFaps API
     * @param _contactInst the contact inst
     * @return true, if is foreign
     * @throws EFapsException on error
     */
    public static boolean isForeign(final Parameter _parameter,
                                    final Instance _contactInst)
        throws EFapsException
    {
        return Contacts_Base.isForeign(_parameter, _contactInst);
    }
}
