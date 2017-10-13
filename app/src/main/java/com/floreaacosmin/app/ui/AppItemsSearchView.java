package com.floreaacosmin.app.ui;

import org.apache.commons.lang3.StringUtils;

import android.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.floreaacosmin.app.fragment.AppItemsView;
import com.floreaacosmin.app.utils.AppInputUtils;
import com.floreaacosmin.notifier.R;

public class AppItemsSearchView {

	private EditText searchView;
    private CheckBox searchIcon;
	private String cursorFilter;

    public AppItemsSearchView(Fragment fragment){
        setupSearchView(fragment);
    }

	private void setupSearchView(Fragment fragment) {
		// Instantiate objects
		LinearLayout actionBarView = (LinearLayout) fragment.getActivity().getActionBar().getCustomView();
		searchView = actionBarView.findViewById(R.id.actionbar_view_search_view);
		searchIcon = actionBarView.findViewById(R.id.actionbar_view_search_button);
		
		// Set SearchIcon OnCheckedChange Listener
		searchIcon.setOnCheckedChangeListener(new ButtonOnCheckedChangeListener(fragment));
		
		if (cursorFilter != null) {
			searchView.setText(cursorFilter);
		}
		// Set SearchView TextChanged Listener
		searchView.addTextChangedListener(new TextChangedListener(fragment));
	}
	
	// Hide or Show the custom Search View
	public void hideSearchView(boolean shouldDissapear) {
		if (searchIcon != null) {
			searchIcon.setVisibility(shouldDissapear ? View.INVISIBLE : View.VISIBLE);
			// Close the Search View when the Navigation Drawer is opened
			if (shouldDissapear) {
				searchIcon.setChecked(false);
			}	
		}
	}

    public CheckBox getSearchIcon() {
        return searchIcon;
    }
	
	// Private methods and classes below 
	
	private class ButtonOnCheckedChangeListener implements OnCheckedChangeListener {

		private final Fragment fragment;

		public ButtonOnCheckedChangeListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// Make the SearchView visible depending on its state
			searchView.setVisibility(searchView.isShown() ? View.INVISIBLE : View.VISIBLE);

			// Clear the search view text and dismiss the keyboard if the search view is dismissed
			if (searchView.getVisibility() == View.INVISIBLE) {
				searchView.setText(null);
				((AppItemsView) fragment).setSearchFilter(null);
				// After the cursor filter has been set to null, the loader must be restarted in order to load all the data
				((AppItemsView) fragment).restartLoader();
				AppInputUtils.getInstance().dismissKeyboard(fragment.getActivity());
			}

		}
	}
	
	/* Called when the action bar search text has changed. Update the search filter, and restart the 
	 * loader to do a new query with this filter. */	
	private class TextChangedListener implements TextWatcher {

		final Fragment fragment;

		public TextChangedListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTextChanged(CharSequence newText, int start, int before, int count) {
			cursorFilter = (!TextUtils.isEmpty(newText.toString()) ? newText.toString() : null);
			((AppItemsView) fragment).setSearchFilter(cursorFilter);
			// Restart the loader only if there was a text entered in the Search View
			if (StringUtils.isNotBlank(cursorFilter)) {
				((AppItemsView) fragment).restartLoader();
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	}
}