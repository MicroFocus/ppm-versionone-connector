package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;

import java.util.Date;
import java.util.List;

public class VersionOneStory extends VersionOneWorkItem {

    private String beginDate;

    private String endDate;

    private String detailEstimateHrs;

    private String doneHrs;

    private String toDoHrs;


    public VersionOneStory(String storyId, String storyName, String beginDate, String endDate, String statusName, String createDate, String detailEstimateHrs, String doneHrs, String toDoHrs, VersionOneWorkPlanIntegration.TaskCreationContext context) {
        super(storyId, storyName, statusName, createDate, context);
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.detailEstimateHrs = detailEstimateHrs;
        this.doneHrs = doneHrs;
        this.toDoHrs = toDoHrs;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDetailEstimateHrs() {
        return detailEstimateHrs;
    }

    public void setDetailEstimateHrs(String detailEstimateHrs) {
        this.detailEstimateHrs = detailEstimateHrs;
    }

    public String getDoneHrs() {
        return doneHrs;
    }

    public void setDoneHrs(String doneHrs) {
        this.doneHrs = doneHrs;
    }

    public String getToDoHrs() {
        return toDoHrs;
    }

    public void setToDoHrs(String toDoHrs) {
        this.toDoHrs = toDoHrs;
    }

    @Override
    public Date getScheduledFinish() {
        return toDate(this.endDate, true);
    }

    @Override
    public Date getScheduledStart() {
        if (beginDate == null) {
            return toDate(this.getCreateDate());
        }
        return toDate(this.beginDate);
    }

    @Override
    public List<ExternalTaskActuals> getActuals() {

        double scheduledEffort = 10.0d;
        if (!"null".equals(detailEstimateHrs)) {
            scheduledEffort = Double.parseDouble(detailEstimateHrs);
        }

        double actualEffort = 0.0d;
        if (context.importActualEffort) {
            if ("null".equals(doneHrs)) {
                actualEffort =  0.0d;
            } else {
                actualEffort = Double.parseDouble(doneHrs);
            }
        }

        Double remainingEffort = null;
        if (context.importActualEffort) {
            if ("null".equals(toDoHrs)) {
                remainingEffort =  0.0d;
            } else {
                remainingEffort = Double.parseDouble(toDoHrs);
            }
        }

        double percentComplete = 0.0;
        if (context.importActualEffort) {

            try {
                double done = Double.parseDouble(doneHrs);
                double todo = Double.parseDouble(toDoHrs);
                percentComplete = done / (done + todo) * 100;
            } catch (Exception e) {
                percentComplete = 0.0;
            }
        }

        Date actualStart = context.importActualEffort ? ((!"null".equals(doneHrs)) && Double.parseDouble(doneHrs) > 0 ? toDate(beginDate) : null) : null;

        return generateActuals(scheduledEffort, actualEffort, remainingEffort, percentComplete, actualStart);
    }

}
