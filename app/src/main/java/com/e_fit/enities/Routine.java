package com.e_fit.enities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Routine {
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
}