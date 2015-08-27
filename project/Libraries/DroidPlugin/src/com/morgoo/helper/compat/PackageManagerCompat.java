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

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/4/13.
 */
public class PackageManagerCompat {
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;
    public static final int DELETE_SUCCEEDED = 1;
    public static final int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
}
