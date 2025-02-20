package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;

import java.util.Date;
import java.util.List;

/**
 * For all VersionOne Types based on Epics.
 *
 * These have PlannedStart and PlannedEnd dates.
 */
public class VersionOneEpic extends VersionOneWorkItem {

    private String plannedStart;

    private String plannedEnd;

    public VersionOneEpic(String storyId, String storyName, String statusName, String createDate, String plannedStart, String plannedEnd, VersionOneWorkPlanIntegration.TaskCreationContext context) {
        super(storyId, storyName, statusName, createDate, context);
        this.plannedStart =  plannedStart;
        this.plannedEnd =  plannedEnd;
    }

    @Override
    public Date getScheduledFinish() {
        return toDate(plannedEnd, true);
    }

    @Override
    public Date getScheduledStart() {
        return toDate(plannedStart);
    }

    @Override
    public List<ExternalTaskActuals> getActuals() {
        return generateActuals(10.0, 0.0, 0.0, 0.0, null);
    }
}
