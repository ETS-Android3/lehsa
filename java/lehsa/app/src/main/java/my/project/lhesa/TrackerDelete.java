/*
* A user can get his/her friend's loaction by typing in the regex for either username or fullname
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER_GET_USER_TRACKERS;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER_REMOVE_USER_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_ASSIGNMENT;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_COURSE_NUMBER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_DATE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_TRACKER_DUE_TIME;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;

public class TrackerDelete extends AppCompatActivity implements
        BackgroundTaskCallback, SearchView.OnQueryTextListener {
    private static final String TAG = TrackerDelete.class.getSimpleName();

    Bundle bundle;
    String loginUserName;

    private volatile ArrayList<String> uCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> usersRemainders = new ArrayList<String>();
    private volatile List<JSONObject> dbAllRemainders = new ArrayList<>();

    private volatile String[] allRemainders; // To display all courses for the user selection
    private volatile boolean[] checkedItems;
    private volatile ArrayList<Integer> mRemainderPosition = new ArrayList<>();
    private static final String courseUnKnown = "None";
    private volatile ArrayList<String> removeRemainders = new ArrayList<String>();
    private volatile int removeRecordIndex = 0;
    private static final String recordDelimiter = "_";

    public TrackerDelete() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_remainder);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        uCourseNumbers = bundle.getStringArrayList("uCourseNumbers");
        ArrayList<String> list = bundle.getStringArrayList("uRemainders");
        for(String e: list){
            try {
                dbAllRemainders.add(new JSONObject(e));
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }

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
        launchTrackerActivity();
        return true;
    }

    private void init() {
        allRemainders = new String[dbAllRemainders.size()];
        checkedItems = new boolean[allRemainders.length];

        for (int i = 0; i < dbAllRemainders.size(); i++) {
            try {
                JSONObject c = dbAllRemainders.get(i);
                String cNum = c.getString(DB_TRACKER_COURSE_NUMBER);
                String title = c.getString(DB_TRACKER_ASSIGNMENT);
                String date = c.getString(DB_TRACKER_DUE_DATE);
                String time = c.getString(DB_TRACKER_DUE_TIME);

                String text = title;
                if (cNum != null) {
                    if (!cNum.isEmpty() && !cNum.equals(courseUnKnown)) {
                        text = cNum + recordDelimiter + title;
                    }
                }
                allRemainders[i] = text + recordDelimiter + date + recordDelimiter + time;
                checkedItems[i] = true;
                mRemainderPosition.add(i);
            } catch (JSONException e) {
                Log.e(TAG, "No object found in the User remainders list");
                e.printStackTrace();
            }
        }

        launchRemoveRemainderDialog();
    }

    private void launchTrackerActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(TrackerDelete.this, Tracker.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void launchRemoveRemainderDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(TrackerDelete.this);
        mBuilder.setTitle(R.string.select_remainders);
        mBuilder.setMultiChoiceItems(allRemainders, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    mRemainderPosition.add(position);
                }else{
                    mRemainderPosition.remove((Integer.valueOf(position)));
                }
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.i(TAG, "Remainders = " + userNewRemaindersListToString());
                processRemovedRemainders();
            }
        });
        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                launchTrackerActivity();
            }
        });
        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mRemainderPosition.clear();
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = mDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < checkedItems.length; i++) {
                            checkedItems[i] = false;
                            mRemainderPosition.clear();
                            mDialog.getListView().setItemChecked(i, false);
                        }
                    }
                });
            }
        });

        Window window = mDialog.getWindow();
//        window.setLayout(Constraints.LayoutParams.MATCH_PARENT, Constraints.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE)); // TRANSPARENT

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.TOP;
        params.x = 0;
        params.y = 250;
        params.width = Constraints.LayoutParams.MATCH_PARENT;
        params.height = Constraints.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);

        mDialog.show();
    }

    private String userNewRemaindersListToString() {
        String item = "";
        for (int i = 0; i < mRemainderPosition.size(); i++) {
            item = item + allRemainders[mRemainderPosition.get(i)];
            if (i != mRemainderPosition.size() - 1) {
                item = item + ", ";
            }
        }
        return item;
    }

    private void processRemovedRemainders(){
        ArrayList<String> uNewRemainders = new ArrayList<String>();

        // First, build new course list
        for (int i = 0; i < mRemainderPosition.size(); i++) {
            uNewRemainders.add(allRemainders[mRemainderPosition.get(i)]);
        }
        // Now, go through all remainders and record deleted
        for (String r : allRemainders){
            if (!uNewRemainders.contains(r)) {
                removeRemainders.add(r);
            }
        }

        if (removeRemainders.size() > 0) {
            removeRemainders();
        } else {
            launchTrackerActivity();
        }
    }

    private void removeRemainders(){
        if (removeRecordIndex < removeRemainders.size()) {
            removeReminderFromDatabase(removeRecordIndex);
            removeRecordIndex += 1;
        } else {
            launchTrackerActivity();
        }
    }
    private void removeReminderFromDatabase(int idx){
        String[] record = removeRemainders.get(idx).split(recordDelimiter);
        String courseNum = courseUnKnown;
        int sIdx = 0;
        if (record.length == 4) {
            courseNum = record[sIdx];
            sIdx +=1;
        } else if (record.length > 4) {
            Log.e(TAG, "Found more than expected fields, expected=4, detected="+record.length);
            launchTrackerActivity();
        }
        String title = record[sIdx++];
        String date = record[sIdx++];
        String time = record[sIdx];

        final DatabaseServer db_server = new DatabaseServer(TrackerDelete.this);
        Log.i(TAG, "Adding reminder = " + title + ", date =" + date + ", time ="+time);
        db_server.execute(CMD_TRACKER, CMD_TRACKER_REMOVE_USER_TRACKER, loginUserName, courseNum, title, date, time);
    }

    private void requestALlRemainders(final String username) {
        final DatabaseServer db_server = new DatabaseServer(TrackerDelete.this);
        db_server.execute(CMD_TRACKER, CMD_TRACKER_GET_USER_TRACKERS, username);
    }

    private long findDifference(String online) {
        // SimpleDateFormat converts the string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long diffInSeconds = 0;
        try {
            // parse method is used to parse the text from a string to produce the date
            Date d1 = sdf.parse(sdf.format(new Date()));
            Date d2 = sdf.parse(online);

            // Calculate time difference in milliseconds
            long diffInMillies = d1.getTime() - d2.getTime();

            diffInSeconds = (diffInMillies / 1000);

            System.out.println(diffInSeconds + " seconds");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diffInSeconds;
    }

    private void handleUserRemaindersResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            removeRemainders();
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
            if (CMD_TRACKER_REMOVE_USER_TRACKER.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                handleUserRemaindersResponse(respData);
            }
        }
    }
}