package my.project.lhesa.utils;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface BackgroundTaskCallback {
    String RESPONSE_SUCCESS = "resp_success";
    String RESPONSE_ERROR = "resp_error";
    String RESPONSE_CODE = "resp_code";
    String REQUEST_TYPE = "req_type";
    String REQUEST_CMD = "req_command";
    String REQUEST_SCMD = "req_sub_command";
    String RESPONSE_MESSAGE = "resp_message";

    void  backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData);
}
