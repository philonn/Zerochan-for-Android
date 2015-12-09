package tr.philon.zerochan.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefManager {
    public static final String KEY_ORDER = "key_order";
    public static final String KEY_TIME = "key_time";
    public static final String KEY_DIMEN = "key_dimen";

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefEditor;
    private static PrefManager sInstance = null;

    protected PrefManager(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefEditor = mPrefs.edit();
    }

    public static void init(Context context) {
        if (sInstance == null) sInstance = new PrefManager(context);
    }

    public static PrefManager getInstance() {
        return sInstance;
    }

    public void putString(String key, String value) {
        mPrefEditor.putString(key, value).commit();
    }

    public String getString(String key) {
        return mPrefs.getString(key, null);
    }

    public void putInt(String key, int value) {
        mPrefEditor.putInt(key, value).commit();
    }

    public int getInt(String key) {
        return mPrefs.getInt(key, 0);
    }
}