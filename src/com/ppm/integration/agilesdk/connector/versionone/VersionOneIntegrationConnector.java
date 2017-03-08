
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.Arrays;
import java.util.List;

import com.ppm.integration.agilesdk.FunctionIntegration;
import com.ppm.integration.agilesdk.IntegrationConnector;
import com.ppm.integration.agilesdk.ui.Field;
import com.ppm.integration.agilesdk.ui.PlainText;

/**
 * @author baijuy The connector provides the integration for ppm with
 *         VersionOne. The field InstanceName and BaseUrl are required and field
 *         Proxy is optional. Once the connector is saved, it can be used to
 *         integrate with workplan or timesheet.
 */
public class VersionOneIntegrationConnector extends IntegrationConnector {

    @Override
    public String getExternalApplicationName() {
        return "VersionOne";
    }

    @Override
    public String getExternalApplicationVersionIndication() {
        return "2016 (16.3.5.270+)";
    }

    @Override
    public String getConnectorVersion() {
        return "1.0";
    }

    @Override
    public String getTargetApplicationIcon() {

        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsQAAA7EAZUrDhsAAAIWSURBVDhPlVPPa9RQEP5etrvasJLU3Qqe9CT+RLYoWrwo6s1LL4o9ePAvUPQk3q0H6UU8CULxDxD0IuKxUNvLIj2URqXqdn9mm6SuzZps8px5bWJsFfSD5JvMzJv53rw80ajX5bCuQ8YxhBCQUoIMkKGYLJC1kynmb2xAuK4rDcMg1//D8zxoquMfEJMijjEndvZhMIvvQV9WH8/Ar3koFGkr5IzCEBce3FJSeWuapqWLsmAFwnUcaZgmpkfOwXOb2I0iQvyAXjZwp/NOJUZRjNVGA0O5nPoeRAOU95YQhgFtQbmA284siuUSXNRREDoGdohHo2dVbG5+AV27i1arjVazBdteg64PK1UaT5XBhe525jElO9g/fhSCZPftdby6eR+nx89g5fMXNKnAkvUBJ44d2VxESBXwXhP7xuxzdONP0PeMYOHZU7x9/QbOuofaag2jpRLy+bzK4+apAj5X0qQmzjg1MYmg58MsHMD7e0+w5vdgLVu4dPF8OlB+/1KwbcqHr1xGKH3IIQ2F6lcsrli4fu3qVnQTOxVksG/sEJ1GH8hp2BUHCGnylcpJ1eifFLSrFiIECL71sISPmJ56qPyclzTjd/ofpFWJ+cdZfPES7blldRrm8YOoTE6o+ST3hXPoGkA4VMCkAtkggxOyyMaZ+fGogKaRweAF7GTevpiRjTMrEP92ndlBLVRC0ulvzPB9Hz8BEWcv9hHRVP4AAAAASUVORK5CYII=";
    }

    @Override
    public List<Field> getDriverConfigurationFields() {
        return Arrays.asList(new Field[] {new PlainText(VersionOneConstants.KEY_BASE_URL, "BASE_URL", "", true),
                new PlainText(VersionOneConstants.KEY_PROXY_HOST, "PROXY_HOST", "", false),
                new PlainText(VersionOneConstants.KEY_PROXY_PORT, "PROXY_PORT", "", false),

        });
    }

    @Override
    public List<FunctionIntegration> getIntegrations() {
        return Arrays.asList(
                new FunctionIntegration[] {new VersionOneWorkPlanIntegration(), new VersionOneTimeSheetIntegration()});
    }
}
