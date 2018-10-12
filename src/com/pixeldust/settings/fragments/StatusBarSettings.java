/*
 * Copyright (C) 2016 The CyanogenMod project
 * Copyright (C) 2017-2018 The LineageOS project
 * Copyright (C) 2019 The PixelDust Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pixeldust.settings.fragments;

import android.content.Context;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v7.preference.DropDownPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.preference.PreferenceGroup;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.view.View;

import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatusBarSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_CATEGORY = "network_traffic";
    private static final String STATUS_BAR_BATTERY_STYLE = "status_bar_battery_style";
    private static final String SHOW_BATTERY_PERCENT = "show_battery_percent";

    public static final int BATTERY_STYLE_PORTRAIT = 0;
    public static final int BATTERY_STYLE_CIRCLE = 1;
    public static final int BATTERY_STYLE_DOTTED_CIRCLE = 2;
    public static final int BATTERY_STYLE_TEXT = 3;
    public static final int BATTERY_STYLE_HIDDEN = 4;

    private PreferenceCategory mNetworkTrafficCategory;
    private DropDownPreference mNetTrafficMode;
    private SwitchPreference mNetTrafficAutohide;
    private DropDownPreference mNetTrafficUnits;
    private SwitchPreference mNetTrafficShowUnits;

    private static List<String> sNonIndexableKeys = new ArrayList<>();

    private ListPreference mBatteryStyle;
    private ListPreference mBatteryPercent;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_statusbar);

        final ContentResolver resolver = getActivity().getContentResolver();

        mNetworkTrafficCategory = (PreferenceCategory) findPreference(NETWORK_TRAFFIC_CATEGORY);

        if (!isNetworkTrafficAvailable()) {
            getPreferenceScreen().removePreference(mNetworkTrafficCategory);
        } else {
            mNetTrafficMode = (DropDownPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_MODE);
            mNetTrafficMode.setOnPreferenceChangeListener(this);
            int mode = Settings.System.getIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_MODE, 0, UserHandle.USER_CURRENT);
            mNetTrafficMode.setValue(String.valueOf(mode));

            mNetTrafficAutohide = (SwitchPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_AUTOHIDE);
            mNetTrafficAutohide.setOnPreferenceChangeListener(this);

            mNetTrafficUnits = (DropDownPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_UNITS);
            mNetTrafficUnits.setOnPreferenceChangeListener(this);
            int units = Settings.System.getIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_UNITS, /* Mbps */ 1, UserHandle.USER_CURRENT);
            mNetTrafficUnits.setValue(String.valueOf(units));

            mNetTrafficShowUnits = (SwitchPreference)
                    findPreference(Settings.System.NETWORK_TRAFFIC_SHOW_UNITS);
            mNetTrafficShowUnits.setOnPreferenceChangeListener(this);

            updateNetworkTrafficEnabledStates(mode);
        }

        mBatteryPercent = (ListPreference) findPreference(SHOW_BATTERY_PERCENT);

        mBatteryStyle = (ListPreference) findPreference(STATUS_BAR_BATTERY_STYLE);
        int batterystyle = Settings.System.getIntForUser(resolver,
                Settings.System.STATUS_BAR_BATTERY_STYLE, BATTERY_STYLE_PORTRAIT,
                UserHandle.USER_CURRENT);
        mBatteryStyle.setOnPreferenceChangeListener(this);

        updateBatteryOptions(batterystyle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNetworkTrafficCategory != null && !isNetworkTrafficAvailable()) {
            getPreferenceScreen().removePreference(mNetworkTrafficCategory);
        }
    }

    private boolean isNetworkTrafficAvailable(){
        if (getResources().getBoolean(
                com.android.internal.R.bool.config_physicalDisplayCutout)){
            return Settings.System.getIntForUser(getActivity().getContentResolver(),
                Settings.System.DISPLAY_CUTOUT_HIDDEN, 0, UserHandle.USER_CURRENT) == 1;
        } else {
            return true;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetTrafficMode) {
            int mode = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_MODE, mode);
            updateNetworkTrafficEnabledStates(mode);
        } else if (preference == mNetTrafficUnits) {
            int units = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NETWORK_TRAFFIC_UNITS, units);
        } else if (preference == mBatteryStyle) {
            int value = Integer.parseInt((String) newValue);
            updateBatteryOptions(value);
            return true;
        }
        return true;
    }

    private void updateNetworkTrafficEnabledStates(int mode) {
        final boolean enabled = mode != 0;
        mNetTrafficAutohide.setEnabled(enabled);
        mNetTrafficUnits.setEnabled(enabled);
        mNetTrafficShowUnits.setEnabled(enabled);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    private void updateBatteryOptions(int batterystyle) {
        mBatteryPercent.setEnabled(batterystyle != BATTERY_STYLE_TEXT && batterystyle != BATTERY_STYLE_HIDDEN);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
