package com.jayway.salesforce;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Representation of an access token, including load and save functionality.
 * 
 * @author Jan Kronquist
 */
public class AccessToken {
	private static final String INSTANCE_URL = "instanceUrl";
	private static final String ACCESS_TOKEN = "accessToken";
	public final String accessToken;
	public final String instanceUrl;

	public AccessToken(String accessToken, String instanceUrl) {
		this.accessToken = accessToken;
		this.instanceUrl = instanceUrl;
	}
	
	public void save(String fileName) throws IOException {
		Properties properties = new Properties();
		properties.setProperty(ACCESS_TOKEN, accessToken);
		properties.setProperty(INSTANCE_URL, instanceUrl);
		properties.store(new FileWriter(fileName), "");
	}

	public static AccessToken load(String fileName) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileReader(fileName));
		return new AccessToken(properties.getProperty(ACCESS_TOKEN), properties.getProperty(INSTANCE_URL));
	}
}
