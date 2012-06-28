package com.jayway.util;

import java.io.IOException;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Handler that performs a redirect to a certain location.
 * 
 * @author Jan Kronquist
 */
public class RedirectHandler implements HttpHandler {
    private final String location;

    public RedirectHandler(String location) {
        this.location = location;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Location", location);
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_MOVED_TEMP, 0);
        exchange.close();
    }

}
