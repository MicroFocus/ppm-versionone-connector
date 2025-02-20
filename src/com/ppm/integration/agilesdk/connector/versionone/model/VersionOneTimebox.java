
package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.pm.ExternalTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VersionOneTimebox extends VersionOneEntity {
    private String timeboxId;

    private String timeboxName;

    private String beginDate;

    private String endDate;

    private String stateCode;

    private List<VersionOneWorkItem> stories;

    public VersionOneTimebox(String timeboxId, String timeboxName, String beginDate, String endDate, String stateCode) {
        this.timeboxId = timeboxId;
        this.timeboxName = timeboxName;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.stateCode = stateCode;
    }

    public String getTimeboxName() {
        return timeboxName;
    }

    public void setTimeboxName(String timeboxName) {
        this.timeboxName = timeboxName;
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

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public List<VersionOneWorkItem> getStories() {
        return stories;
    }

    public void setStories(List<VersionOneWorkItem> stories) {
        this.stories = stories;
    }

    @Override
    public int hashCode() {
        return timeboxId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VersionOneTimebox other = (VersionOneTimebox)obj;
        return this.timeboxId.equalsIgnoreCase(other.getTimeboxId());
    }


    @Override
    public String getName() {
        return this.timeboxName;
    }

    @Override
    public String getId() {
        return timeboxId;
    }

    @Override
    public Date getScheduledFinish() {
        return toDate(this.endDate, true);
    }

    @Override
    public Date getScheduledStart() {

        return toDate(this.beginDate);
    }

    @Override
    public TaskStatus getStatus() {

        return getTaskStatus(this.stateCode);
    }

    @Override
    public List<ExternalTask> getChildren() {
        List<ExternalTask> list = new ArrayList<>();
        for (VersionOneWorkItem s : stories) {
            list.add(s);
        }

        return list;
    }

    private TaskStatus getTaskStatus(String status) {

        switch (status) {
            case "CLSD":
                return TaskStatus.COMPLETED;
            case "FUTR":
                return TaskStatus.READY;
            case "ACTV":
                return TaskStatus.ACTIVE;
            default:
                return TaskStatus.UNKNOWN;
        }
    }

    public String getTimeboxId() {
        return timeboxId;
    }

    public void setTimeboxId(String timeboxId) {
        this.timeboxId = timeboxId;
    }

    @Override
    public String toString() {
        return "VersionOneTimebox{" +
                "timeboxId='" + timeboxId + '\'' +
                ", timeboxName='" + timeboxName + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", stories=" + stories +
                '}';
    }
}
