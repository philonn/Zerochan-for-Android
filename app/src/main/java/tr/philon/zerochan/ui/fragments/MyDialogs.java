package tr.philon.zerochan.ui.fragments;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import tr.philon.zerochan.R;

public class MyDialogs {

    public static void askTagsDownload(Context context, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .title("Tags")
                .content("Do you want to download tags now?")
                .positiveText(R.string.download)
                .negativeText(R.string.cancel)
                .negativeColor(context.getResources().getColor(R.color.text_primary_light))
                .onPositive(callback)
                .show();
    }

    public static MaterialDialog showDownloading(Context context) {
        return new MaterialDialog.Builder(context)
                .autoDismiss(false)
                .content("Downloading")
                .progress(true, 0)
                .show();
    }
}