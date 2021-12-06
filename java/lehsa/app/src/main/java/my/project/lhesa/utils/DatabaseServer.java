package my.project.lhesa.utils;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_DELETE_ACCOUNT;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseServer extends AsyncTask<String,Void,String> {
    private  BackgroundTaskCallback callback;
    Map<String, String> responseStatus;
    List<JSONObject> responseData;

    public DatabaseServer(final BackgroundTaskCallback callback) {
        this.callback = callback;
        responseStatus = new HashMap<String, String>();
        responseData = new ArrayList<JSONObject>();
    }
//    private String parseAuthorizationCommands()

    @Override
    protected String doInBackground(String... args) {
        List<JSONObject> result = new ArrayList<JSONObject>();
        String status = BackgroundTaskCallback.RESPONSE_SUCCESS;
        String resp_message = new String();
        String command = new String();
        String sub_command = new String();
        String user_name = new String();
        String packet_data;
        try {
            command = (String) args[0];
            sub_command = (String) args[1];
            user_name = (String) args[2];
            if (user_name.isEmpty()){
                resp_message = new String("The username can't be empty");
                status = BackgroundTaskCallback.RESPONSE_ERROR;
            } else{
                packet_data = URLEncoder.encode(DatabaseServerConstants.DB_COMMAND, "UTF-8") +
                        "=" + URLEncoder.encode(command, "UTF-8");
                packet_data += "&" + URLEncoder.encode(
                        DatabaseServerConstants.DB_SUB_COMMAND, "UTF-8") +
                        "=" + URLEncoder.encode(sub_command, "UTF-8");
                switch (command) {
                    case DatabaseServerConstants.CMD_AUTHORIZATION:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_AUTHORIZATION_LOGIN:
                            case DatabaseServerConstants.CMD_AUTHORIZATION_UPDATE_PASSWORD:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_PASSWORD, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            case CMD_AUTHORIZATION_DELETE_ACCOUNT:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                break;

                            case DatabaseServerConstants.CMD_AUTHORIZATION_REGISTER:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                packet_data += "&" +URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_FIRSTNAME, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                packet_data += "&" +URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_LASTNAME, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[4], "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_PASSWORD, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[5], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;

                    case DatabaseServerConstants.CMD_COURSE:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_COURSE_GET_COURSE_INFO:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_COURSE_LIST_COURSE_NUMBER, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            case DatabaseServerConstants.CMD_COURSE_GET_USER_COURSES:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                result = request_command(packet_data);
                                break;

                            case DatabaseServerConstants.CMD_COURSE_GET_ALL_COURSE_NUMBERS:
                                result = request_command(packet_data);
                                break;

                            case DatabaseServerConstants.CMD_COURSE_UPDATE_USER_COURSES:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_COURSE_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_ADD_COURSE_NUMBERS_STRING, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_REMOVE_COURSE_NUMBERS_STRING, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[4], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;

                    case DatabaseServerConstants.CMD_FACULTY:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_FACULTY_REQ_ALL_IDS:
                            case DatabaseServerConstants.CMD_FACULTY_REQ_ALL_NAMES:
                                result = request_command(packet_data);
                                break;

                            case CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_COURSE_FACULTY_NAME, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            case DatabaseServerConstants.CMD_FACULTY_REQ_FACULTY_RECORD:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_COURSE_FACULTY_ID, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;

                    case DatabaseServerConstants.CMD_USER:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_USER_GET_USER_INFO:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;

                    case DatabaseServerConstants.CMD_CHATBOT:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_CHATBOT_GET_BOT_ANSWER:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_CHATBOT_ISSUES, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;

                    case DatabaseServerConstants.CMD_TRACKER:
                        switch (sub_command) {
                            case DatabaseServerConstants.CMD_TRACKER_GET_USER_TRACKERS:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                result = request_command(packet_data);
                                break;

                            case DatabaseServerConstants.CMD_TRACKER_ADD_USER_TRACKER:
                            case DatabaseServerConstants.CMD_TRACKER_REMOVE_USER_TRACKER:
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_STUDENT_COURSE_USERNAME, "UTF-8") +
                                        "=" + URLEncoder.encode(user_name, "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_COURSE_NUMBER, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[3], "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_ASSIGNMENT, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[4], "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_DUE_DATE, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[5], "UTF-8");
                                packet_data += "&" + URLEncoder.encode(
                                        DatabaseServerConstants.DB_TRACKER_DUE_TIME, "UTF-8") +
                                        "=" + URLEncoder.encode((String) args[6], "UTF-8");
                                result = request_command(packet_data);
                                break;

                            default:
                                return null;
                        }
                        break;
                    default:
                        return null;
                }
            }
        } catch (Exception e) {
            resp_message = new String("Exception: " + e.getMessage());
            status = BackgroundTaskCallback.RESPONSE_ERROR;
        }

        // Build Response payload
        responseStatus.put(DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME, user_name);
        responseStatus.put(BackgroundTaskCallback.REQUEST_CMD, command);
        responseStatus.put(BackgroundTaskCallback.REQUEST_SCMD, sub_command);
        responseStatus.put(BackgroundTaskCallback.RESPONSE_CODE, status);
        responseStatus.put(BackgroundTaskCallback.RESPONSE_MESSAGE, resp_message);
        responseData.addAll(result);
        return null;
    }

    @Override
    protected void onPostExecute(String notUsed) {
        callback.backgroundTaskCallback(responseStatus, responseData);
    }

    private List<JSONObject> request_command(String data) throws
            IOException, JSONException {
        List<JSONObject> result = new ArrayList<JSONObject>();
        try{
            URL url = new URL(DatabaseServerConstants.DB_LSA_URL);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    if(!line.isEmpty()) {
                        JSONArray jArray = new JSONArray(line);
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jObj = jArray.getJSONObject(i);
                            Log.i("DB Response:", "" + jObj.toString());
                            result.add(jObj);
                        }
                    }
                } catch (JSONException err){
                    throw err;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }
}