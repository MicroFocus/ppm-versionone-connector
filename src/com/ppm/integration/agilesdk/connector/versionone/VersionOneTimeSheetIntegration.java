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

import com.hp.ppm.integration.ValueSet;
import com.hp.ppm.integration.tm.IExternalWorkItem;
import com.hp.ppm.integration.tm.TimeSheetIntegration;
import com.hp.ppm.integration.tm.TimeSheetIntegrationContext;
import com.hp.ppm.integration.ui.DynamicalDropdown;
import com.hp.ppm.integration.ui.Field;
import com.hp.ppm.integration.ui.LineBreaker;
import com.hp.ppm.integration.ui.PasswordText;
import com.hp.ppm.integration.ui.PlainText;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;

public class VersionOneTimeSheetIntegration implements TimeSheetIntegration {
	private VersionOneService service;

	@Override
	public List<IExternalWorkItem> getExternalWorkItems(TimeSheetIntegrationContext arg0, ValueSet values) {

		// Synchronized ?
		final List<IExternalWorkItem> items = Collections.synchronizedList(new LinkedList<IExternalWorkItem>());
		XMLGregorianCalendar start = arg0.currentTimeSheet().getPeriodStartDate();
		XMLGregorianCalendar end = arg0.currentTimeSheet().getPeriodEndDate();

		configureService(values.get(VersionOneConstants.KEY_PROXY_HOST), values.get(VersionOneConstants.KEY_PROXY_PORT),
				values.get(VersionOneConstants.KEY_USERNAME), values.get(VersionOneConstants.KEY_PASSWORD),
				values.get(VersionOneConstants.KEY_BASE_URL));
		String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
		Map<String, Map<String, Long>> map = service.getTimeSheet(start.toString().substring(0, 10),
				end.toString().substring(0, 10), scopeId);

		Set<Entry<String, Map<String, Long>>> entrySet = map.entrySet();

		for (Entry<String, Map<String, Long>> entry : entrySet) {
			long actualEffort = 0;

			Map<String, Long> actualEfforts = entry.getValue();
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
				new PasswordText(VersionOneConstants.KEY_PASSWORD, "PASSWORD", "admin", true), new LineBreaker(),
				new DynamicalDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME, "VersionOne_PROJECT", false) {

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
