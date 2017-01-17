package com.ppm.integration.agilesdk.connector.versionone.model;

public class VersionOneActualForTimesheet {
	private String parentName;
	private String actualName;
	private Double value;
	private String date;

	public VersionOneActualForTimesheet(String parentName, String actualName, Double value, String date) {
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
