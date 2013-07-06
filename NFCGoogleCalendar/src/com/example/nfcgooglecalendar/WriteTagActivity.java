package com.example.nfcgooglecalendar;


import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class WriteTagActivity extends Activity {
	private Spinner spinnerCalendars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_tag);
		
		spinnerCalendars =  (Spinner) findViewById(R.id.spinnerCalendars);
		List<CalendarEntry> items = CalendarAdapter.getCalendars(getContentResolver()); 
		ArrayAdapter<CalendarEntry> adapter = new ArrayAdapter<CalendarEntry>(this,
		        android.R.layout.simple_spinner_dropdown_item, items);
	
		spinnerCalendars.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.write_tag, menu);
		return true;
	}

}
