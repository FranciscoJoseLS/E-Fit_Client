package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;

public class MuscularGroupParcelable implements Parcelable {
    private MuscularGroup muscularGroup;

    public MuscularGroupParcelable(MuscularGroup muscularGroup) {
        this.muscularGroup = muscularGroup;
    }

    protected MuscularGroupParcelable(Parcel in) {
        muscularGroup = MuscularGroup.valueOf(in.readString());
    }

    public static final Creator<MuscularGroupParcelable> CREATOR = new Creator<MuscularGroupParcelable>() {
        @Override
        public MuscularGroupParcelable createFromParcel(Parcel in) {
            return new MuscularGroupParcelable(in);
        }

        @Override
        public MuscularGroupParcelable[] newArray(int size) {
            return new MuscularGroupParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(muscularGroup.name());
    }

    public MuscularGroup getMuscularGroup() {
        return muscularGroup;
    }
}
