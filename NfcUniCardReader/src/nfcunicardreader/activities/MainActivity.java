package nfcunicardreader.activities;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Comparator;

import nfcunicardreader.model.UniCard;
import com.example.testandroidproject.R;
import communication.nfc.NfcHandler;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG_NFC_TEST = "nfc-test";
	private NfcAdapter mNfcAdapter;
	private PendingIntent mNfcPendingIntent;
	private IntentFilter[] mNdefExchangeFilters;
	private String[][] techListsArray;
	private ListView listView;

	private ArrayAdapter<UniCard> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG_NFC_TEST, "onCreate()");

		setContentView(R.layout.activity_main);

		checkAvailableNFCAdapter();

		// Handle all of our received NFC intents in this activity.
		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Intent filters for reading a note from a tag or exchanging over p2p.
		/*
		 * IntentFilter ndefDetected = new IntentFilter(
		 * NfcAdapter.ACTION_NDEF_DISCOVERED); try {
		 * ndefDetected.addDataType("text/plain"); } catch
		 * (MalformedMimeTypeException e) { }
		 */
		IntentFilter techDiscoveredIntent = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);
		// techDiscoveredIntent.getDataScheme(R.xml.nfc_tech_filter);

		// mNdefExchangeFilters = new IntentFilter[] { ndefDetected,
		// techDiscoveredIntent };

		mNdefExchangeFilters = new IntentFilter[] { techDiscoveredIntent };

		this.techListsArray = new String[][] { new String[] {
				MifareClassic.class.getName(), NdefFormatable.class.getName(),
				NfcA.class.getName() } };

		adapter = new ArrayAdapter<UniCard>(this,
				android.R.layout.simple_list_item_1);
		
		adapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				TextView cardCount = (TextView) findViewById(R.id.textViewCardCountNumber);
				cardCount.setText("" + adapter.getCount());
				super.onChanged();
				Log.d(TAG_NFC_TEST, "change on adapter count: " + adapter.getCount());
			}
		});
		

		listView = (ListView) findViewById(R.id.listView1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				final UniCard uc = (UniCard) listView.getItemAtPosition(position);
				Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setTitle(uc.getMarticulationNumber());
				builder.setMessage(uc.toStringNewLines() + "\n\nNotes:");
				final EditText input = new EditText(MainActivity.this);
				input.setText(uc.getNotes());
				builder.setView(input);
				builder.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								uc.setNotes(input.getText().toString());
							}
						});

				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
							};

						});
				builder.create().show();
			}
		});
		this.registerForContextMenu(listView);
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();

		View empty = findViewById(R.id.empty);
		ListView list = (ListView) findViewById(R.id.listView1);
		list.setEmptyView(empty);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
		Log.d(TAG_NFC_TEST, "onPause()");
	}

	@Override
	protected void onResume() {

		super.onResume();
		Log.d(TAG_NFC_TEST, "onResume()");

		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, techListsArray);
		Log.d(TAG_NFC_TEST, "onResumeFinished()");

		processIntent(getIntent());
	}

	private void checkAvailableNFCAdapter() {
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {

			new AlertDialog.Builder(this)
					.setTitle("NFC not available")
					.setMessage(
							"NFC is either not available or enabled on your Phone")
					.show();

			Log.d(TAG_NFC_TEST, "NFC is not available");
			finish();
			return;
		} else {
			Log.d(TAG_NFC_TEST, "NFC is available");
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case R.id.context_delete:
			adapter.remove(adapter.getItem(info.position));

			break;

		default:
			break;
		}

		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_delete_all:
			adapter.clear();
			break;

		case R.id.action_email:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");

			StringWriter sw = new StringWriter();
			for (int i = 0; i < adapter.getCount(); i++) {
				sw.write(adapter.getItem(i).toCsv());
				sw.write("\n");
			}

			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
					sw.toString());
			startActivity(emailIntent);
			break;
		case R.id.action_sort:
			adapter.sort(new Comparator<UniCard>() {
				@Override
				public int compare(UniCard arg0, UniCard arg1) {
					return arg0.compareTo(arg1);
				}
			});
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onNewIntent(Intent intent) {
		setIntent(intent);
	}

	protected void processIntent(Intent intent) {

		Log.d(TAG_NFC_TEST, "new Intent" + intent.getAction());

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			String read_data;

			try {
				read_data = NfcHandler.readMifareClassic(intent, 1);
				UniCard uniCard = new UniCard(read_data);
				adapter.insert(uniCard, 0);

				Uri notification = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(
						getApplicationContext(), notification);
				r.play();

				Builder dialogBuilder = new AlertDialog.Builder(this).setTitle(
						"Read OK").setMessage(uniCard.toStringNewLines());

				AlertDialog dialog = dialogBuilder.create();
				showDialogWithTimeout(dialog, 2000);

			} catch (IOException e) {
				Toast.makeText(this, "Error reading Tag", Toast.LENGTH_SHORT)
						.show();

				Builder dialogBuilder = new AlertDialog.Builder(this).setTitle(
						"Reading Failed").setIcon(R.drawable.ic_launcher);

				AlertDialog dialog = dialogBuilder.create();
				showDialogWithTimeout(dialog, 2000);
			}

		}
	}

	public void showDialogWithTimeout(final Dialog d, int time) {
		d.show();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				d.dismiss();
			}
		}, time);
	}
}
