package com.floreaacosmin.app.toolbox;

import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Basic LRU Memory cache.
 * 
 * @author Trey Robinson
 *
 */
public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache{
	
	private static final String LOG_TAG = LogUtils.makeLogTag(BitmapLruImageCache.class);
	
	@SuppressWarnings("unused")
	public BitmapLruImageCache(int maxSize) {
		super(maxSize);
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}
	
	@Override
	public Bitmap getBitmap(String url) {
		LogUtils.LOGD(LOG_TAG, "Retrieved item from Mem Cache");			

		return get(url);
	}
 
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		LogUtils.LOGD(LOG_TAG, "Added item to Mem Cache");
		
		put(url, bitmap);
	}
}