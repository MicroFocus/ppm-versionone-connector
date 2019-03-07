
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wink.client.ClientRuntimeException;

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
import com.ppm.integration.agilesdk.ui.DynamicDropdown;
import com.ppm.integration.agilesdk.ui.Field;
import com.ppm.integration.agilesdk.ui.LineBreaker;
import com.ppm.integration.agilesdk.ui.PasswordText;
import com.ppm.integration.agilesdk.ui.PlainText;

public class VersionOneWorkPlanIntegration extends WorkPlanIntegration {

    private final Logger logger = Logger.getLogger(this.getClass());

    private VersionOneService service;

    public VersionOneWorkPlanIntegration() {

    }

    @Override
    public List<Field> getMappingConfigurationFields(WorkPlanIntegrationContext context, ValueSet values) {

        List<Field> fields =
                Arrays.asList(new Field[] {new PlainText(VersionOneConstants.KEY_USERNAME, "USERNAME", "", true),

                        new PasswordText(VersionOneConstants.KEY_PASSWORD, "PASSWORD", "", true), new LineBreaker(),
                        new DynamicDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME, "VERSIONONE_PROJECT",
                                true) {

                            @Override
                            public List<String> getDependencies() {
                                return Arrays.asList(new String[] {VersionOneConstants.KEY_USERNAME,
                                        VersionOneConstants.KEY_PASSWORD});
                            }

                            @Override
                            public List<Option> getDynamicalOptions(ValueSet values) {
                                configureService(values);
                                List<VersionOneScope> list = new ArrayList<>();
                                try {
                                    list = service.getProjects();
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
                        }});

        return fields;
    }

    @Override
    public ExternalWorkPlan getExternalWorkPlan(WorkPlanIntegrationContext context, ValueSet values) {
        String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
        configureService(values);

        final List<VersionOneTimebox> timeboxes = service.getTimeboxes(scopeId);
        return new ExternalWorkPlan() {

            @Override
            public List<ExternalTask> getRootTasks() {
                List<ExternalTask> externalTasks = new ArrayList<>();
                for (VersionOneTimebox vt : timeboxes) {
                    externalTasks.add(vt);
                }
                return externalTasks;
            }
        };
    }

    private void configureService(ValueSet values)
    {
        String proxyHost = values.get(VersionOneConstants.KEY_PROXY_HOST);
        String proxyPort = values.get(VersionOneConstants.KEY_PROXY_PORT);
        String username = values.get(VersionOneConstants.KEY_USERNAME);
        String password = values.get(VersionOneConstants.KEY_PASSWORD);
        String baseUri = values.get(VersionOneConstants.KEY_BASE_URL);

        service = service == null ? new VersionOneService() : service;
        IRestConfig config = new VersionOneRestConfig();
        config.setProxy(proxyHost, proxyPort);
        config.setBasicAuthorizaton(username, password);
        RestWrapper wrapper = new RestWrapper(config);
        service.setBaseUri(baseUri);
        service.setWrapper(wrapper);
    }

}
