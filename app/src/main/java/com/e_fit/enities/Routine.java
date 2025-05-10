package com.e_fit.enities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Routine implements Parcelable {
    private UUID routineId;
    private String name;
    private String estimatedDuration;
    private Integer defaultDays;
    private String description;
    private Boolean active;
    private User user;
    private Set<ExerciseRoutine> exerciseRoutines = new HashSet<>();
    private Set<Score> scores = new HashSet<>();

    public Routine() {}

    public Routine(String name, String estimatedDuration, Integer defaultDays, String description, Boolean active) {
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.defaultDays = defaultDays;
        this.description = description;
        this.active = active;
    }

    public Routine(String name, String estimatedDuration, Integer defaultDays, String description, Boolean active, User user) {
        this.name = name;
        this.estimatedDuration = estimatedDuration;
        this.defaultDays = defaultDays;
        this.description = description;
        this.active = active;
        this.user = user;
    }

    protected Routine(Parcel in) {
        long mostSigBits = in.readLong();
        long leastSigBits = in.readLong();
        routineId = new UUID(mostSigBits, leastSigBits);
        name = in.readString();
        estimatedDuration = in.readString();
        if (in.readByte() == 0) {
            defaultDays = null;
        } else {
            defaultDays = in.readInt();
        }
        description = in.readString();
        byte activeVal = in.readByte();
        active = activeVal == 0 ? null : activeVal == 1;
        user = in.readParcelable(User.class.getClassLoader());
        //Cargar las listas de la rutina
        ArrayList<ExerciseRoutine> exerciseRoutineList = new ArrayList<>();
        in.readList(exerciseRoutineList, ExerciseRoutine.class.getClassLoader());
        exerciseRoutines = new HashSet<>(exerciseRoutineList);

        ArrayList<Score> scoreList = new ArrayList<>();
        in.readList(scoreList, Score.class.getClassLoader());
        scores = new HashSet<>(scoreList);
    }

    public UUID getRoutineId() {
        return routineId;
    }

    public void setRoutineId(UUID routineId) {
        this.routineId = routineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Integer getDefaultDays() {
        return defaultDays;
    }

    public void setDefaultDays(Integer defaultDays) {
        this.defaultDays = defaultDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        return "Routine{" +
                "routineId='" + routineId + '\'' +
                ", name='" + name + '\'' +
                ", estimatedDuration='" + estimatedDuration + '\'' +
                ", defaultDays=" + defaultDays +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(routineId.getMostSignificantBits());
        dest.writeLong(routineId.getLeastSignificantBits());
        dest.writeString(name);
        dest.writeString(estimatedDuration);
        if (defaultDays == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(defaultDays);
        }
        dest.writeString(description);
        dest.writeByte((byte) (active == null ? 0 : active ? 1 : 2));
        dest.writeParcelable((Parcelable) user, flags);
        dest.writeList(new ArrayList<>(exerciseRoutines));
        dest.writeList(new ArrayList<>(scores));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Routine> CREATOR = new Creator<Routine>() {
        @Override
        public Routine createFromParcel(Parcel in) {
            return new Routine(in);
        }

        @Override
        public Routine[] newArray(int size) {
            return new Routine[size];
        }
    };
}
