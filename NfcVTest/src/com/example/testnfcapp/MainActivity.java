package com.example.testnfcapp;

import java.io.IOException;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String NFC_TAG = "nfc-test";
	private IntentFilter[] mNdefExchangeFilters;
	private String[][] techListsArray;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mNfcPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		setContentView(R.layout.activity_main);

		IntentFilter techDiscoveredIntent = new IntentFilter(
				NfcAdapter.ACTION_TECH_DISCOVERED);

		mNdefExchangeFilters = new IntentFilter[] { techDiscoveredIntent };
		this.techListsArray = new String[][] { new String[] { NfcV.class
				.getName() } };
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {

		super.onResume();

		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, techListsArray);

		processIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		this.setIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
		Log.d(NFC_TAG, "onPause()");
	}

	private void processIntent(Intent intent) {
		Log.d(NFC_TAG, "got intent" + intent.getAction());

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			Log.d(NFC_TAG, "action_tech_discovered");

			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			NfcV nfcvTag = NfcV.get(tag);

			if (nfcvTag != null) {
				try {
					nfcvTag.connect();
					byte[] buffer;
					
					/*
					 * 0x00 header
					 * 0x20 <read>
					 * 0x06 <block 6>
					 */
					buffer = nfcvTag
							.transceive(new byte[] { 0x00, 0x20, 0x06 });

					String dataRead = new String(buffer);
					
					TextView textViewData = (TextView) findViewById(R.id.textView2);
					textViewData.setText(dataRead);
					
					Log.d(NFC_TAG, new String(buffer));
					Log.d(NFC_TAG, "write");

					/*
					 * 0x00 header
					 * 0x20 <write>
					 * 0x06 <block 6>
					 * 0x61 - a
					 * 0x62 - b
					 * 0x63 - c
					 * 0x64 - d
					 */
					buffer = nfcvTag.transceive(new byte[] { 0x00, 0x21, 0x06,
							0x61, 0x62, 0x63, 0x64 });

					Log.d(NFC_TAG, new String(buffer));

				} catch (IOException e) {
					e.printStackTrace();
				} 
			}
		}
	}


}
