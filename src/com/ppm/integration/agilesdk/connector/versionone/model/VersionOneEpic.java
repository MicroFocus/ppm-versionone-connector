package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
import com.ppm.integration.agilesdk.connector.versionone.delta.DeltaSubTaskCreationLogic;
import com.ppm.integration.agilesdk.pm.ExternalTask;
import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;

import java.util.ArrayList;
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

    private String number;

    private String firstCriteria;

    private String secondCriteria;

    public VersionOneEpic(String storyId, String storyNumber, String storyName, String statusName, String createDate, String plannedStart, String plannedEnd, VersionOneWorkPlanIntegration.TaskCreationContext context) {
        super(storyId, storyName, statusName, createDate, context);
        this.number = storyNumber;
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
        if (DeltaSubTaskCreationLogic.getChildrenNames(getFirstCriteria(), getSecondCriteria()).isEmpty()) {
            // No children, so this Epic Task directly has actuals (with resources).
            return generateActuals(10.0, 0.0, 0.0, 0.0, null);
        } else {
            // This Epic has sub-tasks, so Actuals with resources will be included in the children tasks.
            return new ArrayList<>();
        }
    }

    @Override
    public String getName() {
        return this.number + ": " + this.name;
    }

    @Override
    public List<ExternalTask> getChildren() {
        List<ExternalTask> children = new ArrayList<ExternalTask>();

        final VersionOneEpic realEpic = this;

        List<String> subTaskNames = DeltaSubTaskCreationLogic.getChildrenNames(getFirstCriteria(), getSecondCriteria());

        for (String suffix : subTaskNames) {
            children.add(new ExternalTask() {
                @Override
                public String getId() {
                    return realEpic.getId()+"-"+suffix;
                }

                @Override
                public String getName() {
                    return realEpic.getName() + " - " +suffix;
                }

                @Override
                public TaskStatus getStatus() {
                    return realEpic.getStatus();
                }

                @Override
                public Date getScheduledStart() {
                    return realEpic.getScheduledStart();
                }

                @Override
                public Date getScheduledFinish() {
                    return realEpic.getScheduledFinish();
                }

                @Override
                public List<ExternalTaskActuals> getActuals() {
                    return realEpic.generateActuals(10.0, 0.0, 0.0, 0.0, null);
                }
            });
        }


        return children;

    }

    public String getFirstCriteria() {
        return firstCriteria;
    }

    public void setFirstCriteria(String firstCriteria) {
        this.firstCriteria = firstCriteria;
    }

    public String getSecondCriteria() {
        return secondCriteria;
    }

    public void setSecondCriteria(String secondCriteria) {
        this.secondCriteria = secondCriteria;
    }
}
