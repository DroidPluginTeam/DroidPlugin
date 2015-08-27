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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.morgoo.droidplugin.core.Env;
import com.morgoo.droidplugin.pm.PluginManager;

import java.net.URISyntaxException;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2015/5/27.
 */
public class ShortcutProxyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Intent intent = getIntent();

            if (intent != null) {
                Intent forwordIntent = getForwarIntent();
                if (forwordIntent != null) {
                    forwordIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    forwordIntent.putExtras(intent);
                    if (PluginManager.getInstance().isConnected()) {
                        startActivity(forwordIntent);
                        finish();
                    } else {
                        waitAndStart(forwordIntent);
                    }
                } else {
                    finish();
                }
            } else {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void waitAndStart(final Intent forwordIntent) {
        new Thread() {
            @Override
            public void run() {
                try {
                    PluginManager.getInstance().waitForConnected();
                    startActivity(forwordIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }

            }
        }.start();
    }

    private Intent getForwarIntent() {
        Intent intent = getIntent();
        try {
            if (intent != null) {
                Intent forwordIntent = intent.getParcelableExtra(Env.EXTRA_TARGET_INTENT);
                String intentUri = intent.getStringExtra(Env.EXTRA_TARGET_INTENT_URI);
                if (intentUri != null) {
                    try {
                        return Intent.parseUri(intentUri, 0);
                    } catch (URISyntaxException e) {
                    }
                } else if (forwordIntent != null) {
                    return forwordIntent;
                }
            }
        }catch (Exception e){}
        return null;
    }
}
