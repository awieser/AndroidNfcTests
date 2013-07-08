package com.example.nfcgooglecalendar;

public class CalendarEntry {

	private String displayName;
	private String ownerName;
	private long calID;

	public CalendarEntry(String ownerName, String displayName, long calID) {
		this.calID = calID;
		this.ownerName = ownerName;
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		return displayName + " (" + ownerName + ")";
	}

	public String getOwnerName() {
		return ownerName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getId() {
		return calID;
	}

}
