/*
 * Copyright (C) 2018 The Pixel Dust Project
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

import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.preference.Preference;

import com.android.settings.R;
import com.android.internal.logging.nano.MetricsProto;
import com.android.settings.SettingsPreferenceFragment;

public class StockNavBarSettings extends SettingsPreferenceFragment {

    private static final String PREF_GESTURES = "gesture_settings";
    private Preference mGestures;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.stock_settings);

        // Disable gesture settings if recents type is different from Stock Pie
        /*mGestures = (Preference) findPreference(PREF_GESTURES);
        final int recentsType = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.RECENTS_COMPONENT, 0);
        mGestures.setEnabled(recentsType != 1);
        */
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}