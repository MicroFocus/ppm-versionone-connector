
package com.ppm.integration.agilesdk.connector.versionone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import com.ppm.integration.agilesdk.tm.ExternalWorkItem;
import com.ppm.integration.agilesdk.tm.ExternalWorkItemEffortBreakdown;

public class VersionOneExternalWorkItem extends ExternalWorkItem {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String name = "";

    private long totalEffort = 0;

    private String errorMessage = null;

    private Map<String, Long> timeSpent = new HashMap<>();

    private XMLGregorianCalendar dateFrom;

    private XMLGregorianCalendar dateTo;

    public VersionOneExternalWorkItem(String name, long totalEffort, String errorMessage, XMLGregorianCalendar dateFrom,
            XMLGregorianCalendar dateTo, Map<String, Long> timeSpent) {
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
        return (double)totalEffort;
    }

    @Override
    public ExternalWorkItemEffortBreakdown getEffortBreakDown() {

        ExternalWorkItemEffortBreakdown effortBreakdown = new ExternalWorkItemEffortBreakdown();

        Calendar cursor = dateFrom.toGregorianCalendar();

        while (cursor.before(dateTo.toGregorianCalendar())) {
            String cursorDate = dateFormat.format(cursor.getTime());
            if (timeSpent.containsKey(cursorDate)) {
                long actualEffort = timeSpent.get(cursorDate);
                effortBreakdown.addEffort(cursor.getTime(), (double)actualEffort);

            } else {
                effortBreakdown.addEffort(cursor.getTime(), 0.0);
            }
            cursor.add(Calendar.DAY_OF_MONTH, 1);
        }

        return effortBreakdown;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

}
