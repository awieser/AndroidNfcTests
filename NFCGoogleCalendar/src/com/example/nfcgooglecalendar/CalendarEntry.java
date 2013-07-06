package com.example.nfcgooglecalendar;

public class CalendarEntry {

	private String displayName;
	private String ownerName;

	public CalendarEntry(String ownerName, String displayName) {
		this.ownerName = ownerName;
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName + " (" + ownerName + ")";
	}

}
