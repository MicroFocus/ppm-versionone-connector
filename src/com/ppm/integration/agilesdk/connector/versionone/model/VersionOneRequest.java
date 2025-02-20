package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;

import java.util.Date;
import java.util.List;

/**
 * For all VersionOne Types based on Requests. For example, Features.
 *
 * These do not have start & end dates, only a "Needed by Date", so we display them as Milestones with this as target.
 */
public class VersionOneRequest extends VersionOneWorkItem {

    private String neededByDate;

    public VersionOneRequest(String id, String name, String statusName, String createDate, String neededByDate, VersionOneWorkPlanIntegration.TaskCreationContext context) {
        super(id, name, statusName, createDate, context);
        this.neededByDate =  neededByDate;
    }

    @Override
    public Date getScheduledFinish() {
        return toDate(neededByDate ==  null ? getCreateDate() : neededByDate);
    }

    @Override
    public Date getScheduledStart() {
        return toDate(neededByDate ==  null ? getCreateDate() : neededByDate);
    }

    @Override
    public List<ExternalTaskActuals> getActuals() {
        return generateActuals(10.0, 0.0, 0.0, 0.0, null);
    }
}
