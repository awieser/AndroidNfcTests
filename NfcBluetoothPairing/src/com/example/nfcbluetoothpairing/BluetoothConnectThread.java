package com.example.nfcbluetoothpairing;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothConnectThread extends Thread {
	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private BluetoothSocket btSocket;
	private final BluetoothDevice btDevice;
	private BluetoothAdapter btAdapter;

	public BluetoothConnectThread(BluetoothAdapter mBluetoothAdapter,
			String address, UUID connectUUID) {
		Log.d(NFC_BLUETOOTH_PAIRING, "BluetoothConnectThread:ctor()");
		btSocket = null;
		btDevice = mBluetoothAdapter.getRemoteDevice(address);
		btAdapter = mBluetoothAdapter;

		BluetoothSocket tmp = null;
		try {
			tmp = btDevice.createRfcommSocketToServiceRecord(connectUUID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		btSocket = tmp;
	}

	@Override
	public void run() {
		Log.d(NFC_BLUETOOTH_PAIRING, "BluetoothConnectThread:run()");
		btAdapter.cancelDiscovery();

		try {

			btSocket.connect();
			if (btSocket != null) {
				Log.d("bluetooth-connect-thread", "connected");
				BluetoothActiveSocket activeSocketThread = new BluetoothActiveSocket(
						btSocket);
				activeSocketThread.start();
				MessageService.getinstance().addActiveSocketThread(
						activeSocketThread);
			} else {
				Log.d("bluetooth-connect-thread", "could not connect");
			}
		} catch (IOException e) {
			Log.d("bluetooth-connect-thread", "could not connect");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
