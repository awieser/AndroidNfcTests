package com.example.nfcgooglecalendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
				SettingsActivity.KEY_PREFERENCE_EVENT_TITLE);
		onSharedPreferenceChanged(getPreferenceScreen().getSharedPreferences(),
				SettingsActivity.KEY_PREFERENCE_EVENT_DESCRIPTION);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(SettingsActivity.KEY_PREFERENCE_EVENT_TITLE)
				|| key.equals(SettingsActivity.KEY_PREFERENCE_EVENT_DESCRIPTION)) {
			Preference pref = findPreference(key);
			pref.setSummary(sharedPreferences.getString(key, ""));
		}
	}
}
