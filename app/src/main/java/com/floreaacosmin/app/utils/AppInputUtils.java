package com.floreaacosmin.app.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class AppInputUtils {

	private static AppInputUtils instance;

	private AppInputUtils(){}

	public static synchronized AppInputUtils getInstance() {
		if (instance == null) {
			instance = new AppInputUtils();
		}
		return instance;
	}

	private InputMethodManager inputMethodManager;
	
	// Hide the keyboard if it is active
	public void dismissKeyboard(Activity a) {
		if (inputMethodManager == null) {
			inputMethodManager = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		// Check if the input method is opened and if there is a view focused
		if (inputMethodManager.isActive() && a.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(), 0);
		}
	}
}