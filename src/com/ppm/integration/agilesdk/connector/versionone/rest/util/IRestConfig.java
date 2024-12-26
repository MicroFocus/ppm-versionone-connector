
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

import org.apache.wink.client.ClientConfig;

public interface IRestConfig {
    ClientConfig setProxy(String proxyHost, String proxyPort);

    void setBearerToken(String token);

    String getAuthorizationHeader();

    ClientConfig getClientConfig();
}
