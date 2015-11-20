package tr.philon.zerochan.util;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import tr.philon.zerochan.ui.app.AppContext;

public class PixelUtils {

    public static int pxToDp(float px) {
        return (int) (px / AppContext.get().getResources().getDisplayMetrics().density);
    }

    public static int dpToPx(float dp) {
        return (int) (dp * AppContext.get().getResources().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}