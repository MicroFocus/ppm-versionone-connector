package com.ppm.integration.agilesdk.connector.versionone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hp.ppm.integration.ValueSet;
import com.hp.ppm.integration.pm.IExternalTask;
import com.hp.ppm.integration.pm.IExternalWorkPlan;
import com.hp.ppm.integration.pm.WorkPlanIntegration;
import com.hp.ppm.integration.pm.WorkPlanIntegrationContext;
import com.hp.ppm.integration.ui.DynamicalDropdown;
import com.hp.ppm.integration.ui.Field;
import com.hp.ppm.integration.ui.LineBreaker;
import com.hp.ppm.integration.ui.PasswordText;
import com.hp.ppm.integration.ui.PlainText;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneTimebox;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;

public class VersionOneWorkPlanIntegration implements WorkPlanIntegration {
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
						new LineBreaker(), new DynamicalDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME,
								"VersionOne_PROJECT", true) {

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
	public boolean linkTaskWithExternal(WorkPlanIntegrationContext context, ValueSet values) {
		return false;
	}

	@Override
	public IExternalWorkPlan getExternalWorkPlan(WorkPlanIntegrationContext context, ValueSet values) {
		String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
		configureService(values.get(VersionOneConstants.KEY_PROXY_HOST), values.get(VersionOneConstants.KEY_PROXY_PORT),
				values.get(VersionOneConstants.KEY_USERNAME), values.get(VersionOneConstants.KEY_PASSWORD),
				values.get(VersionOneConstants.KEY_BASE_URL));

		final List<VersionOneTimebox> timeboxes = service.getTimeboxes(scopeId);
		return new IExternalWorkPlan() {

			@Override
			public List<IExternalTask> getRootTasks() {
				List<IExternalTask> externalTasks = new ArrayList<>();
				for (VersionOneTimebox vt : timeboxes) {
					externalTasks.add(vt);
				}
				return externalTasks;
			}
		};
	}

	@Override
	public boolean unlinkTaskWithExternal(WorkPlanIntegrationContext context, ValueSet values) {
		return false;
	}

	@Override
	public String getCustomDetailPage() {
		return null;
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
