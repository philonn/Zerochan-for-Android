package tr.philon.zerochan.util;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import tr.philon.zerochan.ui.app.MyApp;

public class PixelUtils {

    public static int pxToDp(float px) {
        return (int) (px / MyApp.getContext().getResources().getDisplayMetrics().density);
    }

    public static int dpToPx(float dp) {
        return (int) (dp * MyApp.getContext().getResources().getDisplayMetrics().density);
    }

    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MyApp.getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}