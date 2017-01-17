package com.ppm.integration.agilesdk.connector.versionone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
import com.ppm.integration.agilesdk.tm.ExternalWorkItem;
import com.ppm.integration.agilesdk.tm.TimeSheetIntegration;
import com.ppm.integration.agilesdk.tm.TimeSheetIntegrationContext;
import com.ppm.integration.agilesdk.ui.DynamicDropdown;
import com.ppm.integration.agilesdk.ui.Field;
import com.ppm.integration.agilesdk.ui.LineBreaker;
import com.ppm.integration.agilesdk.ui.PasswordText;
import com.ppm.integration.agilesdk.ui.PlainText;

public class VersionOneTimeSheetIntegration extends TimeSheetIntegration {
	private VersionOneService service;

	@Override
	public List<ExternalWorkItem> getExternalWorkItems(TimeSheetIntegrationContext arg0, ValueSet values) {

		// Synchronized ?
		final List<ExternalWorkItem> items = Collections.synchronizedList(new LinkedList<ExternalWorkItem>());
		XMLGregorianCalendar start = arg0.currentTimeSheet().getPeriodStartDate();
		XMLGregorianCalendar end = arg0.currentTimeSheet().getPeriodEndDate();

		configureService(values.get(VersionOneConstants.KEY_PROXY_HOST), values.get(VersionOneConstants.KEY_PROXY_PORT),
				values.get(VersionOneConstants.KEY_USERNAME), values.get(VersionOneConstants.KEY_PASSWORD),
				values.get(VersionOneConstants.KEY_BASE_URL));
		String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
		Map<String, Map<String, Double>> map = service.getTimeSheet(start.toString().substring(0, 10),
				end.toString().substring(0, 10), scopeId);

		Set<Entry<String, Map<String, Double>>> entrySet = map.entrySet();

		for (Entry<String, Map<String, Double>> entry : entrySet) {
			double actualEffort = 0;

			Map<String, Double> actualEfforts = entry.getValue();
			Set<String> keys = actualEfforts.keySet();
			for (String key : keys) {
				actualEffort += actualEfforts.get(key);
			}

			items.add(new VersionOneExternalWorkItem(entry.getKey(), actualEffort, entry.getKey() + " error", start,
					end, entry.getValue()));
		}

		return items;

	}

	@Override
	public List<Field> getMappingConfigurationFields(ValueSet arg0) {
		return Arrays.asList(new Field[] { new PlainText(VersionOneConstants.KEY_USERNAME, "USERNAME", "admin", true),
				new PasswordText(VersionOneConstants.KEY_PASSWORD, "PASSWORD", "hpe1990", true), new LineBreaker(),
				new DynamicDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME, "VERSIONONE_PROJECT", false) {

					@Override
					public List<String> getDependencies() {
						return Arrays.asList(
								new String[] { VersionOneConstants.KEY_USERNAME, VersionOneConstants.KEY_PASSWORD });
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
				}

		});
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
