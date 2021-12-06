/*
* A user can enter the full name, frequency with which location should be updates and interests
*/

package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_DELETE_ACCOUNT;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_LOGIN;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_UPDATE_PASSWORD;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_COURSE;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_COURSE_UPDATE_USER_COURSES;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_ID;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.SessionManager;
import my.project.lhesa.utils.Utils;

public class Settings extends AppCompatActivity implements BackgroundTaskCallback {
    private static final String TAG = Settings.class.getSimpleName();
    private Button btnCourse, btnUpdatePw, btnCDeleteAccount;
    private String loginUserName;
    private SessionManager session;

    private volatile ArrayList<String> allCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> uCourseNumbers = new ArrayList<String>();
    private volatile ArrayList<String> uAssignment = new ArrayList<String>();
    private volatile ArrayList<String> uDueDate = new ArrayList<String>();

    private volatile String[] allCourseList; // To display all courses for the user selection
    private volatile boolean[] checkedItems;
    private volatile ArrayList<Integer> mUserCoursesPosition = new ArrayList<>();
    private volatile boolean isPasswordVerified =false;
    private volatile String processingFunction;
    private volatile  String newPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        btnCourse = (Button)findViewById(R.id.config_course_btn);
        btnUpdatePw = (Button)findViewById(R.id.config_update_password);
        btnCDeleteAccount = (Button)findViewById(R.id.config_delete_account);

        Bundle bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        uCourseNumbers = bundle.getStringArrayList("uCourseNumbers");
        uAssignment = bundle.getStringArrayList("uAssignments");
        uDueDate = bundle.getStringArrayList("uDueDates");
        allCourseNumbers = bundle.getStringArrayList("allCourseNumbers");

        allCourseList = allCourseNumbers.toArray(new String[0]);
        checkedItems = new boolean[allCourseList.length];

        // Update already configured users
        for (String course: uCourseNumbers) {
            for (int i = 0; i < allCourseList.length; i++) {
                if (course.equals(allCourseList[i])){
                    checkedItems[i] = true; // update this based on uCourseNumbers
                    mUserCoursesPosition.add(i);
                    break;
                }
            }
        }
        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        init();
    }

    private void init() {
        btnCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCourses();
            }
        });

        btnUpdatePw.setOnClickListener(v -> updatePasswordDialog());
        btnCDeleteAccount.setOnClickListener(v -> deleteAccountDialog());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        launchHomeActivity();
        return true;
    }

    private boolean validateText(String userTextName, String userTextValue){
        if (userTextValue.isEmpty()) {
            Log.e(TAG, userTextName + " is empty");
            Toast.makeText(getApplicationContext(), userTextName + " is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userTextName.equals("OldPassword") || userTextName.equals("Password")) {
            if (!Utils.isValidPassword(userTextValue)) {
                Log.e(TAG, Utils.getPasswordRequirementsMessage());
                Toast.makeText(getApplicationContext(), Utils.getPasswordRequirementsMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (!Utils.isValidName(userTextValue)) {
                Log.e(TAG, Utils.getNameRequirementsMessage(userTextName));
                Toast.makeText(getApplicationContext(), Utils.getNameRequirementsMessage(userTextName), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void updatePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.update_password, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Update Password")
                .setCancelable(false)
                .setPositiveButton("Update", (dialog, which) -> updatePasswordReadings(dialogView))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void deleteAccountDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.verify_password, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Delete Account")
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> deleteAccountReadings(dialogView))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    //Allows user to update password
    private void updatePasswordReadings(View v) {
        TextInputLayout editOldPassword = v.findViewById(R.id.edit_verify_password);
        TextInputLayout editPassword = v.findViewById(R.id.edit_password);
        TextInputLayout editConfirmPassword = v.findViewById(R.id.edit_confirm_password);

        String oldPass = Objects.requireNonNull(editOldPassword.getEditText()).getText().toString().trim();
        String pWord = Objects.requireNonNull(editPassword.getEditText()).getText().toString().trim();
        String confirmPassWord = Objects.requireNonNull(editConfirmPassword.getEditText()).getText().toString().trim();

        // Check for empty data in the form
        if (validateText("OldPassword", oldPass) &&
                validateText("Password", pWord) &&
                validateText("Password", confirmPassWord)) {
            if (!oldPass.equals(pWord)) {
                if (pWord.equals(confirmPassWord)) {
                    processingFunction="updatePassword";
                    newPassword=pWord;
                    verifyPassword(oldPass);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Passwords are not matching", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "The username and password should not be the same.");
                Toast.makeText(getApplicationContext(),
                        "The username and password should not be the same.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Allows user to update password
    private void deleteAccountReadings(View v) {
        TextInputLayout verifyPassword = v.findViewById(R.id.edit_verify_password);
        String vPass = Objects.requireNonNull(verifyPassword.getEditText()).getText().toString().trim();

        // Check for empty data in the form
        if (validateText("Password", vPass)) {
            processingFunction="deleteAccount";
            verifyPassword(vPass);
        }
    }

    //Database connection to update password
    private void updatePassword(final String password) {
        showDialog("Please wait...");
        final DatabaseServer db_server = new DatabaseServer(Settings.this);
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_UPDATE_PASSWORD, loginUserName, password);
    }

    private void deleteAccountFromDb(){
        showDialog("Please wait...");
        final DatabaseServer db_server = new DatabaseServer(Settings.this);
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_DELETE_ACCOUNT, loginUserName);
    }


    private void showDialog(String title) {
        Utils.showProgressDialog(Settings.this, title);
    }

    private void hideDialog() {
        Utils.hideProgressDialog(Settings.this);
    }

    private void verifyPassword(String password){
        final DatabaseServer db_server = new DatabaseServer(Settings.this);
        isPasswordVerified = false;
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_LOGIN, loginUserName, password);
    }

    private void launchHomeActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(Settings.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void selectCourses(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this);
        mBuilder.setTitle(R.string.select_courses);
        mBuilder.setMultiChoiceItems(allCourseList, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {
                if(isChecked){
                    mUserCoursesPosition.add(position);
                }else{
                    mUserCoursesPosition.remove((Integer.valueOf(position)));
                }
            }
        });
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Log.i(TAG, "selected courses = " + userNewCoursesListToString());
                Utils.hideSoftKeyboard(Settings.this); // Hide Keyboard
                updateNewCoursesList();
            }
        });
        mBuilder.setNegativeButton(R.string.dismiss_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        mBuilder.setNeutralButton(R.string.clear_all_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
                    mUserCoursesPosition.clear();
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
                            mUserCoursesPosition.clear();
                            mDialog.getListView().setItemChecked(i, false);
                        }
                    }
                });
            }
        });
        mDialog.show();
    }

    private String userNewCoursesListToString() {
        String item = "";
        for (int i = 0; i < mUserCoursesPosition.size(); i++) {
            item = item + allCourseList[mUserCoursesPosition.get(i)];
            if (i != mUserCoursesPosition.size() - 1) {
                item = item + ", ";
            }
        }
        return item;
    }

    private String convertListToString(ArrayList<String> dataList){
        String result = "";
        for (String e: dataList)
            result += e.trim() + ",";

        if(!result.isEmpty()) {
            result = result.replaceAll(",+$", "");
        }
        return result;
    }

    private void updateNewCoursesList(){
        ArrayList<String> uNewCourseNumbers = new ArrayList<String>();
        ArrayList<String> addCourseNumbers = new ArrayList<String>();
        ArrayList<String> removeCourseNumbers = new ArrayList<String>();

        // First, build new course list
        for (int i = 0; i < mUserCoursesPosition.size(); i++) {
            uNewCourseNumbers.add(allCourseList[mUserCoursesPosition.get(i)]);
        }
        // Now, go through all courses and update add and delete records
        for (String course : allCourseList){
            if (uNewCourseNumbers.contains(course)) {
                if (!uCourseNumbers.contains(course)) {
                    // Course added in new the list
                    addCourseNumbers.add(course);
                }
            } else {
                if (uCourseNumbers.contains(course)) {
                    // Course deleted in new the list
                    removeCourseNumbers.add(course);
                }
            }
        }

        // Finally, update cache copy with the new list
        uCourseNumbers = uNewCourseNumbers;

        // Update the database
        updateCourses(convertListToString(addCourseNumbers),
                convertListToString(removeCourseNumbers));
    }

    private void updateCourses(final String addCourses, final String removeCourses) {
        final DatabaseServer db_server = new DatabaseServer(Settings.this);
        Log.i(TAG, "Adding courses = " + addCourses + ", Removing courses =" + removeCourses);
        db_server.execute(CMD_COURSE, CMD_COURSE_UPDATE_USER_COURSES, loginUserName, addCourses, removeCourses);
    }

    private void handleUserUpdateResponse(List<JSONObject> respData){
        if (respData.size() <= 0) {
            Log.e(TAG, "Received an empty record from the Database server");
        } else {
            Log.i(TAG, "User new courses updated in the database");
        }
        launchHomeActivity();
    }

    private void logoutUser() {
        Log.i(TAG, "logoutUser: Logging out...");
        // Let session manager to delete the saved user/session info
        session.setLogin(false, null);

        Intent intent = new Intent(Settings.this, SignIn.class);
        startActivity(intent);
        finish();
    }

    private void processAdminCommand(){
        switch(processingFunction){
            case "updatePassword":
                updatePassword(newPassword);
            break;

            case "deleteAccount":
                deleteAccountFromDb();
                break;
        }
        isPasswordVerified=true;
        processingFunction = "";
        newPassword = "";
    }


    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (Objects.equals(respStatus.get(RESPONSE_CODE), RESPONSE_ERROR)) {
            Log.e(TAG, "Failed to update user details: " + respStatus.get(RESPONSE_MESSAGE));
            Toast.makeText(getApplicationContext(), "Failed to update!", Toast.LENGTH_LONG).show();
            launchHomeActivity();
            return;
        }

        switch(Objects.requireNonNull(respStatus.get(REQUEST_CMD))) {
            case CMD_COURSE:
                if (CMD_COURSE_UPDATE_USER_COURSES.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
                    handleUserUpdateResponse(respData);
                }
            break;

            case CMD_AUTHORIZATION:
                switch (Objects.requireNonNull(respStatus.get(REQUEST_SCMD))) {
                    case CMD_AUTHORIZATION_UPDATE_PASSWORD:
                        if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_ERROR) || respData.isEmpty()) {
                            Log.e(TAG, "Failed to verify authorization: " + respStatus.get(RESPONSE_MESSAGE));
                            Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_LONG).show();
                        }
                    break;

                    case CMD_AUTHORIZATION_DELETE_ACCOUNT:
                        if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_ERROR) || respData.isEmpty()) {
                            Log.e(TAG, "Failed to delete account error: " + respStatus.get(RESPONSE_MESSAGE));
                            Toast.makeText(getApplicationContext(), "Failed to delete account", Toast.LENGTH_LONG).show();
                        } else {
                            logoutUser();
                        }
                    break;

                    case CMD_AUTHORIZATION_LOGIN:
                        if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_SUCCESS) && !respData.isEmpty()) {
                            JSONObject resp = Objects.requireNonNull(respData.get(0));
                            // Verify the Database ID to confirm user exists in the DB
                            try {
                                int id =  resp.getInt(DB_STUDENT_DETAILS_ID);
                                if (id >= 1) {
                                    processAdminCommand();
                                } else {
                                    Log.e(TAG, "SignIn error: user not found");
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "SignIn error: id not found in the DB response");
                                e.printStackTrace();
                            }
                        } else{
                            Log.e(TAG, "SignIn error: " + respStatus.get(RESPONSE_MESSAGE));
                            Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_LONG).show();
                        }
                        hideDialog();
                        break;
                }
                hideDialog();
            break;
        }
    }
}