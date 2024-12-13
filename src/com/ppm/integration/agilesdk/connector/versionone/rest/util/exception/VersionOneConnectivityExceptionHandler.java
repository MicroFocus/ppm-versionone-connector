
package com.ppm.integration.agilesdk.connector.versionone.rest.util.exception;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.wink.client.ClientRuntimeException;

import com.ppm.integration.IntegrationException;
import com.ppm.integration.agilesdk.connector.versionone.VersionOneIntegrationConnector;

public class VersionOneConnectivityExceptionHandler implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        uncaughtException(t, e, VersionOneIntegrationConnector.class);
    }

    public void uncaughtException(Thread t, Throwable e, Class cls) {
        if (e instanceof ClientRuntimeException) {
            handleClientRuntimeException((ClientRuntimeException)e, cls);
        } else if (e instanceof RestRequestException) {
            handleClientException((RestRequestException)e, cls);
        } else if (e instanceof IntegrationException) {
            throw (IntegrationException)e;
        } else {
            throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED").setMessage("ERROR_UNEXPECTED",
                    e.getMessage());
        }
    }

    private void handleClientException(RestRequestException e, Class cls) {
        switch (e.getErrorCode()) {
            case "404":
                throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                        .setMessage("ERROR_DOMAIN_NOT_FOUND");
            case "502":
                throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                        .setMessage("ERROR_BAD_GETWAY");
            case "400":
                throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                        .setMessage("ERROR_BAD_REQUEST");
            case "401":
                throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                        .setMessage("ERROR_AUTHENTICATION_FAILED");
            default:
                throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                        .setMessage("ERROR_CONNECTIVITY_ERROR", e.getMessage());
        }

    }

    private void handleClientRuntimeException(ClientRuntimeException e, Class cls) {
        java.net.UnknownHostException unknownHost = extractException(e, java.net.UnknownHostException.class);
        if (unknownHost != null) {
            throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                    .setMessage("ERROR_UNKNOWN_HOST_ERROR", unknownHost.getMessage());
        }

        java.net.ConnectException connectException = extractException(e, java.net.ConnectException.class);
        if (connectException != null) {
            throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                    .setMessage("ERROR_CONNECTIVITY_ERROR");
        }

        throw IntegrationException.build(cls).setErrorCode("ERROR_UNEXPECTED")
                .setMessage("ERROR_CONNECTIVITY_ERROR");
    }

    @SuppressWarnings("unchecked")
    protected <T extends Throwable> T extractException(ClientRuntimeException e, Class<T> clazz) {

        Throwable t = e;
        while (!clazz.isInstance(t) && t != null) {
            t = t.getCause();
        }

        return (T)t;
    }
}