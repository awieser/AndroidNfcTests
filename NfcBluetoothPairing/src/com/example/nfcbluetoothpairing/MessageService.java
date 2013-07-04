package com.example.nfcbluetoothpairing;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class MessageService {

	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private static MessageService instance = null;
	private List<BluetoothActiveSocket> listeningThreads;
	private ArrayAdapter<String> lobbyAdapter;
	private Handler handler;

	private MessageService() {
		Log.d(NFC_BLUETOOTH_PAIRING, "MessageService:ctor()");
		listeningThreads = new ArrayList<BluetoothActiveSocket>();

		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
//				Log.d(NFC_BLUETOOTH_PAIRING, "got message: " + (String) msg.obj);

				switch (msg.what) {
				case 0: // TODO
					if(lobbyAdapter != null){
						lobbyAdapter.add((String) msg.obj);
					}
					break;
				}
			}
		};
	}

	public static MessageService getinstance() {

		if (instance == null) {
			synchronized (MessageService.class) {
				if (instance == null) {
					instance = new MessageService();
				}
			}
		}
		return instance;
	}

	public synchronized void addActiveSocketThread(
			BluetoothActiveSocket activeSocketThread) {
		Log.d(NFC_BLUETOOTH_PAIRING, "MessageService: added a thread");
		listeningThreads.add(activeSocketThread);
	}

	public void unregisterSocket(BluetoothActiveSocket bluetoothActiveSocket) {
		listeningThreads.remove(bluetoothActiveSocket);
	}

	public synchronized void sendLobbyMessage(String msg) {
		handler.obtainMessage(0, "me:" + msg).sendToTarget();
		for (BluetoothActiveSocket socket : listeningThreads) {
			socket.sendMessage(msg);
			Log.d(NFC_BLUETOOTH_PAIRING, "MessageService: send:" + msg);
		}
	}

	public ListAdapter getLobbyAdapter(Context context) {
		if (lobbyAdapter == null) {
			lobbyAdapter = new ArrayAdapter<String>(context, R.layout.lobbymessage);
			
		}
		return lobbyAdapter;
	}

	public Handler getHandler() {
		return handler;
	}

	public void addActiveSocketThreadServer(
			BluetoothActiveSocket activeSocketThread) {
		for (BluetoothActiveSocket socket : listeningThreads) {
			socket.close();
			listeningThreads.remove(socket);
		}
		addActiveSocketThread(activeSocketThread);
		
		
	}

}
