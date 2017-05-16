/*
**        DroidPlugin Project
**
** Copyright(c) 2015 bb.S <bangbang.song@gmail.com>
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

package com.example.TestPlugin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;
import java.util.TreeSet;

/**
 * Created by bb.S on 15-9-23.
 */
public class AllLauncherActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().add(android.R.id.content, new ActFragment()).commit();
    }

    public static class ActFragment extends Fragment {
        public static final  java.lang.String EXTRA_RESOVLEINFO = ActFragment.class.getName() + ".EXTRA_RESOLVEINFO";
        private ListView mListV;
        private PackageManager mPm;

        public ActFragment(){

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mListV = new ListView(getActivity());
            return mListV;
//            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            mPm = getActivity().getPackageManager();
            fillData(mListV);
        }

        void fillData(ListView view){
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> acts = mPm.queryIntentActivities(i, 0);
            ArrayAdapter<ResolveInfo> adapter = new ArrayAdapter<ResolveInfo>(getActivity(), 0, acts){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ResolveInfo r = getItem(position);
                    LinearLayout v = null;
                    if (convertView == null){
                        v = new LinearLayout(getActivity());
                        ImageView iv = new ImageView(getActivity());
                        v.addView(iv, dp2px(100), dp2px(100));
                        TextView t = new TextView(getActivity());
                        v.addView(t, AbsListView.LayoutParams.MATCH_PARENT, dp2px(100));

                        convertView = v;
                    }
                    v = (LinearLayout) convertView;
                    try {
                        ((ImageView)v.getChildAt(0)).setImageDrawable(mPm.getActivityIcon(new ComponentName(r.activityInfo.packageName, r.activityInfo.name)));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    ((TextView)v.getChildAt(1)).setText(r.activityInfo.applicationInfo.className);

                    return v;
//                    return super.getView(position, convertView, parent);
                }
            };

            view.setAdapter(adapter);
            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ResolveInfo r = (ResolveInfo) parent.getAdapter().getItem(position);
                    onCompClick(r);
                }

            });
        }


        private void onCompClick(ResolveInfo r) {
            Intent result = new Intent();
            result.putExtra(EXTRA_RESOVLEINFO, r);
            getActivity().setResult(RESULT_OK, result);
            getActivity().finish();
        }

        private int dp2px(int dp) {
            return dp;
        }
    }

}