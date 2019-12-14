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
import com.morgoo.helper.compat.ITelephonyRegistryCompat;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/5/6.
 */
public class ITelephonyRegistryHookHandle extends BaseHookHandle {

    public ITelephonyRegistryHookHandle(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected void init() {
//        interface ITelephonyRegistry {
//            void addOnSubscriptionsChangedListener(String pkg,
//                                                   IOnSubscriptionsChangedListener callback);
//            void removeOnSubscriptionsChangedListener(String pkg,
//                                                      IOnSubscriptionsChangedListener callback);
//            void listen(String pkg, IPhoneStateListener callback, int events, boolean notifyNow);
//            void listenForSubscriber(in int subId, String pkg, IPhoneStateListener callback, int events,
//                                     boolean notifyNow);
//            void notifyCallState(int state, String incomingNumber);
//            void notifyCallStateForSubscriber(in int subId, int state, String incomingNumber);
//            void notifyServiceStateForPhoneId(in int phoneId, in int subId, in ServiceState state);
//            void notifySignalStrength(in SignalStrength signalStrength);
//            void notifySignalStrengthForSubscriber(in int subId, in SignalStrength signalStrength);
//            void notifyMessageWaitingChangedForPhoneId(in int phoneId, in int subId, in boolean mwi);
//            void notifyCallForwardingChanged(boolean cfi);
//            void notifyCallForwardingChangedForSubscriber(in int subId, boolean cfi);
//            void notifyDataActivity(int state);
//            void notifyDataActivityForSubscriber(in int subId, int state);
//            void notifyDataConnection(int state, boolean isDataConnectivityPossible, String reason, String apn, String apnType, in LinkProperties linkProperties, in NetworkCapabilities networkCapabilities, int networkType, boolean roaming);
//            void notifyDataConnectionForSubscriber(int subId, int state, boolean isDataConnectivityPossible, String reason, String apn, String apnType, in LinkProperties linkProperties, in NetworkCapabilities networkCapabilities, int networkType, boolean roaming);
//            void notifyDataConnectionFailed(String reason, String apnType);
//            void notifyDataConnectionFailedForSubscriber(int subId, String reason, String apnType);
//            void notifyCellLocation(in Bundle cellLocation);
//            void notifyCellLocationForSubscriber(in int subId, in Bundle cellLocation);
//            void notifyOtaspChanged(in int otaspMode);
//            void notifyCellInfo(in List<CellInfo> cellInfo);
//            void notifyPreciseCallState(int ringingCallState, int foregroundCallState, int backgroundCallState);
//            void notifyDisconnectCause(int disconnectCause, int preciseDisconnectCause);
//            void notifyPreciseDataConnectionFailed(String reason, String apnType, String apn, String failCause);
//            void notifyCellInfoForSubscriber(in int subId, in List<CellInfo> cellInfo);
//            void notifyDataConnectionRealTimeInfo(in DataConnectionRealTimeInfo dcRtInfo);
//            void notifyVoLteServiceStateChanged(in VoLteServiceState lteState);
//            void notifyOemHookRawEventForSubscriber(in int subId, in byte[] rawData);
//            void notifySubscriptionInfoChanged();
//            void notifyCarrierNetworkChange(in boolean active);
//        }
        sHookedMethodHandlers.put("addOnSubscriptionsChangedListener", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("removeOnSubscriptionsChangedListener", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("listen", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("listenForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCallState", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCallStateForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyServiceStateForPhoneId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifySignalStrength", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifySignalStrengthForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyMessageWaitingChangedForPhoneId", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCallForwardingChanged", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCallForwardingChangedForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataActivity", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataActivityForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataConnection", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataConnectionForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataConnectionFailed", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataConnectionFailedForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCellLocation", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCellLocationForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyOtaspChanged", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCellInfo", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyPreciseCallState", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDisconnectCause", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyPreciseDataConnectionFailed", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCellInfoForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyDataConnectionRealTimeInfo", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyVoLteServiceStateChanged", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyOemHookRawEventForSubscriber", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifySubscriptionInfoChanged", new MyBaseHandler(mHostContext));
        sHookedMethodHandlers.put("notifyCarrierNetworkChange", new MyBaseHandler(mHostContext));
        addAllMethodFromHookedClass();
    }

    @Override
    protected Class<?> getHookedClass() throws ClassNotFoundException {
        return ITelephonyRegistryCompat.Class();
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
