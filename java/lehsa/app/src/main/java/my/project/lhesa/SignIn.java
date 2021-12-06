/*
* This activity allows the user to login by entering the username and password.
* If the user has forgotten the password, it can be updated by clicking on forgot passowrd
* If it is a new user, he/she can click on sign up button to register
*/

package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_LOGIN;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_UPDATE_PASSWORD;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_ID;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.SessionManager;
import my.project.lhesa.utils.Utils;

public class SignIn extends AppCompatActivity implements BackgroundTaskCallback {
    private static final String TAG = SignIn.class.getSimpleName();

    private MaterialButton btnLogin, btnLinkToRegister, btnForgotPass;
    private TextInputLayout user_name, password;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        user_name = findViewById(R.id.edit_firstname);
        password = findViewById(R.id.edit_password);
        btnLogin = findViewById(R.id.button_login);
        btnLinkToRegister = findViewById(R.id.button_register);
        btnForgotPass = findViewById(R.id.button_update);

        // session manager to remember the login state
        session = new SessionManager(this);

        // check user is already logged in
        if (session.isLoggedIn()) {
            Intent i = new Intent(SignIn.this, Home.class);
            startActivity(i);
            finish();
        }

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private void init() {
        // SignIn button Click Event
        btnLogin.setOnClickListener(view -> {
            // Hide Keyboard
            Utils.hideSoftKeyboard(SignIn.this);

            String uName = Objects.requireNonNull(user_name.getEditText()).getText().toString().trim();
            String pWord = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

            if (validateText("FistName", uName) &&
                    validateText("Password", pWord)) {
                loginProcess(uName, pWord);
            }
        });

        // Link to SignUp Screen
        btnLinkToRegister.setOnClickListener(view -> {
            Intent i = new Intent(SignIn.this, SignUp.class);
            startActivity(i);
        });

        // Forgot Password Dialog
        btnForgotPass.setOnClickListener(v -> forgotPasswordDialog());
    }

    //handles the scenario where user has forgotten password
    private void forgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.forget_password, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Update Password")
                .setCancelable(false)
                .setPositiveButton("Update", (dialog, which) -> forgotPasswordReadings(dialogView))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private boolean validateText(String userTextName, String userTextValue){
        if (userTextValue.isEmpty()) {
            Log.e(TAG, userTextName + " is empty");
            Toast.makeText(getApplicationContext(), userTextName + " is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userTextName.equals("Password")) {
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

    //Allows user to update password
    private void forgotPasswordReadings(View v) {
        TextInputLayout editUsername = v.findViewById(R.id.edit_verify_password);
        TextInputLayout editPassword = v.findViewById(R.id.edit_password);
        TextInputLayout editConfirmPassword = v.findViewById(R.id.edit_confirm_password);

        String uName = Objects.requireNonNull(editUsername.getEditText()).getText().toString().trim();
        String pWord = Objects.requireNonNull(editPassword.getEditText()).getText().toString().trim();
        String confirmPassWord = Objects.requireNonNull(editConfirmPassword.getEditText()).getText().toString().trim();

        // Check for empty data in the form
        if (validateText("UserName", uName) &&
                validateText("Password", pWord) &&
                validateText("Password", confirmPassWord)) {
            if (!uName.equals(pWord)) {
                if (pWord.equals(confirmPassWord)) {
                    updatePassword(uName, pWord);
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

    private void launchHome(String userName){
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, userName);
        Intent upanel = new Intent(SignIn.this, Home.class);
        upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        upanel.putExtras(b);
        startActivity(upanel);
        session.setLogin(true, userName);
        finish();
    }

    //Database connection to verify login details
    private void loginProcess(final String username, final String password) {
        showDialog("Logging in ...");
        final DatabaseServer db_server = new DatabaseServer(SignIn.this);
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_LOGIN, username, password);
    }
    //Database connection to update password
    private void updatePassword(final String username, final String password) {
        showDialog("Please wait...");
        final DatabaseServer db_server = new DatabaseServer(SignIn.this);
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_UPDATE_PASSWORD, username, password);
    }

    private void showDialog(String title) {
        Utils.showProgressDialog(SignIn.this, title);
    }

    private void hideDialog() {
        Utils.hideProgressDialog(SignIn.this);
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        switch(Objects.requireNonNull(respStatus.get(REQUEST_SCMD))){
            case CMD_AUTHORIZATION_LOGIN:
                if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_SUCCESS) && !respData.isEmpty()) {
                    JSONObject resp = Objects.requireNonNull(respData.get(0));
                    // Verify the Database ID to confirm user exists in the DB
                    try {
                        int id =  resp.getInt(DB_STUDENT_DETAILS_ID);
                        if (id >= 1) {
                            launchHome(respStatus.get(DB_STUDENT_DETAILS_USERNAME));
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

            case CMD_AUTHORIZATION_UPDATE_PASSWORD:
                if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_ERROR) || respData.isEmpty()) {
                    Log.e(TAG, "Update error: " + respStatus.get(RESPONSE_MESSAGE));
                    Toast.makeText(getApplicationContext(), "Failed to update password", Toast.LENGTH_LONG).show();
                }
                hideDialog();
                break;

            default:
                break;
        }
    }
}
