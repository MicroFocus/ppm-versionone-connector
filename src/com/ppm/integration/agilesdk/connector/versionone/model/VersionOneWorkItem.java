
package com.ppm.integration.agilesdk.connector.versionone.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ppm.integration.agilesdk.connector.versionone.VersionOneWorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Common class used for all types of VersionOne entities that will be converted as a PPM Task in work plan
 */
public abstract class VersionOneWorkItem extends VersionOneEntity {

    protected VersionOneWorkPlanIntegration.TaskCreationContext context;

    protected List<Long> ownersResourceIds = new ArrayList<>();

    private String id;

    private String name;

    private String statusName;

    private String createDate;


    public VersionOneWorkItem(String storyId, String storyName, String statusName, String createDate,
                               VersionOneWorkPlanIntegration.TaskCreationContext context) {
        this.id = storyId;
        this.name = storyName;
        this.statusName = statusName;
        this.createDate = createDate;
        this.context = context;
    }

    public String getEntityName() {
        return name;
    }

    public void setEntityName(String name) {
        this.name = name;
    }


    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public abstract Date getScheduledFinish();

    @Override
    public abstract Date getScheduledStart();

    @Override
    public abstract List<ExternalTaskActuals> getActuals();

    @Override
    public TaskStatus getStatus() {
        return context.getPpmStatus(this.statusName);
    }

    public void setOwnersNames(JSONObject ownersNames) {

        List<String> owners = new ArrayList<>();

        if (ownersNames != null && ownersNames.has("value")) {
            Object value = ownersNames.get("value");
            if (value != null) {
                if (value instanceof JSONArray) {
                    JSONArray os = (JSONArray) value;
                    if (os != null && os.length() > 0) {
                        for (int oi = 0; oi < os.length(); oi++) {
                            owners.add(os.getString(oi));
                        }
                    }
                } else if (value instanceof String) {
                    owners.add(value.toString());
                }
            }

        }

        for (String owner : owners) {
            Long userId = context.getPPMUserId(owner);
            if (userId != null && !ownersResourceIds.contains(userId)) {
                ownersResourceIds.add(userId);
            }
        }
    }

    protected List<ExternalTaskActuals> generateActuals(double scheduledEffort, double actualEffort, Double remainingEffort, double percentComplete, Date actualStart) {

        List<ExternalTaskActuals> actuals = new ArrayList<>();

        List<Long> resourcesIds = new ArrayList<>(ownersResourceIds);
        if (resourcesIds.isEmpty()) {
            // Nulls are not allowed
            resourcesIds.add(-1L);
        }

        for (Long resourceId : resourcesIds) {
            ExternalTaskActuals actual = new ExternalTaskActuals() {
                @Override
                public double getScheduledEffort() {
                    return scheduledEffort;
                }

                @Override
                public Date getActualStart() {
                    return actualStart;
                }

                @Override
                public double getActualEffort() {
                    return actualEffort;
                }

                @Override
                public double getPercentComplete() {
                    return percentComplete;
                }

                @Override
                public long getResourceId() {
                    return resourceId;
                }

                @Override
                public Double getEstimatedRemainingEffort() {
                    return remainingEffort;
                }
            };
            actuals.add(actual);
        }
        return actuals;
    }
}
