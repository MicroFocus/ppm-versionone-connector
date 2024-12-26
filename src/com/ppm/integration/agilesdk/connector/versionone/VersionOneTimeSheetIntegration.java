
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

import org.apache.log4j.Logger;
import org.apache.wink.client.ClientRuntimeException;

import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.connector.versionone.model.VersionOneScope;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.IRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.RestWrapper;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.VersionOneRestConfig;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.RestRequestException;
import com.ppm.integration.agilesdk.connector.versionone.rest.util.exception.VersionOneConnectivityExceptionHandler;
import com.ppm.integration.agilesdk.tm.ExternalWorkItem;
import com.ppm.integration.agilesdk.tm.TimeSheetIntegration;
import com.ppm.integration.agilesdk.tm.TimeSheetIntegrationContext;
import com.ppm.integration.agilesdk.ui.DynamicDropdown;
import com.ppm.integration.agilesdk.ui.Field;
import com.ppm.integration.agilesdk.ui.LineBreaker;
import com.ppm.integration.agilesdk.ui.PasswordText;


public class VersionOneTimeSheetIntegration extends TimeSheetIntegration {

    private final Logger logger = Logger.getLogger(this.getClass());

    private VersionOneService service;

    @Override
    public List<ExternalWorkItem> getExternalWorkItems(TimeSheetIntegrationContext arg0, ValueSet values) {

        final List<ExternalWorkItem> items = Collections.synchronizedList(new LinkedList<ExternalWorkItem>());
        XMLGregorianCalendar start = arg0.currentTimeSheet().getPeriodStartDate();
        XMLGregorianCalendar end = arg0.currentTimeSheet().getPeriodEndDate();

        configureService(values);

        String scopeId = values.get(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME);
        String username = values.get(VersionOneConstants.KEY_USERNAME);
        Map<String, Map<String, Long>> map = service.getTimeSheet(start.toString().substring(0, 10),
                end.toString().substring(0, 10), scopeId, username);

        Set<Entry<String, Map<String, Long>>> entrySet = map.entrySet();

        for (Entry<String, Map<String, Long>> entry : entrySet) {
            long actualEffort = 0;

            Map<String, Long> actualEfforts = entry.getValue();
            Set<String> keys = actualEfforts.keySet();
            for (String key : keys) {
                actualEffort += actualEfforts.get(key);
            }

            items.add(new VersionOneExternalWorkItem(entry.getKey(), actualEffort, null, start,
                    end, entry.getValue(), scopeId));
        }

        return items;

    }

    @Override
    public List<Field> getMappingConfigurationFields(ValueSet arg0) {
        return Arrays.asList(new Field[] {new PasswordText(VersionOneConstants.KEY_USER_API_TOKEN, "USER_TOKEN", "", true),
                new LineBreaker(),
                new DynamicDropdown(VersionOneConstants.KEY_VERSIONONE_PROJECT_NAME, "VERSIONONE_PROJECT", false) {

                    @Override
                    public List<String> getDependencies() {
                        return Arrays.asList(
                                new String[] {VersionOneConstants.KEY_USER_API_TOKEN});
                    }

                    @Override
                    public List<Option> getDynamicalOptions(ValueSet values) {

                        configureService(values);

                        List<VersionOneScope> list = new ArrayList<>();
                        try {
                            list = service.getProjectsForCurrentPpmUser();
                        } catch (ClientRuntimeException | RestRequestException e) {
                            logger.error("VersionOne TimeSheet", e);
                            new VersionOneConnectivityExceptionHandler().uncaughtException(Thread.currentThread(), e,
                                    VersionOneTimeSheetIntegration.class);
                        } catch (RuntimeException e) {
                            logger.error("VersionOne TimeSheet", e);
                            new VersionOneConnectivityExceptionHandler().uncaughtException(Thread.currentThread(), e,
                                    VersionOneTimeSheetIntegration.class);
                        }
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

}
