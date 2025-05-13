
package com.ppm.integration.agilesdk.connector.versionone;

public interface VersionOneConstants {

    String KEY_BASE_URL = "baseURL";

    String NULL_VALUE = "null";

    String KEY_PROXY_HOST = "proxyHost";

    String KEY_PROXY_PORT = "proxyPort";

    String KEY_ADMIN_API_TOKEN = "adminApiToken";

    String KEY_MISSING_EMAIL_MESSAGE = "missingEmailMessage";

    String WP_INCLUDE_CLOSED_SPRINTS = "includeClosedSprints";

    String WP_INCLUDE_STORIES_NOT_IN_SPRINT = "includeStoriesInNoSprints";

    String KEY_TASK_STATUS_READY = "statusReady";

    String KEY_TASK_STATUS_IN_PROGRESS = "statusInProgress";

    String KEY_TASK_STATUS_COMPLETED = "statusCompleted";

    String KEY_TASK_STATUS_CANCELLED = "statusCancelled";

    String KEY_TASK_STATUS_UNKNOWN = "statusUnknown";

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


    String EMAIL_FIELD = "Subs[ParentMeAndUp.TaggedWith='%WBSid%'].Owners.Email";
    String EPICS_FEATURES_PATH_AND_PARAMS =
            "Epic?Accept=application/json&sort=PlannedStart&sel=Number,Name,Status.Name,CreateDate,PlannedStart,PlannedEnd,%EmailField%&where=(Category.Name='Sub - Feature'|Category.Name='Feature');(%SubsAssetTypeFilter%);Subs.ParentMeAndUp.TaggedWith='%WBSid%'";


    String STORIES_WITH_TIMEBOX_SUFFIX = API_VERSION_API_DATA_ROOT
            + "Story?Accept=application/json&sort=Timebox.BeginDate&sel=Name,Owners.Name,Status.Name,Timebox.Name,Timebox.State.Code,Timebox.BeginDate,Timebox.EndDate,Timebox.ID,CreateDate,Children.Actuals.Value.@Sum,Children.ToDo.@Sum,Children.DetailEstimate.@Sum&where=Scope=";

    String ACTUALS_SUFFIX =
            API_VERSION_API_DATA_ROOT + "Actual?Accept=application/json&sel=Workitem.Parent,Date,Value";

    String TIMEBOX_STATUS_CLOSED = "CLSD";

    String KEY_PICK_RESOURCES_FROM_THESE_SUB_TYPES = "PICK_RESOURCES_FROM_THESE_SUB_TYPES";

    String KEY_AGILITY_ID_FIELD_NAME = "AGILITY_ID_FIELD_NAME";

    String KEY_PPM_REQUEST_FIELD_TYPE = "PPM_REQUEST_FIELD_TYPE";

    String PPM_REQUEST_FIELD_TYPE_REQUEST_HEADER = "reqFieldRequestHeader";

    String PPM_REQUEST_FIELD_TYPE_REQUEST_DETAILS = "reqFieldRequestDetails";

    String KEY_PPM_REQUEST_FIELD_PARAMETER_TYPE = "PPM_REQUEST_FIELD_PARAMETER_TYPE";

    String PPM_REQUEST_FIELD_PARAMETER_TYPE_VISIBLE = "reqFieldParamVisible";

    String PPM_REQUEST_FIELD_PARAMETER_TYPE_PARAMETER = "reqFieldParamParam";

    String KEY_PPM_REQUEST_FIELD_COLUMN = "PPM_REQUEST_FIELD_COLUMN";

    String KEY_PPM_REQUEST_FIELD_BATCH = "PPM_REQUEST_FIELD_BATCH";

}
