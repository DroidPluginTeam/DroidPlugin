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
import com.morgoo.helper.compat.ISmsCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/5/9.
 */
public class ISmsHookHandle  extends BaseHookHandle{
    private static final String TAG = ISmsHookHandle.class.getSimpleName();


    public ISmsHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
//        interface ISms {
//            List<SmsRawData> getAllMessagesFromIccEfForSubscriber(in int subId, String callingPkg);
//            boolean updateMessageOnIccEfForSubscriber(in int subId, String callingPkg,
//                                                      int messageIndex, int newStatus, in byte[] pdu);
//            boolean copyMessageToIccEfForSubscriber(in int subId, String callingPkg, int status,
//                                                    in byte[] pdu, in byte[] smsc);
//            void sendDataForSubscriber(int subId, String callingPkg, in String destAddr,
//                                       in String scAddr, in int destPort, in byte[] data, in PendingIntent sentIntent,
//                                       in PendingIntent deliveryIntent);
//            void sendDataForSubscriberWithSelfPermissions(int subId, String callingPkg, in String destAddr,
//                                                          in String scAddr, in int destPort, in byte[] data, in PendingIntent sentIntent,
//                                                          in PendingIntent deliveryIntent);
//            void sendTextForSubscriber(in int subId, String callingPkg, in String destAddr,
//                                       in String scAddr, in String text, in PendingIntent sentIntent,
//                                       in PendingIntent deliveryIntent, in boolean persistMessageForNonDefaultSmsApp);
//            void sendTextForSubscriberWithSelfPermissions(in int subId, String callingPkg,
//                                                          in String destAddr, in String scAddr, in String text, in PendingIntent sentIntent,
//                                                          in PendingIntent deliveryIntent);
//            void injectSmsPduForSubscriber(
//                    int subId, in byte[] pdu, String format, in PendingIntent receivedIntent);
//            void sendMultipartTextForSubscriber(in int subId, String callingPkg,
//                                                in String destinationAddress, in String scAddress,
//                                                in List<String> parts, in List<PendingIntent> sentIntents,
//                                                in List<PendingIntent> deliveryIntents, in boolean persistMessageForNonDefaultSmsApp);
//            boolean enableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType);
//            boolean disableCellBroadcastForSubscriber(int subId, int messageIdentifier, int ranType);
//            boolean enableCellBroadcastRangeForSubscriber(int subId, int startMessageId, int endMessageId,
//                                                          int ranType);
//            boolean disableCellBroadcastRangeForSubscriber(int subId, int startMessageId,
//                                                           int endMessageId, int ranType);
//            int getPremiumSmsPermission(String packageName);
//            int getPremiumSmsPermissionForSubscriber(int subId, String packageName);
//            void setPremiumSmsPermission(String packageName, int permission);
//            void setPremiumSmsPermissionForSubscriber(int subId, String packageName, int permission);
//            boolean isImsSmsSupportedForSubscriber(int subId);
//            boolean isSmsSimPickActivityNeeded(int subId);
//            int getPreferredSmsSubscription();
//            String getImsSmsFormatForSubscriber(int subId);
//            boolean isSMSPromptEnabled();
//            void sendStoredText(int subId, String callingPkg, in Uri messageUri, String scAddress,
//                                in PendingIntent sentIntent, in PendingIntent deliveryIntent);
//            void sendStoredMultipartText(int subId, String callingPkg, in Uri messageUri,
//                                         String scAddress, in List<PendingIntent> sentIntents,
//                                         in List<PendingIntent> deliveryIntents);

        sHookedMethodHandlers.put("getAllMessagesFromIccEfForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("updateMessageOnIccEfForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("copyMessageToIccEfForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendDataForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendDataForSubscriberWithSelfPermissions",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendTextForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendTextForSubscriberWithSelfPermissions",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("injectSmsPduForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendMultipartTextForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("enableCellBroadcastForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("disableCellBroadcastForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("enableCellBroadcastRangeForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("disableCellBroadcastRangeForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getPremiumSmsPermission",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getPremiumSmsPermissionForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setPremiumSmsPermission",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setPremiumSmsPermissionForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("isImsSmsSupportedForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("isSmsSimPickActivityNeeded",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getPreferredSmsSubscription",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getImsSmsFormatForSubscriber",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("isSMSPromptEnabled",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendStoredText",new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendStoredMultipartText",new MyBaseHandler(mHostContext));

        addAllMethodFromHookedClass();

    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return ISmsCompat.Class();
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
