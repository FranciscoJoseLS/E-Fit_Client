package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashSet;
import java.util.Set;

public class Exercise implements Parcelable {
    private Long exerciseId;
    private String name;
    private MuscularGroup muscularGroup;
    private String description;
    private Set<ExerciseRoutine> exerciseRoutines = new HashSet<>();
    private Set<Score> scores = new HashSet<>();

    public Exercise() {}
    public Exercise(String name, MuscularGroup muscularGroup, String description) {
        this.name = name;
        this.muscularGroup = muscularGroup;
        this.description = description;
    }

    public Long getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(Long exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MuscularGroup getMuscularGroup() {
        return muscularGroup;
    }

    public void setMuscularGroup(MuscularGroup muscularGroup) {
        this.muscularGroup = muscularGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<ExerciseRoutine> getExerciseRoutines() {
        return exerciseRoutines;
    }

    public void setExerciseRoutines(Set<ExerciseRoutine> exerciseRoutines) {
        this.exerciseRoutines = exerciseRoutines;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId=" + exerciseId +
                ", name='" + name + '\'' +
                ", muscularGroup=" + muscularGroup +
                ", description='" + description + '\'' +
                '}';
    }
    protected Exercise(Parcel in) {
        if (in.readByte() == 0) {
            exerciseId = null;
        } else {
            exerciseId = in.readLong();
        }
        name = in.readString();
        muscularGroup = ((MuscularGroupParcelable) in.readParcelable(MuscularGroupParcelable.class.getClassLoader())).getMuscularGroup();
        description = in.readString();
    }

    public void writeToParcel(Parcel dest, int flags) {
        if (exerciseId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(exerciseId);
        }
        dest.writeString(name);
        dest.writeParcelable(new MuscularGroupParcelable(muscularGroup), flags);
        dest.writeString(description);
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };
}