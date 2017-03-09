
package com.ppm.integration.agilesdk.connector.versionone.model;

public class VersionOneActualForTimesheet {
    private String parentName;

    private String actualName;

    private Long value;

    private String date;

    public VersionOneActualForTimesheet(String parentName, String actualName, Long value, String date) {
        this.parentName = parentName;
        this.actualName = actualName;
        this.value = value;
        this.date = date;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getActualName() {
        return actualName;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
