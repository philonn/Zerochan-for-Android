package tr.philon.zerochan.ui.app;

import android.app.Application;

public class MyApplication extends Application {

    public void onCreate() {
        super.onCreate();
        AppContext.set(this);
    }
}