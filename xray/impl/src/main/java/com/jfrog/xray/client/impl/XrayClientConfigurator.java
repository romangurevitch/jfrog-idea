package com.jfrog.xray.client.impl;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.jfrog.client.http.HttpClientConfiguratorBase;
import org.jfrog.client.http.auth.PreemptiveAuthInterceptor;

import java.io.IOException;

/**
 * Created by romang on 1/31/17.
 */
public class XrayClientConfigurator extends HttpClientConfiguratorBase {
    @Override
    protected void additionalConfigByAuthScheme() {
        // Preemptive authorization interceptor
        builder.addInterceptorFirst(new PreemptiveAuthInterceptor());
    }
}

