package tr.philon.zerochan.ui.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import tr.philon.zerochan.data.RequestHandler;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.util.SoupUtils;

public class DetailsActivity extends AppCompatActivity {
    public static final String ARG_IMAGE = "arg_image";

    @Bind(R.id.details_toolbar) Toolbar mToolbar;
    @Bind(R.id.details_image_thumb) ImageView ivThumb;
    @Bind(R.id.details_image) SubsamplingScaleImageView ivFull;

    String urlThumb;
    String urlFull;
    File mImageFile;
    File mImageCache;
    File mSavedImage;
    File mAppFolder;
    Future<File> mIon;

    boolean isReDownloading;
    MaterialDialog mLoadingDialog;
    Call mInformationCall;

    static final int DELAY_DURATION = 200;
    static final int FADE_IN_DURATION = 500;
    static final int FADE_OUT_DURATION = 100;
    static final int SCALE_DURATION = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        urlThumb = getIntent().getStringExtra(ARG_IMAGE);
        urlFull = GalleryItem.getFullImage(urlThumb);
        showThumbImage();

        String fileName = GalleryItem.getFileName(urlFull);
        String folder = File.separator + "Zerochan" + File.separator;

        mImageCache = new File(getExternalCacheDir(), fileName);
        mSavedImage = new File(Environment.getExternalStorageDirectory(), folder + fileName);
        mAppFolder = new File(Environment.getExternalStorageDirectory(), folder);

        if (mSavedImage.exists())
            mImageFile = mSavedImage;
        else mImageFile = mImageCache;

        if (!mImageCache.exists())
            downloadImage();
        else showImageDelayed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!ivFull.isImageLoaded() || ivFull.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            if (ivFull.getScale() == ivFull.getMinScale()) {
                fadeOutAndExit();
            } else {
                ivFull.animateScaleAndCenter(ivFull.getMinScale(), ivFull.getCenter())
                        .withInterruptible(false)
                        .withDuration(SCALE_DURATION)
                        .start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fadeOutAndExit();
                    }
                }, SCALE_DURATION);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RequestHandler.getInstance().cancel();
    }

    private void fadeOutAndExit() {
        ivFull.animate()
                .alpha(0f)
                .setDuration(FADE_OUT_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivFull.setVisibility(View.GONE);
                        DetailsActivity.super.onBackPressed();
                    }
                });
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
                .load(urlFull)
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
        ivFull.setImage(ImageSource.uri(mImageFile.getPath()));
        ivFull.animate().alpha(1f).setDuration(FADE_IN_DURATION).setListener(null);
    }

    private void showImageDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showImage();
            }
        }, DELAY_DURATION);
    }

    private void showThumbImage() {
        Glide.with(this)
                .load(urlThumb)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivThumb);
    }


    public void onShare(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, GalleryItem.getPageLink(urlFull));

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

        loadPage(GalleryItem.getPageLink(urlFull));
    }


    private void loadPage(String url) {
        RequestHandler.getInstance().load(url, new RequestHandler.Callback() {
            Handler mainHandler = new Handler(getMainLooper());

            @Override
            public void onSuccess(final String response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPageLoaded(response);
                    }
                });
            }

            @Override
            public void onFailure(final String message, final Throwable throwable) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mLoadingDialog.isShowing()) {
                            mLoadingDialog.dismiss();
                            makeToast(message);
                        }
                    }
                });
            }
        });
    }

    private void onPageLoaded(String response) {
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
                        if (i == 1) return;
                        if (i == 0) charSequence = charSequence.subSequence(12, charSequence.length());

                        Intent intent = new Intent(DetailsActivity.this, SearchActivity.class);
                        intent.putExtra(SearchActivity.ARG_TAGS, charSequence);
                        intent.putExtra(SearchActivity.ARG_USER, i == 0);
                        startActivity(intent);
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