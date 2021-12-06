package my.project.lhesa;

import android.app.Application;

public class lhesa extends Application {
    public static final String TAG = lhesa.class.getSimpleName();
    private static lhesa mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized lhesa getInstance() {
        return mInstance;
    }
}