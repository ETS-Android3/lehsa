package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_TRACKER_ADD_USER_TRACKER;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;


//this class is to add the take the reminders from the user and inserts into database
public class TrackerAdd extends AppCompatActivity implements BackgroundTaskCallback{
    private static final String TAG = TrackerAdd.class.getSimpleName();
    Button mSubmitBtn, mDateBtn, mTimeBtn;
    EditText mTitleEdit;
    String timeToNotify;
    Bundle bundle;
    String loginUserName;
    private Spinner courseNumbersList;
    private volatile ArrayList<String> uCourseNumbers = new ArrayList<String>();
    private static final String courseUnKnown = "None";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_reminder);

        mTitleEdit = (EditText) findViewById(R.id.editTitle);
        mDateBtn = (Button) findViewById(R.id.btnDate);                                             //assigned all the material reference to get and set data
        mTimeBtn = (Button) findViewById(R.id.btnTime);
        mSubmitBtn = (Button) findViewById(R.id.btnSbumit);
        courseNumbersList = (Spinner)findViewById(R.id.rem_course_number);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        uCourseNumbers = bundle.getStringArrayList("uCourseNumbers");

        String defaultSel = courseUnKnown;
        List<String> spinnerArray =  new ArrayList<String>();
        if (uCourseNumbers != null) {
            if (uCourseNumbers.size() > 0) {
                defaultSel = uCourseNumbers.get(0);
                for (String e: uCourseNumbers) {
                    spinnerArray.add(e);
                }
            }
        }
        spinnerArray.add(courseUnKnown);
        spinner_set_default_value(courseNumbersList, spinnerArray, defaultSel);

        mTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTime();                                                                       //when we click on the choose time button it calls the select time method
            }
        });

        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate();
            }                                        //when we click on the choose date button it calls the select date method
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitleEdit.getText().toString().trim();                               //access the data form the input field
                String date = mDateBtn.getText().toString().trim();                                 //access the date form the choose date button
                String time = mTimeBtn.getText().toString().trim();                                 //access the time form the choose time button
                String courseNumber = String.valueOf(courseNumbersList.getSelectedItem());

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
                } else {
                    if (time.equals("time") || date.equals("date")) {                                               //shows toast if date and time are not selected
                        Toast.makeText(getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT).show();
                    } else {
                        processInsert(courseNumber, title, date, time);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        launchTracker();
        return true;
    }

    private void spinner_set_default_value(Spinner mySpinner, List<String> spinnerArray, String defaultValue){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        int spinnerPosition = adapter.getPosition(defaultValue);
        //Set the default according to value
        mySpinner.setSelection(spinnerPosition);
        mySpinner.setAdapter(adapter);
    }

    //Launches the Get news activity
    private void launchTracker() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("uCourseNumbers", uCourseNumbers);
        Intent intent = new Intent(TrackerAdd.this, Tracker.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void processInsert(String courseNum, String title, String date, String time) {
        String text = title;
        if (courseNum != courseUnKnown)
            text = courseNum+ ": " + title;
        addReminder(courseNum, title, date, time);                  //inserts the title,date,time into sql lite database
        setAlarm(text, date, time);                     //calls the set alarm method to set alarm
        mTitleEdit.setText("");
    }

    public void addReminder(String courseNum, String title, String date, String time){
        final DatabaseServer db_server = new DatabaseServer(TrackerAdd.this);
        Log.i(TAG, "Adding reminder = " + title + ", date =" + date + ", time ="+time);
        db_server.execute(CMD_TRACKER, CMD_TRACKER_ADD_USER_TRACKER, loginUserName, courseNum, title, date, time);
    }

    private void selectTime() {                                                                     //this method performs the time picker task
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        timeToNotify = hourOfDay + ":" + minute;                                                        //temp variable to store the time to set alarm
                        mTimeBtn.setText(FormatTime(hourOfDay, minute));
                    }
                },
                hour,
                minute,
                true
        );
        tpd.setMinTime(hour, minute, 0); // MIN: hours, minute, seconds
        tpd.show(getFragmentManager(), "TimePickerDialog");
    }

    private void selectDate() {                                                                     //this method performs the date picker task
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDateBtn.setText(day + "-" + (month + 1) + "-" + year);                             //sets the selected date as test for button
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr farmat and assigns am or pm
        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }

        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }
        return time;
    }

    private void setAlarm(String text, String date, String time) {
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);                   //assigning alarm manager object to set alarm

        Intent intent = new Intent(getApplicationContext(), TrackerAlarmBroadcast.class);
        intent.putExtra("event", text);                                                       //sending data to alarm class to create channel and notification
        intent.putExtra("time", date);
        intent.putExtra("date", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String dateAndTime = date + " " + timeToNotify;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try {
            Date date1 = formatter.parse(dateAndTime);
            am.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleRemainderUpdateResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            Log.i(TAG, "User new remainder updated in the database");
        }
        launchTracker();
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_ERROR)) {
            Log.e(TAG, "Failed to update user details: " + respStatus.get(RESPONSE_MESSAGE));
            Toast.makeText(getApplicationContext(), "Failed to update!", Toast.LENGTH_LONG).show();
            launchTracker();
            return;
        }

        if (CMD_TRACKER.equals(Objects.requireNonNull(respStatus.get(REQUEST_CMD)))) {
            if (CMD_TRACKER_ADD_USER_TRACKER.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                handleRemainderUpdateResponse(respData);
            }
        }
    }
}