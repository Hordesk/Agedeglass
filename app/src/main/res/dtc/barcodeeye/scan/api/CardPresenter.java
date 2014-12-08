package dtc.barcodeeye.scan.api;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class CardPresenter implements Parcelable {

    private static final String TAG = fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter.class.getSimpleName();
    private final List<Uri> mImages = new ArrayList<Uri>();
    private String mText;
    private String mFooter;
    private PendingIntent mPendingIntent;


    public PendingIntent getPendingIntent() {
        return mPendingIntent;
    }


    /* *********************************************************************
     * Parcelable interface related methods
     */
    protected CardPresenter(Parcel in) {
        in.readList(mImages, Uri.class.getClassLoader());
        mText = in.readString();
        mFooter = in.readString();
        mPendingIntent = in.readParcelable(PendingIntent.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mImages);
        dest.writeString(mText);
        dest.writeString(mFooter);
        dest.writeParcelable(mPendingIntent, 0);
    }

    public static final Parcelable.Creator<fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter> CREATOR = new Parcelable.Creator<fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter>() {
        @Override
        public fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter createFromParcel(Parcel in) {
            return new fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter(in);
        }

        @Override
        public fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter[] newArray(int size) {
            return new fr.nuitdelinfo.dtc.barcodeeye.scan.api.CardPresenter[size];
        }
    };
}
