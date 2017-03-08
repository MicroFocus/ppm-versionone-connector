
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.wink.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneActualForTimesheet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneStory;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneTimebox;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;

public class VersionOneService {
    private final Logger logger = Logger.getLogger(this.getClass());

    private String baseUri;

    private RestWrapper wrapper;

    public VersionOneService() {
    }

    public VersionOneService(String baseUri, RestWrapper wrapper) {
        this.baseUri = baseUri;
        this.wrapper = wrapper;
    }

    public VersionOneService(String baseUri) {
        this.baseUri = baseUri;
    }

    public RestWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(RestWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public List<VersionOneScope> getProjects() {
        ClientResponse response = wrapper.sendGet(baseUri + VersionOneConstants.PROJECT_SUFFIX);

        String jsonStr = response.getEntity(String.class);

        List<VersionOneScope> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("Assets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                VersionOneScope project = new VersionOneScope(
                        obj.getJSONObject("Attributes").getJSONObject("Name").getString("value"), obj.getString("id"));
                list.add(project);

            }
        } catch (JSONException e) {
            logger.error("", e);
        }
        return list;
    }

    public List<VersionOneTimebox> getTimeboxes(String scopeId) {
        Map<VersionOneTimebox, List<VersionOneStory>> iterations = getStoriesWithTimebox(scopeId);
        List<VersionOneTimebox> iterationz = new ArrayList<>();

        Set<VersionOneTimebox> set = iterations.keySet();

        for (VersionOneTimebox vt : set) {
            vt.setStories(iterations.get(vt));
            iterationz.add(vt);
        }

        return iterationz;
    }

    public Map<String, Map<String, Long>> getTimeSheet(String startDate, String endDate, String scopeId,
            String username)
    {
        List<VersionOneActualForTimesheet> list = getActuals(startDate, endDate, scopeId, username);
        Map<String, Map<String, Long>> map = new HashMap<>();
        for (VersionOneActualForTimesheet vaft : list) {
            String parentName = vaft.getParentName();
            if (map.containsKey(parentName)) {
                Map<String, Long> dateEffort = map.get(parentName);
                String date = vaft.getDate();
                if (dateEffort.containsKey(date)) {
                    dateEffort.put(date, dateEffort.get(date) + vaft.getValue());

                } else {
                    dateEffort.put(date, vaft.getValue());

                }
            } else {
                Map<String, Long> dateEffort = new HashMap<>();
                dateEffort.put(vaft.getDate(), vaft.getValue());
                map.put(vaft.getParentName(), dateEffort);
            }
        }

        return map;
    }

    private List<VersionOneActualForTimesheet> getActuals(String startDate, String endDate, String scopeId,
            String username)
    {
        List<VersionOneActualForTimesheet> list = new ArrayList<>();
        String dest = baseUri + VersionOneConstants.ACTUALS_SUFFIX;
        String dateRequestParameter =
                "&where=Date>='" + startDate + "';Date<='" + endDate + "';Member.Username='" + username + "'";
        String projectRequestParameter = "";
        if (!"".equals(scopeId)) {
            projectRequestParameter = VersionOneConstants.SPECIFIC_PROJECT_SUFFIX + scopeId;
        }

        ClientResponse response = wrapper.sendGet(encodeUrl(dest + dateRequestParameter + projectRequestParameter));
        String jsonStr = response.getEntity(String.class);
        try {

            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("Assets");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject actual = jsonArray.getJSONObject(i).getJSONObject("Attributes");
                String parentName = actual.getJSONObject("Workitem.Parent.Name").getString("value");
                Long value = actual.getJSONObject("Value").getLong("value");
                String date = actual.getJSONObject("Date").getString("value");
                VersionOneActualForTimesheet vaft = new VersionOneActualForTimesheet(parentName, "", value, date);
                list.add(vaft);

            }

        } catch (JSONException e) {
            logger.error("", e);
        }

        return list;
    }

    private String encodeUrl(String url) {
        return url.replaceAll(" ", "%20").replaceAll(">", "%3E").replaceAll("<", "%3C").replaceAll("@", "%40");
    }

    private Map<VersionOneTimebox, List<VersionOneStory>> getStoriesWithTimebox(String scopeId) {
        Map<VersionOneTimebox, List<VersionOneStory>> iterations = new HashMap<>();
        ClientResponse response = wrapper.sendGet(baseUri + VersionOneConstants.STORIES_WITH_TIMEBOX_SUFFIX + scopeId);

        String jsonStr = response.getEntity(String.class);
        try {

            JSONObject jsonObj = new JSONObject(jsonStr);

            JSONArray jsonAssets = jsonObj.getJSONArray("Assets");
            for (int i = 0; i < jsonAssets.length(); i++) {
                JSONObject asset = jsonAssets.getJSONObject(i);
                JSONObject attributes = asset.getJSONObject("Attributes");
                String name = attributes.getJSONObject("Name").getString("value");
                String statusName = attributes.getJSONObject("Status.Name").getString("value");
                String beginDate = attributes.getJSONObject("Timebox.BeginDate").getString("value");
                String endDate = attributes.getJSONObject("Timebox.EndDate").getString("value");
                String iterationName = attributes.getJSONObject("Timebox.Name").getString("value");
                String stateCode = attributes.getJSONObject("Timebox.State.Code").getString("value");
                String createDate = attributes.getJSONObject("CreateDate").getString("value");
                String changeDate = attributes.getJSONObject("ChangeDate").getString("value");
                String doneHrs = attributes.getJSONObject("Children.Actuals.Value.@Sum").getString("value");
                String toDoHrs = attributes.getJSONObject("Children.ToDo.@Sum").getString("value");
                String detailEstimateHrs = attributes.getJSONObject("Children.DetailEstimate.@Sum").getString("value");

                VersionOneTimebox timebox = new VersionOneTimebox(iterationName, beginDate, endDate, stateCode);
                VersionOneStory story = new VersionOneStory(name, beginDate, endDate, statusName, createDate,
                        changeDate, detailEstimateHrs, doneHrs, toDoHrs);
                if (iterations.containsKey(timebox)) {
                    iterations.get(timebox).add(story);
                } else {
                    List<VersionOneStory> stories = new ArrayList<>();
                    stories.add(story);
                    iterations.put(timebox, stories);
                }

            }

        } catch (JSONException e) {
            logger.error("", e);
        }
        return iterations;
    }
}
