package com.example.nfcbluetoothpairing;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class StartActivity extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private NfcAdapter mNfcAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothAcceptThread btAcceptThread;
	private UUID randomUUID;
	private BluetoothConnectThread btConnectThread;
	private static final int REQUEST_ENABLE_BT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start);

		// / NFC

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

		if (!mNfcAdapter.isEnabled()) {
			// TODO
		}

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
			this.startActivityForResult(new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
		}
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		Log.d(NFC_BLUETOOTH_PAIRING, "onNdefPushComplete()");
		btAcceptThread = new BluetoothAcceptThread(mBluetoothAdapter,
				randomUUID);
		btAcceptThread.start();
		startLobby();
	}

	private void startLobby() {
		Intent activityIntent = new Intent(this, MainActivity.class);
//		activityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(activityIntent);
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(NFC_BLUETOOTH_PAIRING, "createNdefMessage()");

		randomUUID = UUID.randomUUID();

		byte[] msg = (mBluetoothAdapter.getAddress() + "\n" + randomUUID
				.toString()).getBytes();

		return new NdefMessage(new NdefRecord[] { NdefRecord.createMime(
				"application/vnd.nfcbluetooth", msg) });
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth enabled
			} else {
				// TODO: notify bluetooth not enabled
				finish();
			}

			break;
		}
	}

	@Override
	public void onResume() {
		Log.d(NFC_BLUETOOTH_PAIRING, "onResume()" + getIntent().getAction());
		super.onResume();

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(NFC_BLUETOOTH_PAIRING, "onNewIntent: " + intent.getAction());
		setIntent(intent);
	}

	private void processIntent(Intent intent) {
		Log.d(NFC_BLUETOOTH_PAIRING, "processintent: " + intent.getAction());

		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawMsgs[0];

		String msgString = new String(msg.getRecords()[0].getPayload());

		// textView.setText(msgString);

		String[] x = msgString.split("\n");
		String address = x[0];
		String stringUUID = x[1];

		Log.d(NFC_BLUETOOTH_PAIRING, "connecting to address: " + address
				+ ", uuid= " + stringUUID);
		UUID connectUUID = UUID.fromString(stringUUID);

		if (btConnectThread != null) {
			// TODO stop thread
		}
		btConnectThread = new BluetoothConnectThread(mBluetoothAdapter,
				address, connectUUID);
		btConnectThread.start();
		startLobby();
	}

}
