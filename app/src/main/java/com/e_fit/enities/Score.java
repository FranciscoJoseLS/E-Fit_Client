package com.e_fit.enities;

import java.time.LocalDate;
import java.util.UUID;

public class Score {

    private UUID scoreId;
    private User user;
    private Exercise exercise;
    private Routine routine;
    private LocalDate realizationDate;
    private String comments;
    private String loadValue;

    public Score() {}
    public Score(User user, Exercise exercise, Routine routine, LocalDate realizationDate, String comments, String load) {
        this.user = user;
        this.exercise = exercise;
        this.routine = routine;
        this.realizationDate = realizationDate;
        this.comments = comments;
        this.loadValue = load;
    }

    public Score(Exercise exercise, Routine routine, String comments, String load) {
        this.exercise = exercise;
        this.routine = routine;
        this.comments = comments;
        this.loadValue = load;
    }

    public UUID getScoreId() {
        return scoreId;
    }

    public void setScoreId(UUID scoreId) {
        this.scoreId = scoreId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDate getRealizationDate() {
        return realizationDate;
    }

    public void setRealizationDate(LocalDate realizationDate) {
        this.realizationDate = realizationDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getLoadValue() {
        return loadValue;
    }

    public void setLoadValue(String loadValue) {
        this.loadValue = loadValue;
    }

    @Override
    public String toString() {
        return "Score{" +
                "scoreId=" + scoreId +
                ", user=" + user +
                ", exercise=" + exercise +
                ", routine=" + routine +
                ", realizationDate=" + realizationDate +
                ", comments='" + comments + '\'' +
                ", loadValue='" + loadValue + '\'' +
                '}';
    }
}
