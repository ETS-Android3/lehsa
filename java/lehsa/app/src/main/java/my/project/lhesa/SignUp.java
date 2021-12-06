/*
*This activity allows a new user to sign up
* The user enters his/her username, password and confirms password to complete registration
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_AUTHORIZATION_REGISTER;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.DatabaseServerConstants;
import my.project.lhesa.utils.Utils;


public class SignUp extends AppCompatActivity implements BackgroundTaskCallback {
    private static final String TAG = SignUp.class.getSimpleName();

    private MaterialButton registerBtn;
    private TextInputLayout firstname, lastname, username, password, confirm_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        firstname = findViewById(R.id.edit_firstname);
        lastname = findViewById(R.id.edit_lastname);
        username = findViewById(R.id.edit_verify_password);
        password = findViewById(R.id.edit_password);
        confirm_password = findViewById(R.id.edit_confirm_password);
        registerBtn = findViewById(R.id.button_register);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
    }

    private boolean validateText(String userTextName, String userTextValue){
        if (userTextValue.isEmpty()) {
            Log.e(TAG, userTextName + " is empty");
            Toast.makeText(getApplicationContext(), userTextName + " is empty", Toast.LENGTH_LONG).show();
            return false;
        }

        if (userTextName.equals("Password")) {
            if (!Utils.isValidPassword(userTextValue)) {
                Log.e(TAG, Utils.getPasswordRequirementsMessage() + ", length = " + userTextValue.length());
                Toast.makeText(getApplicationContext(), Utils.getPasswordRequirementsMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Log.e(TAG, "result = " + Utils.isValidName(userTextValue) + ", length = " + userTextValue.length());
            if (!Utils.isValidName(userTextValue)) {
                Log.e(TAG, Utils.getNameRequirementsMessage(userTextName) + ", length = " + userTextValue.length());
                Toast.makeText(getApplicationContext(), Utils.getNameRequirementsMessage(userTextName), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void init() {
        registerBtn.setOnClickListener(view -> {
            Utils.hideSoftKeyboard(SignUp.this);

            String fn = Objects.requireNonNull(firstname.getEditText()).getText().toString().trim();
            String ln = Objects.requireNonNull(lastname.getEditText()).getText().toString().trim();
            String un = Objects.requireNonNull(username.getEditText()).getText().toString().trim();
            String pw = Objects.requireNonNull(password.getEditText()).getText().toString().trim();
            String c_pw = Objects.requireNonNull(confirm_password.getEditText()).getText().toString().trim();

            // Check for empty data in the form
            if (validateText("FistName", fn) &&
                    validateText("LastName", ln) &&
                    validateText("UserName", un) &&
                    validateText("Password", pw)) {
                if (!un.equals(pw)) {
                    if (pw.equals(c_pw)) {
                        // Valid record, go ahead and register
                        registerUser(fn, ln, un, pw);
                    } else {
                        Log.e(TAG, "Passwords are not matching");
                        Toast.makeText(getApplicationContext(),
                                "Passwords are not matching", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "The username and password should not be the same.");
                    Toast.makeText(getApplicationContext(),
                            "The username and password should not be the same.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Enter new user details into database
    private void registerUser(final String fn, final String ln, final String un, final String pw) {
        showDialog("Registering ...");
        final DatabaseServer db_server = new DatabaseServer(SignUp.this);
        db_server.execute(CMD_AUTHORIZATION, CMD_AUTHORIZATION_REGISTER, un, fn, ln, pw);
    }

    private void showDialog(String title) {
        Utils.showProgressDialog(SignUp.this, title);
    }

    private void hideDialog() {
        Utils.hideProgressDialog(SignUp.this);
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (CMD_AUTHORIZATION_REGISTER.equals (respStatus.get(REQUEST_SCMD))) {
            String username = respStatus.get(DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME);
            if (respStatus.get(RESPONSE_CODE).equals(RESPONSE_SUCCESS)) {
                Log.e(TAG, "Registration success: " + username);
                Intent upanel = new Intent(SignUp.this, SignIn.class);
                upanel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(upanel);
                finish();
            } else{
                Log.e(TAG, "Registration error: " + username + ". "+ respStatus.get(RESPONSE_MESSAGE));
                Toast.makeText(getApplicationContext(), "Failed to register a user: " + username,
                        Toast.LENGTH_LONG).show();
            }
            hideDialog();
        }
    }
}
