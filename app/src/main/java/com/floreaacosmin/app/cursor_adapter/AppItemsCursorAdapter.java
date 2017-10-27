package com.floreaacosmin.app.cursor_adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.floreaacosmin.app.application.AppBaseApplication;
import com.floreaacosmin.app.content_provider.AppProviderCategoriesContract;
import com.floreaacosmin.app.toolbox.LogUtils;
import com.floreaacosmin.app.database.AppDBTableColumns;
import com.floreaacosmin.notifier.R;

@SuppressWarnings("deprecation")
public class AppItemsCursorAdapter extends CursorAdapter{
	
	private final String LOG_TAG = LogUtils.makeLogTag(AppItemsCursorAdapter.class);
	
	private final Context context;
	private final LayoutInflater layoutInflater;
	private int rowLayoutType;
	private int rowLayout;
	private ViewHolder viewHolder;
	private Drawable rowBackground;
	private int lastPosition = -1;
	private final int rowLayoutTypeTop = 0;
	private final int rowLayoutTypeMiddle = 1;
	private final int rowLayoutTypeBottom = 2;

	private int NOTIFICATION_INTERNAL_ID_INDEX;
	private int NOTIFICATION_NAME_INDEX;
	private int NOTIFICATION_CONTENT_INDEX;
	private int NOTIFICATION_DATE_INDEX;
	private int NOTIFICATION_AUTHOR_INDEX;
	private int NOTIFICATION_IMAGEURL_INDEX;
	private int NOTIFICATION_READ_INDEX;

	public AppItemsCursorAdapter(Context c) {
		super(c, null, false);
		
		// Save received context in a local variable 		
		this.context = c;
		// Get the LayoutInflater object
		layoutInflater = LayoutInflater.from(context);

        rowBackground = context.getResources().getDrawable(R.drawable.cell_shape_default_top);
	}
	
	@Override
	public Cursor swapCursor(Cursor newCursor) {
	
		LogUtils.LOGD(LOG_TAG, "The cursor was swapped");
		
		if (newCursor != null) {
			NOTIFICATION_INTERNAL_ID_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_INTERNAL_ID);
			NOTIFICATION_NAME_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_NAME);
			NOTIFICATION_CONTENT_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_CONTENT);
			NOTIFICATION_DATE_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_DATE);
			NOTIFICATION_AUTHOR_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_AUTHOR);
			NOTIFICATION_IMAGEURL_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_IMAGEURL);
			NOTIFICATION_READ_INDEX = newCursor.getColumnIndex(AppDBTableColumns.NOTIFICATION_READ);
		}
		
		return super.swapCursor(newCursor);
	}

	/* Called when the ListView requires a new view to display. The CursorAdapter will take 
	 * care of recycling views (unlike the GetView method on regular Adapters). */	
	@SuppressWarnings("deprecation")
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		
		/* When the view will be created for first time, it will be decided 
		 * what layout will be used by the adapter */
        View rowView = layoutInflater.inflate(getRowLayoutResource(getItemViewType(cursor.getPosition())), parent, false);

        RelativeLayout rowLayoutCore = rowView.findViewById(R.id.items_row_layout_root);
		rowLayoutCore.setBackgroundDrawable(getRowLayoutBackground(getItemViewType(cursor.getPosition())));
		
		// Instantiate the ViewHolder
		viewHolder = new ViewHolder();
		// Store the references to the children views
		viewHolder.itemName = rowView.findViewById(R.id.items_row_layout_item_name);
		viewHolder.itemContent = rowView.findViewById(R.id.items_row_layout_item_content);
		viewHolder.itemAuthor = rowView.findViewById(R.id.items_row_layout_item_author);
		viewHolder.itemDate = rowView.findViewById(R.id.items_row_layout_item_date);
		viewHolder.itemImage = rowView.findViewById(R.id.author_row_layout_image);
		viewHolder.itemRead = rowView.findViewById(R.id.items_row_layout_item_read_sign);
		
		// Store data within the view
		rowView.setTag(viewHolder);
		
		return rowView;
	}
	
	// Given a view, update it to display the data provided by the cursor
	@SuppressWarnings("deprecation")
	@Override
	public void bindView(View view, Context context, final Cursor cursor) {
		// Get the ViewHolder back to get fast access to the views
		viewHolder = (ViewHolder) view.getTag();
		
		// The data is taken from the cursor and put it in the views
		viewHolder.itemName.setText(cursor.getString(NOTIFICATION_NAME_INDEX));
		viewHolder.itemAuthor.setText(cursor.getString(NOTIFICATION_AUTHOR_INDEX));
		
		// Set the default background resource every time in order to force the refresh of the Image View
		viewHolder.itemImage.setBackgroundResource(R.drawable.author_background);
		viewHolder.itemImage.setImageUrl(cursor.getString(NOTIFICATION_IMAGEURL_INDEX),
			((AppBaseApplication) context.getApplicationContext()).getImageLoader());
		
		AppAdapterHelper.getInstance().loadArticleSummaryText
			(viewHolder.itemContent,	cursor.getString(NOTIFICATION_CONTENT_INDEX));

		viewHolder.itemDate.setText(cursor.getString(NOTIFICATION_DATE_INDEX));

		int columnArticleInternalIdValue = cursor.getInt(NOTIFICATION_INTERNAL_ID_INDEX);
		viewHolder.itemName.setTag(columnArticleInternalIdValue);
		
		// Get the read value from the cursor to know if the article was opened before or not.
		int columnArticleReadValue = cursor.getInt(NOTIFICATION_READ_INDEX);
		// Set the read sign accordingly
		if (columnArticleReadValue == AppProviderCategoriesContract.UNREAD_VALUE) {
			viewHolder.itemRead.setBackgroundDrawable
				((context.getResources().getDrawable(R.drawable.item_unread_sign)));			
		} else {
			viewHolder.itemRead.setBackgroundDrawable
				((context.getResources().getDrawable(R.drawable.item_read_sign)));			
		}
		
		// Get cursor position
        int position = cursor.getPosition();
		
		// Instantiate and assign Animation Object
        Animation listViewRowAnimation = AnimationUtils.loadAnimation(context, (position > lastPosition) ?
			R.anim.up_from_bottom_animation : R.anim.down_from_top_animation);
		
		// Start the cell animation
		view.startAnimation(listViewRowAnimation);
		
		// Update the current position
		lastPosition = position;
	}
	
	// Define the way to determine which layout to use
	@Override
	public int getItemViewType(int position) {
		// Increment by 1 because the first position returned by the cursor is 0
		int logicPosition = position + 1;
		if (logicPosition % 3 == 1) {
			rowLayoutType = rowLayoutTypeTop;
		} else if (logicPosition % 3 == 2) {
			rowLayoutType = rowLayoutTypeMiddle;
		} else if (logicPosition % 3 == 0) {
			rowLayoutType = rowLayoutTypeBottom;
		}
		return rowLayoutType;
	}

	// Return the number of different layouts	
	@Override
	public int getViewTypeCount() {
		return 3;
	}

	/* Stores resource IDs in a in a ViewHolder class to prevent having to look 
	 * them up each time bindView() is called. */
	private class ViewHolder {	
		private TextView itemName;
		private TextView itemContent;
		private TextView itemAuthor;
		private TextView itemDate;
		private NetworkImageView itemImage;
		private View itemRead;
	}
	
	private int getRowLayoutResource(int rowLayoutType) {
		switch (rowLayoutType) {
		case rowLayoutTypeTop:
			rowLayout = R.layout.item_row_layout_top;
			break;
		case rowLayoutTypeMiddle:
			rowLayout = R.layout.item_row_layout_middle;
			break;
		case rowLayoutTypeBottom:
			rowLayout = R.layout.item_row_layout_bottom;
			break;	
		default:
			break;
		}
		return rowLayout;
	}

	private Drawable getRowLayoutBackground(int rowLayoutType) {
		switch (rowLayoutType) {
		case rowLayoutTypeTop:
			rowBackground = context.getResources().getDrawable(R.drawable.cell_shape_default_top);
			break;
		case rowLayoutTypeMiddle:
			rowBackground = context.getResources().getDrawable(R.drawable.cell_shape_default_middle);
			break;
		case rowLayoutTypeBottom:
			rowBackground = context.getResources().getDrawable(R.drawable.cell_shape_default_bottom);
			break;	
		default:
			break;
		}
		return rowBackground;
	}
}