package com.floreaacosmin.app.data_processor;

import com.floreaacosmin.app.activity.AppBaseActivity;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;

// AsyncQueryHandler is used for asynchronous database operations above the ContentResolver
public class AppAsyncQueryHandler extends AsyncQueryHandler {
	
	public AppAsyncQueryHandler(Context context) {
		super(context.getContentResolver());

		this.context = context;
	}

	private final Context context;

	public static final int ITEM_DETAIL_QUERY = 100;
	
	@Override
	protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
		super.onQueryComplete(token, cookie, cursor);
		
		switch (token) {
			case ITEM_DETAIL_QUERY:
				/* Get the first position in the cursor, otherwise an error 
				 * is thrown because the default index is at "-1" position. */ 
				if (cursor != null) {
					cursor.moveToFirst();
					// Fill the UI with the data from the Content Provider
					((AppBaseActivity) context).getAppItemDetailViewHelper().fillData(cursor);
					// Close the cursor in order to release resources
					cursor.close();
				}			
				break;
				
			default:
				break;
		}		
	}	
}