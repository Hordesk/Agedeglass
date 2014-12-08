/*
 * Copyright (C) 2008 ZXing authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dtc.barcodeeye.scan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Map;

import fr.nuitdelinfo.dtc.barcodeeye.BaseGlassActivity;
import fr.nuitdelinfo.dtc.barcodeeye.R;
import fr.nuitdelinfo.dtc.barcodeeye.image.ImageManager;
import fr.nuitdelinfo.dtc.barcodeeye.migrated.AmbientLightManager;
import fr.nuitdelinfo.dtc.barcodeeye.migrated.FinishListener;
import fr.nuitdelinfo.dtc.barcodeeye.migrated.InactivityTimer;
import fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivityHandler;
import fr.nuitdelinfo.dtc.barcodeeye.scan.ui.ViewfinderView;
import fr.nuitdelinfo.dtc.google.zxing.client.android.camera.CameraManager;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as
 * the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 */
public final class CaptureActivity extends BaseGlassActivity implements
        SurfaceHolder.Callback {


    private static final String TAG = fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivity.class.getSimpleName();

    private CameraManager mCameraManager;
    private fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivityHandler mHandler;
    private ViewfinderView mViewfinderView;
    private boolean mHasSurface;
    private Map<DecodeHintType, ?> mDecodeHints;
    private InactivityTimer mInactivityTimer;
    private AmbientLightManager mAmbientLightManager;
    private ImageManager mImageManager;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivity.class);
        return intent;
    }

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    CameraManager getCameraManager() {
        return mCameraManager;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_capture);

        mImageManager = new ImageManager();

        mHasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
        mAmbientLightManager = new AmbientLightManager(this);

        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        mCameraManager = new CameraManager(getApplication());
        mViewfinderView.setCameraManager(mCameraManager);

        mHandler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (mHasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }

        mAmbientLightManager.start(mCameraManager);

        mInactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mInactivityTimer.onPause();
        mAmbientLightManager.stop();
        mCameraManager.closeDriver();
        if (!mHasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG,
                    "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {

    }

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult
     *            The contents of the barcode.
     * @param barcode
     *            A greyscale bitmap of the camera data which was decoded.
     */
    public void handleDecode(Result rawResult, Bitmap barcode) {
        mInactivityTimer.onActivity();
        String acquisitionResult;
        char[] slash = new char[1];
        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            Toast.makeText(this, "rawResult " + rawResult.getText(), Toast.LENGTH_LONG).show();

           acquisitionResult = rawResult.getText();

           acquisitionResult.getChars(0, 1, slash, 0);

            if(slash!= null && slash.length > 0 && slash[0]=='/')
            {
                Toast.makeText(this, "Fichier patient !", Toast.LENGTH_LONG).show();
                String[] formatedPatientsData = acquisitionResult.split("/");
                //ajouter patient avec formatedPatientsData
            } else {
                Long formatedbarcode= Long.parseLong(acquisitionResult);
                Toast.makeText(this, "Fichier patient non detecte !", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "barcode " + formatedbarcode, Toast.LENGTH_LONG).show();

            }

        }
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (mCameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            mCameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (mHandler == null) {
                mHandler = new CaptureActivityHandler(this, null, mDecodeHints,
                        null, mCameraManager);
            }

        } catch (IOException e) {
            Log.w(TAG, e);
            displayFrameworkBugMessageAndExit();
        } catch (InterruptedException e) {
            Log.w(TAG, e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * FIXME: This should be a glass compatible view (Card)
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }


    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }
}
