package tr.philon.zerochan.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {
    private SharedPreferences mPrefs;
    private static PrefManager sInstance = null;

    protected PrefManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        if (sInstance == null) sInstance = new PrefManager(context);
    }

    public static void putString(String key, String value) {
        sInstance.mPrefs.edit().putString(key, value).commit();
    }

    public static String getString(String key) {
        return sInstance.mPrefs.getString(key, null);
    }

    public static void putInt(String key, int value) {
        sInstance.mPrefs.edit().putInt(key, value).commit();
    }

    public static int getInt(String key) {
        return sInstance.mPrefs.getInt(key, 0);
    }
}