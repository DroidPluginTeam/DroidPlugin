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
import android.os.Build;
import android.util.DisplayMetrics;
import com.morgoo.droidplugin.reflect.MethodUtils;

import java.io.File;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/13.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
class PackageParserApi20 extends PackageParserApi21 {


    public PackageParserApi20(Context context) throws Exception {
        super(context);
    }

    @Override
    public void parsePackage(File sourceFile, int flags) throws Exception {
        /* public Package parsePackage(File sourceFile, String destCodePath,
            DisplayMetrics metrics, int flags)*/
        DisplayMetrics metrics = new DisplayMetrics();
        metrics.setToDefaults();
        String destCodePath = sourceFile.getPath();
        mPackageParser = MethodUtils.invokeConstructor(sPackageParserClass, destCodePath);
        mPackage = MethodUtils.invokeMethod(mPackageParser, "parsePackage", sourceFile, destCodePath, metrics, flags);
    }
}
