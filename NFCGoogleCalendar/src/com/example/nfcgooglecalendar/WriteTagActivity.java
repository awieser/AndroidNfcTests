package com.example.nfcgooglecalendar;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class WriteTagActivity extends Activity {
	private static final String LOG_WRITE_TAG = "WRITE_TAG";
	private Spinner spinnerCalendars;
	private NfcAdapter mNfcAdapter;
	private PendingIntent pendingIntent;
	private IntentFilter[] intentFiltersArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_tag);

		spinnerCalendars = (Spinner) findViewById(R.id.spinnerCalendars);
		List<CalendarEntry> items = CalendarAdapter
				.getCalendars(getContentResolver());
		ArrayAdapter<CalendarEntry> adapter = new ArrayAdapter<CalendarEntry>(
				this, android.R.layout.simple_spinner_dropdown_item, items);

		spinnerCalendars.setAdapter(adapter);

		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		intentFiltersArray = new IntentFilter[] { new IntentFilter(
				NfcAdapter.ACTION_TAG_DISCOVERED) };

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}
	}

	protected void onNewIntent(Intent intent) {
		processIntent(intent);
	}

	@Override
	protected void onResume() {
		mNfcAdapter.enableForegroundDispatch(this, pendingIntent,
				intentFiltersArray, null);
		super.onResume();
	}

	@Override
	protected void onPause() {
		mNfcAdapter.disableForegroundDispatch(this);
		super.onPause();
	}

	private void processIntent(Intent intent) {
		Log.d(LOG_WRITE_TAG, "new intent: " + intent.getAction());

		if (spinnerCalendars.getSelectedItemPosition() != Spinner.INVALID_POSITION
				&& (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction()))) {
			Log.d(LOG_WRITE_TAG, " trying to write tag");

			CalendarEntry cal = (CalendarEntry) spinnerCalendars
					.getSelectedItem();

			String ownerName = cal.getOwnerName();

			NdefRecord mimeRecord = NdefRecord.createMime(
					"application/vnd.nfcgooglecalendar",
					ownerName.getBytes(Charset.forName("US-ASCII")));

			NdefMessage ndefWriteMsg = new NdefMessage(
					new NdefRecord[] { mimeRecord });

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			Ndef ndefTag = Ndef.get(tag);

			if (ndefTag == null) {
				Log.d(LOG_WRITE_TAG, "could not get ndef tag");
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
		}
	}

}
