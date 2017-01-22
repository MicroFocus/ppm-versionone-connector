package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.pm.ExternalTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VersionOneEntity extends ExternalTask {

    protected Date toDate(String d) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(d);
        } catch (ParseException e) {

        }
        return date;
    }
}
