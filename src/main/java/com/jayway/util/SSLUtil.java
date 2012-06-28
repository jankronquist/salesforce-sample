package com.jayway.util;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

/**
 * Various utility functions for working for SSL certificates.
 * 
 * @author Jan Kronquist
 */
public abstract class SSLUtil {
    private SSLUtil() {}
    
    public static void configure(HttpsServer server, SSLContext sslContext) throws Exception {
        server.setHttpsConfigurator (new HttpsConfigurator(sslContext) {
            public void configure (HttpsParameters params) {
                SSLContext c = getSSLContext();
                params.setSSLParameters(c.getDefaultSSLParameters());
            }
        }); 
    }

    public static SSLContext makeSSLContext(String keystoreFileName, String keystorePassword) throws Exception {
        char[] passphrase = keystorePassword.toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(keystoreFileName), passphrase);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, passphrase);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        return ssl;
    }
}
