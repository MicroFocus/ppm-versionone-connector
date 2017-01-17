package com.ppm.integration.agilesdk.connector.versionone.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ppm.integration.agilesdk.pm.ExternalTask;

public class VersionOneEntity extends ExternalTask {

	Date toDate(String d) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(d);
		} catch (ParseException e) {

		}
		return date;
	}
}
