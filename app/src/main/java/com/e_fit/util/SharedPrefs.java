package com.e_fit.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs {

    private static final String PREFS_NAME = "E-Fit";
    public SharedPrefs() {}

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Guardar String
    public static void saveString(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).apply();
    }

    // Obtener String
    public static String getString(Context context, String key, String defaultValue) {
        return getPrefs(context).getString(key, defaultValue);
    }

    // Guardar boolean
    public static void saveBoolean(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).apply();
    }

    // Obtener boolean
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getPrefs(context).getBoolean(key, defaultValue);
    }

    // Guardar int
    public static void saveInt(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).apply();
    }

    // Obtener int
    public static int getInt(Context context, String key, int defaultValue) {
        return getPrefs(context).getInt(key, defaultValue);
    }

    // Eliminar un valor específico
    public static void removeKey(Context context, String key) {
        getPrefs(context).edit().remove(key).apply();
    }

    // Borrar todos los datos
    public static void clearAll(Context context) {
        getPrefs(context).edit().clear().apply();
    }
}
