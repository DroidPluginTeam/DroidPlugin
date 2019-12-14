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

package com.morgoo.helper;

import android.content.ComponentName;
import android.text.TextUtils;

import java.util.Comparator;

public class ComponentNameComparator implements Comparator<ComponentName> {

    @Override
    public int compare(ComponentName lhs, ComponentName rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs != null && rhs == null) {
            return 1;
        } else if (lhs == null && rhs != null) {
            return -1;
        } else {
            if (TextUtils.equals(lhs.getPackageName(), rhs.getPackageName()) && TextUtils.equals(lhs.getShortClassName(), rhs.getShortClassName())) {
                return 0;
            } else {
                return lhs.compareTo(rhs);
            }
        }
    }
}