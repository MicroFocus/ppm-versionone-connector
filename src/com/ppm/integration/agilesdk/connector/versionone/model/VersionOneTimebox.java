
package com.ppm.integration.agilesdk.connector.versionone.model;

import com.ppm.integration.agilesdk.pm.ExternalTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VersionOneTimebox extends VersionOneEntity {
    private String timeboxName;

    private String beginDate;

    private String endDate;

    private String stateCode;

    private List<VersionOneStory> stories;

    public VersionOneTimebox(String timeboxName, String beginDate, String endDate, String stateCode) {

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

    public List<VersionOneStory> getStories() {
        return stories;
    }

    public void setStories(List<VersionOneStory> stories) {
        this.stories = stories;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beginDate == null) ? 0 : beginDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((timeboxName == null) ? 0 : timeboxName.hashCode());
        return result;
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
        if (beginDate == null) {
            if (other.beginDate != null)
                return false;
        } else if (!beginDate.equals(other.beginDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (timeboxName == null) {
            if (other.timeboxName != null)
                return false;
        } else if (!timeboxName.equals(other.timeboxName))
            return false;
        return true;
    }

    @Override
    public String getName() {

        return this.timeboxName;
    }

    @Override
    public Date getScheduledFinish() {

        return toDate(this.endDate);
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
        for (VersionOneStory s : stories) {
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
}
