package com.floreaacosmin.app.ui;

import com.floreaacosmin.notifier.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppToastMessageView {

	private final Context context;
	private Toast toastMessage;
	private final String textMessage;
	private final int toastGravity;
	private final int toastDuration;

	public AppToastMessageView(Context context, String mesage, int toastGravity) {
		this.context = context;
		this.textMessage = mesage;
		this.toastGravity = toastGravity;
		this.toastDuration = Toast.LENGTH_SHORT;
		
		setupMessageView();		
	}

	public AppToastMessageView(Context context, String mesage, int toastGravity, int toastDuration) {
		this.context = context;
		this.textMessage = mesage;
		this.toastGravity = toastGravity;
		this.toastDuration = toastDuration;

		setupMessageView();
	}

	private void setupMessageView() {
		// Get application context
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		// Inflate the Toast view
		View toastMessageLayout = layoutInflater.inflate(R.layout.app_toast_message_layout, new FrameLayout(context));
		
		// Instantiate the Text View and set the custom text to it
		TextView textView = toastMessageLayout.findViewById(R.id.app_toast_message_layout_text_view);
		textView.setText(textMessage);
		
		// Instantiate the Toast
		toastMessage = new Toast(context);
		// Set Toast parameters
		toastMessage.setGravity(toastGravity, 0, 0);
		toastMessage.setDuration(toastDuration);
		toastMessage.setView(toastMessageLayout);
	}
	
	public void showMessage() {
		// Show the toastMessage only if the text is not null
		if (textMessage != null) {
			toastMessage.show();	
		}
	}
	
	public void cancelMessage() {
		if (toastMessage != null) {
			toastMessage.cancel();
		}
	}
}
