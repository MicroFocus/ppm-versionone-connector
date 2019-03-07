
package com.ppm.integration.agilesdk.connector.versionone;

public interface VersionOneConstants {

    String KEY_BASE_URL = "baseURL";

    String NULL_VALUE = "null";

    String KEY_PROXY_HOST = "proxyHost";

    String KEY_PROXY_PORT = "proxyPort";

    String KEY_USERNAME = "username";

    String KEY_PASSWORD = "password";

    String APP_CLIENT_ID = "clientId";

    String APP_CLIENT_SECRET = "clientSecret";

    String KEY_USE_GLOBAL_PROXY = "use_global_proxy";

    String KEY_VERSIONONE_PROJECT_NAME = "versionone_project_name";

    String SPECIFIC_PROJECT_SUFFIX = ";Scope=$myScope&with=$myScope=";

    String API_VERSION_API_DATA_ROOT = "/rest-1.v1/Data/";

    String PROJECT_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Scope?sel=Name&where=AssetState='64'&Accept=application/json";

    String STORIES_WITH_TIMEBOX_SUFFIX = API_VERSION_API_DATA_ROOT
            + "Story?Accept=application/json&sort=Timebox.BeginDate&sel=Name,Status.Name,Timebox.Name,Timebox.State.Code,Timebox.BeginDate,Timebox.EndDate,Timebox.ID,CreateDate,ChangeDate,Children.Actuals.Value.@Sum,Children.ToDo.@Sum,Children.DetailEstimate.@Sum&where=Scope=";

    String ACTUALS_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Actual?Accept=application/json&sel=Workitem.Parent,Date,Value";

}
