package com.floreaacosmin.app.volley;

class AppVolleyContract {
    private AppVolleyContract() {
    }

    // URL to fetch content from during a sync
    public static final String API_URL = "http://10.0.177.253:8080/notifications/all";

	/* When working with JSON data in Android, a JSONArray is used to parse JSON which starts with
	* the array brackets. Arrays in JSON are used to organize a collection of related items. On
	* the other hand, JSONObject is used when dealing with a JSON that begins with curly braces.
	* A JSON object is typically used to contain key/value pairs related to one item. Of course,
	* JSON arrays and objects may be nested inside one another. One common example of this is an API
	* service which returns a JSON object wrapper which some meta data and an array of the items which
	* match your query, such in this case.	 */

    public static final String GSON_NOTIFICATIONS_REQUEST_TAG = "gsonNotificationsRequest";
}