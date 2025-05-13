
package com.ppm.integration.agilesdk.connector.versionone;

import com.hp.ppm.user.model.User;
import com.mercury.itg.util.HibernateTemplate;
import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneEpic;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
import com.ppm.integration.agilesdk.pm.ExternalTask;
import com.ppm.integration.agilesdk.pm.ExternalWorkPlan;
import com.ppm.integration.agilesdk.pm.WorkPlanIntegration;
import com.ppm.integration.agilesdk.pm.WorkPlanIntegrationContext;
import com.ppm.integration.agilesdk.provider.Providers;
import com.ppm.integration.agilesdk.provider.UserProvider;
import com.ppm.integration.agilesdk.ui.CheckBox;
import com.ppm.integration.agilesdk.ui.Field;
import com.ppm.integration.agilesdk.ui.LabelText;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;

import java.util.*;
import java.util.stream.Collectors;

import static com.ppm.integration.agilesdk.connector.versionone.VersionOneConstants.*;

public class VersionOneWorkPlanIntegration extends WorkPlanIntegration {

    private final Logger logger = Logger.getLogger(this.getClass());

    private VersionOneService service;

    public VersionOneWorkPlanIntegration() {

    }

    @Override
    public List<Field> getMappingConfigurationFields(WorkPlanIntegrationContext context, ValueSet values) {

        // First, let's make sure that this project has WBSid defined:
        String wbsID = getWBSId(context, values);

        List<Field> fields = new ArrayList<>(2);

        if (StringUtils.isBlank(wbsID)) {
            // MISSING WBSID!
            fields.add(new LabelText("MISSING_WBSID", "MISSING_WBSID", "", false));
        } else {
            // WBSID is found, not problem.
            fields.add(new LabelText("WBSID_FOUND", "WBSid for this Project:"+wbsID, "", false));
        }

        return fields;
    }

    private String getWBSId(WorkPlanIntegrationContext context, final ValueSet values) {

        // Calling context.currentProject() will fail when called from the work plan - so we must go through current task & work plan ID.
        final long workplanId = context.currentTask().getWorkplanId();
        HibernateTemplate wp = new HibernateTemplate() {

            @Override
            public void run() throws Exception {
                NativeQuery query = getSession().createNativeQuery("select pfm_request_id from pm_projects where project_id = (select project_id from pm_work_plans where work_plan_id = :workplanId)");
                query.setParameter("workplanId", workplanId);
                query.addScalar("pfm_request_id", StandardBasicTypes.LONG);
                setResult(query.uniqueResult());
            }
        };
        wp.doRun();


        final Long requestId = (Long) wp.getResult();

        HibernateTemplate t = new HibernateTemplate() {
            @Override
            public void run() throws Exception {
                String tableName = PPM_REQUEST_FIELD_TYPE_REQUEST_DETAILS.equals(values.get(KEY_PPM_REQUEST_FIELD_TYPE)) ? "KCRT_REQUEST_DETAILS" : "KCRT_REQ_HEADER_DETAILS";
                String columnName = PPM_REQUEST_FIELD_PARAMETER_TYPE_PARAMETER.equals(values.get(KEY_PPM_REQUEST_FIELD_PARAMETER_TYPE)) ? "PARAMETER" : "VISIBLE_PARAMETER";
                Integer batchNumber = StringUtils.isNumeric(values.get(KEY_PPM_REQUEST_FIELD_BATCH)) ? Integer.parseInt(values.get(KEY_PPM_REQUEST_FIELD_BATCH)) : 1;
                Integer columnNumber = StringUtils.isNumeric(values.get(KEY_PPM_REQUEST_FIELD_COLUMN)) ? Integer.parseInt(values.get(KEY_PPM_REQUEST_FIELD_COLUMN)) : 1;
                // Read Project Details fields that supposedly stores wbsID
                NativeQuery query = getSession().createNativeQuery("SELECT "+columnName+columnNumber + " FROM "+tableName+ " WHERE REQUEST_ID = :requestId and BATCH_NUMBER = :batchNumber");
                query.setParameter("requestId", requestId);
                query.setParameter("batchNumber", batchNumber);
                query.addScalar(columnName+columnNumber, StandardBasicTypes.STRING);
                setResult(query.uniqueResult());
            }
        };
        t.doRun();
        return (String)t.getResult();


    }

    private List<String> getValues(String paramValue) {
        List<String> values = new ArrayList<>();

        if (!StringUtils.isBlank(paramValue)) {
            values.addAll(Arrays.stream(StringUtils.split(paramValue, ';')).map(String::trim).collect(Collectors.toList()));
        }

        return values;
    }

    @Override
    public ExternalWorkPlan getExternalWorkPlan(WorkPlanIntegrationContext context, ValueSet values) {

        String wbsID = getWBSId(context, values);

        configureService(values);

        debugValueSet(values);

        TaskCreationContext taskContext = new TaskCreationContext(values);

        List<ExternalTask> externalTasks = new ArrayList<>();

        List<String> subTypesNames = getImportNames(values, VersionOneConstants.KEY_PICK_RESOURCES_FROM_THESE_SUB_TYPES);
        if (subTypesNames == null || subTypesNames.isEmpty()) {
            subTypesNames = new ArrayList<>();
            subTypesNames.add("Story");
            subTypesNames.add("Defect");
        }

        // Importing Epic Entities
        final List<VersionOneEpic> epics = service.importEpicFeaturesEntities(taskContext, wbsID, subTypesNames, values);
        externalTasks.addAll(epics);

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
                if (!VersionOneConstants.KEY_ADMIN_API_TOKEN.equals(key)) {
                    logger.debug("#   " + key + " : " + values.get(key));
                }
            }
        }
    }

    private void configureService(ValueSet values) {
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
    }

    @Override
    public boolean supportTimesheetingAgainstExternalWorkPlan() {
        return true;
    }

    public class TaskCreationContext {

        private Map<String, ExternalTask.TaskStatus> daiStatusToPPMStatus = new HashMap<>();

        private UserProvider userProvider = null;

        public TaskCreationContext(ValueSet config) {
            addStatus(ExternalTask.TaskStatus.READY, VersionOneConstants.KEY_TASK_STATUS_READY, config);
            addStatus(ExternalTask.TaskStatus.IN_PROGRESS, VersionOneConstants.KEY_TASK_STATUS_IN_PROGRESS, config);
            addStatus(ExternalTask.TaskStatus.COMPLETED, VersionOneConstants.KEY_TASK_STATUS_COMPLETED, config);
            addStatus(ExternalTask.TaskStatus.CANCELLED, VersionOneConstants.KEY_TASK_STATUS_CANCELLED, config);
            addStatus(ExternalTask.TaskStatus.UNKNOWN, VersionOneConstants.KEY_TASK_STATUS_UNKNOWN, config);
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
