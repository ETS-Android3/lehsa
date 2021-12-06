/*
* This activity displays the username, full name, interests, update frequency
* Also, it has buttons to navigate to the configure, find friends and get news activities
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_COURSE;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_COURSE_GET_ALL_COURSE_NUMBERS;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_USER;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_COURSE_GET_USER_COURSES;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER_GET_USER_TRACKERS;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_USER_GET_USER_INFO;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_COURSE_COURSE_NUMBER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_FIRSTNAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_LASTNAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_ASSIGNMENT;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_COURSE_NUMBER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_DATE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_TIME;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.SessionManager;

public class Home extends AppCompatActivity implements BackgroundTaskCallback, Runnable {
    private static final String TAG = Home.class.getSimpleName();

    private MaterialButton btnTracker, btnFaculty;

    FloatingActionButton chatbotBtn;
    private MaterialButton btnLogout;
    private SessionManager session;
    private Bundle bundle;
    private TextView headerView;
    private String loginUserName;
    
    private TextView courseNumbsView;
    private TextView usernameView;
    private JSONObject userInfo;
    private volatile ArrayList<String> allCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> uCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> uAssignments = new ArrayList<String>();
    private volatile ArrayList<String> uDueDates = new ArrayList<String>();
    private volatile ArrayList<String> uDueTimes = new ArrayList<String>();
    private volatile List<JSONObject> userConfig = new ArrayList<>();

    // Thread Related data
    private static final int THREAD_SLEEP_TIME_IN_MS = 1000;
    Thread updateTrackerThread = new Thread(Home.this);
    private volatile boolean running = true;
    private volatile Boolean isDbRespReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        headerView = findViewById(R.id.home_header);


        courseNumbsView = findViewById(R.id.courseNum);
        usernameView = findViewById(R.id.username);

        btnLogout = findViewById(R.id.logout);
        btnTracker = findViewById(R.id.feature_tracker);
        btnFaculty = findViewById(R.id.feature_faculty);
        chatbotBtn = (FloatingActionButton) findViewById(R.id.chatbot);

        bundle = getIntent().getExtras();
        session = new SessionManager(Home.this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try{
            loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        } catch (Exception e) {
            loginUserName = session.getLogggedInUsername();
        }
        init();
    }

    private void init() {
        getNextRecord("userInfo");
        btnLogout.setOnClickListener(v -> logoutUser());
        btnFaculty.setOnClickListener(v -> launchFaculty());
        btnTracker.setOnClickListener(v -> launchTracker());
//        btnFaq.setOnClickListener(v -> launchFaq());
        chatbotBtn.setOnClickListener(v -> launchChatbot());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {// User chose the "Settings" item, show the app settings UI...
            launchConfiguration();
            return true;
        }// If we got here, the user's action was not recognized.
        // Invoke the superclass to handle it.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        while(running) {
            try {
                // Sleep for update frequency cycle/refresh time
                Thread.sleep(THREAD_SLEEP_TIME_IN_MS);

            } catch (InterruptedException e) {
                Log.d(TAG, "UpdateTrackerThread: " + "Thread interrupted, so exiting....");
                running = false;
            }
        }
    }

    private void getNextRecord(String recordType){
        isDbRespReceived = false;
        switch(recordType){
            case "userInfo":
                getUserInfo(loginUserName);
                break;

            case "userCourses":
                getUserCourseNumbers(loginUserName);
                break;

            case "allCourseNumbers":
                getAllCourseNumbers(loginUserName);
                break;

            case "userTrackers":
                getUserTrackers(loginUserName);
                break;
        }
    }

    //Launches the settings activity
    private void launchConfiguration() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        b.putStringArrayList("allCourseNumbers", allCourseNumbers);
        Intent intent = new Intent(Home.this, Settings.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    //Launches the FacultySearch activity
    private void launchFaculty() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(Home.this, FacultySearch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    //Launches the Get news activity
    private void launchTracker() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(Home.this, Tracker.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void launchChatbot() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(Home.this, Chatbot.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void startUpdateTrackerThread() {
        if (this.updateTrackerThread.getState() == Thread.State.NEW) {
            Log.d(TAG, "UpdateTrackerThread: Starting thread: " + updateTrackerThread);
            this.updateTrackerThread.start();
            Log.d(TAG, "UpdateTrackerThread: Background process successfully started");
        }
    }

    private void stopUpdateTrackerThread() {
        Log.i(TAG, "UpdateTrackerThread: Stopping thread: "+ updateTrackerThread);
        if (updateTrackerThread != null) {
            running = false;
            updateTrackerThread.interrupt();
            Log.i(TAG, "UpdateTrackerThread: Thread successfully stopped.");
        }
    }

    //Invoked when user clicks on logout button
    private void logoutUser() {
        Log.i(TAG, "logoutUser: Logging out...");
        // Let session manager to delete the saved user/session info
        session.setLogin(false, null);

        // Stop the thread
        stopUpdateTrackerThread();

        Intent intent = new Intent(Home.this, SignIn.class);
        startActivity(intent);
        finish();
    }

    //Retrieves user information from the database
    private void getUserInfo(final String username) {
        final DatabaseServer db_server = new DatabaseServer(Home.this);
        db_server.execute(CMD_USER, CMD_USER_GET_USER_INFO, username);
    }

    private void getUserCourseNumbers(final String username) {
        final DatabaseServer db_server = new DatabaseServer(Home.this);
        db_server.execute(CMD_COURSE, CMD_COURSE_GET_USER_COURSES, username);
    }

    private void getAllCourseNumbers(final String username) {
        final DatabaseServer db_server = new DatabaseServer(Home.this);
        db_server.execute(CMD_COURSE, CMD_COURSE_GET_ALL_COURSE_NUMBERS, username);
    }

    private void getUserTrackers(final String username) {
        final DatabaseServer db_server = new DatabaseServer(Home.this);
        db_server.execute(CMD_TRACKER, CMD_TRACKER_GET_USER_TRACKERS, username);
    }

    //Display user information in the home screen
    private void displayUserInfo(String fName){
        headerView.setText(Html.fromHtml(fName));
        //start thread
        // startUpdateTrackerThread();
    }

    private void displayUserCourses(String courses){
        if (courses != null) {
            if (!courses.isEmpty()) {
                courseNumbsView.setText(Html.fromHtml("<b>Courses : </b>" + courses));
            }
        }

        //start thread
        startUpdateTrackerThread();
    }

    private String convertListToString(List<String> dataList){
        String result = "";
        for (String e: dataList)
            result += e.trim() + ", ";

        if(!result.isEmpty()) {
            result = result.trim().replaceAll(",+$", "");
        }
        return result;
    }

    private void handleUserInfoResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            try {
                userInfo = Objects.requireNonNull(respData.get(0));
                String fullName = userInfo.getString(DB_STUDENT_DETAILS_FIRSTNAME) +
                        " " + userInfo.getString(DB_STUDENT_DETAILS_LASTNAME);
                displayUserInfo(fullName);
            } catch (JSONException err) {
                Log.d(TAG, err.toString());
                Toast.makeText(getApplicationContext(),
                        "Failed to fetch user info from the database", Toast.LENGTH_LONG).show();
            }
        }
        getNextRecord("userCourses");
    }

    private void handleUserCoursesResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            try {
                for (JSONObject c : respData) {
                    String cNum = c.getString(DB_TRACKER_COURSE_NUMBER);
                    if (cNum != null ){
                        if (!cNum.isEmpty()){
                            uCourseNumbers.add(c.getString(DB_TRACKER_COURSE_NUMBER));
                        }
                    }
                }
                String uCourseNumbersStr = convertListToString(uCourseNumbers);
                displayUserCourses(uCourseNumbersStr);
            } catch (JSONException err) {
                Log.d(TAG, err.toString());
                Toast.makeText(getApplicationContext(),
                        "Failed to fetch user info from the database", Toast.LENGTH_LONG).show();
            }
        }
        getNextRecord("allCourseNumbers");
    }

    private void handleAllCourseNumbersResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            userConfig = respData;
            try {
                for (JSONObject c : userConfig) {
                    allCourseNumbers.add(c.getString(DB_STUDENT_COURSE_COURSE_NUMBER));
                }
            } catch (JSONException err) {
                Log.d(TAG, err.toString());
                Toast.makeText(getApplicationContext(),
                        "Failed to fetch all course numbers from the database", Toast.LENGTH_LONG).show();
            }
        }
        getNextRecord("userTrackers");
    }

    private void handleUserTrackersResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            userConfig = respData;
            try {
                for (JSONObject c : userConfig) {
                    uAssignments.add(c.getString(DB_TRACKER_ASSIGNMENT));
                    uDueDates.add(c.getString(DB_TRACKER_DUE_DATE));
                    uDueTimes.add(c.getString(DB_TRACKER_DUE_TIME));
                }
                String uCourseNumbersStr = convertListToString(uCourseNumbers);
                displayUserCourses(uCourseNumbersStr);
            } catch (JSONException err) {
                Log.d(TAG, err.toString());
                Toast.makeText(getApplicationContext(),
                        "Failed to fetch user info from the database", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_ERROR)) {
            Log.e(TAG, "Failed to fetch user details: " + respStatus.get(RESPONSE_MESSAGE));
            Toast.makeText(getApplicationContext(), "Invalid user info", Toast.LENGTH_LONG).show();
            isDbRespReceived = true;
            return;
        }

        switch(Objects.requireNonNull(respStatus.get(REQUEST_CMD))) {
            case CMD_USER:
                if (CMD_USER_GET_USER_INFO.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                    handleUserInfoResponse(respData);
                }
            break;

            case CMD_COURSE:
                switch(Objects.requireNonNull(respStatus.get(REQUEST_SCMD))) {
                    case CMD_COURSE_GET_USER_COURSES:
                        handleUserCoursesResponse(respData);
                    break;

                    case CMD_COURSE_GET_ALL_COURSE_NUMBERS:
                        handleAllCourseNumbersResponse(respData);
                    break;
                }
            break;

            case CMD_TRACKER:
                if (CMD_TRACKER_GET_USER_TRACKERS.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                    handleUserTrackersResponse(respData);
                }
            break;
        }

        // Set the flag after processing the response
        isDbRespReceived = true;
    }
}