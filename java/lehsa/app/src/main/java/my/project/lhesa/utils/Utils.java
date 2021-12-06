package my.project.lhesa.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import my.project.lhesa.widget.ProgressBarDialog;

import java.io.ByteArrayOutputStream;


public class Utils extends DialogFragment {
    private static final String TAG = Utils.class.getSimpleName();
    private static final int USERNAME_MIN_LENGTH = 1;
    private static final int USERNAME_MAX_LENGTH = 40;
    private static final int PASSWORD_MIN_LENGTH = 1;
    private static final int PASSWORD_MAX_LENGTH = 20;

    public static boolean isValidName(String name) {
        if (!name.isEmpty()) {
            String ePattern = "^[A-Za-z]\\w{"+USERNAME_MIN_LENGTH+","+USERNAME_MAX_LENGTH+"}$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(name);
            return m.matches();
        } else {
            return false;
        }
    }

    public static boolean isValidPassword(String password) {
        if (!password.isEmpty()) {
            String ePattern = "^[A-Za-z]\\w{"+PASSWORD_MIN_LENGTH+","+PASSWORD_MAX_LENGTH+"}$";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
            java.util.regex.Matcher m = p.matcher(password);
            return m.matches();
        } else {
            return false;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View focusedView = activity.getCurrentFocus();

        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (focusedView == null) {
            focusedView = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(
                focusedView.getWindowToken(), 0);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {
            Log.d(TAG, "Exception", e);
        }
    }


    public static void showProgressDialog(Context context, String title) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        DialogFragment newFragment = ProgressBarDialog.newInstance(title);
        newFragment.show(fm, "dialog");
    }

    public static void hideProgressDialog(Context context) {
        FragmentManager fm = ((AppCompatActivity)context).getSupportFragmentManager();
        Fragment prev = fm.findFragmentByTag("dialog");
        try {
            if (prev != null) {
                DialogFragment df = (DialogFragment) prev;
                df.dismiss();
            }
        } catch (IllegalStateException e){
            Log.d(TAG, "Failed to dismissal dialog, error: "+e.getMessage());
        }
    }

    public static String getNameRequirementsMessage(String fieldName){
        return fieldName + " should be " + USERNAME_MIN_LENGTH +"-" + USERNAME_MAX_LENGTH +" Alphanumeric characters";
    }

    public static String getPasswordRequirementsMessage(){
        return "Password should be " + PASSWORD_MIN_LENGTH +"-" + PASSWORD_MIN_LENGTH +" Alphanumeric characters";
    }


    public static float[] stringToFloatArray(String text){
        String fdata[] = text.split(" ");
        float[] array_f = new float[fdata.length];
        for (int i = 0; i < fdata.length; i++){
            array_f[i] = Float.parseFloat(fdata[i]);
        }
        return array_f;
    }

    public static String floatArrayToString(float [] data){
        StringBuilder builder = new StringBuilder();
        for (float value : data) {
            builder.append(String.valueOf(value) + " ");
        }
        String text = builder.toString();
        return text.trim();
    }

    // The face matching problem is done by computing the cosine similarity between
    // two face feature vectors. Cosine similarity is a measure of similarity between
    // two non-zero vectors of an inner product space. It can be calculated by:
    //    ???????????????????? = cos(??) = (??1 * ??2)/(???1? ???2?)
    // Where ??1, ??2 are feature vectors extracted from two face images, respectively.
    public static double cosineSimilarity(float[] testImageFeatVector, float[] knownImageFeatVector) {
        double vectorsProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        Log.d(TAG, "testImage = "+testImageFeatVector.length);
        Log.d(TAG, "knownImage = "+knownImageFeatVector.length);

        // Iterate over all elements to compute the equation -
        // (a1 b1 + a2 b2 + ...) / sqrt(a1^2 + a2^2) sqrt(b1^2 + b2^2) ....
        for (int i = 0; i < testImageFeatVector.length; i++) {
            vectorsProduct += ((double) testImageFeatVector[i] * knownImageFeatVector[i]);
            normA += Math.pow(testImageFeatVector[i], 2);
            normB += Math.pow(knownImageFeatVector[i], 2);
        }

        // The resulting similarity ranges from
        //   -1 -> exactly opposite
        //    1 -> exactly the same
        //    0 -> indicating orthogonality or decorrelation
        //    while in-between values indicate intermediate similarity or dissimilarity.
        return vectorsProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }


    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
}
