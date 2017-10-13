package com.floreaacosmin.app.data_processor;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/* The calling activity does not wait for the result so in order to receive the service 
 * result this class is used. An interface that the calling activity has to implement is 
 * defined in order to get notified when the data is available. In this interface it is 
 * defined a Bundle that holds the data retrieved.*/
public class AppDataResultInterface extends ResultReceiver {

	private static AppDataResultInterface instance;

	private AppDataResultInterface(Handler handler) {
		super(handler);
	}

	public static synchronized AppDataResultInterface getInstance() {
		if (instance == null) {
			instance = new AppDataResultInterface(new Handler());
		}
		return instance;
	}

	private Receiver receiver;

	public interface Receiver {
        void onReceiveResult(int resultCode, Bundle resultData);
    }	

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}
	
	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (receiver != null) {
			receiver.onReceiveResult(resultCode, resultData);
		}
	}
}