
package com.ppm.integration.agilesdk.connector.versionone.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneConstants;
import org.apache.log4j.Logger;

import com.ppm.integration.agilesdk.pm.ExternalTask;

public class VersionOneEntity extends ExternalTask implements VersionOneConstants {
    private final Logger logger = Logger.getLogger(this.getClass());

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * VersionOne will add one day to all finish dates because of some obscure reason of midnight not being included in closing date window.
     * Basically, the finish date is exclusive (that's how I understand it). Since it's inclusive in PPM, we remove one day to the finish dates.
     */
    private Date adjustFinishDate(Date d) {
        if (d == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    Date toDate(String dateStr) {
        return toDate(dateStr, false);
    }

    Date toDate(String dateStr, boolean adjustDateForFinish) {
        if (dateStr != null && !NULL_VALUE.equalsIgnoreCase(dateStr)) {

            Date d = null;
            try {
                if (dateStr.length() > 10) {
                    dateStr = dateStr.substring(0, 10);
                }
                d = format.parse(dateStr);
            } catch (ParseException e) {
                logger.error("Date Parse Error,the input dateStr is " + dateStr, e);
                return getDefaultDate();
            }
            return adjustDateForFinish ? adjustFinishDate(d) : d;
        } else {
            return getDefaultDate();

        }
    }

    private Date getDefaultDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
