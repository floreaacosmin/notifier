/*
 * Copyright (C) 2013 lytsing.org
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

package com.floreaacosmin.app.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

/* When a request object in created in Volley, an error listener needs to be specified, Volley invokes the 
 * onErrorResponse callback method of that listener passing an instance of the VolleyError object when there 
 * is an error while performing the request. 
 * 
 * The following is the list of exceptions in Volley:
 * 
 * AuthFailureError — If you are trying to do Http Basic authentication then this error is most likely to come.
 * 
 * NetworkError — Socket disconnection, server down, DNS issues might result in this error.
 * 
 * NoConnectionError — Similar to NetworkError, but fires when device does not have internet connection, your 
 * error handling logic can club NetworkError and NoConnectionError together and treat them similarly.
 * 
 * ParseError — While using JsonObjectRequest or JsonArrayRequest if the received JSON is malformed then this 
 * exception will be generated. If you get this error then it is a problem that should be fixed instead of 
 * being handled.
 * 
 * ServerError — The server responded with an error, most likely with 4xx or 5xx HTTP status codes.
 * 
 * TimeoutError — Socket timeout, either server is too busy to handle the request or there is some network latency 
 * issue. By default Volley times out the request after 2.5 seconds, use a RetryPolicy if you are consistently 
 * getting this error. */
 
public class VolleyErrorHelper {
    /* Returns appropriate message which is to be displayed to the user
     * against the specified error object. */
    public static String getMessage(Object error) {

        if (error instanceof TimeoutError) {
            return VolleyHTTPCodes.GENERAL_SERVER_DOWN;
        }
        else if (isServerProblem(error)) {
            return handleServerError(error);
        }
        else if (isNetworkProblem(error)) {
            return VolleyHTTPCodes.NETWORKING_ERROR;
        }
        return VolleyHTTPCodes.GENERAL_ERROR;
    }

    /* Determines whether the error is related to network. */
    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    /* Determines whether the error is related to server. */
    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

     /* Handles the server error, tries to determine whether to show a stock message or to
      * show a message retrieved from the server. */
    private static String handleServerError(Object error) {

        VolleyError volleyError = (VolleyError) error;
        NetworkResponse response = volleyError.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 304:
                    return VolleyHTTPCodes.HTTP_ERROR_304;
                case 400:
                    return VolleyHTTPCodes.HTTP_ERROR_400;
                case 401:
                    return VolleyHTTPCodes.HTTP_ERROR_401;
                case 402:
                    return VolleyHTTPCodes.HTTP_ERROR_402;
                case 403:
                    return VolleyHTTPCodes.HTTP_ERROR_403;
                case 404:
                    return VolleyHTTPCodes.HTTP_ERROR_404;
                case 500:
                    return VolleyHTTPCodes.HTTP_ERROR_500;
                case 502:
                    return VolleyHTTPCodes.HTTP_ERROR_502;
                case 503:
                    return VolleyHTTPCodes.HTTP_ERROR_503;
                default:
                    return VolleyHTTPCodes.GENERAL_SERVER_DOWN;
            }
        }

        return VolleyHTTPCodes.GENERAL_ERROR;
    }
}
