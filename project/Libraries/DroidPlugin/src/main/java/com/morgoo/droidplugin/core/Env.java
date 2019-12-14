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

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/2/6.
 */
public class Env {
    public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    public static final String ACTION_UNINSTALL_SHORTCUT = "com.android.launcher.action.UNINSTALL_SHORTCUT";

    public static final String EXTRA_TARGET_INTENT = "com.morgoo.droidplugin.OldIntent";
    public static final String EXTRA_TARGET_INTENT_URI = "com.morgoo.droidplugin.OldIntent.Uri";
    public static final String EXTRA_TARGET_INFO = "com.morgoo.droidplugin.OldInfo";
    public static final String EXTRA_STUB_INFO = "com.morgoo.droidplugin.NewInfo";
    public static final String EXTRA_TARGET_AUTHORITY = "TargetAuthority";
    public static final String EXTRA_TYPE = "com.morgoo.droidplugin.EXTRA_TYPE";
    public static final String EXTRA_ACTION = "com.morgoo.droidplugin.EXTRA_ACTION";

}
