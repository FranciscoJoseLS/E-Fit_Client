package com.e_fit.api;

import android.os.AsyncTask;
import com.e_fit.enities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class UserClient {

    private String BASE_URL;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public UserClient() {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = API_Connection.ENDPOINT + "/user/users";
    }

    public interface UserCallback {
        void onUserReceived(User user);
        void onUserSent();
        void onError(Exception e);
    }

    // GET Method: Retrieve a user by ID or email
    public void getUser(String args, String param, UserCallback callback) {
        if (args.equals("mail")) {
            new GetUserTask("/email/" + param, callback).execute();
        } else {
            new GetUserTask("/" + param, callback).execute();
        }
    }

    private class GetUserTask extends AsyncTask<Void, Void, User> {
        private String params;
        private UserCallback callback;

        public GetUserTask(String params, UserCallback callback) {
            this.params = params;
            this.callback = callback;
        }

        @Override
        protected User doInBackground(Void... voids) {
            User user = null;
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + params)
                        .build();

                Call call = httpClient.newCall(request);
                Response response = call.execute();
                String body = response.body().string();

                user = objectMapper.readValue(body, User.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                callback.onUserReceived(user);
            } else {
                callback.onError(new Exception("Error al cargar el usuario"));
            }
        }
    }

    // POST Method: Create a new user
    public void createUser(User user, UserCallback callback) {
        new CreateUserTask(user, callback).execute();
    }

    private class CreateUserTask extends AsyncTask<Void, Void, Boolean> {
        private User user;
        private UserCallback callback;

        public CreateUserTask(User user, UserCallback callback) {
            this.user = user;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String json = objectMapper.writeValueAsString(user);
                RequestBody body = RequestBody.create(MediaType.get("application/json"), json);

                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .post(body)
                        .build();

                Response response = httpClient.newCall(request).execute();
                boolean success = response.isSuccessful();

                return success;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onUserSent();
            } else {
                callback.onError(new Exception("Error al crear el usuario"));
            }
        }
    }

    // PUT Method: Update an existing user
    public void updateUser(User user, UserCallback callback) {
        new UpdateUser(user, callback).execute();
    }

    private class UpdateUser extends AsyncTask<Void, Void, Boolean> {
        private String params;
        private User user;
        private UserCallback callback;

        public UpdateUser(User user, UserCallback callback) {
            this.user = user;
            this.callback = callback;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String json = objectMapper.writeValueAsString(user);
                RequestBody body = RequestBody.create(MediaType.get("application/json"), json);

                Request request = new Request.Builder()
                        .url(BASE_URL + "/" + user.getUserId())
                        .put(body)
                        .build();

                Response response = httpClient.newCall(request).execute();
                boolean success = response.isSuccessful();

                return success;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onUserSent();
            } else {
                callback.onError(new Exception("Error al enviar la información"));
            }
        }
    }
}