package com.example.nfcbluetoothpairing;


import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends Activity {

	private static final String NFC_BLUETOOTH_PAIRING = "nfc-bluetooth-pairing";
	private ListView listViewChat;
	private EditText editTextChatMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		listViewChat = (ListView) findViewById(R.id.listchat);
		listViewChat.setAdapter(MessageService.getinstance().getLobbyAdapter(getApplicationContext()));
		editTextChatMessage = (EditText) findViewById(R.id.editTextChatMessage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendChatMessage(View view){
		Log.d(NFC_BLUETOOTH_PAIRING, "sendChatMessage()");
		MessageService.getinstance().sendLobbyMessage(editTextChatMessage.getText().toString());
	}
}
