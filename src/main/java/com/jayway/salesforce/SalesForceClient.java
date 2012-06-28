package com.jayway.salesforce;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Contains the necessary properties to request an access token.
 * 
 * @author Jan Kronquist
 */
public class SalesForceClient {
    public static final String environment = "https://login.salesforce.com";
    public static final String tokenUrl = environment + "/services/oauth2/token";

    private static final String CLIENT_ID = "clientId";
    private static final String CLIENT_SECRET = "clientSecret";
	private final String clientId;
    private final String clientSecret;
    
	public SalesForceClient(String clientId, String clientSecret) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}
	
	public void save(String fileName) throws IOException {
		Properties properties = new Properties();
		properties.setProperty(CLIENT_ID, clientId);
        properties.setProperty(CLIENT_SECRET, clientSecret);
		properties.store(new FileWriter(fileName), "");
	}

	public static SalesForceClient load(String fileName) throws IOException {
		try {
            Properties properties = new Properties();
            properties.load(new FileReader(fileName));
            return new SalesForceClient(properties.getProperty(CLIENT_ID), properties.getProperty(CLIENT_SECRET));
        } catch (FileNotFoundException e) {
            System.err.format("Expected a property file '%s' with the following properties:\n", fileName);
            System.err.format("%s=<SalesForce client id>\n", CLIENT_ID);
            System.err.format("%s=<SalesForce client secret>\n", CLIENT_SECRET);
            System.exit(1);
            return null;
        }
	}

    public AccessToken retrieveAccessToken(String code, String callbackUri) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost post = new HttpPost(tokenUrl);
        LinkedList<NameValuePair> parameters = new LinkedList<NameValuePair>();
        parameters.add(new BasicNameValuePair("code", code));
        parameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
        parameters.add(new BasicNameValuePair("client_id", clientId));
        parameters.add(new BasicNameValuePair("client_secret", clientSecret));
        parameters.add(new BasicNameValuePair("redirect_uri", callbackUri));
        post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));

        try {
            HttpResponse httpResponse = httpclient.execute(post);
            JSONObject authResponse = new JSONObject(
                    new JSONTokener(new InputStreamReader(httpResponse.getEntity().getContent())));
            
            return new AccessToken(authResponse.getString("access_token"), authResponse.getString("instance_url"));
        } finally {
            post.releaseConnection();
        }
    }

    public String getAuthUrl(String callbackUri) throws UnsupportedEncodingException {
        return environment + "/services/oauth2/authorize?response_type=code&client_id=" + clientId + "&redirect_uri="
                + URLEncoder.encode(callbackUri, "UTF-8");
    }
}
