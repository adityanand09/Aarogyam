package com.busi.adi.aarogyam.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class AilmentRecord implements Parcelable {
    public String ailmentName;
    public String description;
    public String medicines;
    public String ailImgPath;

    public AilmentRecord(){}

    public AilmentRecord(String ailmentName, String description, String medicines, String ailImgPath) {
        this.ailmentName = ailmentName;
        this.description = description;
        this.medicines = medicines;
        this.ailImgPath = ailImgPath;
    }

    protected AilmentRecord(Parcel in) {
        ailmentName = in.readString();
        description = in.readString();
        medicines = in.readString();
        ailImgPath = in.readString();
    }

    public static final Creator<AilmentRecord> CREATOR = new Creator<AilmentRecord>() {
        @Override
        public AilmentRecord createFromParcel(Parcel in) {
            return new AilmentRecord(in);
        }

        @Override
        public AilmentRecord[] newArray(int size) {
            return new AilmentRecord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ailmentName);
        parcel.writeString(description);
        parcel.writeString(medicines);
        parcel.writeString(ailImgPath);
    }
}
