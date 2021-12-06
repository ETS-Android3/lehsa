package my.project.lhesa.utils;

public final class DatabaseServerConstants {
    public static String DB_LSA_URL = "http://192.168.1.252:8888/lsa/lsa.php";

    public static final String CMD_AUTHORIZATION = "authorization";
        public static final String CMD_AUTHORIZATION_LOGIN = "login";
        public static final String CMD_AUTHORIZATION_REGISTER = "register";
        public static final String CMD_AUTHORIZATION_UPDATE_PASSWORD = "update_password";
        public static final String CMD_AUTHORIZATION_DELETE_ACCOUNT = "delete_account";

    public static final String CMD_COURSE = "course";
        public static final String CMD_COURSE_GET_COURSE_INFO ="get_course_info";
        public static final String CMD_COURSE_GET_ALL_COURSE_NUMBERS ="get_all_course_numbers";
        public static final String CMD_COURSE_GET_USER_COURSES = "get_user_courses";
        public static final String CMD_COURSE_UPDATE_USER_COURSES = "update_user_courses";

    public static final String CMD_FACULTY = "faculty";
        public static final String CMD_FACULTY_REQ_ALL_IDS = "request_all_ids";
        public static final String CMD_FACULTY_REQ_ALL_NAMES = "request_all_faculty_names";
        public static final String CMD_FACULTY_REQ_FACULTY_RECORD = "request_faculty_record";
        public static final String CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME = "request_faculty_record_by_name";

    public static final String CMD_CHATBOT = "chatbot";
    public static final String CMD_CHATBOT_GET_BOT_ANSWER = "bot_answer";

    public static final String CMD_TRACKER = "tracker";
        public static final String CMD_TRACKER_GET_USER_TRACKERS = "get_user_trackers";
        public static final String CMD_TRACKER_ADD_USER_TRACKER = "add_user_tracker";
        public static final String CMD_TRACKER_REMOVE_USER_TRACKER = "remove_user_tracker";

    public static final String CMD_USER = "user";
        public static final String CMD_USER_GET_USER_INFO = "get_user_info";

    // Database command header/generic fields
    public static final String DB_COMMAND = "command";
    public static final String DB_SUB_COMMAND = "sub_command";

    // DATABASE Schema
    // student_details
    public static final String DB_STUDENT_DETAILS_ID = "id";
    public static final String DB_STUDENT_DETAILS_FIRSTNAME = "first_name";
    public static final String DB_STUDENT_DETAILS_LASTNAME = "last_name";
    public static final String DB_STUDENT_DETAILS_USERNAME = "user_name";
    public static final String DB_STUDENT_DETAILS_PASSWORD = "password";

    // student_course
    public static final String DB_STUDENT_COURSE_USERNAME = "user_name";
    public static final String DB_STUDENT_COURSE_COURSE_NUMBER = "course_number";

    // assignment_tracker
    public static final String DB_TRACKER_COURSE_NUMBER = "course_number";
    public static final String DB_TRACKER_ASSIGNMENT = "assignment";
    public static final String DB_TRACKER_DUE_DATE = "due_date";
    public static final String DB_TRACKER_DUE_TIME = "due_time";
    public static final String DB_TRACKER_ADD_COURSE_NUMBERS_STRING = "add_course_numbers_string";
    public static final String DB_TRACKER_REMOVE_COURSE_NUMBERS_STRING = "remove_course_numbers_string";

    // faculty
    public static final String DB_COURSE_FACULTY_ID = "id";
    public static final String DB_COURSE_FACULTY_NAME = "fac_name";
    public static final String DB_COURSE_FACULTY_EMAIL = "fac_email";
    public static final String DB_COURSE_FACULTY_PHONE = "fac_phone";
    public static final String DB_COURSE_FACULTY_POSITION = "fac_position";
    public static final String DB_COURSE_FACULTY_RESEARCH_LINK = "fac_research_link";
    public static final String DB_COURSE_FACULTY_OFFICE = "fac_office";
    public static final String DB_COURSE_FACULTY_FEATURE_VECTORS = "fac_feature_vectors";

    // course_list
    public static final String DB_COURSE_LIST_COURSE_NUMBER = "course_number";
    public static final String DB_COURSE_LIST_COURSE_NAME = "course_name";
    public static final String DB_COURSE_LIST_PROFESSOR = "professor";
    public static final String DB_COURSE_LIST_DAYS_OFFERED = "days_offered";
    public static final String DB_COURSE_LIST_TIMING = "timing";
    public static final String DB_COURSE_LIST_LIST = "ta_list";

    // chatbot
    public static final String DB_CHATBOT_KEYWORD = "keyword";
    public static final String DB_CHATBOT_ISSUES = "issues";
    public static final String DB_CHATBOT_ANSWER = "answer";
}
