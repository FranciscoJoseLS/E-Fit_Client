package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;

public class ExerciseTypeParcelable implements Parcelable {
    private ExerciseType exerciseType;

    public ExerciseTypeParcelable(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    protected ExerciseTypeParcelable(Parcel in) {
        exerciseType = ExerciseType.valueOf(in.readString());
    }

    public static final Creator<ExerciseTypeParcelable> CREATOR = new Creator<ExerciseTypeParcelable>() {
        @Override
        public ExerciseTypeParcelable createFromParcel(Parcel in) {
            return new ExerciseTypeParcelable(in);
        }

        @Override
        public ExerciseTypeParcelable[] newArray(int size) {
            return new ExerciseTypeParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciseType.name());
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }
}