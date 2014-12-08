package fr.nuitdelinfo.dtc.barcodeeye.image;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class ImageManager {

    private static final String TAG = ImageManager.class.getSimpleName();
    private static final String PHOTO_DIR = "BarcodeEye";
    private final File mDir;

    public ImageManager() {
        File publicDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        mDir = new File(publicDirectory.getAbsolutePath() + File.separator + PHOTO_DIR);
        if (mDir.mkdirs() || mDir.isDirectory()) {
            // good!
        } else {
            Log.e(TAG, "Unable to create photo directory! " + mDir.toString());
        }
    }



}
