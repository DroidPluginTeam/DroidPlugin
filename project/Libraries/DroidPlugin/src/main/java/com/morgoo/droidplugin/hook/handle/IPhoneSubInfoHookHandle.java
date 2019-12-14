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
import com.morgoo.helper.compat.IPhoneSubInfoCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/5/6.
 */
public class IPhoneSubInfoHookHandle extends BaseHookHandle {

    public IPhoneSubInfoHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {

//        interface IPhoneSubInfo {
//            /**
//             * Retrieves the unique device ID, e.g., IMEI for GSM phones.
//             */
//            String getDeviceId(String callingPackage);
//            /**
//             * Retrieves the unique Network Access ID
//             */
//            String getNaiForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the unique device ID of a phone for the device, e.g., IMEI
//             * for GSM phones.
//             */
//            String getDeviceIdForPhone(int phoneId, String callingPackage);
//            /**
//             * Retrieves the IMEI.
//             */
//            String getImeiForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the software version number for the device, e.g., IMEI/SV
//             * for GSM phones.
//             */
//            String getDeviceSvn(String callingPackage);
//            /**
//             * Retrieves the software version number of a subId for the device, e.g., IMEI/SV
//             * for GSM phones.
//             */
//            String getDeviceSvnUsingSubId(int subId, String callingPackage);
//            /**
//             * Retrieves the unique sbuscriber ID, e.g., IMSI for GSM phones.
//             */
//            String getSubscriberId(String callingPackage);
//            /**
//             * Retrieves the unique subscriber ID of a given subId, e.g., IMSI for GSM phones.
//             */
//            String getSubscriberIdForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the Group Identifier Level1 for GSM phones.
//             */
//            String getGroupIdLevel1(String callingPackage);
//            /**
//             * Retrieves the Group Identifier Level1 for GSM phones of a subId.
//             */
//            String getGroupIdLevel1ForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the serial number of the ICC, if applicable.
//             */
//            String getIccSerialNumber(String callingPackage);
//            /**
//             * Retrieves the serial number of a given subId.
//             */
//            String getIccSerialNumberForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the phone number string for line 1.
//             */
//            String getLine1Number(String callingPackage);
//            /**
//             * Retrieves the phone number string for line 1 of a subcription.
//             */
//            String getLine1NumberForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the alpha identifier for line 1.
//             */
//            String getLine1AlphaTag(String callingPackage);
//            /**
//             * Retrieves the alpha identifier for line 1 of a subId.
//             */
//            String getLine1AlphaTagForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves MSISDN Number.
//             */
//            String getMsisdn(String callingPackage);
//            /**
//             * Retrieves the Msisdn of a subId.
//             */
//            String getMsisdnForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the voice mail number.
//             */
//            String getVoiceMailNumber(String callingPackage);
//            /**
//             * Retrieves the voice mail number of a given subId.
//             */
//            String getVoiceMailNumberForSubscriber(int subId, String callingPackage);
//            /**
//             * Retrieves the complete voice mail number.
//             */
//            String getCompleteVoiceMailNumber();
//            /**
//             * Retrieves the complete voice mail number for particular subId
//             */
//            String getCompleteVoiceMailNumberForSubscriber(int subId);
//            /**
//             * Retrieves the alpha identifier associated with the voice mail number.
//             */
//            String getVoiceMailAlphaTag(String callingPackage);
//            /**
//             * Retrieves the alpha identifier associated with the voice mail number
//             * of a subId.
//             */
//            String getVoiceMailAlphaTagForSubscriber(int subId, String callingPackage);
//            /**
//             * Returns the IMS private user identity (IMPI) that was loaded from the ISIM.
//             * @return the IMPI, or null if not present or not loaded
//             */
//            String getIsimImpi();
//            /**
//             * Returns the IMS home network domain name that was loaded from the ISIM.
//             * @return the IMS domain name, or null if not present or not loaded
//             */
//            String getIsimDomain();
//            /**
//             * Returns the IMS public user identities (IMPU) that were loaded from the ISIM.
//             * @return an array of IMPU strings, with one IMPU per string, or null if
//             *      not present or not loaded
//             */
//            String[] getIsimImpu();
//            /**
//             * Returns the IMS Service Table (IST) that was loaded from the ISIM.
//             * @return IMS Service Table or null if not present or not loaded
//             */
//            String getIsimIst();
//            /**
//             * Returns the IMS Proxy Call Session Control Function(PCSCF) that were loaded from the ISIM.
//             * @return an array of PCSCF strings with one PCSCF per string, or null if
//             *      not present or not loaded
//             */
//            String[] getIsimPcscf();
//            /**
//             * TODO: Deprecate and remove this interface. Superceded by getIccsimChallengeResponse.
//             * Returns the response of ISIM Authetification through RIL.
//             * @return the response of ISIM Authetification, or null if
//             *     the Authentification hasn't been successed or isn't present iphonesubinfo.
//             */
//            String getIsimChallengeResponse(String nonce);
//            /**
//             * Returns the response of the SIM application on the UICC to authentication
//             * challenge/response algorithm. The data string and challenge response are
//             * Base64 encoded Strings.
//             * Can support EAP-SIM, EAP-AKA with results encoded per 3GPP TS 31.102.
//             *
//             * @param subId subscription ID to be queried
//             * @param appType ICC application type (@see com.android.internal.telephony.PhoneConstants#APPTYPE_xxx)
//             * @param data authentication challenge data
//             * @return challenge response
//             */
//            String getIccSimChallengeResponse(int subId, int appType, String data);
//        }

        sHookedMethodHandlers.put("getDeviceId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getNaiForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDeviceIdForPhone", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getImeiForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDeviceSvn", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getDeviceSvnUsingSubId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSubscriberId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getSubscriberIdForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getGroupIdLevel1", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getGroupIdLevel1ForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIccSerialNumber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIccSerialNumberForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getLine1Number", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getLine1NumberForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getLine1AlphaTag", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getLine1AlphaTagForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getMsisdn", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getMsisdnForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getVoiceMailNumber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getVoiceMailNumberForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getCompleteVoiceMailNumber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getCompleteVoiceMailNumberForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getVoiceMailAlphaTag", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getVoiceMailAlphaTagForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimImpi", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimDomain", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimImpu", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimIst", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimPcscf", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIsimChallengeResponse", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getIccSimChallengeResponse", new MyBaseHandler(mHostContext));

        addAllMethodFromHookedClass();
    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return IPhoneSubInfoCompat.Class();
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
