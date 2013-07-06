package com.example.nfcgooglecalendar;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public static final String KEY_PREFERENCE_EVENT_TITLE = "prefCalendarTitle";
	public static final String KEY_PREFERENCE_EVENT_DESCRIPTION = "prefCalendarDescription";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();

	}
}