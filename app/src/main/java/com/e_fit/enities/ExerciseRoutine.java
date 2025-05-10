package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExerciseRoutine implements Parcelable {
    private UUID exerciseRoutineId;
    private Exercise exercise;
    private Routine routine;
    private Integer nSets;
    private List<SetTypeParcelable> setTypes;
    private Long rest;
    private int superSerie;
    private ExerciseType exerciseType;
    private int ordered;

    public ExerciseRoutine() {}

    public ExerciseRoutine(Exercise exercise, Routine routine, Integer nSets, List<SetTypeParcelable> setTypes, Long rest, int superSerie, ExerciseType exerciseType, int ordered) {
        this.exercise = exercise;
        this.routine = routine;
        this.nSets = nSets;
        this.setTypes = setTypes;
        this.rest = rest;
        this.superSerie = superSerie;
        this.exerciseType = exerciseType;
        this.ordered = ordered;
    }

    public ExerciseRoutine(Exercise exercise, Routine routine, Integer nSets, Long rest, int superSerie, ExerciseType exerciseType, int ordered, List<SetTypeParcelable> setTypes) {
        this.exercise = exercise;
        this.routine = routine;
        this.nSets = nSets;
        this.rest = rest;
        this.superSerie = superSerie;
        this.exerciseType = exerciseType;
        this.ordered = ordered;
        this.setTypes = setTypes;
    }

    public UUID getExerciseRoutineId() {
        return exerciseRoutineId;
    }

    public void setExerciseRoutineId(UUID exerciseRoutineId) {
        this.exerciseRoutineId = exerciseRoutineId;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Integer getnSets() {
        return nSets;
    }

    public void setnSets(Integer nSets) {
        this.nSets = nSets;
    }

    public List<SetTypeParcelable> getSetTypes() {
        return setTypes;
    }

    public void setSetTypes(List<SetTypeParcelable> setTypes) {
        this.setTypes = setTypes;
    }

    public Long getRest() {
        return rest;
    }

    public void setRest(Long rest) {
        this.rest = rest;
    }

    public int getSuperSerie() {
        return superSerie;
    }

    public void setSuperSerie(int superSerie) {
        this.superSerie = superSerie;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    @Override
    public String toString() {
        return "ExerciseRoutine{" +
                "exerciseRoutineId=" + exerciseRoutineId +
                ", exercise=" + exercise +
                ", routine=" + routine +
                ", nSets=" + nSets +
                ", setTypes=" + setTypes +
                ", rest=" + rest +
                ", superSerie=" + superSerie +
                ", exerciseType=" + exerciseType +
                ", ordered=" + ordered +
                '}';
    }

    protected ExerciseRoutine(Parcel in) {
        String id = in.readString();
        exerciseRoutineId = id != null ? UUID.fromString(id) : null;
        exercise = in.readParcelable(Exercise.class.getClassLoader());
        routine = in.readParcelable(Routine.class.getClassLoader());
        nSets = in.readByte() == 0 ? null : in.readInt(); // nullable Integer
        setTypes = new ArrayList<>();
        in.readList(setTypes, SetTypeParcelable.class.getClassLoader());
        rest = in.readByte() == 0 ? null : in.readLong(); // nullable Long
        superSerie = in.readInt();
        exerciseType = ((ExerciseTypeParcelable) in.readParcelable(ExerciseTypeParcelable.class.getClassLoader())).getExerciseType();
        ordered = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(exerciseRoutineId != null ? exerciseRoutineId.toString() : null);
        dest.writeParcelable(exercise, flags);
        dest.writeParcelable(routine, flags);
        if (nSets == null) {
            dest.writeByte((byte) 0); // null
        } else {
            dest.writeByte((byte) 1); // non-null
            dest.writeInt(nSets);
        }
        dest.writeList(setTypes);
        if (rest == null) {
            dest.writeByte((byte) 0); // null
        } else {
            dest.writeByte((byte) 1); // non-null
            dest.writeLong(rest);
        }
        dest.writeInt(superSerie);
        dest.writeParcelable(new ExerciseTypeParcelable(exerciseType), flags);
        dest.writeInt(ordered);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseRoutine> CREATOR = new Creator<ExerciseRoutine>() {
        @Override
        public ExerciseRoutine createFromParcel(Parcel in) {
            return new ExerciseRoutine(in);
        }

        @Override
        public ExerciseRoutine[] newArray(int size) {
            return new ExerciseRoutine[size];
        }
    };
}
