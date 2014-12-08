package fr.nuitdelinfo.dtc.barcodeeye;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import fr.nuitdelinfo.dtc.agedeglass.R;
import fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivity;

public class LaunchActivity extends Activity {

    private static final String TAG = LaunchActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        // delayed camera activity
        // see: https://code.google.com/p/google-glass-api/issues/detail?id=259
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startApplication();
            }
        }, 100);

    }
    private void startApplication() {
            startActivity(CaptureActivity.newIntent(this));
        finish();
    }
}
