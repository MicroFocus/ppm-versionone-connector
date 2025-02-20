
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.*;


import com.kintana.core.util.StringUtils;
import com.mercury.itg.core.ContextProviderImpl;
import com.ppm.integration.IntegrationException;
import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.*;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
import com.ppm.integration.agilesdk.provider.Providers;
import org.apache.log4j.Logger;
import org.apache.wink.client.ClientResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public List<VersionOneScope> getProjectsForCurrentPpmUser(ValueSet values) {
        String currentUserEmail = getPpmCurrentUserEmail();

        if (StringUtils.isNullOrEmptyOrBlank(currentUserEmail)) {
            String missingEmailMessage = values.get(VersionOneConstants.KEY_MISSING_EMAIL_MESSAGE);
            if (StringUtils.isNullOrEmptyOrBlank(missingEmailMessage)) {
                throwExceptionWithUserVisibleErrorMessage(true, "ERROR_EMAIL_NOT_CAPTURED_IN_PPM");
            } else {
                throwExceptionWithUserVisibleErrorMessage(false, missingEmailMessage);
            }
        }

        String url = (baseUri + VersionOneConstants.MEMBER_SCOPES_SUFFIX).replace("%EMAIL%", currentUserEmail);

        ClientResponse response = wrapper.sendGet(url);

        String jsonStr = response.getEntity(String.class);

        List<VersionOneScope> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("Assets");

            if (jsonArray.length() == 0) {
                // User email is not a match
                throw new RuntimeException("No user found in DAI Agility with email address "+currentUserEmail);
            }
            if (jsonArray.length() > 1) {
                // Multiple users match
                throw new RuntimeException("More than one user was found in DAI Agility with email address "+currentUserEmail);
            }


            JSONObject obj = jsonArray.getJSONObject(0).getJSONObject("Attributes");

            JSONArray scopes = obj.getJSONObject("Scopes").getJSONArray("value");
            JSONArray scopesNames = obj.getJSONObject("Scopes.Name").getJSONArray("value");

            if (scopes.length() != scopesNames.length()) {
                throwExceptionWithUserVisibleErrorMessage(true, "ERROR_SCOPE_ID_NAME_DIFF");
            }

            for (int i = 0 ; i < scopes.length() ; i++) {
                String id = scopes.getJSONObject(i).getString("idref");
                String name = scopesNames.getString(i);
                VersionOneScope project = new VersionOneScope(name, id);
                list.add(project);
            }

        } catch (JSONException e) {
            logger.error("", e);
            throwExceptionWithUserVisibleErrorMessage(true, "ERROR_UNEXPECTED");
        }

        list.sort(new Comparator<VersionOneScope>() {
            @Override
            public int compare(VersionOneScope o1, VersionOneScope o2) {
                return o1.getScopeName().compareToIgnoreCase(o2.getScopeName());
            }
        });

        return list;
    }

    private void throwExceptionWithUserVisibleErrorMessage(boolean resolveMessage, String messageKey, String...params) {
        String message = messageKey;
        if (resolveMessage) {
            message = Providers.getLocalizationProvider(VersionOneIntegrationConnector.class).getConnectorText(messageKey, params);
        }

        throw IntegrationException.build(VersionOneIntegrationConnector.class).setMessage(message);
    }

    private String getPpmCurrentUserEmail() {
        com.mercury.itg.core.user.model.User user = new ContextProviderImpl().getCurrentUser();
        if (user != null) {
                String username = user.getUsername();
                if (username != null && username.contains("@") && username.contains(".")) {
                    // Username should be an email address - this is much more reliable than reading from the email user field since this cannot be easily tempered with.
                    return username;
                }
                return user.getEmailAddress();
        }

        return null;
    }

    public List<VersionOneTimebox> getTimeboxes(VersionOneWorkPlanIntegration.TaskCreationContext context, String scopeId, boolean includeClosedSprints, boolean includeStoriesInNoSprint) {

        Map<VersionOneTimebox, List<VersionOneWorkItem>> iterations = getStoriesPerTimebox(context, scopeId, includeClosedSprints, includeStoriesInNoSprint);

        List<VersionOneTimebox> timeboxes = new ArrayList<>();

        for (Map.Entry<VersionOneTimebox, List<VersionOneWorkItem>> entry : iterations.entrySet()) {

            VersionOneTimebox timebox = entry.getKey();
            if (timebox == null) {
                timebox = new VersionOneTimebox(null, Providers.getLocalizationProvider(VersionOneIntegrationConnector.class).getConnectorText("NO_ITERATION_DEFINED_TASK_NAME"), null, null, "FUTR");
            }

            timebox.setStories(entry.getValue());
            timeboxes.add(timebox);
        }

        return timeboxes;
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

    private Map<VersionOneTimebox, List<VersionOneWorkItem>> getStoriesPerTimebox(VersionOneWorkPlanIntegration.TaskCreationContext context, String scopeId, boolean includeClosedSprints, boolean includeStoriesInNoSprint) {
        Map<VersionOneTimebox, List<VersionOneWorkItem>> storiesPerTimebox = new HashMap<>();
        String getString = baseUri + VersionOneConstants.STORIES_WITH_TIMEBOX_SUFFIX + "%22"+scopeId+"%22";
        if (!includeClosedSprints) {
            getString += ";Timebox.State.Code!='CLSD'";
        }
        if (!includeStoriesInNoSprint) {
            getString += ";Timebox.ID!=''";
        }
        ClientResponse response = wrapper.sendGet(getString);

        String jsonStr = response.getEntity(String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("<=== Received JSon: "+jsonStr);
        }

        try {

            JSONObject jsonObj = new JSONObject(jsonStr);

            JSONArray jsonAssets = jsonObj.getJSONArray("Assets");
            for (int i = 0; i < jsonAssets.length(); i++) {
                JSONObject asset = jsonAssets.getJSONObject(i);
                JSONObject attributes = asset.getJSONObject("Attributes");
                String name = attributes.getJSONObject("Name").getString("value");
                String id = asset.getString("id");
                String statusName = attributes.getJSONObject("Status.Name").getString("value");

                String beginDate = attributes.getJSONObject("Timebox.BeginDate").getString("value");
                String endDate = attributes.getJSONObject("Timebox.EndDate").getString("value");

                String createDate = attributes.getJSONObject("CreateDate").getString("value");
                String doneHrs = attributes.getJSONObject("Children.Actuals.Value.@Sum").getString("value");
                String toDoHrs = attributes.getJSONObject("Children.ToDo.@Sum").getString("value");
                String detailEstimateHrs = attributes.getJSONObject("Children.DetailEstimate.@Sum").getString("value");

                Object timeboxIdValue = attributes.getJSONObject("Timebox.ID").get("value");

                VersionOneTimebox timebox = null;
                if (timeboxIdValue != null && timeboxIdValue instanceof JSONObject) {
                    String timeboxId = ((JSONObject)timeboxIdValue).getString("idref");

                    String iterationName = attributes.getJSONObject("Timebox.Name").getString("value");
                    String stateCode = attributes.getJSONObject("Timebox.State.Code").getString("value");

                    timebox = new VersionOneTimebox(timeboxId, iterationName, beginDate, endDate, stateCode);
                }

                VersionOneStory story = new VersionOneStory(id, name, beginDate, endDate, statusName, createDate,
                        detailEstimateHrs, doneHrs, toDoHrs, context);
                story.setOwnersNames(attributes.getJSONObject("Owners.Name"));
                if (storiesPerTimebox.containsKey(timebox)) {
                    storiesPerTimebox.get(timebox).add(story);
                    if (logger.isDebugEnabled()) {
                        logger.debug("++ Adding new story to timebox ID " + timeboxIdValue + ": "+story.toString());
                    }
                } else {
                    List<VersionOneWorkItem> stories = new ArrayList<>();
                    stories.add(story);
                    storiesPerTimebox.put(timebox, stories);
                    if (logger.isDebugEnabled()) {
                        if (timebox != null) {
                            logger.debug("+ Adding new Timebox:" + timebox.toString());
                        }
                        logger.debug("++ Adding new story to Timebox ID " + timeboxIdValue + ": "+story.toString());
                    }
                }
            }

        } catch (JSONException e) {
            logger.error("", e);
        }


        // Following corrections are here just in case query string isn't working properly.
        /*if (!includeStoriesInNoSprint) {
            storiesPerTimebox.remove(null);
        }
        if (!includeClosedSprints) {
            List<VersionOneTimebox> timeboxes = new ArrayList<>(storiesPerTimebox.keySet());
            for (VersionOneTimebox timebox : timeboxes) {
                if (VersionOneConstants.TIMEBOX_STATUS_CLOSED.equalsIgnoreCase(timebox.getStateCode())) {
                    storiesPerTimebox.remove(timebox);
                }
            }
        }*/

        return storiesPerTimebox;
    }

    public static VersionOneService fromValueSet(ValueSet values) {

        VersionOneService service = new VersionOneService();

        String proxyHost = values.get(VersionOneConstants.KEY_PROXY_HOST);
        String proxyPort = values.get(VersionOneConstants.KEY_PROXY_PORT);

        String apiKey = values.get(VersionOneConstants.KEY_ADMIN_API_TOKEN);
        String baseUri = values.get(VersionOneConstants.KEY_BASE_URL);

        service = (service == null ? new VersionOneService() : service);
        IRestConfig config = new VersionOneRestConfig();
        config.setProxy(proxyHost, proxyPort);
        config.setBearerToken(apiKey);
        RestWrapper wrapper = new RestWrapper(config);
        service.setBaseUri(baseUri);
        service.setWrapper(wrapper);

        return service;
    }

    public List<VersionOneRequest> importRequestEntities(VersionOneWorkPlanIntegration.TaskCreationContext taskContext, String requestTypeName, String scopeId, ValueSet values) {
        List<VersionOneRequest> requests = new ArrayList<>();
        String getString = baseUri + VersionOneConstants.REQUESTS_SUFFIX + "%22"+ requestTypeName + "%22;Scope=%22"+scopeId+"%22";
        ClientResponse response = wrapper.sendGet(getString);

        String jsonStr = response.getEntity(String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("<=== Received JSon: "+jsonStr);
        }

        try {

            JSONObject jsonObj = new JSONObject(jsonStr);

            JSONArray jsonAssets = jsonObj.getJSONArray("Assets");
            for (int i = 0; i < jsonAssets.length(); i++) {
                JSONObject asset = jsonAssets.getJSONObject(i);
                JSONObject attributes = asset.getJSONObject("Attributes");
                String name = attributes.getJSONObject("Name").getString("value");
                String id = asset.getString("id");
                String statusName = attributes.getJSONObject("Status.Name").getString("value");
                String createDate = attributes.getJSONObject("CreateDate").getString("value");

                String neededByDate = attributes.has("Custom_NeededbyDate") ? attributes.getJSONObject("Custom_NeededbyDate").getString("value") : null;

                VersionOneRequest request = new VersionOneRequest(id, name, statusName, createDate, neededByDate, taskContext);
                request.setOwnersNames(attributes.getJSONObject("Owner.Name"));
                requests.add(request);
            }

        } catch (JSONException e) {
            logger.error("", e);
        }

        return requests;
    }

    public List<VersionOneEpic> importEpicEntities(VersionOneWorkPlanIntegration.TaskCreationContext taskContext, String epicTypesName, String scopeId, ValueSet values) {
        List<VersionOneEpic> epics = new ArrayList<>();
        String getString = baseUri + VersionOneConstants.EPICS_SUFFIX + "%22"+ epicTypesName + "%22;Scope=%22"+scopeId+"%22";
        ClientResponse response = wrapper.sendGet(getString);

        String jsonStr = response.getEntity(String.class);

        if (logger.isDebugEnabled()) {
            logger.debug("<=== Received JSon: "+jsonStr);
        }

        try {

            JSONObject jsonObj = new JSONObject(jsonStr);

            JSONArray jsonAssets = jsonObj.getJSONArray("Assets");
            for (int i = 0; i < jsonAssets.length(); i++) {
                JSONObject asset = jsonAssets.getJSONObject(i);
                JSONObject attributes = asset.getJSONObject("Attributes");
                String name = attributes.getJSONObject("Name").getString("value");
                String id = asset.getString("id");
                String statusName = attributes.getJSONObject("Status.Name").getString("value");
                String createDate = attributes.getJSONObject("CreateDate").getString("value");

                String plannedStart = attributes.has("PlannedStart") ? attributes.getJSONObject("PlannedStart").getString("value") : null;
                String plannedEnd = attributes.has("PlannedEnd") ? attributes.getJSONObject("PlannedEnd").getString("value") : null;

                VersionOneEpic epic = new VersionOneEpic(id, name, statusName, createDate, plannedStart, plannedEnd, taskContext);
                epic.setOwnersNames(attributes.getJSONObject("Owners.Name"));
                epics.add(epic);
            }

        } catch (JSONException e) {
            logger.error("", e);
        }

        return epics;
    }
}
