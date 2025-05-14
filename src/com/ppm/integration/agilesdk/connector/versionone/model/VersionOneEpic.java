package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
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
        // Actuals with resources are included in the children tasks.
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.number + ": " + this.name;
    }

    @Override
    public List<ExternalTask> getChildren() {
        List<ExternalTask> children = new ArrayList<ExternalTask>();

        final VersionOneEpic realEpic = this;

        // For Delta, each EPIC should contain one child for CAPEX and one for OPEX.
        // First, capex
        children.add(new ExternalTask() {
            @Override
            public String getId() {
                return realEpic.getId()+"-CAP";
            }

            @Override
            public String getName() {
                return realEpic.getName() + " - CAP";
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
        // Then, opex
        children.add(new ExternalTask() {
            @Override
            public String getId() {
                return realEpic.getId()+"-OP";
            }

            @Override
            public String getName() {
                return realEpic.getName() + " - OP";
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


        return children;

    }
}
