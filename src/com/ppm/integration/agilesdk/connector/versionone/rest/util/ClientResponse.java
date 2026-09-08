package com.ppm.integration.agilesdk.connector.versionone.rest.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Wrapper over Spring's ResponseEntity to mimic Apache Wink's ClientResponse API.
 * This allows minimal changes when migrating from Wink to Spring HTTP.
 */
public class ClientResponse {
    private ResponseEntity<String> responseEntity;

    public ClientResponse(ResponseEntity<String> responseEntity) {
        this.responseEntity = responseEntity;
    }

    /**
     * Returns HTTP status code (mimics Wink ClientResponse.getStatusCode()).
     */
    public int getStatusCode() {
        return responseEntity.getStatusCodeValue();
    }

    /**
     * Returns response body as specified type (mimics Wink ClientResponse.getEntity(Class)).
     * Currently only supports String.class.
     */
    public <T> T getEntity(Class<T> type) {
        if (type == String.class) {
            return type.cast(responseEntity.getBody());
        }
        throw new UnsupportedOperationException("Only String.class is supported");
    }

    /**
     * Returns HTTP reason phrase (mimics Wink ClientResponse.getMessage()).
     */
    public String getMessage() {

        HttpStatus status = HttpStatus.resolve(getStatusCode());
        return status != null ? status.getReasonPhrase() : String.valueOf(getStatusCode());
    }

    /**
     * Returns the underlying Spring ResponseEntity for advanced usage.
     */
    public ResponseEntity<String> getResponseEntity() {
        return responseEntity;
    }
}

