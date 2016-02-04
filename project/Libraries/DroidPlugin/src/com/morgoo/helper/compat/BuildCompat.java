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

package com.morgoo.helper.compat;

import android.os.Build;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2016/2/4.
 */
public class BuildCompat {

    public final static String[] SUPPORTED_ABIS;

    public final static String[] SUPPORTED_32_BIT_ABIS;

    public static final String[] SUPPORTED_64_BIT_ABIS;

    static {
        //init SUPPORTED_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_ABIS != null) {
                SUPPORTED_ABIS = new String[Build.SUPPORTED_ABIS.length];
                System.arraycopy(Build.SUPPORTED_ABIS, 0, SUPPORTED_ABIS, 0, SUPPORTED_ABIS.length);
            } else {
                SUPPORTED_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }

        //init SUPPORTED_32_BIT_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_32_BIT_ABIS != null) {
                SUPPORTED_32_BIT_ABIS = new String[Build.SUPPORTED_32_BIT_ABIS.length];
                System.arraycopy(Build.SUPPORTED_32_BIT_ABIS, 0, SUPPORTED_32_BIT_ABIS, 0, SUPPORTED_32_BIT_ABIS.length);
            } else {
                SUPPORTED_32_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_32_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }

        //init SUPPORTED_64_BIT_ABIS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.SUPPORTED_64_BIT_ABIS != null) {
                SUPPORTED_64_BIT_ABIS = new String[Build.SUPPORTED_64_BIT_ABIS.length];
                System.arraycopy(Build.SUPPORTED_64_BIT_ABIS, 0, SUPPORTED_64_BIT_ABIS, 0, SUPPORTED_64_BIT_ABIS.length);
            } else {
                SUPPORTED_64_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
        } else {
            SUPPORTED_64_BIT_ABIS = new String[]{Build.CPU_ABI, Build.CPU_ABI2};
        }
    }
}
