package nfclogger.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import nfclogger.db.model.DatabaseEntry;

public class CustomAdapter<T> extends BaseAdapter {
	Context context;
	private ArrayList<DatabaseEntry> items;
	private int viewResourceId;

	public CustomAdapter(Context context, int viewResourceId) {
		super();
		this.context = context;
		this.viewResourceId = viewResourceId;

		items = new ArrayList<DatabaseEntry>();
	}

	@Override
	public long getItemId(int position) {
		DatabaseEntry item = (DatabaseEntry) getItem(position);
		return item.getId();
	}

	@Override
	public DatabaseEntry getItem(int position) {
		return items.get(position);
	}

	@Override
	public int getCount() {
		if (items == null) {
			return 0;
		} else {
			return items.size();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(viewResourceId, parent, false);
		}
		DatabaseEntry item = getItem(position);
		
		((TextView) convertView).setText(item.toString());
		convertView.setId(position);
		return convertView;
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	public void addAll(List<DatabaseEntry> addList) {
		items.addAll(addList);
		notifyDataSetChanged();
	}
}
