package me.priyesh.pebblesms;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Preferences {

    public final class Key {
        public static final String CONTACT_1 = "contact_1";
        public static final String CONTACT_2 = "contact_2";
        public static final String CONTACT_3 = "contact_3";
        public static final String CONTACT_4 = "contact_4";
        public static final String CONTACT_5 = "contact_5";
        public static final String CONTACTS_SIZE = "contacts_size";
    }

    private static Preferences sPreferences = null;
    private SharedPreferences mSharedPrefs = null;

    private Preferences(Context context) {
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized Preferences getInstance(Context context) {
        if (sPreferences == null) sPreferences = new Preferences(context);
        return sPreferences;
    }

    public void putString(String key, String value) {
        mSharedPrefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return mSharedPrefs.getString(key, defaultValue);
    }

    public void putInt(String key, int value) {
        mSharedPrefs.edit().putInt(key, value).apply();
    }

    public int getInt(String key, int defaultValue) {
        return mSharedPrefs.getInt(key, defaultValue);
    }
}
