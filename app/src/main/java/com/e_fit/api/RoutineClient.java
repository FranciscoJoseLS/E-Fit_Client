package com.e_fit.api;

import android.os.AsyncTask;

import com.e_fit.enities.Exercise;
import com.e_fit.enities.ExerciseRoutine;
import com.e_fit.enities.ExerciseType;
import com.e_fit.enities.MuscularGroup;
import com.e_fit.enities.Routine;
import com.e_fit.enities.SetTypeParcelable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RoutineClient {

    private String BASE_URL;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RoutineClient() {
        this.httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        this.objectMapper = new ObjectMapper();
        this.BASE_URL = API_Connection.ENDPOINT + "/routine";
    }

    public interface RoutineCallback {
        void onRoutinesReceived(List<Routine> retrievedRoutines);

        void onError(Exception e);
    }

    // GET Method: Retrieve a routines from a user
    public void getRoutines(String userId, RoutineCallback callback) {
        new GetRoutines(userId, callback).execute();
    }

    private class GetRoutines extends AsyncTask<Void, Void, List<Routine>> {
        private RoutineCallback callback;
        private final String userId;

        public GetRoutines(String userId, RoutineCallback callback) {
            this.callback = callback;
            this.userId = userId;
        }

        @Override
        protected List<Routine> doInBackground(Void... voids) {
            List<Routine> routine = new ArrayList<Routine>();
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/user/" + userId)
                        .build();

                Call call = httpClient.newCall(request);
                Response response = call.execute();
                String body = response.body().string();

                routine = objectMapper.readValue(body, new TypeReference<List<Routine>>() {
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routine;
        }

        @Override
        protected void onPostExecute(List<Routine> routine) {
            if (!routine.isEmpty()) {
                callback.onRoutinesReceived(routine);
            } else {
                callback.onError(new Exception("Error al cargar rutinas"));
            }
        }
    }

    // POST Method: Create a routines to user

    public interface RoutineOperationCallback {
        void onSuccess();

        void onError(Exception e);
    }

    public void createRoutine(Routine routine, String userId, RoutineOperationCallback callback) {
        new PostRoutine(routine, userId, callback).execute();
    }

    private class PostRoutine extends AsyncTask<Void, Void, Boolean> {
        private final Routine routine;
        private final String userId;
        private final RoutineOperationCallback callback;
        private Exception error;

        public PostRoutine(Routine routine, String userId, RoutineOperationCallback callback) {
            this.routine = routine;
            this.callback = callback;
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Convertir Parcelable Routine a RoutineRequest object
                RoutineRequest routineRequest = convertToRoutineRequest(routine);
                routineRequest.setExercises(new ArrayList<>());
                String json = objectMapper.writeValueAsString(routineRequest);

                Request request = new Request.Builder()
                        .url(BASE_URL + "/user/" + userId)
                        .post(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                Response response = httpClient.newCall(request).execute();

                return response.isSuccessful();
            } catch (Exception e) {
                this.error = e;
                return false;
            }
        }

        private RoutineRequest convertToRoutineRequest(Routine routine) {
            RoutineRequest routineRequest = new RoutineRequest();
            // Routine fields
            routineRequest.setName(routine.getName());
            routineRequest.setDescription(routine.getDescription());
            routineRequest.setEstimatedDuration(routine.getEstimatedDuration());
            routineRequest.setDefaultDays(routine.getDefaultDays());
            routineRequest.setActive(routine.getActive());

            routineRequest.setExercises(null);

            return routineRequest;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onSuccess();
            } else {
                callback.onError(error != null ? error : new Exception("Error al crear rutinas"));
            }
        }
    }

    // PUT Method: Update a routine

    public void updateRoutine(Routine routine, String userId, RoutineOperationCallback callback) {
        new PutRutine(routine, userId, callback).execute();
    }

    private class PutRutine extends AsyncTask<Void, Void, Boolean> {
        private final Routine routine;
        private final String userId;
        private final RoutineOperationCallback callback;
        private Exception error;

        public PutRutine(Routine routine, String userId, RoutineOperationCallback callback) {
            this.routine = routine;
            this.callback = callback;
            this.userId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Convertir Parcelable Routine a RoutineRequest object
                RoutineRequest routineRequest = convertToRoutineRequest(routine);
                String json = objectMapper.writeValueAsString(routineRequest);

                Request request = new Request.Builder()
                        .url(BASE_URL + "/" + routine.getRoutineId() + "/user/" + userId)
                        .put(RequestBody.create(MediaType.parse("application/json"), json))
                        .build();

                Response response = httpClient.newCall(request).execute();

                return response.isSuccessful();
            } catch (Exception e) {
                this.error = e;
                return false;
            }
        }

        private RoutineRequest convertToRoutineRequest(Routine routine) {
            RoutineRequest routineRequest = new RoutineRequest();
            // Routine fields
            routineRequest.setName(routine.getName());
            routineRequest.setDescription(routine.getDescription());
            routineRequest.setEstimatedDuration(routine.getEstimatedDuration());
            routineRequest.setDefaultDays(routine.getDefaultDays());
            routineRequest.setActive(routine.getActive());

            routineRequest.setExercises(null);

            return routineRequest;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                callback.onSuccess();
            } else {
                callback.onError(error != null ? error : new Exception("Error al actualizar rutinas"));
            }
        }
    }

    private static class RoutineRequest {
        private String name;
        private String description;
        private String estimatedDuration;
        private Integer defaultDays;
        private Boolean active;
        private List<ExerciseRoutine> exercises;

        public RoutineRequest() {
            this.exercises = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public List<ExerciseRoutine> getExercises() {
            return exercises;
        }

        public void setExercises(List<ExerciseRoutine> exercises) {
            this.exercises = exercises;
        }
    }

    // Métodos para obtener los ejercicios de una rutina
    public interface ExerciseRoutinesCallback {
        void onExerciseRoutinesReceived(List<ExerciseRoutine> exerciseRoutines);
        void onError(Exception e);
    }

    public void getExercisesForRoutine(String routineId, ExerciseRoutinesCallback callback) {
        new GetExercisesForRoutineTask(routineId, callback).execute();
    }

    private class GetExercisesForRoutineTask extends AsyncTask<Void, Void, List<ExerciseRoutine>> {
        private final String routineId;
        private final ExerciseRoutinesCallback callback;
        private Exception error;

        GetExercisesForRoutineTask(String routineId, ExerciseRoutinesCallback callback) {
            this.routineId = routineId;
            this.callback = callback;
        }

        @Override
        protected List<ExerciseRoutine> doInBackground(Void... voids) {
            try {
                // Construct the full URL explicitly and correctly
                String fullUrl = API_Connection.ENDPOINT + "/exerciseRoutine/routine/" + routineId;
                Request request = new Request.Builder()
                        .url(fullUrl)
                        .build();
                Response response = httpClient.newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new Exception("Error al obtener ejercicios: " + response.code());
                }
                String body = response.body().string();
                // Check for empty body
                if (body == null || body.trim().isEmpty()) {
                    return Collections.emptyList();
                }

                ObjectMapper mapper = new ObjectMapper();
                List<JsonNode> jsonNodes = mapper.readValue(body, new TypeReference<List<JsonNode>>() {});
                List<ExerciseRoutine> exerciseRoutines = new ArrayList<>();
                // Itera sobre cada JsonNode para extraer los datos necesarios
                for (JsonNode node : jsonNodes) {
                    // Crea un nuevo objeto Exercise
                    Exercise exercise = new Exercise();
                    exercise.setExerciseId(node.get("exerciseId").asLong());  // Asumiendo que exerciseId es un int
                    exercise.setName(node.get("name").asText());
                    exercise.setDescription(node.get("description").asText());
                    exercise.setMuscularGroup(MuscularGroup.valueOf(node.get("muscularGroup").asText()));
                    // Crea un nuevo objeto ExerciseRoutine
                    ExerciseRoutine exerciseRoutine = new ExerciseRoutine();
                    exerciseRoutine.setExerciseRoutineId(UUID.fromString(node.get("exerciseRoutineId").asText()));
                    exerciseRoutine.setnSets(node.get("nSets").asInt());
                    // Deserializa la lista de setTypes si existe.
                    if (node.has("setTypes")) {
                        exerciseRoutine.setSetTypes(mapper.readValue(node.get("setTypes").toString(), new TypeReference<List<SetTypeParcelable>>() {
                        }));
                    } else {
                        exerciseRoutine.setSetTypes(new ArrayList<>());
                    }
                    exerciseRoutine.setRest(node.get("rest").asLong());
                    exerciseRoutine.setSuperSerie(node.get("superSerie").asInt());
                    exerciseRoutine.setExerciseType(ExerciseType.valueOf(node.get("exerciseType").asText())); // Asegúrate de que ExerciseType tiene el valor correcto
                    exerciseRoutine.setOrdered(node.get("ordered").asInt());
                    exerciseRoutine.setExercise(exercise); // Asigna el objeto Exercise a ExerciseRoutine
                    exerciseRoutines.add(exerciseRoutine);
                }
                return exerciseRoutines;
            } catch (Exception e) {
                error = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<ExerciseRoutine> exerciseRoutines) {
            if (exerciseRoutines != null) {
                callback.onExerciseRoutinesReceived(exerciseRoutines);
            } else {
                callback.onError(error);
            }
        }
    }

}