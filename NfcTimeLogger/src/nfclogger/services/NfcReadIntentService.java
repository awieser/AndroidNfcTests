package nfclogger.services;
import nfclogger.db.TimeloggerDatabaseOpenHelper;

import com.example.nfctimelogger.R;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;

public class NfcReadIntentService extends IntentService {
	private static final String NFC_TIMELOGGER = "nfc-timelogger";
	public static String NFC_INTENT = "NFC_INTENT";

	public NfcReadIntentService() {
		super("NfcReadIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		intent = (Intent) intent.getExtras().get(NFC_INTENT);

		TimeloggerDatabaseOpenHelper databaseOpenHelper = new TimeloggerDatabaseOpenHelper(getApplicationContext());

		Log.d(NFC_TIMELOGGER,
				"handling following intent in service: " + intent.getAction());

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			
			Parcelable[] ndefMessages = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			
			if (ndefMessages != null) {
				NdefMessage[] msgs = new NdefMessage[ndefMessages.length];
				for (int i = 0; i < ndefMessages.length; i++) {
					msgs[i] = (NdefMessage) ndefMessages[i];

					NdefRecord[] records = msgs[i].getRecords();
					for (NdefRecord ndefRecord : records) {
						String payload = new String(ndefRecord.getPayload());
						Log.d(NFC_TIMELOGGER, payload);
						NotificationManager notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
						
						
						String resultAction = databaseOpenHelper.handleCategory(payload);
						
						Notification notification = new Notification.Builder(
								this).setContentTitle("Timelogger")
								.setTicker(resultAction + payload)
								.setContentText(payload)
								.setSmallIcon(R.drawable.ic_launcher).build();
						notifManager.notify(1234, notification);
						Log.d(NFC_TIMELOGGER, "notification sent");
						

						Intent updateIntent = new Intent("FRAGMENT_UPDATE");
						this.sendBroadcast(updateIntent);
					}
				}
			}
		}

		databaseOpenHelper.close();
	}
}
