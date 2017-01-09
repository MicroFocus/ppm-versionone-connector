package com.ppm.integration.agilesdk.connector.versionone.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hp.ppm.integration.pm.IExternalTask;
import com.hp.ppm.integration.pm.ITaskActual;

public class VersionOneEntity implements IExternalTask {

	@Override
	public List<ITaskActual> getActuals() {

		return null;
	}

	@Override
	public List<IExternalTask> getChildren() {

		return null;
	}

	@Override
	public String getId() {

		return null;
	}

	@Override
	public String getName() {

		return null;
	}

	@Override
	public long getOwnerId() {

		return 0;
	}

	@Override
	public String getOwnerRole() {

		return null;
	}

	@Override
	public Date getScheduleFinish() {

		return null;
	}

	@Override
	public Date getScheduleStart() {

		return null;
	}

	@Override
	public TaskStatus getStatus() {

		return null;
	}

	@Override
	public boolean isMilestone() {

		return false;
	}

	Date toDate(String d) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(d);
		} catch (ParseException e) {
			
		}
		return date;
	}
}
