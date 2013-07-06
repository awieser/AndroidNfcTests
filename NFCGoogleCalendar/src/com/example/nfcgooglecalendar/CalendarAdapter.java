package com.example.nfcgooglecalendar;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.CalendarContract.Calendars;

public class CalendarAdapter {
	
	public static final String[] EVENT_PROJECTION = new String[] {
			Calendars._ID, // 0
			Calendars.ACCOUNT_NAME, // 1
			Calendars.CALENDAR_DISPLAY_NAME, // 2
			Calendars.OWNER_ACCOUNT // 3
	};

	// The indices for the projection array above.
//	private static final int PROJECTION_ID_INDEX = 0;
//	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

	public static List<CalendarEntry> getCalendars(ContentResolver cr) {
		List<CalendarEntry> ret = new ArrayList<CalendarEntry>();
		Cursor cur = null;
		cur = cr.query(Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);
		
		while (cur.moveToNext()) {
			long calID = 0;
			String displayName = null;
			String accountName = null;
			String ownerName = null;

			// Get the field values
//			calID = cur.getLong(PROJECTION_ID_INDEX);
			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
//			accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			ret.add(new CalendarEntry(ownerName,displayName));

		}
		return ret;
	}

}
