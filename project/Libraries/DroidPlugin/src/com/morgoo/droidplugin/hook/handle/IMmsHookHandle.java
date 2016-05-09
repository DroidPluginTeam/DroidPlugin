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
import com.morgoo.helper.compat.IMmsCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/5/9.
 */
public class IMmsHookHandle extends BaseHookHandle {

    public IMmsHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {

//        interface IMms {
//            void sendMessage(int subId, String callingPkg, in Uri contentUri,
//                             String locationUrl, in Bundle configOverrides, in PendingIntent sentIntent);
//            void downloadMessage(int subId, String callingPkg, String locationUrl,
//                                 in Uri contentUri, in Bundle configOverrides,
//                                 in PendingIntent downloadedIntent);
//            Bundle getCarrierConfigValues(int subId);
//            Uri importTextMessage(String callingPkg, String address, int type, String text,
//                                  long timestampMillis, boolean seen, boolean read);
//            Uri importMultimediaMessage(String callingPkg, in Uri contentUri, String messageId,
//                                        long timestampSecs, boolean seen, boolean read);
//            boolean deleteStoredMessage(String callingPkg, in Uri messageUri);
///           boolean deleteStoredConversation(String callingPkg, long conversationId);
//            boolean updateStoredMessageStatus(String callingPkg, in Uri messageUri,
//                                              in ContentValues statusValues);
//            boolean archiveStoredConversation(String callingPkg, long conversationId, boolean archived);
//            Uri addTextMessageDraft(String callingPkg, String address, String text);
//            Uri addMultimediaMessageDraft(String callingPkg, in Uri contentUri);
//            void sendStoredMessage(int subId, String callingPkg, in Uri messageUri,
//                                   in Bundle configOverrides, in PendingIntent sentIntent);
//            void setAutoPersisting(String callingPkg, boolean enabled);
//            boolean getAutoPersisting();
//        }


        sHookedMethodHandlers.put("sendMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("downloadMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getCarrierConfigValues", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("importTextMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("importMultimediaMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("deleteStoredMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("deleteStoredConversation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("updateStoredMessageStatus", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("archiveStoredConversation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("addTextMessageDraft", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("addMultimediaMessageDraft", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("sendStoredMessage", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("setAutoPersisting", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("getAutoPersisting", new MyBaseHandler(mHostContext));

        addAllMethodFromHookedClass();

    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return IMmsCompat.Class();
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
