package com.example.nfcbluetoothpairing;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothAcceptThread extends Thread{
	
	private static final String NAME = "nfcBluetoothPairing";
	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private final BluetoothServerSocket btServersocket;

	
	public BluetoothAcceptThread(BluetoothAdapter btAdapter, UUID randomUUID) {
		Log.d(NFC_BLUETOOTH_PAIRING, "BluetoothAccpetThread:ctor()");
		BluetoothServerSocket tmp = null;
		try {
			tmp = btAdapter.listenUsingRfcommWithServiceRecord(NAME, randomUUID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		btServersocket = tmp;
	}
	
	public void run() {
		Log.d(NFC_BLUETOOTH_PAIRING, "BluetoothAccpetThread:run()");
		while(true){
			BluetoothSocket socket = null;		
			try {
				socket = btServersocket.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(socket != null){
				Log.d("bluetoothacceptthread", "got a socket");
				BluetoothActiveSocket activeSocketThread = new BluetoothActiveSocket(socket);
				activeSocketThread.start();
				MessageService msgService = MessageService.getinstance();
				msgService.addActiveSocketThread(activeSocketThread);
			}
		}
	}
}
