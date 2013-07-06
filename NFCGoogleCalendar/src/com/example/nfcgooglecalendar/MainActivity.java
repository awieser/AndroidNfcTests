package com.example.nfcgooglecalendar;

import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	private Spinner spinnerCalendars;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		spinnerCalendars = (Spinner) findViewById(R.id.spinnerMainCalendars);
		List<CalendarEntry> items = CalendarAdapter
				.getCalendars(getContentResolver());
		ArrayAdapter<CalendarEntry> adapter = new ArrayAdapter<CalendarEntry>(
				this, android.R.layout.simple_spinner_dropdown_item, items);

		spinnerCalendars.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
			
		case R.id.action_write_tag:
			startActivity(new Intent(this, WriteTagActivity.class));
			break;
		}

		return super.onOptionsItemSelected(item);
	}


	public void buttonClickAddEvent(View v) {
		Intent intent = new Intent(this,AddEventActivity.class);
		intent.setAction(AddEventActivity.MANUAL_ADD_EVENT);
		CalendarEntry calEntry = (CalendarEntry) spinnerCalendars.getSelectedItem();
		intent.putExtra(AddEventActivity.CALENDAR_ID,calEntry.getId());
		startActivity(intent);
		
	}

}
