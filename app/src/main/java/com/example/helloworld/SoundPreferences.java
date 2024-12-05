// SoundPreferences.java
package com.example.helloworld;

import android.content.Context;
import android.content.SharedPreferences;

public class SoundPreferences {
    private static final String PREFS_NAME = "GamePrefs";
    private static final String SOUND_KEY = "sound";

    public static void setSoundEnabled(Context context, boolean isEnabled) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SOUND_KEY, isEnabled);
        editor.apply();
    }

    public static boolean isSoundEnabled(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SOUND_KEY, true);
    }
}