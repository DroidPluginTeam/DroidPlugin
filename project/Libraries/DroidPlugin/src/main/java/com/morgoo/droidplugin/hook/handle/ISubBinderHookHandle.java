/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/
package com.morgoo.droidplugin.hook.handle;

import android.content.Context;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.HookedMethodHandler;
import com.morgoo.helper.compat.ISubCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/5/6.
 */
public class ISubBinderHookHandle extends BaseHookHandle {

    public ISubBinderHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {

//        interface ISub {
//            /**
//             * @param callingPackage The package maing the call.
//             * @return a list of all subscriptions in the database, this includes
//             * all subscriptions that have been seen.
//             */
//            List<SubscriptionInfo> getAllSubInfoList(String callingPackage);
//            /**
//             * @param callingPackage The package maing the call.
//             * @return the count of all subscriptions in the database, this includes
//             * all subscriptions that have been seen.
//             */
//            int getAllSubInfoCount(String callingPackage);
//            /**
//             * Get the active SubscriptionInfo with the subId key
//             * @param subId The unique SubscriptionInfo key in database
//             * @param callingPackage The package maing the call.
//             * @return SubscriptionInfo, maybe null if its not active
//             */
//            SubscriptionInfo getActiveSubscriptionInfo(int subId, String callingPackage);
//            /**
//             * Get the active SubscriptionInfo associated with the iccId
//             * @param iccId the IccId of SIM card
//             * @param callingPackage The package maing the call.
//             * @return SubscriptionInfo, maybe null if its not active
//             */
//            SubscriptionInfo getActiveSubscriptionInfoForIccId(String iccId, String callingPackage);
//            /**
//             * Get the active SubscriptionInfo associated with the slotIdx
//             * @param slotIdx the slot which the subscription is inserted
//             * @param callingPackage The package maing the call.
//             * @return SubscriptionInfo, maybe null if its not active
//             */
//            SubscriptionInfo getActiveSubscriptionInfoForSimSlotIndex(int slotIdx, String callingPackage);
//            /**
//             * Get the SubscriptionInfo(s) of the active subscriptions. The records will be sorted
//             * by {@link SubscriptionInfo#getSimSlotIndex} then by {@link SubscriptionInfo#getSubscriptionId}.
//             *
//             * @param callingPackage The package maing the call.
//             * @return Sorted list of the currently {@link SubscriptionInfo} records available on the device.
//             * <ul>
//             * <li>
//             * If null is returned the current state is unknown but if a {@link OnSubscriptionsChangedListener}
//             * has been registered {@link OnSubscriptionsChangedListener#onSubscriptionsChanged} will be
//             * invoked in the future.
//             * </li>
//             * <li>
//             * If the list is empty then there are no {@link SubscriptionInfo} records currently available.
//             * </li>
//             * <li>
//             * if the list is non-empty the list is sorted by {@link SubscriptionInfo#getSimSlotIndex}
//             * then by {@link SubscriptionInfo#getSubscriptionId}.
//             * </li>
//             * </ul>
//             */
//            List<SubscriptionInfo> getActiveSubscriptionInfoList(String callingPackage);
//            /**
//             * @param callingPackage The package making the call.
//             * @return the number of active subscriptions
//             */
//            int getActiveSubInfoCount(String callingPackage);
//            /**
//             * @return the maximum number of subscriptions this device will support at any one time.
//             */
//            int getActiveSubInfoCountMax();
//            /**
//             * Add a new SubscriptionInfo to subinfo database if needed
//             * @param iccId the IccId of the SIM card
//             * @param slotId the slot which the SIM is inserted
//             * @return the URL of the newly created row or the updated row
//             */
//            int addSubInfoRecord(String iccId, int slotId);
//            /**
//             * Set SIM icon tint color by simInfo index
//             * @param tint the icon tint color of the SIM
//             * @param subId the unique SubscriptionInfo index in database
//             * @return the number of records updated
//             */
//            int setIconTint(int tint, int subId);
//            /**
//             * Set display name by simInfo index
//             * @param displayName the display name of SIM card
//             * @param subId the unique SubscriptionInfo index in database
//             * @return the number of records updated
//             */
//            int setDisplayName(String displayName, int subId);
//            /**
//             * Set display name by simInfo index with name source
//             * @param displayName the display name of SIM card
//             * @param subId the unique SubscriptionInfo index in database
//             * @param nameSource, 0: DEFAULT_SOURCE, 1: SIM_SOURCE, 2: USER_INPUT
//             * @return the number of records updated
//             */
//            int setDisplayNameUsingSrc(String displayName, int subId, long nameSource);
//            /**
//             * Set phone number by subId
//             * @param number the phone number of the SIM
//             * @param subId the unique SubscriptionInfo index in database
//             * @return the number of records updated
//             */
//            int setDisplayNumber(String number, int subId);
//            /**
//             * Set data roaming by simInfo index
//             * @param roaming 0:Don't allow data when roaming, 1:Allow data when roaming
//             * @param subId the unique SubscriptionInfo index in database
//             * @return the number of records updated
//             */
//            int setDataRoaming(int roaming, int subId);
//            int getSlotId(int subId);
//            int[] getSubId(int slotId);
//            int getDefaultSubId();
//            int clearSubInfo();
//            int getPhoneId(int subId);
//            /**
//             * Get the default data subscription
//             * @return Id of the data subscription
//             */
//            int getDefaultDataSubId();
//            void setDefaultDataSubId(int subId);
//            int getDefaultVoiceSubId();
//            void setDefaultVoiceSubId(int subId);
//            int getDefaultSmsSubId();
//            void setDefaultSmsSubId(int subId);
//            void clearDefaultsForInactiveSubIds();
//            int[] getActiveSubIdList();
//            void setSubscriptionProperty(int subId, String propKey, String propValue);
//            String getSubscriptionProperty(int subId, String propKey, String callingPackage);
//            /**
//             * Get the SIM state for the slot idx
//             * @return SIM state as the ordinal of IccCardConstants.State
//             */
//            int getSimStateForSlotIdx(int slotIdx);
//            boolean isActiveSubId(int subId);
//        }



        sHookedMethodHandlers.put("getAllSubInfoList", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getAllSubInfoCount", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubscriptionInfo", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubscriptionInfoForIccId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubscriptionInfoForSimSlotIndex", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubscriptionInfoList", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubInfoCountMax", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("addSubInfoRecord", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setIconTint", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDisplayName", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDisplayNameUsingSrc", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDisplayNumber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDataRoaming", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSlotId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDefaultSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("clearSubInfo", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getPhoneId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDefaultDataSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("etDefaultDataSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDefaultVoiceSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDefaultVoiceSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDefaultSmsSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setDefaultSmsSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("clearDefaultsForInactiveSubIds", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getActiveSubIdList", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setSubscriptionProperty", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSubscriptionProperty", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSimStateForSlotIdx", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("isActiveSubId", new MyBaseHandler(mHostContext));
        addAllMethodFromHookedClass();
    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return ISubCompat.Class();
    }

    @Override
    protected HookedMethodHandler newBaseHandler() throws ClassNotFoundException {
        return new MyBaseHandler(mHostContext);
    }

    private static class MyBaseHandler extends ReplaceCallingPackageHookedMethodHandler {
        public MyBaseHandler(Context context) {
            super(context);
        }
    }
}
