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
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.MethodUtils;
import com.morgoo.helper.Log;
import com.morgoo.helper.compat.UserHandleCompat;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by zhangyong on 2015/2/11.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class PackageParserApi21 extends PackageParser {

    private static final String TAG = PackageParserApi21.class.getSimpleName();
    protected Class<?> sPackageUserStateClass;
    protected Class<?> sPackageParserClass;
    protected Class<?> sActivityClass;
    protected Class<?> sServiceClass;
    protected Class<?> sProviderClass;
    protected Class<?> sInstrumentationClass;
    protected Class<?> sPermissionClass;
    protected Class<?> sPermissionGroupClass;
    protected Class<?> sArraySetClass;

    protected Object mPackage;
    protected Object mDefaultPackageUserState;

    protected int mUserId;


    public PackageParserApi21(Context context) throws Exception {
        super(context);
        initClasses();
    }


    private void initClasses() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        sPackageParserClass = Class.forName("android.content.pm.PackageParser");
        sActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
        sServiceClass = Class.forName("android.content.pm.PackageParser$Service");
        sProviderClass = Class.forName("android.content.pm.PackageParser$Provider");
        sInstrumentationClass = Class.forName("android.content.pm.PackageParser$Instrumentation");
        sPermissionClass = Class.forName("android.content.pm.PackageParser$Permission");
        sPermissionGroupClass = Class.forName("android.content.pm.PackageParser$PermissionGroup");
        try {
            sArraySetClass = Class.forName("android.util.ArraySet");
        } catch (ClassNotFoundException e) {
        }

        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1) {
            sPackageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            mDefaultPackageUserState = sPackageUserStateClass.newInstance();
            mUserId = UserHandleCompat.getCallingUserId();
        }
    }


    @Override
    public void parsePackage(File file, int flags) throws Exception {
         /* public Package parsePackage(File packageFile, int flags) throws PackageParserException*/
        mPackageParser = sPackageParserClass.newInstance();
        mPackage = MethodUtils.invokeMethod(mPackageParser, "parsePackage", file, flags);
    }

    @Override
    public void collectCertificates(int flags) throws Exception {
        // public void collectCertificates(Package pkg, int flags) throws PackageParserException
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "collectCertificates",
                mPackage.getClass(), int.class);
        method.invoke(mPackageParser, mPackage, flags);
    }


    @Override
    public ActivityInfo generateActivityInfo(Object activity, int flags) throws Exception {
        /*   public static final ActivityInfo generateActivityInfo(Activity a, int flags,
            PackageUserState state, int userId) */
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generateActivityInfo", sActivityClass, int.class, sPackageUserStateClass, int.class);
        return (ActivityInfo) method.invoke(null, activity, flags, mDefaultPackageUserState, mUserId);
    }


    @Override
    public ServiceInfo generateServiceInfo(Object service, int flags) throws Exception {
        /* public static final ServiceInfo generateServiceInfo(Service s, int flags,
            PackageUserState state, int userId)*/
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generateServiceInfo", sServiceClass, int.class, sPackageUserStateClass, int.class);
        return (ServiceInfo) method.invoke(null, service, flags, mDefaultPackageUserState, mUserId);
    }


    @Override
    public ProviderInfo generateProviderInfo(Object provider, int flags) throws Exception {
        /*  public static final ProviderInfo generateProviderInfo(Provider p, int flags,
            PackageUserState state, int userId) */
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generateProviderInfo", sProviderClass, int.class, sPackageUserStateClass, int.class);
        return (ProviderInfo) method.invoke(null, provider, flags, mDefaultPackageUserState, mUserId);
    }

    @Override
    public InstrumentationInfo generateInstrumentationInfo(
            Object instrumentation, int flags) throws Exception {
        /*  public static final InstrumentationInfo generateInstrumentationInfo(
            Instrumentation i, int flags)*/
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generateInstrumentationInfo", sInstrumentationClass, int.class);
        return (InstrumentationInfo) method.invoke(null, instrumentation, flags);
    }

    @Override
    public ApplicationInfo generateApplicationInfo(int flags) throws Exception {
        /* public static ApplicationInfo generateApplicationInfo(Package p, int flags,
            PackageUserState state, int userId) */
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generateApplicationInfo", mPackage.getClass(), int.class, sPackageUserStateClass, int.class);
        return (ApplicationInfo) method.invoke(null, mPackage, flags, mDefaultPackageUserState, mUserId);
    }

    @Override
    public PermissionGroupInfo generatePermissionGroupInfo(
            Object permissionGroup, int flags) throws Exception {
        /*  public static final PermissionGroupInfo generatePermissionGroupInfo(
            PermissionGroup pg, int flags)*/
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePermissionGroupInfo", sPermissionGroupClass, int.class);
        return (PermissionGroupInfo) method.invoke(null, permissionGroup, flags);

    }

    @Override
    public PermissionInfo generatePermissionInfo(
            Object permission, int flags) throws Exception {
        /*public static final PermissionInfo generatePermissionInfo(
            Permission p, int flags)*/
        Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePermissionInfo", sPermissionClass, int.class);
        return (PermissionInfo) method.invoke(null, permission, flags);
    }

    @Override
    public PackageInfo generatePackageInfo(
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions) throws Exception {
        /*public static PackageInfo generatePackageInfo(PackageParser.Package p,
            int gids[], int flags, long firstInstallTime, long lastUpdateTime,
            HashSet<String> grantedPermissions, PackageUserState state, int userId) */
        try {
            Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePackageInfo",
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, Set.class, sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "get generatePackageInfo 1 fail", e);
        }

        try {
            Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePackageInfo",
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, HashSet.class, sPackageUserStateClass, int.class);
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissions, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "get generatePackageInfo 2 fail", e);
        }

        try {
            Method method = MethodUtils.getAccessibleMethod(sPackageParserClass, "generatePackageInfo",
                    mPackage.getClass(),
                    int[].class, int.class, long.class, long.class, sArraySetClass, sPackageUserStateClass, int.class);

            Object grantedPermissionsArray = null;
            try {
                Constructor constructor = sArraySetClass.getConstructor(Collection.class);
                grantedPermissionsArray = constructor.newInstance(grantedPermissions);
            } catch (Exception e) {
            }
            if (grantedPermissionsArray == null) {
                grantedPermissionsArray = grantedPermissions;            }
            return (PackageInfo) method.invoke(null, mPackage, gids, flags, firstInstallTime, lastUpdateTime, grantedPermissionsArray, mDefaultPackageUserState, mUserId);
        } catch (NoSuchMethodException e) {
            Log.i(TAG, "get generatePackageInfo 3 fail", e);
        }

        throw new NoSuchMethodException("Can not found method generatePackageInfo");


    }

    @Override
    public List getActivities() throws Exception {
        /*PackageParser.Package.activities*/
        return (List) FieldUtils.readField(mPackage, "activities");
    }

    @Override
    public List getServices() throws Exception {
         /*PackageParser.Package.services*/
        return (List) FieldUtils.readField(mPackage, "services");
    }

    @Override
    public List getProviders() throws Exception {
         /*PackageParser.Package.providers*/
        return (List) FieldUtils.readField(mPackage, "providers");
    }

    @Override
    public List getPermissions() throws Exception {
         /*PackageParser.Package.permissions*/
        return (List) FieldUtils.readField(mPackage, "permissions");
    }

    @Override
    public List getPermissionGroups() throws Exception {
         /*PackageParser.Package.permissionGroups*/
        return (List) FieldUtils.readField(mPackage, "permissionGroups");
    }

    @Override
    public List getRequestedPermissions() throws Exception {
       /*PackageParser.Package.requestedPermissions*/
        return (List) FieldUtils.readField(mPackage, "requestedPermissions");
    }

    @Override
    public List getReceivers() throws Exception {
         /*PackageParser.Package.requestedPermissions*/
        return (List) FieldUtils.readField(mPackage, "receivers");
    }

    @Override
    public List getInstrumentations() throws Exception {
        /*PackageParser.Package.instrumentation*/
        return (List) FieldUtils.readField(mPackage, "instrumentation");
    }


    @Override
    public String getPackageName() throws Exception {
         /*PackageParser.Package.packageName*/
        return (String) FieldUtils.readField(mPackage, "packageName");
    }

    @Override
    public String readNameFromComponent(Object data) throws Exception {
        return (String) FieldUtils.readField(data, "className");
    }

    @Override
    public List<IntentFilter> readIntentFilterFromComponent(Object data) throws Exception {
        return (List) FieldUtils.readField(data, "intents");
    }

    @Override
    public void writeSignature(Signature[] signatures) throws Exception {
        FieldUtils.writeField(mPackage, "mSignatures", signatures);
    }
}
