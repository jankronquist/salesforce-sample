package com.jayway.salesforce;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jayway.util.HttpUtil;

/**
 * Performs some requests to the SalesForce REST API based on their Java sample.
 * 
 * @author Jan Kronquist
 */
public class Demo {
    private static AccessToken accessToken;

    public static void main(String[] args) throws Exception {
        if (!new File(GetAccessToken.FILE_ACCESS_TOKEN).exists()) {
            GetAccessToken.main(args);
            System.out.println("Press ENTER when done");
            System.in.read();
        }
        accessToken = AccessToken.load(GetAccessToken.FILE_ACCESS_TOKEN);
        showAccounts();
    }

    private static void showAccounts() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();

        HttpGet get = new HttpGet(accessToken.instanceUrl
                + "/services/data/v20.0/query?q=SELECT+Id,+Name+from+Account+LIMIT+100");

        // set the token in the header
        get.addHeader("Authorization", "OAuth " + accessToken.accessToken);
        try {
            HttpResponse httpResponse = httpclient.execute(get);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                JSONObject response = HttpUtil.entityAsJSON(httpResponse);
                System.out.println("Query response: " + response.toString(2));

                System.out.println(response.getString("totalSize") + " record(s) returned\n\n");

                JSONArray results = response.getJSONArray("records");

                for (int i = 0; i < results.length(); i++) {
                    System.out.println(results.getJSONObject(i).getString("Id") + ", "
                            + results.getJSONObject(i).getString("Name") + "\n");
                }
            } else {
                System.err.println(httpResponse.getStatusLine());
                httpResponse.getEntity().writeTo(System.err);
            }
        } finally {
            get.releaseConnection();
        }
    }
}
