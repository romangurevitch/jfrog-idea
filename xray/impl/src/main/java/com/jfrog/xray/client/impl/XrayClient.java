package com.jfrog.xray.client.impl;

import com.jfrog.xray.client.Xray;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

/**
 * Created by romang on 2/2/17.
 */
public class XrayClient {

    static public Xray create(CloseableHttpClient preConfiguredClient, String url) {
        return new XrayImpl(preConfiguredClient, url);
    }

    /**
     * Username, API key, and custom url
     */
    static public Xray create(String url, String username, String password) {
        XrayClientConfigurator configurator = new XrayClientConfigurator();
        configurator.setHostFromUrl(url);
        configurator.setCredentials(username, password, true);

        return new XrayImpl(configurator.getClient(), url);
    }
}
