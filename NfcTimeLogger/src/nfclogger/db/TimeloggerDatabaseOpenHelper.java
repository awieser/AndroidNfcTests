package nfclogger.db;

import java.util.ArrayList;
import java.util.List;

import nfclogger.db.model.Category;
import nfclogger.db.model.DatabaseEntry;
import nfclogger.db.model.TimeStamp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TimeloggerDatabaseOpenHelper extends SQLiteOpenHelper {

	private static final String NFC_TIMELOGGER = "nfc-timelogger";

	private static final int DATABASE_VERSION = 8;
	private static final String DATABASE_NAME = "timelogger";

	private static final String CREATE_TABLE_PREFIX = "CREATE TABLE IF NOT EXISTS ";

	private static final String COLUMN_ID = "id";
	private static final String COLUMN_CATEGORY_NAME = "name";
	private static final String COLUMN_TIMESTAMP_BEGIN = "begin";
	private static final String COLUMN_TIMESTAMP_END = "end";
	private static final String CREATE_COLUMN_ID = COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT";
	private static final String CREATE_COLUMN_NAME = COLUMN_CATEGORY_NAME
			+ " TEXT NOT NULL";
	private static final String CREATE_COLUMN_TIMESTAMP_POSTFIX = " TEXT";

	private static final String TABLE_CATEGORY_NAME = "category";
	private static final String TABLE_TIMESTAMP_NAME = "timestamp";

	private static final String TABLE_CREATE_CATEGORY = CREATE_TABLE_PREFIX
			+ TABLE_CATEGORY_NAME + " (" + CREATE_COLUMN_ID + ","
			+ CREATE_COLUMN_NAME + ");";

	private static final String COLUMN_CATEGORY_CATEGORY_FOREIGN_KEY = "category_id";

	private static final String TABLE_CREATE_TIMESTAMP = CREATE_TABLE_PREFIX
			+ TABLE_TIMESTAMP_NAME + " (" + CREATE_COLUMN_ID + ","
			+ COLUMN_TIMESTAMP_BEGIN + CREATE_COLUMN_TIMESTAMP_POSTFIX + ", "
			+ COLUMN_TIMESTAMP_END + CREATE_COLUMN_TIMESTAMP_POSTFIX + ","
			+ COLUMN_CATEGORY_CATEGORY_FOREIGN_KEY + " INTEGER , "
			+ "FOREIGN KEY(" + COLUMN_CATEGORY_CATEGORY_FOREIGN_KEY
			+ ") REFERENCES " + TABLE_CATEGORY_NAME + "(" + COLUMN_ID + ")"
			+ ");";

	private static Context context;

	public TimeloggerDatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		TimeloggerDatabaseOpenHelper.context = context;
	}

	public TimeloggerDatabaseOpenHelper() {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(NFC_TIMELOGGER, "creating tables");
		db.execSQL(TABLE_CREATE_CATEGORY);
		db.execSQL(TABLE_CREATE_TIMESTAMP);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIMESTAMP_NAME);
		onCreate(db);
	}

	public String handleCategory(String categoryName) {
		Log.d(NFC_TIMELOGGER, "handle category: " + categoryName);
		Category category = getCategory(categoryName);
		if (category == null)
			category = addCategory(categoryName);

		TimeStamp timestamp = category.getLastTimeStamp();
		if (timestamp == null
				|| (timestamp.getBegin() != null && timestamp.getEnd() != null)) {
			addTimestampBegin(category.getId());

			return "start: ";
		} else if (timestamp.getEnd() == null) {
			addTimestampEnd(timestamp.getId());
			return "end: ";
		}
		return "error";

	}

	private void addTimestampBegin(long categoryId) {
		Log.d(NFC_TIMELOGGER, "adding timestamp with category_id= "
				+ categoryId);
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TIMESTAMP_BEGIN, TimeStamp.getDateTimeNow());
		values.put(COLUMN_CATEGORY_CATEGORY_FOREIGN_KEY, categoryId);
		long row_id = db.insert(TABLE_TIMESTAMP_NAME, null, values);
		Log.d(NFC_TIMELOGGER, "added timestamp id=" + row_id);
		db.close();
	}

	private void addTimestampEnd(long id) {
		Log.d(NFC_TIMELOGGER, "adding  end time to timestamp with id= " + id);
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_TIMESTAMP_END, TimeStamp.getDateTimeNow());
		db.update(TABLE_TIMESTAMP_NAME, values, COLUMN_ID + "=?",
				new String[] { Long.toString(id) });
		long row_id = db.insert(TABLE_TIMESTAMP_NAME, null, values);
		Log.d(NFC_TIMELOGGER, "added timestamp id=" + row_id);
		db.close();
	}

	public Category getCategory(String categoryName) {
		Log.d(NFC_TIMELOGGER, "get category: " + categoryName);
		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(TABLE_CATEGORY_NAME, new String[] { COLUMN_ID,
				COLUMN_CATEGORY_NAME }, COLUMN_CATEGORY_NAME + "=?",
				new String[] { categoryName }, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			return new Category(Integer.parseInt(cursor.getString(0)),
					cursor.getString(1));
		}

		return null;
	}

	private Category addCategory(String categoryName) {
		Log.d(NFC_TIMELOGGER, "adding category: " + categoryName);
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_CATEGORY_NAME, categoryName);
		long row_id = db.insert(TABLE_CATEGORY_NAME, null, values);
		db.close();
		DataSourceSingleton.getInstance()
				.addCategory(getCategory(categoryName));
		return new Category(row_id, categoryName);
	}

	public List<Category> getCategories() {
		Log.d(NFC_TIMELOGGER, "getCategories");
		List<Category> categories = new ArrayList<Category>();

		String selectQuery = "SELECT * FROM " + TABLE_CATEGORY_NAME
				+ " ORDER BY " + COLUMN_ID + " DESC";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				categories.add(new Category(Integer.parseInt(cursor
						.getString(0)), cursor.getString(1)));
//				Log.d(NFC_TIMELOGGER, cursor.getString(1));
			} while (cursor.moveToNext());
		}
		return categories;
	}

	public List<DatabaseEntry> getTimestamps(long id) {
		return getTimestamps(id,null);
	}

	public DatabaseEntry getLastTimestamp(long id) {
		DatabaseEntry entry = null;

		List<DatabaseEntry> timestamps = getTimestamps(id, "1");
		if (timestamps.size() > 0)
			entry = timestamps.get(0);
		return entry;
	}

	private List<DatabaseEntry> getTimestamps(long id, String limit) {
		List<DatabaseEntry> timestamps = new ArrayList<DatabaseEntry>();
		Log.d(NFC_TIMELOGGER, "getTimestamps");

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_TIMESTAMP_NAME, new String[] {
				COLUMN_ID, COLUMN_TIMESTAMP_BEGIN, COLUMN_TIMESTAMP_END },
				COLUMN_CATEGORY_CATEGORY_FOREIGN_KEY + "=?",
				new String[] { Long.toString(id) }, null, null, COLUMN_ID
						+ " DESC", limit);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				timestamps.add(new TimeStamp(Integer.parseInt(cursor
						.getString(0)), cursor.getString(1), cursor
						.getString(2)));
//				Log.d(NFC_TIMELOGGER,
//						cursor.getString(1) + "--" + cursor.getString(2));
			} while (cursor.moveToNext());
		}

		return timestamps;
	}
}