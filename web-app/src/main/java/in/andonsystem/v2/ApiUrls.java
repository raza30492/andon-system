package in.andonsystem.v2;

/**
 * Created by razamd on 3/30/2017.
 */
public class ApiUrls {
    public static final String ROOT_URL_USERS = "/api/v2/users";
    public static final String URL_USERS_USER = "/{userId}";
    public static final String URL_USERS_USER_SEARCH_BY_NAME = "/search/byName";
    public static final String URL_USERS_USER_SEARCH_BY_EMAIL = "/search/byEmail";
    public static final String ROOT_URL_PROBLEMS = "/api/v2/problems";
    public static final String ROOT_URL_TEAMS = "/api/v2/teams";
    public static final String ROOT_URL_BUYERS = "/api/v2/buyers";
    public static final String URL_BUYERS_BUYER = "/{buyerId}";
    public static final String ROOT_URL_ISSUES = "/api/v2/issues";
    public static final String URL_ISSUES_ISSUE = "/{issueId}";
    public static final String URL_ISSUES_DOWNTIME_BY_TEAM = "/downtime/byTeam";
    public static final String URL_ISSUES_DOWNTIME_BY_BUYER = "/downtime/byBuyer";
    public static final String URL_ISSUES_DOWNTIME_BY_PROBLEM = "/downtime/byProblem";

    public static final String ROOT_URL_MISCELLANEOUS = "/api/v2/misc";
    public static final String URL_MISCELLANEOUS_CONFIG = "/config";
    public static final String URL_CURRENT_TIME = "/current_time";
    public static final String URL_CHANGE_PASSWORD = "/change_password";
    public static final String URL_FORGOT_PASSWORD_SEND_OTP = "/forgot_password/send_otp";
    public static final String URL_FORGOT_PASSWORD_VERIFY_OTP = "/forgot_password/verify_otp";
    public static final String URL_FORGOT_PASSWORD_CHANGE_PASSWORD = "/forgot_password/change_password";
    public static final String URL_STYLE_CHANGE_OVER = "/style_change_over";

    public static final String APK_DOWNLOAD_URL = "/download";
}
