
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;

public class VersionOneRestConfig implements IRestConfig {
    private ClientConfig clientConfig;

    private BasicAuthSecurityHandler basicAuthHandler;

    private String authenticationHeader;

    public VersionOneRestConfig() {
        clientConfig = new ClientConfig();
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    @Override
    public ClientConfig setProxy(String proxyHost, String proxyPort) {

        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty()) {
            clientConfig.proxyHost(proxyHost);
            clientConfig.proxyPort(Integer.parseInt(proxyPort));
        }
        return clientConfig;
    }

    @Override
    public String getAuthorizationHeader() {

        return authenticationHeader;
    }

    @Override
    public void setBearerToken(String token) {
        authenticationHeader = "Bearer: "+token;
    }

}
