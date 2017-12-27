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

package org.efaps.esjp.contacts.util;

import java.util.UUID;

import org.efaps.admin.common.SystemConfiguration;
import org.efaps.admin.program.esjp.EFapsApplication;
import org.efaps.admin.program.esjp.EFapsUUID;
import org.efaps.api.annotation.EFapsSysConfAttribute;
import org.efaps.api.annotation.EFapsSysConfLink;
import org.efaps.api.annotation.EFapsSystemConfiguration;
import org.efaps.esjp.admin.common.systemconfiguration.BooleanSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.StringSysConfAttribute;
import org.efaps.esjp.admin.common.systemconfiguration.SysConfLink;
import org.efaps.util.cache.CacheReloadException;

/**
 *
 * @author The eFaps Team
 */
@EFapsUUID("cf88f04d-4f77-44c0-9b79-6a121f95f5cc")
@EFapsApplication("eFapsApp-Contacts")
@EFapsSystemConfiguration("77f5f440-f251-46d0-8603-add22f99f7f0")
public final class Contacts
{
    /** The base. */
    public static final String BASE = "org.efaps.contacts.";

    /** Contacts-Configuration. */
    public static final UUID SYSCONFUUID = UUID.fromString("77f5f440-f251-46d0-8603-add22f99f7f0");

    /**
     * Key used for Caching of Queries. Should be rested when something in
     * Contacts is changed.
     */
    public static final String CACHKEY = "org.efaps.esjp.contacts.Contacts";

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute ACTIVATECONTACTGROUPS = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateContactGroups")
                    .description("Activate the ContactGroup UserInterface..");

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute ACTIVATESUBCONTACTS = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateSubContacts")
                    .description("Activate the SubContacts UserInterface.");

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute CLASSFINANCIALACTIVATE = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateClassFinancialInformation")
                    .description("Activate the ContactGroup UserInterface..");

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute ACTIVATETRADENAMESEARCH = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateTradeNameSearch")
                    .description("Activate the search in tradename for autocomplete.");

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute ACTIVATEBLOCKGROUPS = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateBlockGroups")
                    .description("Activate the BlockGroup Mechanism.");

    /** See description. */
    @EFapsSysConfAttribute
    public static final BooleanSysConfAttribute ACTIVATENOTE = new BooleanSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "ActivateNote")
                    .description("Activate the possiblity to add a general Note to a Contact.");

    /** See description. */
    @EFapsSysConfLink
    public static final SysConfLink HOMECOUNTRY = new SysConfLink()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "HomeCountry")
                    .description("Instanc eo fthe HomeCountry to be "
                                    + "able to distinguihs foreign contacts form lovcal contacts.");

    /** See description. */
    @EFapsSysConfAttribute
    public static final StringSysConfAttribute TAXID_RESTURI = new StringSysConfAttribute()
                    .sysConfUUID(Contacts.SYSCONFUUID)
                    .key(Contacts.BASE + "taxid.RESTURI")
                    .description("The base url for the REST interface.");

    /**
     * Singelton.
     */
    private Contacts()
    {
    }

    /**
     * @return the SystemConfigruation for Constacs
     * @throws CacheReloadException on error
     */
    public static SystemConfiguration getSysConfig()
        throws CacheReloadException
    {
        // Contacts-Configuration
        return SystemConfiguration.get(Contacts.SYSCONFUUID);
    }
}
