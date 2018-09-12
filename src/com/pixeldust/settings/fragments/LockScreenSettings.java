package com.pixeldust.settings.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.pixeldust.PixeldustUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.pixeldust.settings.preferences.CustomSeekBarPreference;

public class LockScreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String FACE_UNLOCK_PREF = "face_auto_unlock";
    private static final String FACE_UNLOCK_PACKAGE = "com.android.facelock";
    private static final String CUSTOM_TEXT_CLOCK_FONT_SIZE  = "custom_text_clock_font_size";
    private static final String LOCK_CLOCK_FONTS = "lock_clock_fonts";

    private SwitchPreference mFaceUnlock;
    private CustomSeekBarPreference mCustomTextClockFontSize;
    private ListPreference mLockClockFonts;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.pixeldust_settings_lockscreen);

        boolean mFaceUnlockEnabled = Settings.Secure.getIntForUser(getActivity().getContentResolver(),
                Settings.Secure.FACE_AUTO_UNLOCK, getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_face_unlock_enabled_by_default) ? 1 : 0,
                UserHandle.USER_CURRENT) != 0;

        mFaceUnlock = (SwitchPreference) findPreference(FACE_UNLOCK_PREF);
        mFaceUnlock.setChecked(mFaceUnlockEnabled);

        if (!PixeldustUtils.isPackageInstalled(getActivity(), FACE_UNLOCK_PACKAGE)) {
            mFaceUnlock.setEnabled(false);
            mFaceUnlock.setSummary(getActivity().getString(
                    R.string.face_auto_unlock_not_available));
        }

        // Custom Text Clock Size
        mCustomTextClockFontSize = (CustomSeekBarPreference) findPreference(CUSTOM_TEXT_CLOCK_FONT_SIZE);
        mCustomTextClockFontSize.setValue(Settings.System.getInt(getContentResolver(),
                Settings.System.CUSTOM_TEXT_CLOCK_FONT_SIZE, 32));
        mCustomTextClockFontSize.setOnPreferenceChangeListener(this);

        // Lockscren Clock Fonts
        mLockClockFonts = (ListPreference) findPreference(LOCK_CLOCK_FONTS);
        mLockClockFonts.setValue(String.valueOf(Settings.System.getInt(
                getContentResolver(), Settings.System.LOCK_CLOCK_FONTS, 29)));
        mLockClockFonts.setSummary(mLockClockFonts.getEntry());
        mLockClockFonts.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mCustomTextClockFontSize) {
            int top = (Integer) newValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.CUSTOM_TEXT_CLOCK_FONT_SIZE, top*1);
            return true;
        } else if (preference == mLockClockFonts) {
            Settings.System.putInt(getContentResolver(), Settings.System.LOCK_CLOCK_FONTS,
                    Integer.valueOf((String) newValue));
            mLockClockFonts.setValue(String.valueOf(newValue));
            mLockClockFonts.setSummary(mLockClockFonts.getEntry());
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.PIXELDUST;
    }
}
