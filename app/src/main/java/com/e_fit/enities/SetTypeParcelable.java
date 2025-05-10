package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class SetTypeParcelable implements Parcelable {
    private SetType setType;

    public SetTypeParcelable() {}
    public SetTypeParcelable(SetType setType) {
        this.setType = setType;
    }

    @JsonCreator
    public static SetTypeParcelable fromString(String value) {
        if (value == null) {
            return null;
        }
        return new SetTypeParcelable(SetType.valueOf(value));
    }

    @JsonValue
    public String toString() {
        return setType.name();
    }

    public SetType getSetType() {
        return setType;
    }

    public void setSetType(SetType setType) {
        this.setType = setType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.setType.name());
    }

    protected SetTypeParcelable(Parcel in) {
        this.setType = SetType.valueOf(in.readString());
    }

    public static final Creator<SetTypeParcelable> CREATOR = new Creator<SetTypeParcelable>() {
        @Override
        public SetTypeParcelable createFromParcel(Parcel in) {
            return new SetTypeParcelable(in);
        }

        @Override
        public SetTypeParcelable[] newArray(int size) {
            return new SetTypeParcelable[size];
        }
    };
}
