package com.ppm.integration.agilesdk.connector.versionone.model;

import java.util.Date;

public class VersionOneStory extends VersionOneEntity {
	private String storyName;
	private String beginDate;
	private String endDate;
	private String statusName;

	public VersionOneStory(String storyName, String beginDate, String endDate, String statusName) {
		this.storyName = storyName;
		this.beginDate = beginDate;
		this.endDate = endDate;
		this.statusName = statusName;
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

		return toDate(this.beginDate);
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
			// return TaskStatus.UNKNOWN; the statau unkonwn will case an exceptionn
			return TaskStatus.ON_HOLD;
		}
	}
}
