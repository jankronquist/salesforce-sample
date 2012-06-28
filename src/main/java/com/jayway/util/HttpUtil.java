package com.jayway.util;

import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.sun.net.httpserver.HttpExchange;

/**
 * Various utility functions for working with HTTP and JSON.
 * 
 * @author Jan Kronquist
 */
public abstract class HttpUtil {
    private HttpUtil() {}

    public static String getQueryParam(String name, HttpExchange exchange) {
        List<NameValuePair> params = new URIBuilder(exchange.getRequestURI()).getQueryParams();
        for (NameValuePair p : params) {
            if (p.getName().equals(name)) {
                return p.getValue();
            }
        }
        return null;
    }

    public static JSONObject entityAsJSON(HttpResponse httpResponse) throws Exception {
        return new JSONObject(new JSONTokener(new InputStreamReader(httpResponse.getEntity().getContent())));
    }

}
