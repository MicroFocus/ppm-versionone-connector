
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

public interface IRestConfig {
    void setProxy(String proxyHost, String proxyPort);

    String getProxyHost();

    String getProxyPort();

    void setBearerToken(String token);

    String getAuthorizationHeader();
}
