package com.example.nfcbluetoothpairing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class BluetoothActiveSocket extends Thread{

	private static final int MESSAGE_READ = 0;
	private BluetoothSocket socket;
	private InputStream in;
	private OutputStream out;

	public BluetoothActiveSocket(BluetoothSocket socket) {
		this.socket = socket;
		
		out = null;
		in = null;
		try {
			in =  socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		while(true){
			byte[] buffer = new byte[1024];
			try {
				in.read(buffer);
				MessageService.getinstance().getHandler().obtainMessage(MESSAGE_READ, new String(buffer)).sendToTarget();
			} catch (IOException e) {
				MessageService.getinstance().unregisterSocket(this);
				break;
			}
		}
	}
	
	
	public void sendMessage(String msg){
		if(out == null){
			// TODO
			return;
		}
			
		try {
			out.write(msg.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {}
			in = null;
			out = null;
		}
	}
}
