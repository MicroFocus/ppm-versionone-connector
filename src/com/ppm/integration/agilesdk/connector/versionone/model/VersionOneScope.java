
package com.ppm.integration.agilesdk.connector.versionone.model;

public class VersionOneScope {
    private String scopeName;

    private String scopeId;

    public VersionOneScope(String scopeName, String scopeId) {
        this.scopeName = scopeName;
        this.scopeId = scopeId;
    }

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

}
