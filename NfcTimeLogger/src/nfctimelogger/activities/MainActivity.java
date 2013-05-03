package nfctimelogger.activities;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import nfclogger.db.DataSourceSingleton;
import nfclogger.db.model.Category;

import com.example.nfctimelogger.R;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private static final String NFC_TIMELOGGER = "nfc-timelogger";
	public static final String FRAGMENT_UPDATE = "FRAGMENT_UPDATE";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private NdefMessage ndefWriteMsg;
	private BroadcastReceiver broadcastReceiver;
	private IntentFilter broadcastReceiverFilter;

	private NfcAdapter mNfcAdapter;

	private IntentFilter[] intentFiltersArray;

	private PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		// NFC STUFF

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		intentFiltersArray = new IntentFilter[] {
				new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)};

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		broadcastReceiverFilter = new IntentFilter(FRAGMENT_UPDATE);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				Log.d(NFC_TIMELOGGER,
						"detailfragment_intent = " + intent.getAction());
				if (intent.getAction().equals(FRAGMENT_UPDATE)) {
					DataSourceSingleton.getInstance().updateSelectedCategory(getApplicationContext());
				}
			}

		};

	}

	@Override
	protected void onResume() {
		Log.d(NFC_TIMELOGGER, "onResume()");
		this.registerReceiver(broadcastReceiver, broadcastReceiverFilter);
		mNfcAdapter.disableForegroundDispatch(this);
		DataSourceSingleton.getInstance().updateSelectedCategory(getApplicationContext());
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(NFC_TIMELOGGER, "onPause()");
		unregisterReceiver(broadcastReceiver);
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment;
			switch (position) {
			case 0:
				fragment = new DetailFragment();
				break;
			case 1:
				fragment = new SummaryFragment();
				break;
			case 2:
				fragment = new CreateTagFragment();
				break;

			default:
				// TODO Error Handling
				fragment = null;
				break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}

	}

	
	public static class CreateTagFragment extends Fragment {
		public CreateTagFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.fragment_create_tags, container,
					false);
		}

	}

	public static class DetailFragment extends Fragment {

		public DetailFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.fragment_detail, container,
					false);

			ListView listViewTimestamps = (ListView) view.getRootView()
					.findViewById(R.id.listViewTimestamps);

			listViewTimestamps.setAdapter(DataSourceSingleton.getInstance()
					.getSelectedTimestampsAdapter(view.getContext()));

			final Spinner spinnerCategory = (Spinner) view
					.findViewById(R.id.spinnerCategory);

			spinnerCategory
					.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View view, int position, long arg3) {
							Category category = (Category) spinnerCategory
									.getItemAtPosition(position);

							if (category != null && view != null) {
								DataSourceSingleton
										.getInstance()
										.setSelectedCategory(
												category);
							}
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}

					});

			spinnerCategory.setAdapter(DataSourceSingleton.getInstance()
					.getCategoryAdapter(view.getContext()));

			return view;
		}

	}

	public static class SummaryFragment extends Fragment {
		public SummaryFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_summary, container,
					false);
			
			ListView listviewSummary = (ListView) view.findViewById(R.id.listViewSummary);
			
			listviewSummary.setAdapter(DataSourceSingleton.getInstance().getCategorySummaryAdapter(view.getContext()));
			
			return view;
		}

	}

	public void writeTag(View view) {
		Log.d(NFC_TIMELOGGER, "writing tag...");

		EditText tagName = (EditText) findViewById(R.id.editText_tagName);

		String tagNameText = tagName.getText().toString();

		Log.d(NFC_TIMELOGGER, "tag-name: " + tagName.getText());
		NdefRecord mimeRecord = NdefRecord.createMime(
				"application/vnd.nfctimelogger",
				tagNameText.getBytes(Charset.forName("US-ASCII")));

		mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
				intentFiltersArray, null);
		
		
		ndefWriteMsg = new NdefMessage(new NdefRecord[] { mimeRecord });
	}

	protected void onNewIntent(Intent intent) {
		processIntent(intent);
	}

	private void processIntent(Intent intent) {
		Log.d(NFC_TIMELOGGER, "new intent: " + intent.getAction());

		// TODO: better checks
		if (ndefWriteMsg != null
				&& (NfcAdapter.ACTION_TAG_DISCOVERED
						.equals(intent.getAction()))) {
			Log.d(NFC_TIMELOGGER, " trying to write tag");

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			

			Ndef ndefTag = Ndef.get(tag);
			
			if(ndefTag == null){
				Log.d(NFC_TIMELOGGER, "could not get ndef tag");
			}
			
			try {
				ndefTag.connect();
				if (ndefTag.isWritable()) {

					if (ndefTag.getMaxSize() < ndefWriteMsg.toByteArray().length) {
						Toast.makeText(this, "Not enough space on Tag!",
								Toast.LENGTH_LONG).show();
						
						
					} else {
						ndefTag.writeNdefMessage(ndefWriteMsg);

						Toast.makeText(this, "Tag is written!",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(this, "Tag is not writable ... aborting !",
							Toast.LENGTH_LONG).show();
				}
				ndefTag.close();

			} catch (IOException e) {
				Toast.makeText(this, "Tag could not be written (IO-ERROR)!",
						Toast.LENGTH_LONG).show();
			} catch (FormatException e) {
				e.printStackTrace();
			}
			ndefWriteMsg = null;
			
		} else {
			// read tag in service
			/*Context context = getApplicationContext();
			Intent nfcIntent = new Intent(getApplicationContext(),
					NfcReadIntentService.class);

			nfcIntent.putExtra(NfcReadIntentService.NFC_INTENT, intent);
			context.startService(nfcIntent);*/
		}

	}
}
