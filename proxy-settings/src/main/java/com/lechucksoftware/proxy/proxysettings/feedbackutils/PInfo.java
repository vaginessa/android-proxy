package com.lechucksoftware.proxy.proxysettings.feedbackutils;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import timber.log.Timber;

public class PInfo implements Parcelable
{
    public String appname = "";
    public String pname = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable icon;
    public ApplicationInfo applicationInfo;
    
    void prettyPrint() 
    {
        Timber.d("%s\t%s\t%s\t%s",appname, pname, versionName, versionCode);
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.appname);
        dest.writeString(this.pname);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);

        Bitmap bitmap = ((BitmapDrawable) this.icon).getBitmap();
        dest.writeParcelable(bitmap, flags);

        dest.writeParcelable(this.applicationInfo, 0);
    }

    public PInfo() {}

    private PInfo(Parcel in)
    {
        this.appname = in.readString();
        this.pname = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();

        Bitmap bitmap = in.readParcelable(getClass().getClassLoader());
        this.icon = new BitmapDrawable(bitmap);

        this.applicationInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
    }

    public static final Creator<PInfo> CREATOR = new Creator<PInfo>()
    {
        public PInfo createFromParcel(Parcel source) {return new PInfo(source);}

        public PInfo[] newArray(int size) {return new PInfo[size];}
    };
}
