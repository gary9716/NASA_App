package com.lab430.model;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by lab430 on 15/6/20.
 */
public class PSRestClient {
    public final static String debug_tag = "restClient";

    private static final String BASE_URL = ProjectConfig.serverURL;
    public static final PSRestClient instance = new PSRestClient();
    private AsyncHttpClient client = null;

    private PSRestClient() {
        client = new AsyncHttpClient();
        client.setTimeout(10000);
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
