package nfclogger.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import nfclogger.db.model.Category;
import nfclogger.db.model.CategoryBase;
import nfclogger.db.model.CategorySummary;
import nfclogger.db.model.TimeStamp;

public class DataSourceSingleton {

	private static final String NFC_TIMELOGGER = "nfc-timelogger";
	private static DataSourceSingleton instance;
	private Category selectedCategory;
	private CustomAdapter<TimeStamp> selectedTimestampsAdapter;
	private ArrayAdapter<Category> categoryAdapter;
	private ArrayAdapter<CategoryBase> categorySummaryAdapter;

	private DataSourceSingleton() {
		selectedCategory = null;
		selectedTimestampsAdapter = null;
		categoryAdapter = null;
		categorySummaryAdapter = null;
	}

	public static DataSourceSingleton getInstance() {
		if (instance == null)
			instance = new DataSourceSingleton();

		return instance;
	}

	public void addCategory(Category category) {
		categoryAdapter.add(category);
		categorySummaryAdapter.add(new CategorySummary(category));
	}

	public void setSelectedCategory(Category category) {
		selectedCategory = category;
		if (selectedTimestampsAdapter != null) {
			selectedTimestampsAdapter.clear();
			selectedTimestampsAdapter.addAll(selectedCategory.getTimestamps());
		}
	}

	public ListAdapter getSelectedTimestampsAdapter(Context context) {
		if (selectedTimestampsAdapter == null) {
			selectedTimestampsAdapter = new CustomAdapter<TimeStamp>(context,
					android.R.layout.simple_list_item_1);
		}
		return selectedTimestampsAdapter;
	}

	public ArrayAdapter<Category> getCategoryAdapter(Context context) {

		if (categoryAdapter == null) {
			categoryAdapter = new ArrayAdapter<Category>(context,
					android.R.layout.simple_spinner_item);

			TimeloggerDatabaseOpenHelper dbOpenHelpener = new TimeloggerDatabaseOpenHelper(
					context.getApplicationContext());
			categoryAdapter.addAll(dbOpenHelpener.getCategories());
			dbOpenHelpener.close();
		}
		return categoryAdapter;
	}

	public ArrayAdapter<CategoryBase> getCategorySummaryAdapter(
			Context context) {
		
		if (categorySummaryAdapter == null) {
			categorySummaryAdapter = new ArrayAdapter<CategoryBase>(context,
					android.R.layout.simple_list_item_1);
		}
		categorySummaryUpdate(context);
		return categorySummaryAdapter;
	}

	private void categorySummaryUpdate(Context context) {
		if(categorySummaryAdapter == null)
			return;
		
		categorySummaryAdapter.clear();
		TimeloggerDatabaseOpenHelper dbOpenHelpener = new TimeloggerDatabaseOpenHelper(
				context);

		List<Category> categories = dbOpenHelpener.getCategories();
		ArrayList<CategorySummary> categoriesSummary = new ArrayList<CategorySummary>();
		for (Category c : categories) {
			categoriesSummary.add(new CategorySummary(c));
		}
		categorySummaryAdapter.addAll(categoriesSummary);
		dbOpenHelpener.close();
	}

	public void updateSelectedCategory(Context context) {
		setSelectedCategory(selectedCategory);
		categorySummaryUpdate(context);
	}
}
