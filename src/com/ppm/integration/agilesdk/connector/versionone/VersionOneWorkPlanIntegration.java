package com.ppm.integration.agilesdk.connector.versionone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneTimebox;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
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
	private VersionOneService service;

	public VersionOneWorkPlanIntegration() {

	}

	public VersionOneWorkPlanIntegration(String username, String password, String baseUri) {
		configureService(null, null, username, password, baseUri);
	}

	public VersionOneWorkPlanIntegration(String proxyHost, String proxyPort, String username, String password,
			String baseUri) {
		configureService(proxyHost, proxyPort, username, password, baseUri);

	}

	@Override
	public List<Field> getMappingConfigurationFields(WorkPlanIntegrationContext context, ValueSet values) {

		List<Field> fields = Arrays
				.asList(new Field[] { new PlainText(VersionOneConstants.KEY_USERNAME, "USERNAME", "admin", true),

						new PasswordText(VersionOneConstants.KEY_PASSWORD, "PASSWORD", "hpe1990", true),
						new LineBreaker(), new DynamicDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME,
								"VERSIONONE_PROJECT", true) {

							@Override
							public List<String> getDependencies() {
								return Arrays.asList(new String[] { VersionOneConstants.KEY_USERNAME,
										VersionOneConstants.KEY_PASSWORD });
							}

							@Override
							public List<Option> getDynamicalOptions(ValueSet values) {
								configureService(values.get(VersionOneConstants.KEY_PROXY_HOST),
										values.get(VersionOneConstants.KEY_PROXY_PORT),
										values.get(VersionOneConstants.KEY_USERNAME),
										values.get(VersionOneConstants.KEY_PASSWORD),
										values.get(VersionOneConstants.KEY_BASE_URL));

								List<VersionOneScope> list = service.getProjects();

								List<Option> optionList = new ArrayList<>();
								for (VersionOneScope project : list) {
									Option option = new Option(project.getScopeId(), project.getScopeName());

									optionList.add(option);
								}
								return optionList;
							}
						} });

		return fields;
	}

	@Override
	public ExternalWorkPlan getExternalWorkPlan(WorkPlanIntegrationContext context, ValueSet values) {
		String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
		configureService(values.get(VersionOneConstants.KEY_PROXY_HOST), values.get(VersionOneConstants.KEY_PROXY_PORT),
				values.get(VersionOneConstants.KEY_USERNAME), values.get(VersionOneConstants.KEY_PASSWORD),
				values.get(VersionOneConstants.KEY_BASE_URL));

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

	private void configureService(String proxyHost, String proxyPort, String username, String password,
			String baseUri) {
		service = service == null ? new VersionOneService() : service;
		IRestConfig config = new VersionOneRestConfig();
		config.setProxy(proxyHost, proxyPort);
		config.setBasicAuthorizaton(username, password);
		RestWrapper wrapper = new RestWrapper(config);
		service.setBaseUri(baseUri);
		service.setWrapper(wrapper);
	}

}
