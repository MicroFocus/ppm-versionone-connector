
package com.ppm.integration.agilesdk.connector.versionone;

public interface VersionOneConstants {

    String KEY_BASE_URL = "baseURL";

    String NULL_VALUE = "null";

    String KEY_PROXY_HOST = "proxyHost";

    String KEY_PROXY_PORT = "proxyPort";

    String KEY_USER_API_TOKEN = "userApiToken";

    String KEY_ADMIN_API_TOKEN = "adminApiToken";

    String KEY_MISSING_EMAIL_MESSAGE = "missingEmailMessage";

    String WP_INCLUDE_CLOSED_SPRINTS = "includeClosedSprints";

    String WP_INCLUDE_STORIES_NOT_IN_SPRINT = "includeStoriesInNoSprints";

    String KEY_ALWAYS_USE_ADMIN_API_TOKEN = "alwaysUseAdminToken";

    String KEY_USERNAME = "username";

    String KEY_TASK_STATUS_READY = "statusReady";

    String KEY_TASK_STATUS_IN_PROGRESS = "statusInProgress";

    String KEY_TASK_STATUS_COMPLETED = "statusCompleted";

    String KEY_TASK_STATUS_CANCELLED = "statusCancelled";

    String KEY_TASK_STATUS_UNKNOWN = "statusUnknown";

    String KEY_ALLOW_STORIES = "allowStories";

    String KEY_ALLOW_REQUESTS = "allowRequests";

    String KEY_ALLOW_EPICS = "allowEpics";

    String KEY_IMPORT_STORIES = "importStories";

    String KEY_IMPORT_REQUEST_PREFIX = "importReq_";

    String KEY_IMPORT_EPIC_PREFIX = "importEpic_";

    String KEY_VERSIONONE_PROJECT_NAME = "versionone_project_name";

    String KEY_IMPORT_ACTUAL_EFFORT = "importActualEffort";

    String SPECIFIC_PROJECT_SUFFIX = ";Scope=$myScope&with=$myScope=";

    String API_VERSION_API_DATA_ROOT = "/rest-1.v1/Data/";

    String PROJECT_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Scope?sel=Name&where=AssetState='64'&Accept=application/json";

    // Need to replace %EMAIL% with a valid email address (search won't be case-sensitive)
    String MEMBER_SCOPES_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Member?sel=Scopes,Scopes.Name&Accept=application/json&where=Email='%EMAIL%'";

    String REQUESTS_SUFFIX = API_VERSION_API_DATA_ROOT +
            "Request?Accept=application/json&sort=Custom_NeededbyDate&sel=Name,Owner.Name,Status.Name,CreateDate,Custom_NeededbyDate&where=Category.Name=";

    String EPICS_SUFFIX = API_VERSION_API_DATA_ROOT +
            "Epic?Accept=application/json&sort=PlannedStart&sel=Name,Owners.Name,Status.Name,CreateDate,PlannedStart,PlannedEnd&where=Category.Name=";

    String STORIES_WITH_TIMEBOX_SUFFIX = API_VERSION_API_DATA_ROOT
            + "Story?Accept=application/json&sort=Timebox.BeginDate&sel=Name,Owners.Name,Status.Name,Timebox.Name,Timebox.State.Code,Timebox.BeginDate,Timebox.EndDate,Timebox.ID,CreateDate,Children.Actuals.Value.@Sum,Children.ToDo.@Sum,Children.DetailEstimate.@Sum&where=Scope=";

    String ACTUALS_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Actual?Accept=application/json&sel=Workitem.Parent,Date,Value";

    String TIMEBOX_STATUS_CLOSED = "CLSD";
}
