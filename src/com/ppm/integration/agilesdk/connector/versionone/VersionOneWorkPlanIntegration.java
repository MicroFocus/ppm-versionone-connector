
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.*;
import java.util.stream.Collectors;

import com.hp.ppm.user.model.User;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneEpic;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneRequest;
import com.ppm.integration.agilesdk.provider.Providers;
import com.ppm.integration.agilesdk.provider.UserProvider;
import com.ppm.integration.agilesdk.ui.*;
import org.apache.log4j.Logger;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.commons.lang3.StringUtils;

import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneTimebox;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.RestRequestException;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.VersionOneConnectivityExceptionHandler;
import com.ppm.integration.agilesdk.pm.ExternalTask;
import com.ppm.integration.agilesdk.pm.ExternalWorkPlan;
import com.ppm.integration.agilesdk.pm.WorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.WorkPlanIntegrationContext;

public class VersionOneWorkPlanIntegration extends WorkPlanIntegration {

    private final Logger logger = Logger.getLogger(this.getClass());

    private VersionOneService service;

    public VersionOneWorkPlanIntegration() {

    }

    @Override
    public List<Field> getMappingConfigurationFields(WorkPlanIntegrationContext context, ValueSet values) {

        List<Field> fields = new ArrayList<>(2);

        final boolean alwaysUseAdminToken = values.getBoolean(VersionOneConstants.KEY_ALWAYS_USE_ADMIN_API_TOKEN, false);

        if (!alwaysUseAdminToken) {
            fields.add(new PasswordText(VersionOneConstants.KEY_USER_API_TOKEN, "USER_TOKEN", "", true));
            fields.add(new LineBreaker());
        }

        fields.add(new DynamicDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME, "VERSIONONE_PROJECT",
                                true) {

                            @Override
                            public List<String> getDependencies() {
                                if (alwaysUseAdminToken) {
                                    return new ArrayList<>();
                                } else {
                                    return Arrays.asList(new String[]{VersionOneConstants.KEY_USER_API_TOKEN});
                                }
                            }

                            @Override
                            public List<Option> getDynamicalOptions(ValueSet values) {
                                configureService(values);
                                List<VersionOneScope> list = new ArrayList<>();
                                try {
                                    list = service.getProjectsForCurrentPpmUser(values);
                                } catch (ClientRuntimeException | RestRequestException e) {
                                    logger.error("VersionOne Workplan", e);
                                    new VersionOneConnectivityExceptionHandler().uncaughtException(
                                            Thread.currentThread(), e, VersionOneWorkPlanIntegration.class);
                                } catch (RuntimeException e) {
                                    logger.error("VersionOne Workplan", e);
                                    new VersionOneConnectivityExceptionHandler().uncaughtException(
                                            Thread.currentThread(), e, VersionOneWorkPlanIntegration.class);
                                }

                                List<Option> optionList = new ArrayList<>();
                                for (VersionOneScope project : list) {
                                    Option option = new Option(project.getScopeId(), project.getScopeName());
                                    optionList.add(option);
                                }
                                return optionList;
                            }
                        });

        fields.add(new CheckBox(VersionOneConstants.WP_INCLUDE_CLOSED_SPRINTS, "WP_INCLUDE_CLOSED_SPRINTS", "", false));

        fields.add(new CheckBox(VersionOneConstants.WP_INCLUDE_STORIES_NOT_IN_SPRINT, "WP_INCLUDE_STORIES_NOT_IN_SPRINT", "", false));

        fields.add(new LineBreaker());

        fields.add(new LabelText("", "ENTITIES_IMPORT_OPTIONS", "block", false));

        if (values.getBoolean(VersionOneConstants.KEY_ALLOW_STORIES, true)) {
            fields.add(new CheckBox(VersionOneConstants.KEY_IMPORT_STORIES, "ALLOW_STORIES", true));
        }

        List<String> requestTypeNames = getValues(values.get(VersionOneConstants.KEY_ALLOW_REQUESTS));
        for (String requestTypeName : requestTypeNames) {
            fields.add(new CheckBox(VersionOneConstants.KEY_IMPORT_REQUEST_PREFIX + requestTypeName, requestTypeName, false));
        }

        List<String> epicTypeNames = getValues(values.get(VersionOneConstants.KEY_ALLOW_EPICS));
        for (String epicTypeName : epicTypeNames) {
            fields.add(new CheckBox(VersionOneConstants.KEY_IMPORT_EPIC_PREFIX + epicTypeName, epicTypeName, false));
        }

        return fields;
    }

    private List<String> getValues(String paramValue) {
        List<String> values = new ArrayList<>();

        if (!StringUtils.isBlank(paramValue)) {
            values.addAll(Arrays.stream(StringUtils.split(paramValue,';')).map(String::trim).collect(Collectors.toList()));
        }

        return values;
    }

    @Override
    public ExternalWorkPlan getExternalWorkPlan(WorkPlanIntegrationContext context, ValueSet values) {
        String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
        configureService(values);

        debugValueSet(values);

        TaskCreationContext taskContext = new TaskCreationContext(values);

        List<ExternalTask> externalTasks = new ArrayList<>();

        int entitiesCount = 0;
        if (values.getBoolean(VersionOneConstants.KEY_IMPORT_STORIES, true)) {
            ++entitiesCount;
        }
        List<String> requestTypesNames = getImportNames(values, VersionOneConstants.KEY_IMPORT_REQUEST_PREFIX);
        entitiesCount += requestTypesNames.size();
        List<String> epicTypesNames = getImportNames(values, VersionOneConstants.KEY_IMPORT_EPIC_PREFIX);
        entitiesCount += epicTypesNames.size();

        // Importing Stories
        if (values.getBoolean(VersionOneConstants.KEY_IMPORT_STORIES, true)) {
            final List<VersionOneTimebox> sprints = service.getTimeboxes(taskContext, scopeId, values.getBoolean(VersionOneConstants.WP_INCLUDE_CLOSED_SPRINTS, false), values.getBoolean(VersionOneConstants.WP_INCLUDE_STORIES_NOT_IN_SPRINT, false));
            if (entitiesCount <= 1) {
                externalTasks.addAll(sprints);
            } else {
                externalTasks.add(getWrappingTask(Providers.getLocalizationProvider(VersionOneIntegrationConnector.class).getConnectorText("ALLOW_STORIES"), sprints));
            }
        }

        // Importing Requests Entities

        for (String requestTypesName : requestTypesNames) {
            final List<VersionOneRequest> requests = service.importRequestEntities(taskContext, requestTypesName, scopeId, values);
            if (entitiesCount <= 1) {
                externalTasks.addAll(requests);
            } else {
                externalTasks.add(getWrappingTask(requestTypesName + "s", requests));
            }
        }

        // Importing Epic Entities

        for (String epicTypesName : epicTypesNames) {
            final List<VersionOneEpic> epics = service.importEpicEntities(taskContext, epicTypesName, scopeId, values);
            if (entitiesCount <= 1) {
                externalTasks.addAll(epics);
            } else {
                externalTasks.add(getWrappingTask(epicTypesName + "s", epics));
            }
        }

        return new ExternalWorkPlan() {
            @Override
            public List<ExternalTask> getRootTasks() {
                return externalTasks;
            }
        };
    }

    private ExternalTask getWrappingTask(String wrappingTaskName, List<? extends ExternalTask> tasks) {
        return new ExternalTask() {
            @Override
            public String getName() {
                return wrappingTaskName;
            }

            @Override
            public List<ExternalTask> getChildren() {
                return (List<ExternalTask>) tasks;
            }
        };
    }

    private List<String> getImportNames(ValueSet values, String prefix) {

        List<String> names = new ArrayList<>();

        if (values == null || prefix == null) {
            return names;
        }

        for (String key : values.keySet()) {
            if (key.startsWith(prefix)) {
                if (values.getBoolean(key, false)) {
                    String name = key.substring(prefix.length());
                    if (!StringUtils.isBlank(name)) {
                        names.add(name.trim());
                    }
                }
            }
        }
        return names;
    }

    private void debugValueSet(ValueSet values) {
        if (logger.isDebugEnabled()) {
            logger.debug("### Starting work plan sync. List of Value Set (except API Token:");
            for (String key : values.keySet()) {
                if (!VersionOneConstants.KEY_ADMIN_API_TOKEN.equals(key) && !VersionOneConstants.KEY_USER_API_TOKEN.equals(key)) {
                    logger.debug("#   "+key+" : "+values.get(key));
                }
            }
        }
    }

    private void configureService(ValueSet values)
    {
        String proxyHost = values.get(VersionOneConstants.KEY_PROXY_HOST);
        String proxyPort = values.get(VersionOneConstants.KEY_PROXY_PORT);

        String apiKey = values.get(VersionOneConstants.KEY_ADMIN_API_TOKEN);
        if (!values.getBoolean(VersionOneConstants.KEY_ALWAYS_USE_ADMIN_API_TOKEN, false)) {
            apiKey = values.get(VersionOneConstants.KEY_USER_API_TOKEN);
        }

        String baseUri = values.get(VersionOneConstants.KEY_BASE_URL);

        service = (service == null ? new VersionOneService() : service);
        IRestConfig config = new VersionOneRestConfig();
        config.setProxy(proxyHost, proxyPort);
        config.setBearerToken(apiKey);
        RestWrapper wrapper = new RestWrapper(config);
        service.setBaseUri(baseUri);
        service.setWrapper(wrapper);
    }

    @Override
    public boolean supportTimesheetingAgainstExternalWorkPlan() {
        return true;
    }

    public class TaskCreationContext {

        private Map<String, ExternalTask.TaskStatus> daiStatusToPPMStatus = new HashMap<>();

        private UserProvider userProvider = null;

        public boolean importActualEffort = false;
        public TaskCreationContext(ValueSet config) {
            addStatus(ExternalTask.TaskStatus.READY, VersionOneConstants.KEY_TASK_STATUS_READY, config);
            addStatus(ExternalTask.TaskStatus.IN_PROGRESS, VersionOneConstants.KEY_TASK_STATUS_IN_PROGRESS, config);
            addStatus(ExternalTask.TaskStatus.COMPLETED, VersionOneConstants.KEY_TASK_STATUS_COMPLETED, config);
            addStatus(ExternalTask.TaskStatus.CANCELLED, VersionOneConstants.KEY_TASK_STATUS_CANCELLED, config);
            addStatus(ExternalTask.TaskStatus.UNKNOWN, VersionOneConstants.KEY_TASK_STATUS_UNKNOWN, config);
            importActualEffort = config.getBoolean(VersionOneConstants.KEY_IMPORT_ACTUAL_EFFORT, false);
        }

        public ExternalTask.TaskStatus getPpmStatus(String daiStatusName) {
            ExternalTask.TaskStatus status = daiStatusToPPMStatus.get(daiStatusName.trim().toUpperCase());
            if (status != null) {
                return status;
            }
            return ExternalTask.TaskStatus.UNKNOWN;
        }

        private void addStatus(ExternalTask.TaskStatus ppmStatus, String configKeyName, ValueSet config) {
            String daiStatuses = config.get(configKeyName);

            if (!StringUtils.isBlank(daiStatuses)) {
                for (String daiStatus : StringUtils.split(daiStatuses, ';')) {
                    daiStatusToPPMStatus.put(daiStatus.trim().toUpperCase(), ppmStatus);
                }
            }
        }

        public Long getPPMUserId(String owner) {
            if (userProvider == null) {
                userProvider = Providers.getUserProvider(VersionOneIntegrationConnector.class);
            }
            User u = userProvider.getByUsername(owner);
            if (u == null) {
                u = userProvider.getByEmail(owner);
            }
            return u == null ? null : u.getUserId();
        }
    }

}
