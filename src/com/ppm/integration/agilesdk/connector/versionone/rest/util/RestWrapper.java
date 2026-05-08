
package com.ppm.integration.agilesdk.connector.versionone.rest.util;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.kintana.core.logging.LogManager;
import com.kintana.core.logging.Logger;

import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.RestRequestException;

public class RestWrapper {
    private RestTemplate restTemplate;

    private final Logger logger = LogManager.getLogger(RestWrapper.class);

    private IRestConfig config;

    public RestWrapper(IRestConfig config) {
        this.config = config;
        restTemplate = createRestTemplate();
    }

    public RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        if (config.getProxyHost() != null && !config.getProxyHost().isEmpty()
                && config.getProxyPort() != null && !config.getProxyPort().isEmpty()) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(config.getProxyHost(), Integer.parseInt(config.getProxyPort())));
            requestFactory.setProxy(proxy);
        }

        RestTemplate template = new RestTemplate(requestFactory);
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.HttpStatus statusCode) {
                return false;
            }
        });
        return template;
    }

    private HttpEntity<?> getRequestHeaders(String uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", config.getAuthorizationHeader());
        return new HttpEntity<>(headers);
    }

    public ClientResponse sendGet(String uri) {
        if (logger.isDebugEnabled()) {
            logger.debug("===> GET " + uri);
        }

        HttpEntity<?> requestEntity = getRequestHeaders(uri);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
        
        int statusCode = response.getStatusCodeValue();
        if (statusCode != 200) {
            if (logger.isDebugEnabled()) {
                logger.debug("###> ERROR, not getting HTTP 200 Response. Status code: " + statusCode + ", response message: " + response.getStatusCode().getReasonPhrase());
            }
            throw new RestRequestException(statusCode + "", response.getStatusCode().getReasonPhrase());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("<=== HTTP 200");
        }
        return new ClientResponse(response);
    }
}
