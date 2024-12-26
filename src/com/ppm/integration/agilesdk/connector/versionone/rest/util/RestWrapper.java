
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.RestRequestException;

public class RestWrapper {
    private RestClient restClient;

    private final Logger logger = Logger.getLogger(this.getClass());

    private IRestConfig config;

    public RestWrapper(IRestConfig config) {
        this.config = config;
        restClient = createRestClient(config);
    }


    public RestClient createRestClient(IRestConfig config) {
        restClient = new RestClient(config.getClientConfig());
        return restClient;
    }

    private Resource getResource(String uri) {
        Resource resource = restClient.resource(uri).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", config.getAuthorizationHeader());
        return resource;
    }

    public ClientResponse sendGet(String uri) {
        if (logger.isDebugEnabled()) {
            logger.debug("===> GET "+uri);
        }
        Resource resource = this.getResource(uri);
        ClientResponse response = resource.get();
        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            if (logger.isDebugEnabled()) {
                logger.debug("###> ERROR, not getting HTTP 200 Response. Status code: "+statusCode + ", response message: "+response.getMessage());
            }
            throw new RestRequestException(statusCode + "", response.getMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("<=== HTTP 200");
        }
        return response;
    }
}
