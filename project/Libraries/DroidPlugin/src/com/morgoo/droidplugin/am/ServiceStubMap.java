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

package com.morgoo.droidplugin.am;

import android.content.pm.ServiceInfo;
import android.text.TextUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/4.
 */
class ServiceStubMap {


    private static class MyServiceInfo implements Comparable<MyServiceInfo> {
        private ServiceInfo info;

        MyServiceInfo(ServiceInfo info) {
            this.info = info;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MyServiceInfo) {
                MyServiceInfo obj1 = (MyServiceInfo) o;
                return TextUtils.equals(obj1.info.name, info.name);
            } else {
                return false;
            }
        }

        @Override
        public int compareTo(MyServiceInfo another) {
            return Collator.getInstance().compare(info.name, another.info.name);
        }
    }

    //key=stub service, value = plugin service info list
    private Map<MyServiceInfo, List<MyServiceInfo>> mServiceStubMap = new TreeMap<MyServiceInfo, List<MyServiceInfo>>();

    List<ServiceInfo> getPluginInfosByStub(ServiceInfo stubInfo) {
        ArrayList<ServiceInfo> arrayList = new ArrayList<ServiceInfo>();
        if (stubInfo != null) {
            MyServiceInfo info = new MyServiceInfo(stubInfo);
            List<MyServiceInfo> infos = mServiceStubMap.get(info);
            if (infos != null && infos.size() > 0) {
                for (MyServiceInfo myServiceInfo : infos) {
                    arrayList.add(myServiceInfo.info);
                }
            }
        }
        return arrayList;
    }

    ServiceInfo getStubInfoByPlugin(ServiceInfo pluginInfo) {
        MyServiceInfo object = new MyServiceInfo(pluginInfo);
        for (MyServiceInfo info : mServiceStubMap.keySet()) {
            List<MyServiceInfo> infos = mServiceStubMap.get(info);
            if (infos != null && infos.contains(object)) {
                return info.info;
            }
        }
        return null;
    }

    void addToMap(ServiceInfo stubInfo, ServiceInfo pluginInfo) {
        MyServiceInfo stub = new MyServiceInfo(stubInfo);
        MyServiceInfo plugin = new MyServiceInfo(pluginInfo);
        List<MyServiceInfo> list =  mServiceStubMap.get(stub);
        if (list == null) {
            list = new ArrayList<MyServiceInfo>(1);
            mServiceStubMap.put(stub, list);
        }
        list.add(plugin);
    }

    void removeFormMap(ServiceInfo pluginInfo) {
        MyServiceInfo plugin = new MyServiceInfo(pluginInfo);
        for (List<MyServiceInfo> infos : mServiceStubMap.values()) {
            infos.remove(plugin);
        }
    }


}
