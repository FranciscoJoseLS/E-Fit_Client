package com.e_fit.api;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScoreClient {

    private String BASE_URL;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ScoreClient() {
        this.httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = API_Connection.ENDPOINT + "/score";
    }

    public interface PostScoreCallback {
        void onScorePosted(boolean success);
        void onError(Exception e);
    }

    public void postScore(String userId, Long exerciseId, String routineId, String comment, String load,  PostScoreCallback callback) {
        new PostScoreTask(userId, exerciseId, routineId, comment, load, callback).execute();
    }

    private class PostScoreTask extends AsyncTask<Void, Void, Boolean> {
        private final Long exerciseId;
        private final String routineId;
        private final String userId;
        private final String comment;
        private final String load;

        private final PostScoreCallback callback;

        public PostScoreTask(String userId, Long exerciseId, String routineId, String comment, String load, PostScoreCallback callback) {
            this.exerciseId = exerciseId;
            this.routineId = routineId;
            this.userId = userId;
            this.comment = comment;
            this.load = load;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Construir el JSONObject manualmente
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("exerciseId", exerciseId);
                jsonBody.put("routineId", routineId);
                jsonBody.put("userId", userId);
                jsonBody.put("comment", comment);
                jsonBody.put("load", load);

                Log.d("JSON_REQUEST", jsonBody.toString());

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
                Request request = new Request.Builder()
                        .url(BASE_URL+"/new")
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
                callback.onScorePosted(true);
            } else {
                callback.onError(new Exception("Error al guardar el entrenamiento"));
            }
        }
    }
}
