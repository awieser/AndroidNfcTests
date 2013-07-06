package com.example.nfcgooglecalendar;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
	public static final String KEY_PREFERENCE_CALENDAR_TITLE = "prefCalendarTitle";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();

	}
}