package com.e_fit.api;

import android.os.AsyncTask;
import com.e_fit.enities.Exercise;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;;

public class ExerciseClient {

    private String BASE_URL;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExerciseClient() {
        this.httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = API_Connection.ENDPOINT + "/exercise/exercises";
    }

    public interface ExerciseCallback {
        void onExercisesReceived(List<Exercise> retrivedExercises);
        void onError(Exception e);
    }

    // GET Method: Retrieve a exercises
    public void getExercises(ExerciseCallback callback) {
        new GetExercises(callback).execute();
    }

    private class GetExercises extends AsyncTask<Void, Void, List<Exercise>> {
        private ExerciseCallback callback;

        public GetExercises(ExerciseCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<Exercise> doInBackground(Void... voids) {
            List<Exercise> exercise = new ArrayList<Exercise>();
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .build();

                Call call = httpClient.newCall(request);
                Response response = call.execute();
                String body = response.body().string();

                    exercise = objectMapper.readValue(body,  new TypeReference<List<Exercise>>(){});
            } catch (Exception e) {
                e.printStackTrace();
            }
            return exercise;
        }

        @Override
        protected void onPostExecute(List<Exercise> exercise) {
            if (!exercise.isEmpty()) {
                callback.onExercisesReceived(exercise);
            } else {
                callback.onError(new Exception("Error al cargar los ejercicios"));
            }
        }
    }

}