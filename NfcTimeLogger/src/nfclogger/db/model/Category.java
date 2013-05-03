package nfclogger.db.model;

import java.util.List;

import android.util.Log;


import nfclogger.db.TimeloggerDatabaseOpenHelper;

public class Category extends CategoryBase{

	private String name;

	public Category(long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public List<DatabaseEntry> getTimestamps(){
		TimeloggerDatabaseOpenHelper db = new TimeloggerDatabaseOpenHelper();
		List<DatabaseEntry> timestamps = db.getTimestamps(id);
		db.close();
		return timestamps;
	}
	public String getName() {
		return name;
	}
	
	public boolean equals(Category o) {
		return name.equals(o.getName());
	}

	public TimeStamp getLastTimeStamp() {
		TimeloggerDatabaseOpenHelper db = new TimeloggerDatabaseOpenHelper();
		DatabaseEntry t = db.getLastTimestamp(id);
		db.close();
		return (TimeStamp) t;
	}

	
	public long getTimeSum() {
		long sum =0;
		
		List<DatabaseEntry> timestamps = getTimestamps();
		for (DatabaseEntry timestamp : timestamps) {
			
			TimeStamp t = (TimeStamp)timestamp;
			sum += t.getTimeDiff();
			Log.d("nfc-timelogger", "timestamp." + sum);
		}
		
		// TODO Auto-generated method stub
		return sum;
	}
}
