/*
* A user can get his/her friend's loaction by typing in the regex for either username or fullname
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY_REQ_ALL_NAMES;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_EMAIL;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_NAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_OFFICE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_PHONE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_POSITION;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_RESEARCH_LINK;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.Utils;

public class FacultySearch extends AppCompatActivity implements
        BackgroundTaskCallback, SearchView.OnQueryTextListener {
    private static final String TAG = FacultySearch.class.getSimpleName();

    private Button btnSearch, btnFaceDetect;
    SearchView editSearch;

    Bundle bundle;
    String loginUserName;

    private volatile ArrayList<String> facultyNamesList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty);

        btnSearch = (Button)findViewById(R.id.faculty_search);
        btnFaceDetect = (Button)findViewById(R.id.faculty_face);
        editSearch = (SearchView) findViewById(R.id.faculty_name);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        requestAllFacultyNames();
        init();
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
        Intent dashboard = new Intent(this, Home.class);
        dashboard.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(dashboard);
        finish();
        return true;
    }

    private void init() {
        btnSearch.setOnClickListener(v -> {
            // Hide Keyboard
            Utils.hideSoftKeyboard(FacultySearch.this);

            String faculty_name = Objects.requireNonNull(editSearch.getQuery()).toString().trim();

            if (!faculty_name.isEmpty()) {
                faculty_name = checkIfFacultyExists(faculty_name);
                if (faculty_name != null){
                    requestFacultyInfo(faculty_name);
                }else {
                    Log.e(TAG, "FacultySearch not found: "+ faculty_name);
                    Toast.makeText(getApplicationContext(), "FacultySearch not found", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Enter valid search keyword!", Toast.LENGTH_LONG).show();
            }
        });

        btnFaceDetect.setOnClickListener(v -> launchFaceDetect());

    }

    private String checkIfFacultyExists(String facultyName){
        for (String e : facultyNamesList) {
            if (e.toLowerCase().contains(facultyName.toLowerCase())) {
                return e;
            }
        }
        return null;
    }

    //Launches the Tracker activity
    private void launchFaceDetect() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putStringArrayList("names", facultyNamesList);
        Intent intent = new Intent(FacultySearch.this, FacultyFaceDetect.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void launchFacultyDisplay(String message) {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putString("message", message);
        Intent intent = new Intent(FacultySearch.this, FacultyDisplay.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void requestFacultyInfo(String faculty_name){
        final DatabaseServer db_server = new DatabaseServer(FacultySearch.this);
        db_server.execute(CMD_FACULTY, CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME, loginUserName, faculty_name);
    }

    private void requestAllFacultyNames(){
        final DatabaseServer db_server = new DatabaseServer(FacultySearch.this);
        db_server.execute(CMD_FACULTY, CMD_FACULTY_REQ_ALL_NAMES, loginUserName);
    }

    private String constructDisplayString(String name, String value){
        if (value != null){
            if (!value.isEmpty()) {
                return "  " + name + ": " + value + "\n";
            }
        }
        return "";
    }

    private void processFacultyRecord(List<JSONObject> respData){
        try {
            for (JSONObject element : respData) {
                JSONObject facRecordData = Objects.requireNonNull(element);
                String finalRecord = new String();
                finalRecord += constructDisplayString("Name", facRecordData.getString(DB_COURSE_FACULTY_NAME));
                finalRecord += constructDisplayString("Email", facRecordData.getString(DB_COURSE_FACULTY_EMAIL));
                finalRecord += constructDisplayString("Phone", facRecordData.getString(DB_COURSE_FACULTY_PHONE));
                finalRecord += constructDisplayString("Position", facRecordData.getString(DB_COURSE_FACULTY_POSITION));
                finalRecord += constructDisplayString("Office", facRecordData.getString(DB_COURSE_FACULTY_OFFICE));
                finalRecord += constructDisplayString("Research", facRecordData.getString(DB_COURSE_FACULTY_RESEARCH_LINK));
                launchFacultyDisplay(finalRecord);
            }
        } catch (JSONException err) {
            Log.d(TAG, "FacultySearch record parse error:" + err.toString());
            Toast.makeText(getApplicationContext(),
                    "Failed to parse faculty name info from the database response", Toast.LENGTH_LONG).show();
        }
    }

    private void handleAllFacultyNamesResponse(List<JSONObject> respData){
        try {
            for (JSONObject element : respData) {
                JSONObject facRecordData = Objects.requireNonNull(element);
                facultyNamesList.add( facRecordData.getString(DB_COURSE_FACULTY_NAME));
            }
        } catch (JSONException err) {
            Log.d(TAG, "FacultySearch Names fetch error:" + err.toString());
            Toast.makeText(getApplicationContext(),
                    "Failed to parse faculty names info from the database response", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (CMD_FACULTY.equals(Objects.requireNonNull(respStatus.get(REQUEST_CMD)))) {
            switch (Objects.requireNonNull(respStatus.get(REQUEST_SCMD))) {
                case CMD_FACULTY_REQ_ALL_NAMES:
                    if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_SUCCESS)) {
                        if (respData.size() > 0) {
                            handleAllFacultyNamesResponse(respData);
                        } else {
                            Log.e(TAG, "Received an empty record from the Database server");
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch faculty names: " + respStatus.get(RESPONSE_MESSAGE));
                    }
                    break;
                case CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME:
                    if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_SUCCESS)) {
                        if (respData.size() > 0) {
                            processFacultyRecord(respData);
                        } else {
                            Log.e(TAG, "Received an empty record from the Database server");
                        }
                    } else {
                        Log.e(TAG, "Failed to fetch faculty info: " + respStatus.get(RESPONSE_MESSAGE));
                        Toast.makeText(getApplicationContext(), "Failed to fetch faculty info",
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    }
}