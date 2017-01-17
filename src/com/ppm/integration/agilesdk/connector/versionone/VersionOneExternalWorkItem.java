package com.ppm.integration.agilesdk.connector.versionone;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import com.ppm.integration.agilesdk.tm.ExternalWorkItem;
import com.ppm.integration.agilesdk.tm.ExternalWorkItemEffortBreakdown;

public class VersionOneExternalWorkItem extends ExternalWorkItem {
	private String name = "";
	private Double totalEffort = 0d;
	private String errorMessage = null;

	private Map<String, Double> timeSpent = new HashMap<>();
	private XMLGregorianCalendar dateFrom;
	private XMLGregorianCalendar dateTo;

	public VersionOneExternalWorkItem(String name, Double totalEffort, String errorMessage,
			XMLGregorianCalendar dateFrom, XMLGregorianCalendar dateTo, Map<String, Double> timeSpent) {
		this.name = name;
		this.totalEffort = totalEffort;
		this.errorMessage = errorMessage;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.timeSpent = timeSpent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Double getTotalEffort() {
		return totalEffort;
	}

	// @Override
	// public String getExternalData() {
	//
	// JSONObject json = new JSONObject();
	//
	// ExternalWorkItemActualEfforts actual = new
	// ExternalWorkItemActualEfforts();
	//
	// Calendar cursor = dateFrom.toGregorianCalendar();
	//
	// while (cursor.before(dateTo.toGregorianCalendar())) {
	// String cursorDate = dateFormat.format(cursor.getTime());
	// if (timeSpent.containsKey(cursorDate)) {
	// long actualEffort = timeSpent.get(cursorDate);
	// actual.getEffortList().put(cursorDate, (double) actualEffort);
	//
	// } else {
	// actual.getEffortList().put(cursorDate, 0.0);
	// }
	// cursor.add(Calendar.DAY_OF_MONTH, 1);
	// }
	//
	// json.put(ExternalWorkItemActualEfforts.JSON_KEY_FOR_ACTUAL_EFFORT,
	// actual.toJson());
	//
	// return json.toString();
	// }
	@Override
	public ExternalWorkItemEffortBreakdown getEffortBreakDown() {
		ExternalWorkItemEffortBreakdown eb = new ExternalWorkItemEffortBreakdown();
		Set<String> keys = timeSpent.keySet();
		for (String date : keys) {
			eb.addEffort(date, timeSpent.get(date));
		}
		return eb;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

}
