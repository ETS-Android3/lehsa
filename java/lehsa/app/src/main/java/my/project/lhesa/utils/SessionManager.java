package my.project.lhesa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

// Reference: https://github.com/akrajilwar/Android-Login-And-Registration
public class SessionManager {
    // LogCat tag
    private static final String TAG = SessionManager.class.getSimpleName();
 
    // Shared Preferences
    SharedPreferences pref;
 
    Editor editor;
    Context _context;
 
    // Shared pref mode
    int PRIVATE_MODE = 0;
 
    // Shared preferences file name
    private static final String PREF_NAME = "SignIn";
     
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String LOGGEDIN_USERNAME = "username";
 
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    public void setLogin(boolean isLoggedIn, String username) {

        if(isLoggedIn){
            editor.putString(LOGGEDIN_USERNAME, username);
        }
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
 
        // commit changes
        editor.commit();
 
        Log.d(TAG, "User login session modified!");
    }
     
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String getLogggedInUsername(){
        return pref.getString(LOGGEDIN_USERNAME, "");
    }
}