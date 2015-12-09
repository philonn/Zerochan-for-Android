package tr.philon.zerochan.ui.app;

import android.app.Application;

import tr.philon.zerochan.data.prefs.PrefManager;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        AppContext.set(this);
        PrefManager.init(this);
    }
}