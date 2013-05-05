package com.example.nfcbluetoothpairing;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;

import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity implements CreateNdefMessageCallback {

	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private NfcAdapter mNfcAdapter;
	private TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(NFC_BLUETOOTH_PAIRING, "onNewIntent: " + intent.getAction());
		 setIntent(intent);
	}

	@Override
	public void onResume() {
		Log.d(NFC_BLUETOOTH_PAIRING, "onResume()" + getIntent().getAction());
		super.onResume();
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	};

	private void processIntent(Intent intent) {
		Log.d(NFC_BLUETOOTH_PAIRING, "processintent: " + intent.getAction());

		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];

		textView.setText(new String(msg.getRecords()[0].getPayload()));

	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		return new NdefMessage(new NdefRecord[] {NdefRecord.createMime("application/vnd.nfcbluetooth", "hallo".getBytes())});
	}

	

}
