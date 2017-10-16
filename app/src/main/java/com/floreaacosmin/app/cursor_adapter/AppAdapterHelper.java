package com.floreaacosmin.app.cursor_adapter;

import com.floreaacosmin.app.content_provider.AppProviderContentContract;

import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

public class AppAdapterHelper {

	private static AppAdapterHelper instance;

	private AppAdapterHelper(){}

	public static synchronized AppAdapterHelper getInstance() {
		if (instance == null) {
			instance = new AppAdapterHelper();
		}
		return instance;
	}

    public void loadHtmlContent(WebView webView, String htmlContent) {
    	/* Using composed MIME type and encoding null because the encoding parameter 
    	 * is ignored in the loadData method and the loadDataWithBaseUrl although it 
    	 * works requires two additional url parameters which if set to null renders 
    	 * the WebView empty on loading. */
    	webView.loadData(htmlContent, AppProviderContentContract.MIME_TYPE, null);
    }

    public void loadArticleSummaryText(TextView articleSummary, String articleSummaryText) {
        //noinspection deprecation
        articleSummary.setText(Html.fromHtml(articleSummaryText));
    }
}
