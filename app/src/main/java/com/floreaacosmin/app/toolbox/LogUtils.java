/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Modified November 2014 - Cosmin Florea 

package com.floreaacosmin.app.toolbox;

import android.util.Log;

@SuppressWarnings("unused")
public class LogUtils {

	// Enable or disable the application logs
	public static final boolean DEBUG_ENABLED = true;

    public static String makeLogTag(String str) {
        return str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class<?> cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGD(final String tag, String message) {
    	if (DEBUG_ENABLED) {
        	Log.d(tag, message);	
    	}
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
    	if (DEBUG_ENABLED) {
        	Log.d(tag, message, cause);    		
    	}
    }

    public static void LOGV(final String tag, String message) {
    	if (DEBUG_ENABLED) {
            Log.v(tag, message);	
    	}
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
    	if (DEBUG_ENABLED) {
        	Log.v(tag, message, cause);	
    	}
    }

    public static void LOGI(final String tag, String message) {
    	if (DEBUG_ENABLED) {
            Log.i(tag, message);	
    	}
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
    	if (DEBUG_ENABLED) {
            Log.i(tag, message, cause);	
    	}
    }

    public static void LOGW(final String tag, String message) {
    	if (DEBUG_ENABLED) {
            Log.w(tag, message);	
    	}
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
    	if (DEBUG_ENABLED) {
            Log.w(tag, message, cause);	
    	}
    }

    public static void LOGE(final String tag, String message) {
    	if (DEBUG_ENABLED) {
            Log.e(tag, message);	
    	}
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
    	if (DEBUG_ENABLED) {
            Log.e(tag, message, cause);	
    	}
    }

    private LogUtils() {
    }
}