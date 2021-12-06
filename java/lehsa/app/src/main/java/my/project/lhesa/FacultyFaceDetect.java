package my.project.lhesa;

import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY;
import static my.project.lhesa.utils.DatabaseServerConstants.CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_EMAIL;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_FEATURE_VECTORS;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_NAME;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_OFFICE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_PHONE;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_POSITION;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_COURSE_FACULTY_RESEARCH_LINK;
import static my.project.lhesa.utils.DatabaseServerConstants.DB_STUDENT_DETAILS_USERNAME;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import my.project.lhesa.utils.BackgroundTaskCallback;
import my.project.lhesa.utils.DatabaseServer;
import my.project.lhesa.utils.MyFaceDetection;
import my.project.lhesa.utils.Utils;

public class FacultyFaceDetect extends AppCompatActivity implements BackgroundTaskCallback, Runnable {
    private static final String TAG = FacultyFaceDetect.class.getSimpleName();

    protected static final boolean SAVE_PREVIEW_BITMAP = true;
	Bundle bundle;
    String loginUserName;

    // One Preview Image
    ImageView previewImage;
	private Classifier classifier;
    private ArrayList<String> facultyNamesList = new ArrayList<String>();

    private static final int INPUT_SIZE = 160;
    private static final int OUTPUT_SIZE = 320;
    private static final int IMAGE_MEAN = 127;
    private static final float IMAGE_STD = 128.0f;
    private static final String INPUT_NAME = "input_1";
    private static final String OUTPUT_NAME = "output_node0";

    private static final String MODEL_FILE = "file:///android_asset/fr-feature-tf1.3-android.pb";
    private static final String LABEL_FILE = "file:///android_asset/imagenet_comp_graph_label_strings.txt";

    // constant to compare the activity result code
    private int SELECT_PICTURE = 200;

    // Threads synchronisation data
    private static final int THREAD_SLEEP_TIME_IN_MS = 1000;
    Thread MyThread = new Thread(FacultyFaceDetect.this);
    private volatile boolean running = true;
    private volatile Uri selectedImageUri;
    private volatile Boolean isImageReadyToProcess = false;
    private volatile Boolean isDbResponseReceived = false;
    private volatile List<float[]> testImageFeatureVector = new ArrayList<>();
    private volatile List<String> testImageResults = new ArrayList<>();
    private volatile List<JSONObject> featureVectors = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.face_detect);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // register the UI widgets with their appropriate IDs
        previewImage = findViewById(R.id.preview);

        bundle = getIntent().getExtras();
        loginUserName = bundle.getString(DB_STUDENT_DETAILS_USERNAME);
        facultyNamesList = bundle.getStringArrayList("names");
        Log.d(TAG, "facultyNamesList = " + facultyNamesList);

        classifier = TensorFlowImageClassifier.create(
                        getAssets(),
                        MODEL_FILE,
                        LABEL_FILE,
                        INPUT_SIZE,
                        IMAGE_MEAN,
                        IMAGE_STD,
                        INPUT_NAME,
                        OUTPUT_NAME,
                        OUTPUT_SIZE);
        startThread();
        imageChooser();
    }

    private void launchFacultyDisplay(String message) {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        b.putString("message", message);
        Intent intent = new Intent(FacultyFaceDetect.this, FacultyDisplay.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void startThread() {
        if (this.MyThread.getState() == Thread.State.NEW) {
            Log.d(TAG, "MyThread: Starting thread: " + MyThread);
            this.MyThread.start();
            Log.d(TAG, "MyThread: Background process successfully started");
        }
    }

    private void stopThread() {
        Log.i(TAG, "MyThread: Stopping thread: "+ MyThread);
        if (MyThread != null) {
            running = false;
            MyThread.interrupt();
            Log.i(TAG, "MyThread: Thread successfully stopped.");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        stopThread();
        launchHomeActivity();
        return true;
    }

    private void launchHomeActivity() {
        Bundle b = new Bundle();
        b.putString(DB_STUDENT_DETAILS_USERNAME, loginUserName);
        Intent intent = new Intent(FacultyFaceDetect.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    // this function is triggered when
    // the Select Image Button is clicked
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public Bitmap generateBitmapFromRect(Bitmap bitmap, Rect rect){
        Log.d(TAG, "Original Image size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
        Log.d(TAG, "Rectangle image: position left=" + rect.left + ", top=" + rect.top + ", size:"+ rect.width() + "x" + rect.height());
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());

        Log.d(TAG, "Cropped bitmap size: " + cropBitmap.getWidth() + "x" + cropBitmap.getHeight());
        Bitmap resizedBitmap=Bitmap.createBitmap(cropBitmap, 0,0,INPUT_SIZE, INPUT_SIZE);

        return resizedBitmap;
    }

    private boolean validateBitmap(Bitmap bitmap, Rect rect){
        if (rect.left + rect.width() > bitmap.getWidth() ||
                rect.top + rect.height() > bitmap.getHeight()) {
            Log.d(TAG, "Original Image size: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            Log.d(TAG, "Rectangle image: position left=" + rect.left + ", top=" + rect.top + ", size:"+ rect.width() + "x" + rect.height());
            return false;
        }

        if (rect.width() < INPUT_SIZE || rect.height() < INPUT_SIZE){
            return false;
        }
        return true;
    }

    private void requestFacultyInfo(String faculty_name){
        final DatabaseServer db_server = new DatabaseServer(FacultyFaceDetect.this);
        db_server.execute(CMD_FACULTY, CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME, loginUserName, faculty_name);
    }

    private void displayToast(String msg) {
        synchronized (this) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private synchronized boolean processImage(){
        BitmapDrawable a = (BitmapDrawable) previewImage.getDrawable();
        Bitmap org_bm = ((BitmapDrawable) previewImage.getDrawable()).getBitmap();
        InputImage image;
        try {
            image = InputImage.fromFilePath(this, selectedImageUri);
            MyFaceDetection detection = new MyFaceDetection();
            Task<List<Face>> taskResult = detection.detectFaces(image);
            int cnt = 0;
            int maxSeconds = 1000;
            while (!taskResult.isComplete() && cnt < maxSeconds) {
                TimeUnit.SECONDS.sleep(1);
                cnt += 1;
                Log.d(TAG, "Waiting time(secs) :" + cnt);
            }
            if(cnt >= maxSeconds){
                Log.e(TAG, "Tensorflow model took more than 1000s to extract the image");
                return false;
            }
            List<Face> faces = taskResult.getResult();
            for (Face face : faces) {
                Rect bounds = face.getBoundingBox();
                if (!validateBitmap(org_bm, bounds)){
                    String msg = "Detected face size is less than input size. " +
                            "Detected: " + bounds.width() + "x" + bounds.height() +
                            ", Expected: " + INPUT_SIZE + "x" + INPUT_SIZE;
                    Log.i(TAG, msg);
                    displayToast(msg);
                    continue;
                }
                Bitmap bm = generateBitmapFromRect(org_bm, bounds);

                // Run the inference call, and we don't care about the output of
                // the Recognition since we are not validating against the
                // pre-trained images. Instead, read the output float array and
                // compare the same with our MySQL database vectors.
                float[] imageFeatureVector = classifier.extractFeatureVector(bm);
                Log.i(TAG, "FacultyFaceDetect image feature vector data: %s" + Arrays.toString(imageFeatureVector));
                testImageFeatureVector.add(imageFeatureVector);
            }
            if(testImageFeatureVector.size() == 0){
                Log.d(TAG, "No valid faces found!");
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            return false;
        }
    }

    private void showDialog(String title) {
        Utils.showProgressDialog(FacultyFaceDetect.this, title);
    }

    private void hideDialog() {
        Utils.hideProgressDialog(FacultyFaceDetect.this);
    }

    private void processFacultyRecord(List<JSONObject> respData){
        JSONObject featureVectInfo = Objects.requireNonNull(respData.get(0));
        featureVectors.add(featureVectInfo);
        isDbResponseReceived = true;
    }

    private String constructDisplayString(String name, String value){
        if (value != null){
            if (!value.isEmpty()) {
                return "  " + name + ": " + value + "\n";
            }
        }
        return "";
    }

    private void displayFacultyInfo(JSONObject facRecordData) {
        try {
                String finalRecord = new String();
                finalRecord += constructDisplayString("Name", facRecordData.getString(DB_COURSE_FACULTY_NAME));
                finalRecord += constructDisplayString("Email", facRecordData.getString(DB_COURSE_FACULTY_EMAIL));
                finalRecord += constructDisplayString("Phone", facRecordData.getString(DB_COURSE_FACULTY_PHONE));
                finalRecord += constructDisplayString("Position", facRecordData.getString(DB_COURSE_FACULTY_POSITION));
                finalRecord += constructDisplayString("Office", facRecordData.getString(DB_COURSE_FACULTY_OFFICE));
                finalRecord += constructDisplayString("Research", facRecordData.getString(DB_COURSE_FACULTY_RESEARCH_LINK));
                launchFacultyDisplay(finalRecord);
        } catch (JSONException err) {
            Log.d(TAG, "FacultySearch record parse error:" + err.toString());
            Toast.makeText(getApplicationContext(),
                    "Failed to parse faculty name info from the database response", Toast.LENGTH_LONG).show();
        }
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        showDialog("Please wait...");

        // Accept image only if we are ready to accept it
        if(!isImageReadyToProcess) {
            if (resultCode == RESULT_OK) {
                // compare the resultCode with the
                // SELECT_PICTURE constant
                if (requestCode == SELECT_PICTURE) {
                    // Get the uri of the image from data
                    selectedImageUri = data.getData();
                    if (null != selectedImageUri) {
                        // update the preview image in the layout
                        previewImage.setImageURI(selectedImageUri);
                        isImageReadyToProcess = true;
                    } else {
                        Log.d(TAG, "No image selected!");
                    }
                }
            }
        }
    }

    // Computes the Cosine similarity between the test image and
    // all records in the database, and then finally, based on the similarity,
    // picks the top value face and displays.
    private synchronized void faceRecognizeAndSetTheResult(){
        for (float[] testImageVec: testImageFeatureVector) {
            String resultName = "";
            Double resultSimilarity = 0.0;
            JSONObject resultRecord = new JSONObject();

            for (JSONObject r : featureVectors) {
                String name = "";
                String imgVectorStr = "";
                try {
                    name = r.getString(DB_COURSE_FACULTY_NAME);
                    imgVectorStr = r.getString(DB_COURSE_FACULTY_FEATURE_VECTORS);
                } catch (JSONException e) {
                    Log.d(TAG, "Failed to extract feature_vector info from the database, error:" + e.getMessage());
                }

                float[] knownImgVec = Utils.stringToFloatArray(imgVectorStr);
                Double similarity = Utils.cosineSimilarity(testImageVec, knownImgVec);
                if (resultName.isEmpty()){
                    // Record first entry
                    resultName = name;
                    resultSimilarity = similarity;
                    resultRecord = r;
                } else {
                    // consecutive records
                    if (resultSimilarity < similarity){
                        resultName = name;
                        resultSimilarity = similarity;
                        resultRecord = r;
                    }
                }
            }

            int matchPercentage = (int)(resultSimilarity *100);
            Log.i(TAG, "Similarity Match: Name:" + resultName + ", " +
                    String.format("%.5f",resultSimilarity) + "(" + matchPercentage + "%)\n");
            // display if similarity is above 50%
            if(matchPercentage > 50) {
                displayFacultyInfo(resultRecord);
            } else {
                Log.i(TAG, "Similarity score less than 50%");
            }
        }
    }

    private void getAllFeatureVectorRecordsFromDatabase() throws InterruptedException {
        for(String name: facultyNamesList) {
            requestFacultyInfo(name);

            isDbResponseReceived = false;
            while (!isDbResponseReceived) {
                // Wait for a second and then check for response flag
                Thread.sleep(THREAD_SLEEP_TIME_IN_MS);
            }
        }
    }

    @Override
    public void run() {
        // running flag will be used to user signals
        while (running) {
            try {
//                Log.d(TAG, "Thread is running....");
                // There are two things to check in background, one check if user has selected image,
                // and then, extract the feature vector, after that,
                // run a check against database records to find the user face
                if(isImageReadyToProcess){
                    // Extracts the feature vector from the image
                    if(processImage()) {

                        // Get all Records from the MySQL database
                        getAllFeatureVectorRecordsFromDatabase();

                        // Compare two feature vectors
                        faceRecognizeAndSetTheResult();
                    }
                    // Unlock for new picture
                    isImageReadyToProcess = false;
                    hideDialog();
                }

                // loop periodicity of 2secs
                Thread.sleep(2 * THREAD_SLEEP_TIME_IN_MS);

            } catch (InterruptedException e) {
                Log.d(TAG, "Thread interrupted, so exiting....");
                running = false;
            }
        }
    }

    @Override
    public void backgroundTaskCallback(Map<String, String> respStatus, List<JSONObject> respData) {
        if (CMD_FACULTY.equals(Objects.requireNonNull(respStatus.get(REQUEST_CMD)))) {
            if (CMD_FACULTY_REQ_FACULTY_RECORD_BY_NAME.equals(Objects.requireNonNull(respStatus.get(REQUEST_SCMD)))) {
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
            }
        }
    }
}
