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

package com.morgoo.droidplugin.core;

import android.os.Build;

import com.morgoo.helper.Log;

import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/4.
 */
public class PluginClassLoader extends DexClassLoader {

    public PluginClassLoader(String apkfile, String optimizedDirectory, String libraryPath, ClassLoader systemClassLoader) {
        super(apkfile, optimizedDirectory, libraryPath, systemClassLoader);
    }

    private static final List<String> sPreLoader = new ArrayList<>();

    static {
        sPreLoader.add("QIKU");
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {

        if (Build.MANUFACTURER != null && sPreLoader.contains(Build.MANUFACTURER.toUpperCase())) {
            try {
                /**
                 * FUCK QIKU！
                 * 这里适配奇酷手机青春版。
                 * 因为奇酷手机自己加载了自己修改过的的Support V4库，在插件中也用了这个库的时候，ClassLoader会优先加载奇酷手机自带的Support V4库。
                 * 原因在于，奇酷手机没有预加载插件中打的Support V4库。详情可以研究super.loadClass(className, resolve)标准实现
                 * 但是这可能会导致类不兼容，出现java.lang.IncompatibleClassChangeError。因为插件编译时使用的插件的Support V4，而奇酷手机则使
                 * 用的是它修改过的Support V4。
                 *
                 * SO,在Class Loader加载某个Class的时候，我们优先从自己的ClassLoader中加载Class，如果找不到，再从Parent Class Loader中去加载。
                 * 这样修改后，Class的加载顺序就跟系统的不一样了。
                 *
                 */
                Class<?> clazz = findClass(className);
                if (clazz != null) {
                    return clazz;
                }
            } catch (ClassNotFoundException e) {
                Log.e("PluginClassLoader", "UCK QIKU:error", e);
            }
        }
        return super.loadClass(className, resolve);
    }
}
