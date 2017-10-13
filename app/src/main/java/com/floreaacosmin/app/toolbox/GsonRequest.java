/*
  Copyright 2013 Ognyan Bankov

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

// Modified by Cosmin Florea - December 2014 

package com.floreaacosmin.app.toolbox;

import com.floreaacosmin.app.data_processor.AppDataHelper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONObject;

/**
 * Volley adapter for JSON requests with POST method that will be parsed into Java objects by Gson.
 */
@SuppressWarnings("SameParameterValue")
public class GsonRequest<T> extends Request<T> {
    private Gson mGson = new Gson();
    private final Class<T> clazz;
    private Map<String, String> headers;
    private Map<String, String> params;
    private final Listener<T> listener;
    private final JSONObject jsonRequest;
    
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    @SuppressWarnings("unused")
    public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener,
                       ErrorListener errorListener, JSONObject jsonRequest) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.jsonRequest = jsonRequest;
        mGson = new Gson();
    }

    /**
     * Make a POST request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method, String url, Class<T> clazz, Map<String, String> params, Listener<T> listener, 
    	ErrorListener errorListener, JSONObject jsonRequest) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.params = params;
        this.listener = listener;
        this.headers = null;
        this.jsonRequest = jsonRequest;
        mGson = new Gson();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    mGson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
            
        } catch (UnsupportedEncodingException | JsonSyntaxException | OutOfMemoryError e) {
            AppDataHelper.getInstance().sendResultToReceiver(response, 0);
			return Response.error(new ParseError(e));
        }
    }

	// Insert the JSON Object in the body of the request
	@Override
	public byte[] getBody() throws AuthFailureError {
		return jsonRequest.toString().getBytes();
	}
	
	// Overwrite the response body content type
	@Override 
	public String getBodyContentType() {
		return "application/json"; 
	}	
}