package com.example.nfcgooglecalendar;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;

public class CalendarAdapter {

	public static final String[] EVENT_PROJECTION = new String[] {
			Calendars._ID, // 0
			Calendars.CALENDAR_DISPLAY_NAME, // 1
			Calendars.OWNER_ACCOUNT, // 2
			Calendars.CALENDAR_ACCESS_LEVEL, };

	static String selection = "((" + Calendars.CALENDAR_ACCESS_LEVEL + " = "
			+ Calendars.CAL_ACCESS_EDITOR + ") OR  ("
			+ Calendars.CALENDAR_ACCESS_LEVEL + " = "
			+ Calendars.CAL_ACCESS_CONTRIBUTOR + ") OR  ("
			+ Calendars.CALENDAR_ACCESS_LEVEL + " = "
			+ Calendars.CAL_ACCESS_OWNER + "))";

	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 1;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 2;

	public static List<CalendarEntry> getCalendars(ContentResolver cr) {
		List<CalendarEntry> ret = new ArrayList<CalendarEntry>();
		Cursor cur = null;
		cur = cr.query(Calendars.CONTENT_URI, EVENT_PROJECTION, selection,
				null, null);

		while (cur.moveToNext()) {
			String displayName = null;
			String ownerName = null;

			long calId = 0;

			calId = cur.getLong(PROJECTION_ID_INDEX);

			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			ret.add(new CalendarEntry(ownerName, displayName, calId));

		}
		return ret;
	}

	public static CalendarEntry getCalendarEntry(String ownerNameSearch,
			ContentResolver cr) {
		Cursor cur = null;
		cur = cr.query(Calendars.CONTENT_URI, EVENT_PROJECTION, selection,
				null, null);

		while (cur.moveToNext()) {
			long calID = 0;
			String displayName = null;
			String ownerName = null;

			// Get the field values
			calID = cur.getLong(PROJECTION_ID_INDEX);
			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			if (ownerName.equals(ownerNameSearch)) {
				return new CalendarEntry(ownerName, displayName, calID);
			}

		}
		return null;
	}

	public static long addEvent(ContentResolver cr, long id, String eventTitle,
			String eventDescription, long startMillis, long endMillis) {
		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, startMillis);
		values.put(Events.DTEND, endMillis);
		values.put(Events.TITLE, eventTitle);
		values.put(Events.DESCRIPTION, eventDescription);
		values.put(Events.CALENDAR_ID, id);
		values.put(Events.EVENT_TIMEZONE, "Europe/Vienna");
		Uri uri = cr.insert(Events.CONTENT_URI, values);
		return Long.parseLong(uri.getLastPathSegment());

	}

	public static CalendarEntry getCalendarEntry(long cal_id, ContentResolver cr) {
		Cursor cur = null;
		cur = cr.query(Calendars.CONTENT_URI, EVENT_PROJECTION, selection,
				null, null);

		while (cur.moveToNext()) {
			long calID = 0;
			String displayName = null;
			String ownerName = null;

			// Get the field values
			calID = cur.getLong(PROJECTION_ID_INDEX);
			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

			if (calID == cal_id) {
				return new CalendarEntry(ownerName, displayName, calID);
			}

		}
		return null;
	}

}
