package org.colowords;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
    private static final String PREFS_NAME     = "settings";
    public static final String  KEY_DIFFICULTY = "difficulty";
    public static final String  KEY_LANGUAGE   = "language";
    public static final String  KEY_MAX_SCORE      = "max_score";

    // Save values to SharedPreferences
    public static void Save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Retrieve values from SharedPreferences
    public static String GetPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

}
