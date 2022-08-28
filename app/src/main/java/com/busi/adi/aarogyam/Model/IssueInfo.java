package com.busi.adi.aarogyam.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class IssueInfo implements Parcelable {
    public String IssueTitle;
    public String IssueInfo;


    public IssueInfo(){}
    public IssueInfo(String it, String ii){
        this.IssueTitle = it;
        this.IssueInfo = ii;
    }

    protected IssueInfo(Parcel in) {
        IssueTitle = in.readString();
        IssueInfo = in.readString();
    }

    public static final Creator<IssueInfo> CREATOR = new Creator<IssueInfo>() {
        @Override
        public IssueInfo createFromParcel(Parcel in) {
            return new IssueInfo(in);
        }

        @Override
        public IssueInfo[] newArray(int size) {
            return new IssueInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(IssueTitle);
        parcel.writeString(IssueInfo);
    }
}
