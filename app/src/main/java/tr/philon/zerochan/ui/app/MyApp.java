package tr.philon.zerochan.ui.app;

import android.app.Application;
import android.content.Context;

import tr.philon.zerochan.data.prefs.PrefManager;

public class MyApp extends Application {
    private static MyApp mInstance;
    private Context mContext;

    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;
        PrefManager.init(this);
    }

    public static Context getContext() {
        return mInstance.mContext;
    }
}