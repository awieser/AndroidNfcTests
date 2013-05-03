package nfclogger.db.model;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeStamp extends DatabaseEntry{

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private String end;
	private String begin;

	public TimeStamp(long id, String begin, String end) {
		this.id = id;
		this.begin = begin;
		this.end = end;
	}

	@Override
	public String toString() {
		return begin + " -- " + end;
	}

	public Date getEnd() {
		return getDate(end);
	}

	public Date getBegin() {
		return getDate(begin);
	}

	static public String getDateTimeNow() {
		Date date = new Date();
		return dateFormat.format(date);
	}

	static private Date getDate(String date) {
		if(date == null){
			return null;
		}
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public long getTimeDiff() {
		if( end == null || begin == null)
			return 0;
		
		return (getEnd().getTime() - getBegin().getTime() ) / 1000;		
	}

}
