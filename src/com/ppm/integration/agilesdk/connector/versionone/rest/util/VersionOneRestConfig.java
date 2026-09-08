
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

/**
 * Configuration holder for VersionOne REST client.
 * Stores bearer token and proxy settings for use by RestWrapper.
 */
public class VersionOneRestConfig implements IRestConfig {
    private String proxyHost;
    private String proxyPort;
    private String authenticationHeader;

    public VersionOneRestConfig() {
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
    }

    @Override
    public String getProxyPort() {
        return proxyPort;
    }

    @Override
    public void setProxy(String proxyHost, String proxyPort) {
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort != null && !proxyPort.isEmpty()) {
            // Keep prior behavior: fail fast when proxy port is not numeric.
            Integer.parseInt(proxyPort);
            this.proxyHost = proxyHost;
            this.proxyPort = proxyPort;
        }
    }

    @Override
    public String getAuthorizationHeader() {
        return authenticationHeader;
    }

    @Override
    public void setBearerToken(String token) {
        authenticationHeader = "Bearer " + token;
    }

}
