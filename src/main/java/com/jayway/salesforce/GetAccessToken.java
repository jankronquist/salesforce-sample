package com.jayway.salesforce;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import com.jayway.util.HttpUtil;
import com.jayway.util.RedirectHandler;
import com.jayway.util.SSLUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;

/**
 * Tool for getting a SalesForce access token.
 * 
 * @author Jan Kronquist
 */
public class GetAccessToken {
    private static final String FILE_CLIENT_CONFIG = "clientConfig.properties";
    public static final String FILE_ACCESS_TOKEN = "accessToken.properties";

    public static void main(String[] args) throws Exception {
        createCallbackServer();
        initiateOAuthFlow();
    }

    private static void createCallbackServer() throws Exception {
        final SalesForceClient client = SalesForceClient.load(FILE_CLIENT_CONFIG);
        
        final String callbackUri = "https://localhost:8443/callback";

        final HttpsServer server = HttpsServer.create(new InetSocketAddress(8443), 10);
        SSLUtil.configure(server, SSLUtil.makeSSLContext("localhost-keystore.jks", "password"));
        server.createContext("/redirect", new RedirectHandler(client.getAuthUrl(callbackUri)));
        server.createContext("/callback", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    AccessToken accessToken = client.retrieveAccessToken(HttpUtil.getQueryParam("code", exchange), callbackUri);
                    accessToken.save(FILE_ACCESS_TOKEN);
                    System.out.println("AccessToken received!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                server.stop(300);
            }
        });
        server.start();
    }

    private static void initiateOAuthFlow() throws Exception {
        URI uri = new URI("https://localhost:8443/redirect");
        if (Desktop.getDesktop() != null) {
            Desktop.getDesktop().browse(uri);
            
            System.out.println("Your browser have navigated to the following URL:");
            System.out.println(uri);
        } else {
            System.out.println("Please open the following URL in your browser:");
            System.out.println(uri);
        }
        System.out.println("Note: Ignore the certicate warnings at localhost!");        
        System.out.println("Please login and grant access!");
        System.out.println(".... waiting ....");
    }
}
