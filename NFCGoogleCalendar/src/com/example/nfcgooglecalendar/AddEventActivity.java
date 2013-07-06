package com.example.nfcgooglecalendar;

import java.util.Calendar;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddEventActivity extends Activity {

	private static final String LOG_ADD_EVENT = "ADD_EVENT";
	public static final String MANUAL_ADD_EVENT = "MANUAL_ADD_EVENT";
	public static final String CALENDAR_ID = "CALENDAR_ID";
	private TextView textViewAddEvent;
	private CalendarEntry calendarEntry;
	private TimePicker timePicker;
	private String eventTitle;
	private String eventDescription;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		textViewAddEvent = (TextView) findViewById(R.id.textViewAddEvent);
		proccessIntent(getIntent());

		textViewAddEvent.append(" " + calendarEntry.getDisplayName());

		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		timePicker.setIs24HourView(DateFormat.is24HourFormat(this));

		Calendar time = Calendar.getInstance();

		timePicker.setCurrentHour(time.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(time.get(Calendar.MINUTE));

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		eventTitle = sharedPrefs.getString(
				SettingsActivity.KEY_PREFERENCE_EVENT_TITLE, getResources()
						.getString(R.string.pref_event_title_default_value));
		eventDescription = sharedPrefs.getString(
				SettingsActivity.KEY_PREFERENCE_EVENT_DESCRIPTION,
				getResources().getString(
						R.string.pref_event_description_default_value));
	}

	@Override
	protected void onNewIntent(Intent intent) {
		proccessIntent(intent);
		super.onNewIntent(intent);
	}

	private void proccessIntent(Intent intent) {
		Log.d(LOG_ADD_EVENT, "new intent, action=" + intent.getAction());
		if (intent.getAction().equals(AddEventActivity.MANUAL_ADD_EVENT)) {
			calendarEntry = null;
			long cal_id = intent.getExtras().getLong(CALENDAR_ID);
			calendarEntry = CalendarAdapter.getCalendarEntry(cal_id,
					getContentResolver());
			if (calendarEntry == null) {
				finishNoCalendarFound();
			}
		}
		if (intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
			calendarEntry = null;
			Parcelable[] ndefMessages = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

			if (ndefMessages != null) {
				NdefMessage[] msgs = new NdefMessage[ndefMessages.length];
				for (int i = 0; i < ndefMessages.length; i++) {
					msgs[i] = (NdefMessage) ndefMessages[i];

					NdefRecord[] records = msgs[i].getRecords();
					for (NdefRecord ndefRecord : records) {
						String ownerName = new String(ndefRecord.getPayload());
						Log.d(LOG_ADD_EVENT, ownerName);
						calendarEntry = CalendarAdapter.getCalendarEntry(
								ownerName, getContentResolver());
					}
				}
				if (calendarEntry == null) {
					finishNoCalendarFound();
				}
			}
		}
	}

	private void finishNoCalendarFound() {
		String toastmsg = "No calendar found!";
		Toast.makeText(this, toastmsg, Toast.LENGTH_LONG).show();
		finish();
	}

	public void buttonClickHalfHour(View v) {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.MINUTE, 30);
		long endMillis = time.getTimeInMillis();
		addEvent(endMillis);
	}

	public void buttonClickOneHour(View v) {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.HOUR, 1);
		long endMillis = time.getTimeInMillis();
		addEvent(endMillis);
	}

	public void buttonClickTwoHours(View v) {
		Calendar time = Calendar.getInstance();
		time.add(Calendar.HOUR, 2);
		long endMillis = time.getTimeInMillis();
		addEvent(endMillis);
	}

	public void buttonClickManual(View v) {
		Calendar time = Calendar.getInstance();
		time.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		time.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		long endMillis = time.getTimeInMillis();
		addEvent(endMillis);
	}

	private void addEvent(long endMillis) {
		Calendar beginTime = Calendar.getInstance();
		long startMillis = beginTime.getTimeInMillis();
		CalendarAdapter.addEvent(getContentResolver(), calendarEntry.getId(),
				eventTitle, eventDescription, startMillis, endMillis);
		String toastmsg = "Added event title:" + eventTitle
				+ " \n to calendar:" + calendarEntry.getDisplayName() + "\n";
		Toast.makeText(this, toastmsg, Toast.LENGTH_LONG).show();
		finish();
	}
}
