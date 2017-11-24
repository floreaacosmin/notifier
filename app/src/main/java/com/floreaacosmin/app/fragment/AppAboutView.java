package com.floreaacosmin.app.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.cunoraz.gifview.library.GifView;
import com.floreaacosmin.notifier.R;

public class AppAboutView extends Fragment {
    
	public AppAboutView(){}
	
	private View rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Must be set to true so invalidateOptionsMenu() can be called
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.app_about_view, container, false);

        return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Enabled in order to save the state across different changed (including screen configuration)
		setRetainInstance(true);

		TextView firstLineTextView;
		NetworkImageView authorPicture;
		TextView authorName;
		TextView articleDate;
		TextView articleName;
		TextView articleDateArticlePreview;
		TextView articleAuthor;
		TextView articleSummary;
		GifView aboutGifView;

		// Instantiate the objects present in the layout which need configured and setup them
		firstLineTextView = rootView.findViewById(R.id.app_item_detail_view_title);
		firstLineTextView.setText(R.string.about_first_line);
		authorPicture = rootView.findViewById(R.id.author_row_layout_image);
		authorPicture.setDefaultImageResId(R.drawable.jerry_maguire);
		authorName = rootView.findViewById(R.id.app_item_detail_view_author_name);
		authorName.setText(R.string.about_article_author_name);
		articleDate = rootView.findViewById(R.id.app_item_detail_view_item_date);
		articleDate.setText(R.string.about_article_date);
		articleName = rootView.findViewById(R.id.items_row_layout_item_name);
		articleName.setText(R.string.about_article_name);
		articleDateArticlePreview = rootView.findViewById(R.id.items_row_layout_item_date);
		articleDateArticlePreview.setText(R.string.about_article_date);
		articleAuthor = rootView.findViewById(R.id.items_row_layout_item_author);
		articleAuthor.setText(R.string.about_article_author_name);
		articleSummary = rootView.findViewById(R.id.items_row_layout_item_content);
		articleSummary.setText(R.string.about_article_summary);

		// Setup the gif animation
		aboutGifView = rootView.findViewById(R.id.about_gif_animation);
		aboutGifView.setGifResource(R.mipmap.jerry_maguire_animation);
		aboutGifView.play();
	}
}