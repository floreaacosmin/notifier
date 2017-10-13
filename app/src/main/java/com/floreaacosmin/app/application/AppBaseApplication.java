package com.floreaacosmin.app.application;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.floreaacosmin.app.toolbox.BitmapLruImageCache;
import com.floreaacosmin.app.toolbox.LogUtils;

public class AppBaseApplication extends Application {
	
	/* Volley provides utilities which makes the networking for Android applications easier and faster. The good 
	 * thing about Volley is that it abstracts away the low level details of what HTTP client library is being used 
	 * under the hood and helps you focus on writing nice and clean RESTful HTTP requests. Additionally all requests 
	 * in Volley are executed asynchronously on a different thread without blocking your “main thread”. The best way 
	 * to maintain volley core objects and request queue is, making them global by creating a singleton class which 
	 * extends the Application object.*/
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

    private final String LOG_TAG = LogUtils.makeLogTag(AppBaseApplication.class);

	@Override
	public void onCreate() {
		/* The AsyncTask expects to be initialized on the Main thread. This might not happen when 
		 * a BroadcastReceeiver is also used in the application. A workaround is to ensure that 
		 * AsyncTask is initialized in the main application using the code below. */
        final String ASYNC_TASK_CLASS = "android.os.AsyncTask";

		try {
            Class.forName(ASYNC_TASK_CLASS);
        } catch (ClassNotFoundException e) {
            LogUtils.LOGD(LOG_TAG, "class was not found: " + e.toString());
		}

		super.onCreate();
	}

    // Volley Singleton part starting

	/* Return The Volley Request queue, the queue will be created if it is null. RequestQueue 
	 * should be made once and then referred to it across the Application. */
	private RequestQueue getRequestQueue() {
        if (requestQueue == null) {
    		/* All requests in Volley are placed in a queue first and then processed. 
    		 * This is a global queue for all the requests. */
        	requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
	}
	
	// Calculate the Image Cache Size based on the maximum amount that the Heap can expand to
	private int getImageCacheSize() { 
		int maxHeapMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		
		return maxHeapMemory / 8;
	}
	
	public ImageLoader getImageLoader() {
        ImageCache imageCache;

		if (imageLoader == null) {
			// Initialize the image cache class
			imageCache = new BitmapLruImageCache(getImageCacheSize());
			// Initialize the image loader and use a memory cache (L1)
			imageLoader = new ImageLoader(getRequestQueue(), imageCache);
		}
		return imageLoader;
	}	
	
    public <T> void addToRequestQueue(Request<T> request, @SuppressWarnings("SameParameterValue") String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }
	
    // Volley Singleton part ending


}