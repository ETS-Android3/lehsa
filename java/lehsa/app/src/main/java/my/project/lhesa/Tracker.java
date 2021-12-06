/*
* A user can get his/her friend's loaction by typing in the regex for either username or fullname
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER_GET_USER_TRACKERS;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_ASSIGNMENT;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_COURSE_NUMBER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_DATE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_TIME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Tracker extends AppCompatActivity implements
        BackgroundTaskCallback, SearchView.OnQueryTextListener {
    private static final String TAG = Tracker.class.getSimpleName();

    Bundle bundle;
    String loginUserName;
    FloatingActionButton addRemainderBtn, removeRemainderBtn;
    RecyclerView mRecyclerview;
    ArrayList<TrackerModel> dataHolder = new ArrayList<TrackerModel>();                                               //Array list to add reminders and display in recyclerview
    myAdapter adapter;
    private volatile ArrayList<String> uCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> userRemainders = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracker);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        uCourseNumbers = bundle.getStringArrayList("uCourseNumbers");

        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        addRemainderBtn = (FloatingActionButton) findViewById(R.id.add_remainder);                     //Floating action button to change activity
        removeRemainderBtn = (FloatingActionButton) findViewById(R.id.remove_remainder);

        adapter = new myAdapter(dataHolder);
        mRecyclerview.setAdapter(adapter);

        requestALlRemainders(loginUserName);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        init();
    }

    @Override
    public void onBackPressed() {
        finish();                                                                                   //Makes the user to exit form the app
        super.onBackPressed();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        launchHomeActivity();
        return true;
    }

    private void init() {
        addRemainderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchAddRemainderActivity();
            }
        });

        removeRemainderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRemoveRemainderActivity();
            }
        });
    }

    private void launchHomeActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(Tracker.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void launchAddRemainderActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(Tracker.this, TrackerAdd.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void launchRemoveRemainderActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uRemainders", userRemainders);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(Tracker.this, TrackerDelete.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void requestALlRemainders(final String username) {
        final DatabaseServer db_server = new DatabaseServer(Tracker.this);
        db_server.execute(CMD_TRACKER, CMD_TRACKER_GET_USER_TRACKERS, username);
    }

    private void displayRemainder(String cNumb, String title, String date, String time){
        String text = title;
        if (uCourseNumbers != null) {
            if (uCourseNumbers.size() > 0) {
                for (String c : uCourseNumbers) {
                    if (c.equals(cNumb)) {
                        text = cNumb + ": " + title;
                        break;
                    }
                }
            }
        }
        TrackerModel model = new TrackerModel(text, date, time);
        dataHolder.add(model);
    }

    private void handleUserRemaindersResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            try {
                for (JSONObject c : respData) {
                    String cNum = c.getString(DB_TRACKER_COURSE_NUMBER);
                    String title = c.getString(DB_TRACKER_ASSIGNMENT);
                    String date = c.getString(DB_TRACKER_DUE_DATE);
                    String time = c.getString(DB_TRACKER_DUE_TIME);
                    displayRemainder(cNum, title, date, time);
                    userRemainders.add(c.toString());
                }
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
            return;
        }

        if (CMD_TRACKER.equals(Objects.requireNonNull(respStatus.get(REQUEST_CMD)))) {
            if (CMD_TRACKER_GET_USER_TRACKERS.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                handleUserRemaindersResponse(respData);
            }
        }
    }
}