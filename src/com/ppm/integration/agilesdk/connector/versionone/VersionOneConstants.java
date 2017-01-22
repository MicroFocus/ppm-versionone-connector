package com.ppm.integration.agilesdk.connector.versionone;

public class VersionOneConstants {

    public static final String KEY_BASE_URL = "baseURL";

    public static final String KEY_PROXY_HOST = "proxyHost";

    public static final String KEY_PROXY_PORT = "proxyPort";

    public static final String KEY_USERNAME = "username";

    public static final String KEY_PASSWORD = "password";

    public static final String APP_CLIENT_ID = "clientId";

    public static final String APP_CLIENT_SECRET = "clientSecret";

    public static final String KEY_USE_GLOBAL_PROXY = "use_global_proxy";

    public static final String KEY_VERSIONONE_PROJECT_NAME = "versionone_project_name";

    public static final String SPECIFIC_PROJECT_SUFFIX = ";Scope=$myScope&with=$myScope=";

    private static final String API_VERSION_API_DATA_ROOT = "/rest-1.v1/Data/";

    public static final String PROJECT_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Scope?sel=Name&where=AssetState='64'&Accept=application/json";

    public static final String STORIES_WITH_TIMEBOX_SUFFIX = API_VERSION_API_DATA_ROOT
            + "Story?Accept=application/json&sel=Name,Status.Name,Timebox.Name,Timebox.State.Code,Timebox.BeginDate,Timebox.EndDate&where=Scope=$myScope&with=$myScope=";

    public static final String ACTUALS_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Actual?Accept=application/json&sel=Workitem.Parent,Date,Value";

}
