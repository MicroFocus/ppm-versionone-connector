
package com.ppm.integration.agilesdk.connector.versionone.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.ppm.integration.agilesdk.pm.ExternalTaskActuals;



public class VersionOneStory extends VersionOneEntity {
    private String storyName;

    private String beginDate;

    private String endDate;

    private String statusName;

    private String createDate;

    private String changeDate;

    private String detailEstimateHrs;

    private String doneHrs;

    private String toDoHrs;

    public VersionOneStory(String storyName, String beginDate, String endDate, String statusName, String createDate,
            String changeDate, String detailEstimateHrs, String doneHrs, String toDoHrs) {
        this.storyName = storyName;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.statusName = statusName;
        this.createDate = createDate;
        this.changeDate = changeDate;
        this.detailEstimateHrs = detailEstimateHrs;
        this.doneHrs = doneHrs;
        this.toDoHrs = toDoHrs;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
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

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
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
    public String getName() {
        return this.storyName;
    }

    @Override
    public Date getScheduledFinish() {
        return toDate(this.endDate);
    }

    @Override
    public Date getScheduledStart() {
        if (beginDate.compareTo(createDate) < 0) {
            return toDate(this.createDate);
        }
        return toDate(this.beginDate);
    }

    @Override
    public List<ExternalTaskActuals> getActuals() {
        ExternalTaskActuals etl = new ExternalTaskActuals() {
            @Override
            public double getScheduledEffort() {
                if ("null".equals(detailEstimateHrs)) {
                    return 0.0;
                }
                return Double.parseDouble(detailEstimateHrs);
            }

            @Override
            public Date getActualFinish() {
                return super.getActualFinish();
            }

            @Override
            public Date getActualStart() {
                return (!"null".equals(doneHrs)) && Double.parseDouble(doneHrs) > 0 ? toDate(beginDate) : null;
            }

            @Override
            public Date getEstimatedFinishDate() {
                return super.getEstimatedFinishDate();
            }

            @Override
            public Double getEstimatedRemainingEffort() {
                if ("null".equals(toDoHrs)) {
                    return 0.0;
                }
                return Double.parseDouble(toDoHrs);
            }

            @Override
            public double getPercentComplete() {
                try {
                    double done = Double.parseDouble(doneHrs);
                    double todo = Double.parseDouble(toDoHrs);
                    return done / (done + todo) * 100;
                } catch (Exception e) {
                    return 0.0;
                }
            }

        };
        return Arrays.asList(new ExternalTaskActuals[] {etl});
    }

    @Override
    public TaskStatus getStatus() {

        return getTaskStatus(this.statusName);
    }

    private TaskStatus getTaskStatus(String status) {

        switch (status) {
            case "Future":
                return TaskStatus.IN_PLANNING;
            case "In Progress":
                return TaskStatus.IN_PROGRESS;
            case "Done":
                return TaskStatus.COMPLETED;
            case "Accepted":
                return TaskStatus.COMPLETED;
            default:
                return TaskStatus.UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return "VersionOneStory{" +
                "storyName='" + storyName + '\'' +
                ", beginDate='" + beginDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", statusName='" + statusName + '\'' +
                ", createDate='" + createDate + '\'' +
                ", changeDate='" + changeDate + '\'' +
                ", detailEstimateHrs='" + detailEstimateHrs + '\'' +
                ", doneHrs='" + doneHrs + '\'' +
                ", toDoHrs='" + toDoHrs + '\'' +
                '}';
    }
}
