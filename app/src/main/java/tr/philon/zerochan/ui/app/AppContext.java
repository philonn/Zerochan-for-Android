package tr.philon.zerochan.ui.app;

import android.content.Context;

public class AppContext {
    private static Context sContext;

    public static void set(Context context) {
        sContext = context;
    }

    public static Context get() {
        return sContext;
    }
}