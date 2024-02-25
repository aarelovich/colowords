package org.colowords;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
    private static final String PREFS_NAME       = "settings";
    public static final String  KEY_DIFFICULTY   = "difficulty";
    public static final String  KEY_LANGUAGE     = "language";
    public static final String  KEY_STYLE        = "style";
    public static final String  KEY_UI_FONT      = "font_ui";
    public static final String  KEY_MESSAGE_FONT = "font_message";
    public static final String  KEY_LETTER_FONT  = "font_letter";
    public static final String  KEY_MAX_SCORE     = "max_score";
    public static final String KEY_TEXT_OVERRIDE  = "def_text_override";
    public static final String KEY_DL_OVERRIDE  = "disp_letter_override";
    public static final String KEY_HL_OVERRIDE  = "hinted_letter_override";

    // Save values to SharedPreferences
    public static void Save(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void SaveAsInt(Context context, String key, int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int GetAsInt(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, -1);
    }

    public static void ClearPreferences(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        List<String> allPreferences = new ArrayList<>();
        allPreferences.add(KEY_LANGUAGE);
        allPreferences.add(KEY_DIFFICULTY);
        allPreferences.add(KEY_STYLE);
        allPreferences.add(KEY_UI_FONT);
        allPreferences.add(KEY_MESSAGE_FONT);
        allPreferences.add(KEY_LETTER_FONT);
        allPreferences.add(KEY_MAX_SCORE);

        for (String key: allPreferences){
            if (sharedPreferences.contains(key)){
                sharedPreferences.edit().remove(key);
            }
        }
    }

    // Retrieve values from SharedPreferences
    public static String GetPreference(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

}
