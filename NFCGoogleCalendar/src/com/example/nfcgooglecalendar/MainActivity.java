package com.example.nfcgooglecalendar;

import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String[] EVENT_PROJECTION = new String[] {
	    Calendars._ID,                           // 0
	    Calendars.ACCOUNT_NAME,                  // 1
	    Calendars.CALENDAR_DISPLAY_NAME,         // 2
	    Calendars.OWNER_ACCOUNT                  // 3
	};
	  
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
	private TextView textViewTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 /////////////////////////////////////// START
		textViewTest = (TextView) findViewById(R.id.textview_test);
		// Projection array. Creating indices for this array instead of doing
		// dynamic lookups improves performance.
		
		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		/*String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND (" 
		                        + Calendars.ACCOUNT_TYPE + " = ?) AND ("
		                        + Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = new String[] {"sampleuser@gmail.com", "com.google",
		        "sampleuser@gmail.com"}; */
		// Submit the query and get a Cursor object back. 
//		cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
		cur = cr.query(uri, EVENT_PROJECTION,null,null,null);
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
		    long calID = 0;
		    String displayName = null;
		    String accountName = null;
		    String ownerName = null;
		      
		    // Get the field values
		    calID = cur.getLong(PROJECTION_ID_INDEX);
		    displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
		    accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
		    ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
		              
		    // Do something with the values...
		    
		    textViewTest.append(calID + "- " + displayName + "\n");
		}
		
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
        	
//            Intent i = new Intent(this, UserSettingActivity.class);
//            startActivityForResult(i, RESULT_SETTINGS);
            break;
 
        }
 
		return super.onOptionsItemSelected(item);
	}
	
	/* private void showUserSettings() {
	        SharedPreferences sharedPrefs = PreferenceManager
	                .getDefaultSharedPreferences(this);
	 
	        StringBuilder builder = new StringBuilder();
	 
	        builder.append("\n Title: " + sharedPrefs.getString("title", "NULL"));
	 
	        builder.append("\n Send report:" + sharedPrefs.getBoolean("prefSendReport", false));
	 
	        builder.append("\n Sync Frequency: " + sharedPrefs.getString("prefSyncFrequency", "NULL"));
	 
	        TextView settingsTextView = (TextView) findViewById(R.id.textUserSettings);
	 
	        settingsTextView.setText(builder.toString());
	    }*/

	public void buttonClickAddEvent(View v){
		long calID = 1;
		
		
		long startMillis = 0; 
		long endMillis = 0;     
		Calendar beginTime = Calendar.getInstance();
		startMillis = beginTime.getTimeInMillis();
		beginTime.add(Calendar.HOUR, 1);
		endMillis = beginTime.getTimeInMillis();
		

		

		// get the event ID that is the last element in the Uri
		long eventID = addEvent(calID, startMillis, endMillis, "Meeting", "");
		textViewTest.append("added Event to " + calID + "\n");
		
	}
	
	private long addEvent(long calID, long startMillis, long endMillis, String title, String description){
		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, title);
		values.put(Events.DESCRIPTION, description);
		values.put(Events.CALENDAR_ID, calID);
		values.put(Events.EVENT_TIMEZONE, "Europe/Vienna");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		return Long.parseLong(uri.getLastPathSegment());
	}
}
