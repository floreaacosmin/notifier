package com.floreaacosmin.app.drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.floreaacosmin.notifier.R;

// Custom adapter class for the ListView used to display the items in the Drawer layout
class AppDrawerListAdapter extends BaseAdapter {

	private final Activity activity;
	private final ArrayList<AppDrawerItem> appDrawerItemsArray;
	private int selectedItem;

	public void setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	public AppDrawerListAdapter(Activity a, ArrayList<AppDrawerItem> appDrawerItemsArray) {
		this.activity = a;
		this.appDrawerItemsArray = appDrawerItemsArray;
	}

	@Override
	public int getCount() {
		return appDrawerItemsArray.size();
	}

	@Override
	public Object getItem(int position) {
		return appDrawerItemsArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return appDrawerItemsArray.indexOf(getItem(position));
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Instantiate LayoutInflater
		LayoutInflater layoutInflater = activity.getLayoutInflater();

		ViewHolder viewHolder;
		/* If convertView is not null, it can be reused directly, no inflation required. 
		 * Inflate a new View only when the convertView is null */
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.drawer_row_layout, parent, false);
			/* Create a ViewHolder and store references to the children views, avoid 
			 * unnecessary calls to findViewById() on each row, which is expensive */
			viewHolder = new ViewHolder();
			viewHolder.drawerItemIcon = convertView.findViewById(R.id.drawer_row_layout_item_icon);
			viewHolder.drawerItemTitle = convertView.findViewById(R.id.drawer_row_layout_item_title);
			viewHolder.drawerCircleSelect = convertView.findViewById(R.id.drawer_row_layout_item_circle_select);
			viewHolder.drawerDetailIcon = convertView.findViewById(R.id.drawer_row_layout_detail_icon);
			viewHolder.drawerInsideRow = convertView.findViewById(R.id.drawer_row_inside_layout);
			convertView.setTag(viewHolder);
		} else {
			// Get the ViewHolder back to get fast access to the views
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (position == selectedItem) {
			viewHolder.drawerItemTitle.setTextColor(activity.getResources().getColor(R.color.black));
			viewHolder.drawerCircleSelect.setBackgroundDrawable
				((activity.getResources().getDrawable(R.drawable.cell_shape_circle_selected)));
			viewHolder.drawerInsideRow.setBackgroundDrawable
				((activity.getResources().getDrawable(R.drawable.cell_shape_default_round_white_blue_margin_subtle)));		
		} else {
			viewHolder.drawerItemTitle.setTextColor(activity.getResources().getColor(R.color.darker_grey));
			viewHolder.drawerCircleSelect.setBackgroundDrawable
				((activity.getResources().getDrawable(R.drawable.cell_shape_circle_background)));
			viewHolder.drawerInsideRow.setBackgroundDrawable
				((activity.getResources().getDrawable(R.drawable.drawer_row_background)));
		}
        
		// Bind the data
		viewHolder.drawerItemIcon.setImageResource(appDrawerItemsArray.get(position).getIcon());        
        viewHolder.drawerItemTitle.setText(appDrawerItemsArray.get(position).getName());

        // Display the detail icon only when it is the case
        viewHolder.drawerDetailIcon.setBackgroundDrawable
                ((activity.getResources().getDrawable(appDrawerItemsArray.get(position).getSecondIcon())));

        return convertView;
	}

	// Hold set of views, without the need of looking them up repeatedly
	private class ViewHolder {	
		private TextView drawerItemTitle;
		private ImageView drawerItemIcon;
		private View drawerCircleSelect;
		private ImageView drawerDetailIcon;
		private RelativeLayout drawerInsideRow;
	}		
}