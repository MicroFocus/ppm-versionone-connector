
package com.ppm.integration.agilesdk.connector.versionone;

import java.util.Arrays;
import java.util.List;

import com.ppm.integration.agilesdk.FunctionIntegration;
import com.ppm.integration.agilesdk.IntegrationConnector;
import com.ppm.integration.agilesdk.ValueSet;
import com.ppm.integration.agilesdk.provider.LocalizationProvider;
import com.ppm.integration.agilesdk.provider.Providers;
import com.ppm.integration.agilesdk.ui.*;

/**
 * @author baijuy The connector provides the integration for ppm with
 *         VersionOne. The field InstanceName and BaseUrl are required and field
 *         Proxy is optional. Once the connector is saved, it can be used to
 *         integrate with workplan or timesheet.
 */
public class VersionOneIntegrationConnector extends IntegrationConnector {

    @Override
    public String getExternalApplicationName() {
        return "Digital.ai Agility";
    }

    @Override
    public String getExternalApplicationVersionIndication() {
        return "2024";
    }

    @Override
    public String getConnectorVersion() {
        return "2.1";
    }

    @Override
    public String getTargetApplicationIcon() {
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAVUSURBVEhLfVZtTJVlGL6e93wDAnIAFTig4gdKIIoLFrNMUGlzli6otZY//OP6mdWUnFs1NS3+tOX6UVttbbVA3dRMFtKa1UzyI79FMz3QgeR4ADnncD7fp+t5zwE5GF3bw/ve9/M+930/1/1xEJgGTZ37s6WILwZEHYR8DlKWSSBP7QkIL//eBORJCXma8s32+pYh4+AUPOag+cReTdq15VLqr/PwOqoKuaYLhD7h4Xan0MTBcFyeO9bQEk9sJZBysLlrj1WXoomn3uLZSqqmveEUKEdX+HErdO2b9rU7wwn1JAPNP75L45YdUsodFB0JbQKkABbNBKvJQkkiHI8hqqtAld0UhLhadWF6/8iaHYYTw8HG7/dodof2Ujyuf0YxTekUlOFZjixU5cxHQboTMywOpkJiOBKA2z+IC747GOH7FISEENuEtH7VVv9m3Kw0dqu5Sur623ydMG7VzHgqvwzrC1fAlZEPszDBHw1B0wTSzXaEYhGsGvXgqPsMrgzdw6rZ5bBpFpz+55o9GItsB+LXaeasaDq1N4uBfqhLuZUKTRlXH75auhrPFlShP+jDD54L8AQeGPf1MeIY6anNW0Tn1XRmQ/vdn1HlnM8gzPjk+nEMhkYUd19qEG+Yyrc0VLBadlIxUxnn9bB6TgU2z63DuQe3cbyvG0uyC9FYVM0on8BI2I+LQ3dwc6QPfYFBuNLzUZO/GHaTFYFYGN3eHgRjYVpBHlcXLyxq6c6ljCsUpTmxjrS4ebiLkTfQ2XLnQpzyXMJHlw/jl/vXmNtEcq8Ou43oQ/EI8h0zEwlNgl8U6ECdpgvZSNmgRqPfaucCRpWL79zdWMH3BZkFaL3cjg7POSNqlWAFRc0rpc9iGalRFTXZeBKCFDVqdLUoqYDDZEMRjUf1GP4OPiA1LvzUfxUlGbOwnJWkqmocNpZs+UwXlvKbMd7gz9F+9PLWEZ6dhMVmjoG88XJWhzKtafCGR2EieTm2GegZ7sXqgkqjUiZjJBLEx1eP8i3hlHlkf0RZaWOGrECzuQY1j5D0xIeUwqgWi9kCfzyELFu60WwKKpAsBuJhhXl4U7VUtfkYWFyS+UeQGj3fTwpMVtTgONeRyatG0T82hOrcRege7EF5dgk2uJ6kvBCbSmrxYkld8tT/QMBLInAjKRrV0BfwGk1VzPK7yE5dljMXFtb3yd7fUZo5By8U12K2PQdnvbd54j9SmwJ5w7T0tbWsf45jfq0IUt2qkluWXYSOvvMGTc+X1BgdfII9cfjerzjjvYEB3k5hcuKnQOfWQTbaGkWaGstGo/ljIcPoM2wqleSu/j8wEBzCHPaHGnJ3/APjbWBUUFlWITv3IWIyZUor9NFBq6liy/oA8zCPimouIxzPmI+daUPdrCVYkuXC7dEBo8FusQ+Uodlp2WjgGHm59GnuF+Oy7y6GoylDT4XQJqB/bRhs6txXKYX+ObUrlaxg4bCr4bxpLFyJ4ow8o3JUEahmtLKaONDosN8YdtdG3NBTq+ciY916qL7lvOGg8dh7IsNh3iyF/IJihtIpqLmUa81EpXMeCh1OZFiT45rR9vq9uOT7C6PRIMNNcpZAkGurHpXfHmncpU9kaFPXPotJyu38+B2KE07GoW6k+kCZijAX8cc5VwjQ4H4p4/sPNeyOKMWEA4XNnQcsQsQ28pW/anIFn1MacVoofkgLDmi6PNy2dlfU0BIpDhQ2dHwg7Ca9gjvbKK7njebyOZ0jZdhNJjvI3KcQ8UuH1uxOScZjDsbRfGrfDNKxgNO2hg2iJm4Zl5NLneG/LejhLU8yUb9x/1ZbfctD6qYA+BfqjSkn5D8SfwAAAABJRU5ErkJggg==";
    }

    @Override
    public List<Field> getDriverConfigurationFields() {

        LocalizationProvider lp = Providers.getLocalizationProvider(VersionOneIntegrationConnector.class);

        return Arrays.asList(new Field[] {new PlainText(VersionOneConstants.KEY_BASE_URL, "BASE_URL", "", true),
                new PlainText(VersionOneConstants.KEY_PROXY_HOST, "PROXY_HOST", "", false),
                new PlainText(VersionOneConstants.KEY_PROXY_PORT, "PROXY_PORT", "", false),
                new PasswordText(VersionOneConstants.KEY_ADMIN_API_TOKEN, "ADMIN_TOKEN", "", true),
                new LineBreaker(),
                new LabelText("EMAIL_NOTICE", "EMAIL_NOTICE", "", false),
                new CheckBox(VersionOneConstants.KEY_ALWAYS_USE_ADMIN_API_TOKEN, "USE_ADMIN_TOKEN", "", false),
                new PlainText(VersionOneConstants.KEY_MISSING_EMAIL_MESSAGE, "MISSING_EMAIL_MESSAGE", lp.getConnectorText("ERROR_EMAIL_NOT_CAPTURED_IN_PPM"), "block", false),
                new LineBreaker(),
                new LabelText("", "STATUS_MAPPING_LABEL", "block", false),
                new LineBreaker(),
                new PlainText(VersionOneConstants.KEY_TASK_STATUS_READY, "STATUS_READY_LABEL", "Future", false),
                new PlainText(VersionOneConstants.KEY_TASK_STATUS_IN_PROGRESS, "STATUS_IN_PROGRESS_LABEL", "In Progress;In Test", false),
                new PlainText(VersionOneConstants.KEY_TASK_STATUS_COMPLETED, "STATUS_COMPLETED_LABEL", "Done;Accepted", false),
                new PlainText(VersionOneConstants.KEY_TASK_STATUS_CANCELLED, "STATUS_CANCELLED_LABEL", "", false),
                new PlainText(VersionOneConstants.KEY_TASK_STATUS_UNKNOWN, "STATUS_UNKNOWN_LABEL", "", false),
                new LineBreaker(),

                new LabelText("", "IMPORT_OPTIONS", "block", false),
                new LineBreaker(),
                new CheckBox(VersionOneConstants.KEY_ALLOW_STORIES, "ALLOW_STORIES", "", false),
                new PlainText(VersionOneConstants.KEY_ALLOW_REQUESTS, "REQUEST_CATEGORY_TYPES", "Feature", false),
                new PlainText(VersionOneConstants.KEY_ALLOW_EPICS, "EPIC_CATEGORY_TYPES", "Epic", false),
                new LineBreaker(),

                new LabelText("", "ACTUAL_EFFORT_IMPORT_LABEL", "block", false),
                new LineBreaker(),
                new CheckBox(VersionOneConstants.KEY_IMPORT_ACTUAL_EFFORT, "IMPORT_ACTUAL_EFFORT", "", false)
        });
    }

    @Override
    public List<FunctionIntegration> getIntegrations() {
        return Arrays.asList(
                // new FunctionIntegration[] {new VersionOneWorkPlanIntegration(), new VersionOneTimeSheetIntegration()});
                // Removing Timesheet integration as of PPM 25.1 - We can't reliably get username of current user with API Token
                new FunctionIntegration[] {new VersionOneWorkPlanIntegration()});
    }

    @Override
    public String testConnection(ValueSet instanceConfigurationParameters) {
        try {
            VersionOneService service = VersionOneService.fromValueSet(instanceConfigurationParameters);
            service.getProjects();
        } catch (Exception e) {
            return e.getMessage();
        }

        return null;
    }
}
