package tr.philon.zerochan.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.util.SoupUtils;

public class DetailsActivity extends AppCompatActivity {
    public static final String ARG_IMAGE = "arg_image";

    @Bind(R.id.details_toolbar) Toolbar mToolbar;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.details_image) SubsamplingScaleImageView mSSImageView;

    String mImageUrl;
    File mImageFile;
    File mImageCache;
    File mSavedImage;
    File mAppFolder;
    Future<File> mIon;

    boolean isReDownloading;
    MaterialDialog mLoadingDialog;
    Call mInformationCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mImageUrl = GalleryItem.getFullImage(getIntent().getStringExtra(ARG_IMAGE));
        String fileName = GalleryItem.getFileName(mImageUrl);
        String folder = File.separator + "Zerochan" + File.separator;

        mImageCache = new File(getExternalCacheDir(), fileName);
        mSavedImage = new File(Environment.getExternalStorageDirectory(), folder + fileName);
        mAppFolder = new File(Environment.getExternalStorageDirectory(), folder);

        if (mSavedImage.exists())
            mImageFile = mSavedImage;
        else mImageFile = mImageCache;

        if (!mSavedImage.exists() && !mImageCache.exists())
            downloadImage();
        else showImage();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    private void makeToast(String text) {
        Toast.makeText(DetailsActivity.this, text, Toast.LENGTH_LONG).show();
    }


    private void downloadImage() {
        if (mIon != null) {
            mIon.cancel();
            mIon = null;
        }

        mIon = Ion.with(DetailsActivity.this)
                .load(mImageUrl)
                .write(mImageFile)
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File result) {
                        if (e != null) {
                            Log.v("ionError", e.getMessage());
                            makeToast(e.getMessage());
                            return;
                        }

                        if (isReDownloading)
                            makeToast("Download complete");
                        else showImage();

                    }
                });
    }

    private void showImage() {
        mSSImageView.setImage(ImageSource.uri(mImageFile.getPath()));
        mProgressBar.setVisibility(View.INVISIBLE);

        Animation anim = AnimationUtils.loadAnimation(DetailsActivity.this, R.anim.fade_in);
        anim.setDuration(800);
        mSSImageView.startAnimation(anim);
    }


    public void onShare(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, GalleryItem.getPageLink(mImageUrl));

        startActivity(Intent.createChooser(intent, "Share image"));
    }

    public void onDownload(View view) {
        if (mIon != null && !mIon.isDone() && !mIon.isCancelled()) {
            return;
        }

        if (!mAppFolder.exists()) {
            boolean success = mAppFolder.mkdir();

            if (!success) {
                makeToast("Something went wrong");
                return;
            }
        }

        if (mSavedImage.exists()) {
            makeToast("Image already downloaded");
            return;
        }

        isReDownloading = true;
        mImageFile = mSavedImage;
        downloadImage();
    }

    public void onInformation(View view) {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            return;

        mLoadingDialog = new MaterialDialog.Builder(this)
                .autoDismiss(false)
                .content(R.string.loading)
                .progress(true, 0)
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (mInformationCall != null) mInformationCall.cancel();
                    }
                })
                .show();

        loadPage(GalleryItem.getPageLink(mImageUrl));
    }


    private void loadPage(String url) {
        OkHttpClient mHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        mInformationCall = mHttpClient.newCall(request);

        mInformationCall.enqueue(new Callback() {
            Handler mainHandler = new Handler(DetailsActivity.this.getMainLooper());

            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                            makeToast(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String body;
                if (response.isSuccessful()) body = response.body().string();
                else body = "";

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            Log.d("details-information", response.message());
                            makeToast(response.message());
                        } else {
                            onSuccess(body);
                        }
                    }
                });
            }
        });
    }

    private void onSuccess(String response) {
        if (!mLoadingDialog.isShowing()) return;

        List<String> details = SoupUtils.getImageDetails(response);

        String[] array = new String[details.size()];
        for (int i = 0; i <= details.size() - 1; i++) {
            array[i] = details.get(i);
        }

        mLoadingDialog.dismiss();
        new MaterialDialog.Builder(this)
                .title("Details")
                .items(array)
                .positiveText("OK")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if (i == 0)
                            charSequence = charSequence.subSequence(12, charSequence.length());

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setPrimaryClip(ClipData.newPlainText("simple text", charSequence));
                        makeToast("'" + charSequence + "'" + " copied to clipboard");
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                    }
                })
                .show();
    }
}