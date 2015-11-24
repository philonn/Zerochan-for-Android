package tr.philon.zerochan.ui.fragments;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import tr.philon.zerochan.R;

public class MyDialogs {

    public static MaterialDialog showLoading(Context context) {
        return new MaterialDialog.Builder(context)
                .autoDismiss(false)
                .content(R.string.loading)
                .progress(true, 0)
                .show();
    }
}