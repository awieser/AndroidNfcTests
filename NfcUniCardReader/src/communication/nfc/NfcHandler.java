package communication.nfc;

import java.io.IOException;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.util.Log;

public class NfcHandler {
	private static final String TAG_NFC_TEST = "nfc-test";

	
	public static String readMifareClassic(Intent intent, int sector) throws  IOException {
		int sectorlist[] = new int[1];
		sectorlist[0] = sector;		
		return readMifareClassic(intent,sectorlist);
	}
	
	public static String readMifareClassic(Intent intent) throws  IOException {
		
		return readMifareClassic(intent,null);
	}

	public static String readMifareClassic(Intent intent, int[] sectorlist) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();

		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		MifareClassic mfc = MifareClassic.get(tag);

		mfc.connect();
		boolean auth = false;
		
		if(sectorlist == null){
			sectorlist = new int[mfc.getSectorCount()];
			for (int i = 0; i < sectorlist.length; i++) {
				sectorlist[i] = i;
			}
		} 

		for (int sectorCounter : sectorlist) {
			Log.d(TAG_NFC_TEST, "Sector " + sectorCounter);

			auth = mfc.authenticateSectorWithKeyA(sectorCounter,
					MifareClassic.KEY_DEFAULT);

			if (auth) {
				int blockStart = mfc.sectorToBlock(sectorCounter);
				int blockEnd = blockStart
						+ mfc.getBlockCountInSector(sectorCounter);

				for (int blockCounter = blockStart; blockCounter < blockEnd; blockCounter++) {
					byte[] data = mfc.readBlock(blockCounter);
					stringBuilder.append(new String(data));
				}
			} 
		}
		mfc.close();
		return stringBuilder.toString();
	}

}
