package com.floreaacosmin.app.cursor_adapter;

import com.floreaacosmin.app.content_provider.AppProviderContentContract;

import android.database.Cursor;
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
	
    public void loadArticleDate(TextView articleDateView, Cursor cursor, 
    	int articleDataUnixIndex, int articleDataIndex) {

		long currentUnixTime;
		long articleUnixDate;
		long oneDay;

    	oneDay = AppProviderContentContract.ONE_DAY_IN_SECONDS;
    	currentUnixTime = System.currentTimeMillis() / 1000L;
    	// Get the article date in Unix time 
    	articleUnixDate = cursor.getInt(articleDataUnixIndex);
    	
    	// The date is today or yesterday
    	if (currentUnixTime - articleUnixDate < 2*oneDay) {
    		// The date is only today
    		if (currentUnixTime - articleUnixDate < oneDay) {
    			articleDateView.setText(String.format("%s%s", AppProviderContentContract.DATE_PREFIX,
                        AppProviderContentContract.DATE_TODAY));
    		} else {
    			articleDateView.setText(String.format("%s%s", AppProviderContentContract.DATE_PREFIX,
                        AppProviderContentContract.DATE_YESTERDAY));
    		}
    	} else {
    		articleDateView.setText(String.format("%s%s", AppProviderContentContract.DATE_PREFIX,
                    cursor.getString(articleDataIndex)));
    	}
    }

    public void loadArticleSummaryText(TextView articleSummary, String articleSummaryText) {
        //noinspection deprecation
        articleSummary.setText(Html.fromHtml(articleSummaryText));
    }
}
