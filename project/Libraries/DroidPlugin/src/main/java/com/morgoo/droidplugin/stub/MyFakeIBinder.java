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

package com.morgoo.droidplugin.stub;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

import java.io.FileDescriptor;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/3/4.
 */
public class MyFakeIBinder implements IBinder {

    @Override
    public String getInterfaceDescriptor() throws RemoteException {
        return null;
    }

    @Override
    public boolean pingBinder() {
        return false;
    }

    @Override
    public boolean isBinderAlive() {
        return false;
    }

    @Override
    public IInterface queryLocalInterface(String s) {
        return null;
    }

    @Override
    public void dump(FileDescriptor fileDescriptor, String[] strings) throws RemoteException {

    }

    @Override
    public void dumpAsync(FileDescriptor fileDescriptor, String[] strings) throws RemoteException {

    }

    @Override
    public boolean transact(int i, Parcel parcel, Parcel parcel1, int i1) throws RemoteException {
        return false;
    }

    @Override
    public void linkToDeath(DeathRecipient deathRecipient, int i) throws RemoteException {

    }

    @Override
    public boolean unlinkToDeath(DeathRecipient deathRecipient, int i) {
        return false;
    }
}
