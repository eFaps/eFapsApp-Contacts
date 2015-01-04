/*
 * Copyright 2003 - 2015 The eFaps Team
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
 * Revision:        $Rev: 13356 $
 * Last Changed:    $Date: 2014-07-17 17:54:10 -0500 (Thu, 17 Jul 2014) $
 * Last Changed By: $Author: jan@moxter.net $
 */

package org.efaps.esjp.contacts.listener;

import java.util.Map;

import org.efaps.admin.event.Parameter;
import org.efaps.admin.program.esjp.EFapsRevision;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.admin.program.esjp.IEsjpListener;
import org.efaps.db.Instance;
import org.efaps.util.EFapsException;

/**
 * TODO comment!
 *
 * @author The eFaps Team
 * @version $Id: IOnCreateDocument.java 13356 2014-07-17 22:54:10Z jan@moxter.net $
 */
@EFapsUUID("ae413b0b-a7cb-447f-9b53-deb26bf0aa63")
@EFapsRevision("$Rev: 13356 $")
public interface IOnContact
    extends IEsjpListener
{
    void add2UpdateMap4Contact(final Parameter _parameter,
                               final Instance _contactInstance,
                               final Map<String, Object> _map)
        throws EFapsException;
}
