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

package com.morgoo.droidplugin.pm.parser;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.morgoo.droidplugin.reflect.FieldUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 负责Intent匹配。
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/15.
 */
public class IntentMatcher {

    private static final String TAG = IntentMatcher.class.getSimpleName();

    private static final Comparator<ResolveInfo> mResolvePrioritySorter =
            new Comparator<ResolveInfo>() {
                public int compare(ResolveInfo r1, ResolveInfo r2) {
                    int v1 = r1.priority;
                    int v2 = r2.priority;
                    //System.out.println("Comparing: q1=" + q1 + " q2=" + q2);
                    if (v1 != v2) {
                        return (v1 > v2) ? -1 : 1;
                    }
                    v1 = r1.preferredOrder;
                    v2 = r2.preferredOrder;
                    if (v1 != v2) {
                        return (v1 > v2) ? -1 : 1;
                    }
                    if (r1.isDefault != r2.isDefault) {
                        return r1.isDefault ? -1 : 1;
                    }
                    v1 = r1.match;
                    v2 = r2.match;
                    //System.out.println("Comparing: m1=" + m1 + " m2=" + m2);
                    if (v1 != v2) {
                        return (v1 > v2) ? -1 : 1;
                    }
//                    if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
//                        if (r1.system != r2.system) {
//                            return r1.system ? -1 : 1;
//                        }
//                    }
                    return 0;
                }
            };

    public static final List<ResolveInfo> resolveReceiverIntent(Context context, Map<String, PluginPackageParser> pluginPackages, Intent intent, String resolvedType, int flags) throws Exception {
        if (intent == null || context == null) {
            return null;
        }
        List<ResolveInfo> list = new ArrayList<ResolveInfo>(1);
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (intent.getSelector() != null) {
                    intent = intent.getSelector();
                    comp = intent.getComponent();

                }
            }
        }

        if (comp != null && comp.getPackageName() != null) {
            PluginPackageParser parser = pluginPackages.get(comp.getPackageName());
            if (parser != null) {
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
            Collections.sort(list, mResolvePrioritySorter);
            return list;
        }


        final String pkgName = intent.getPackage();
        if (pkgName != null) {
            PluginPackageParser parser = pluginPackages.get(pkgName);
            if (parser != null) {
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
        } else {
            for (PluginPackageParser parser : pluginPackages.values()) {
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
            }

        }
        Collections.sort(list, mResolvePrioritySorter);
        return list;
    }


    public static final List<ResolveInfo> resolveServiceIntent(Context context, Map<String, PluginPackageParser> pluginPackages, Intent intent, String resolvedType, int flags) throws Exception {
        if (intent == null || context == null) {
            return null;
        }
        List<ResolveInfo> list = new ArrayList<ResolveInfo>(1);
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (intent.getSelector() != null) {
                    intent = intent.getSelector();
                    comp = intent.getComponent();

                }
            }
        }

        if (comp != null && comp.getPackageName() != null) {
            PluginPackageParser parser = pluginPackages.get(comp.getPackageName());
            if (parser != null) {

                queryIntentServiceForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }

            } else {
                //intent指定的包名不在我们的插件列表中。
            }
            Collections.sort(list, mResolvePrioritySorter);
            return list;
        }


        final String pkgName = intent.getPackage();
        if (pkgName != null) {
            PluginPackageParser parser = pluginPackages.get(pkgName);
            if (parser != null) {
                queryIntentServiceForPackage(context, parser, intent, flags, list);
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
        } else {
            for (PluginPackageParser parser : pluginPackages.values()) {
                queryIntentServiceForPackage(context, parser, intent, flags, list);
            }
        }
        Collections.sort(list, mResolvePrioritySorter);
        return list;
    }


    public static final List<ResolveInfo> resolveProviderIntent(Context context, Map<String, PluginPackageParser> pluginPackages, Intent intent, String resolvedType, int flags) throws Exception {
        if (intent == null || context == null) {
            return null;
        }
        List<ResolveInfo> list = new ArrayList<ResolveInfo>(1);
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (intent.getSelector() != null) {
                    intent = intent.getSelector();
                    comp = intent.getComponent();

                }
            }
        }

        if (comp != null && comp.getPackageName() != null) {
            PluginPackageParser parser = pluginPackages.get(comp.getPackageName());
            if (parser != null) {
                queryIntentProviderForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
            Collections.sort(list, mResolvePrioritySorter);
            return list;
        }


        final String pkgName = intent.getPackage();
        if (pkgName != null) {
            PluginPackageParser parser = pluginPackages.get(pkgName);
            if (parser != null) {
                queryIntentProviderForPackage(context, parser, intent, flags, list);
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
        } else {
            for (PluginPackageParser parser : pluginPackages.values()) {
                queryIntentProviderForPackage(context, parser, intent, flags, list);
            }

        }
        Collections.sort(list, mResolvePrioritySorter);
        return list;
    }


    public static final List<ResolveInfo> resolveActivityIntent(Context context, Map<String, PluginPackageParser> pluginPackages, Intent intent, String resolvedType, int flags) throws Exception {
        if (intent == null || context == null) {
            return null;
        }
        List<ResolveInfo> list = new ArrayList<ResolveInfo>(1);
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (intent.getSelector() != null) {
                    intent = intent.getSelector();
                    comp = intent.getComponent();

                }
            }
        }

        if (comp != null && comp.getPackageName() != null) {
            PluginPackageParser parser = pluginPackages.get(comp.getPackageName());
            if (parser != null) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
            Collections.sort(list, mResolvePrioritySorter);
            return list;
        }


        final String pkgName = intent.getPackage();
        if (pkgName != null) {
            PluginPackageParser parser = pluginPackages.get(pkgName);
            if (parser != null) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
        } else {
            for (PluginPackageParser parser : pluginPackages.values()) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
            }

        }
        Collections.sort(list, mResolvePrioritySorter);
        return list;
    }

    public static final List<ResolveInfo> resolveIntent(Context context, Map<String, PluginPackageParser> pluginPackages, Intent intent, String resolvedType, int flags) throws Exception {
        if (intent == null || context == null) {
            return null;
        }
        List<ResolveInfo> list = new ArrayList<ResolveInfo>(1);
        ComponentName comp = intent.getComponent();
        if (comp == null) {
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                if (intent.getSelector() != null) {
                    intent = intent.getSelector();
                    comp = intent.getComponent();

                }
            }
        }

        if (comp != null && comp.getPackageName() != null) {
            PluginPackageParser parser = pluginPackages.get(comp.getPackageName());
            if (parser != null) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
                queryIntentServiceForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
                queryIntentProviderForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
                if (list.size() > 0) {
                    Collections.sort(list, mResolvePrioritySorter);
                    return list;
                }
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
            Collections.sort(list, mResolvePrioritySorter);
            return list;
        }


        final String pkgName = intent.getPackage();
        if (pkgName != null) {
            PluginPackageParser parser = pluginPackages.get(pkgName);
            if (parser != null) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
                queryIntentServiceForPackage(context, parser, intent, flags, list);
                queryIntentProviderForPackage(context, parser, intent, flags, list);
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
            } else {
                //intent指定的包名不在我们的插件列表中。
            }
        } else {
            for (PluginPackageParser parser : pluginPackages.values()) {
                queryIntentActivityForPackage(context, parser, intent, flags, list);
                queryIntentServiceForPackage(context, parser, intent, flags, list);
                queryIntentProviderForPackage(context, parser, intent, flags, list);
                queryIntentReceiverForPackage(context, parser, intent, flags, list);
            }

        }
        Collections.sort(list, mResolvePrioritySorter);
        return list;
    }

    private static void queryIntentReceiverForPackage(Context context, PluginPackageParser packageParser, Intent intent, int flags, List<ResolveInfo> outList) throws Exception {
        List<ActivityInfo> receivers = packageParser.getReceivers();
        if (receivers != null && receivers.size() >= 0) {
            for (ActivityInfo receiver : receivers) {
                List<IntentFilter> intentFilters = packageParser.getReceiverIntentFilter(receiver);
                if (intentFilters != null && intentFilters.size() > 0) {
                    for (IntentFilter intentFilter : intentFilters) {
                        int match = intentFilter.match(context.getContentResolver(), intent, true, "");
                        if (match >= 0) {
                            ActivityInfo flagInfo = packageParser.getReceiverInfo(new ComponentName(receiver.packageName, receiver.name), flags);
                            if ((flags & PackageManager.MATCH_DEFAULT_ONLY) != 0) {
                                if (intentFilter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                                    ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                    resolveInfo.match = match;
                                    resolveInfo.isDefault = true;
                                    outList.add(resolveInfo);
                                } else {
                                    //只是匹配默认。这里也算匹配不上。
                                }
                            } else {
                                ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                resolveInfo.match = match;
                                resolveInfo.isDefault = false;
                                outList.add(resolveInfo);
                            }
                        }
                    }
                    if (outList.size() <= 0) {
                        //没有在插件包中找到IntentFilter匹配的ACTIVITY
                    }
                } else {
                    //该插件包中没有具有IntentFilter的ACTIVITY
                }
            }
        } else {
            //该插件apk包中没有ACTIVITY
        }
    }

    private static void queryIntentProviderForPackage(Context context, PluginPackageParser packageParser, Intent intent, int flags, List<ResolveInfo> outList) throws Exception {
        List<ProviderInfo> providerInfos = packageParser.getProviders();
        if (providerInfos != null && providerInfos.size() >= 0) {
            for (ProviderInfo providerInfo : providerInfos) {
                ComponentName className = new ComponentName(providerInfo.packageName, providerInfo.name);
                List<IntentFilter> intentFilters = packageParser.getProviderIntentFilter(className);
                if (intentFilters != null && intentFilters.size() > 0) {
                    for (IntentFilter intentFilter : intentFilters) {
                        int match = intentFilter.match(context.getContentResolver(), intent, true, "");
                        if (match >= 0) {
                            ProviderInfo flagInfo = packageParser.getProviderInfo(new ComponentName(providerInfo.packageName, providerInfo.name), flags);
                            if ((flags & PackageManager.MATCH_DEFAULT_ONLY) != 0) {
                                if (intentFilter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                                    ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                    resolveInfo.match = match;
                                    resolveInfo.isDefault = true;
                                    outList.add(resolveInfo);
                                } else {
                                    //只是匹配默认。这里也算匹配不上。
                                }
                            } else {
                                ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                resolveInfo.match = match;
                                resolveInfo.isDefault = false;
                                outList.add(resolveInfo);
                            }
                        }
                    }
                    if (outList.size() <= 0) {
                        //没有在插件包中找到IntentFilter匹配的Service
                    }
                } else {
                    //该插件包中没有具有IntentFilter的Service
                }
            }
        } else {
            //该插件apk包中没有Service
        }
    }

    private static void queryIntentServiceForPackage(Context context, PluginPackageParser packageParser, Intent intent, int flags, List<ResolveInfo> outList) throws Exception {
        List<ServiceInfo> serviceInfos = packageParser.getServices();
        if (serviceInfos != null && serviceInfos.size() >= 0) {
            for (ServiceInfo serviceInfo : serviceInfos) {
                ComponentName className = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                List<IntentFilter> intentFilters = packageParser.getServiceIntentFilter(className);
                if (intentFilters != null && intentFilters.size() > 0) {
                    for (IntentFilter intentFilter : intentFilters) {
                        int match = intentFilter.match(context.getContentResolver(), intent, true, "");
                        if (match >= 0) {
                            ServiceInfo flagServiceInfo = packageParser.getServiceInfo(new ComponentName(serviceInfo.packageName, serviceInfo.name), flags);
                            if ((flags & PackageManager.MATCH_DEFAULT_ONLY) != 0) {
                                if (intentFilter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                                    ResolveInfo resolveInfo = newResolveInfo(flagServiceInfo, intentFilter);
                                    resolveInfo.match = match;
                                    resolveInfo.isDefault = true;
                                    outList.add(resolveInfo);
                                } else {
                                    //只是匹配默认。这里也算匹配不上。
                                }
                            } else {
                                ResolveInfo resolveInfo = newResolveInfo(flagServiceInfo, intentFilter);
                                resolveInfo.match = match;
                                resolveInfo.isDefault = false;
                                outList.add(resolveInfo);
                            }
                        }
                    }
                    if (outList.size() <= 0) {
                        //没有在插件包中找到IntentFilter匹配的Service
                    }
                } else {
                    //该插件包中没有具有IntentFilter的Service
                }
            }
        } else {
            //该插件apk包中没有Service
        }
    }

    private static void queryIntentActivityForPackage(Context context, PluginPackageParser packageParser, Intent intent, int flags, List<ResolveInfo> outList) throws Exception {
        List<ActivityInfo> activityInfos = packageParser.getActivities();
        if (activityInfos != null && activityInfos.size() >= 0) {
            for (ActivityInfo activityInfo : activityInfos) {
                ComponentName className = new ComponentName(activityInfo.packageName, activityInfo.name);
                List<IntentFilter> intentFilters = packageParser.getActivityIntentFilter(className);
                if (intentFilters != null && intentFilters.size() > 0) {
                    for (IntentFilter intentFilter : intentFilters) {
                        int match = intentFilter.match(context.getContentResolver(), intent, true, "");
                        if (match >= 0) {
                            ActivityInfo flagInfo = packageParser.getActivityInfo(new ComponentName(activityInfo.packageName, activityInfo.name), flags);
                            if ((flags & PackageManager.MATCH_DEFAULT_ONLY) != 0) {
                                if (intentFilter.hasCategory(Intent.CATEGORY_DEFAULT)) {
                                    ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                    resolveInfo.match = match;
                                    resolveInfo.isDefault = true;
                                    outList.add(resolveInfo);
                                } else {
                                    //只是匹配默认。这里也算匹配不上。
                                }
                            } else {
                                ResolveInfo resolveInfo = newResolveInfo(flagInfo, intentFilter);
                                resolveInfo.match = match;
                                resolveInfo.isDefault = false;
                                outList.add(resolveInfo);
                            }
                        }
                    }
                    if (outList.size() <= 0) {
                        //没有在插件包中找到IntentFilter匹配的ACTIVITY
                    }
                } else {
                    //该插件包中没有具有IntentFilter的ACTIVITY
                }
            }
        } else {
            //该插件apk包中没有ACTIVITY
        }
    }

    @TargetApi(VERSION_CODES.KITKAT)
    private static ResolveInfo newResolveInfo(ProviderInfo providerInfo, IntentFilter intentFilter) {
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.providerInfo = providerInfo;
        resolveInfo.filter = intentFilter;
        resolveInfo.resolvePackageName = providerInfo.packageName;
        resolveInfo.labelRes = providerInfo.labelRes;
        resolveInfo.icon = providerInfo.icon;
        resolveInfo.specificIndex = 1;
//      默认就是false，不用再设置了。
//        resolveInfo.system = false;
        resolveInfo.priority = intentFilter.getPriority();
        resolveInfo.preferredOrder = 0;
        return resolveInfo;
    }

    private static ResolveInfo newResolveInfo(ServiceInfo serviceInfo, IntentFilter intentFilter) {
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.serviceInfo = serviceInfo;
        resolveInfo.filter = intentFilter;
        resolveInfo.resolvePackageName = serviceInfo.packageName;
        resolveInfo.labelRes = serviceInfo.labelRes;
        resolveInfo.icon = serviceInfo.icon;
        resolveInfo.specificIndex = 1;
//        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//          默认就是false，不用再设置了。
//        resolveInfo.system = false;
//        }
        resolveInfo.priority = intentFilter.getPriority();
        resolveInfo.preferredOrder = 0;
        return resolveInfo;
    }

    private static ResolveInfo newResolveInfo(ActivityInfo activityInfo, IntentFilter intentFilter) {
        ResolveInfo resolveInfo = new ResolveInfo();
        resolveInfo.activityInfo = activityInfo;
        resolveInfo.filter = intentFilter;
        resolveInfo.resolvePackageName = activityInfo.packageName;
        resolveInfo.labelRes = activityInfo.labelRes;
        resolveInfo.icon = activityInfo.icon;
        resolveInfo.specificIndex = 1;
//        if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
////            默认就是false，不用再设置了。
//            resolveInfo.system = false;
//        }
        resolveInfo.priority = intentFilter.getPriority();
        resolveInfo.preferredOrder = 0;
        return resolveInfo;
    }

    public static ResolveInfo findBest(List<ResolveInfo> infos) {
        return infos.get(0);
    }
}
