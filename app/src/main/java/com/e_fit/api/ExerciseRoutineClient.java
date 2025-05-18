package com.e_fit.api;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.json.JSONException;

public class ExerciseRoutineClient {

    private String BASE_URL;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ExerciseRoutineClient() {
        this.httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = API_Connection.ENDPOINT + "/exerciseRoutine/";
    }

    public interface PostExerciseRoutineCallback {
        void onExerciseRoutinePosted(boolean success);
        void onError(Exception e);
    }

    public void postExerciseRoutine(Long exerciseId, String routineId, int nSets, Long rest, int superSerie, String exerciseType, int ordered, PostExerciseRoutineCallback callback) {
        new PostExerciseRoutineTask(exerciseId, routineId, nSets, rest, superSerie, exerciseType, ordered, callback).execute();
    }

    private class PostExerciseRoutineTask extends AsyncTask<Void, Void, Boolean> {
        private final Long exerciseId;
        private final String routineId;
        private final int nSets;
        private final Long rest;
        private final int superSerie;
        private final String exerciseType;
        private final int ordered;
        private final PostExerciseRoutineCallback callback;

        public PostExerciseRoutineTask(Long exerciseId, String routineId, int nSets, Long rest, int superSerie, String exerciseType, int ordered, PostExerciseRoutineCallback callback) {
            this.exerciseId = exerciseId;
            this.routineId = routineId;
            this.nSets = nSets;
            this.rest = rest;
            this.superSerie = superSerie;
            this.exerciseType = exerciseType;
            this.ordered = ordered;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Construir el JSONObject manualmente
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("exerciseId", exerciseId);
                jsonBody.put("routineId", routineId);
                jsonBody.put("nSets", nSets);
                jsonBody.put("rest", rest);
                jsonBody.put("superSerie", superSerie);
                jsonBody.put("exerciseType", exerciseType);
                jsonBody.put("ordered", ordered);


                Log.d("JSON", jsonBody.toString()); // Importante: Imprime el JSON *antes* de enviarlo

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .post(body)
                        .build();
                Call call = httpClient.newCall(request);
                Response response = call.execute();
                return response.isSuccessful();
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onExerciseRoutinePosted(true);
            } else {
                callback.onError(new Exception("Error al guardar la rutina de ejercicio"));
            }
        }
    }
}
