/*
* A user can get his/her friend's loaction by typing in the regex for either username or fullname
*/
package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import my.project.lhesa.utils.BackgroundTaskCallback;


public class FacultyDisplay extends AppCompatActivity implements
        BackgroundTaskCallback, SearchView.OnQueryTextListener {
    private static final String TAG = FacultyDisplay.class.getSimpleName();
    private LinearLayout facultyLayout;
    private String displayMessage;

    Bundle bundle;
    String loginUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_display);

        facultyLayout = (LinearLayout)findViewById(R.id.faculty_info_layout);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        displayMessage = bundle.getString("message");
        Log.i(TAG, "Display message = " + displayMessage);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
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
        launchFaculty();
        return true;
    }

    //Launches the FacultySearch activity
    private void launchFaculty() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(FacultyDisplay.this, FacultySearch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void init() {
        displayFacultyInfo(displayMessage);
    }

    private void displayFacultyInfo(String displayMessage){
        String REGEX = "(?!<a[^>]*?>)(http[^\\s]+)(?![^<]*?</a>)";

        TextView textView= new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(displayMessage);
        textView.setMovementMethod(new ScrollingMovementMethod());
        Linkify.addLinks(textView, Linkify.WEB_URLS);
        Linkify.addLinks(textView, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(textView, Pattern.compile(REGEX), "https://");
        textView.setLinksClickable(true);

        facultyLayout.addView(textView);
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
    }
}