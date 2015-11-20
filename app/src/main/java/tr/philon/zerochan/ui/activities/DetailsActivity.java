package tr.philon.zerochan.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.GalleryItem;

public class DetailsActivity extends AppCompatActivity {
    public static final String ARG_IMAGE = "arg_image";

    @Bind(R.id.details_toolbar) Toolbar mToolbar;
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.details_image) SubsamplingScaleImageView mSSImageView;

    String mImageUrl;
    File mImageCache;
    Future<File> mIonFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        showLoading();

        mImageUrl = GalleryItem.getFullImage(getIntent().getStringExtra(ARG_IMAGE));
        mImageCache = new File(getExternalCacheDir(), GalleryItem.getFileName(mImageUrl));

        if(!mImageCache.exists()){
            mIonFile = Ion.with(DetailsActivity.this)
                    .load(mImageUrl)
                    .write(mImageCache)
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            if (e != null) {
                                Toast.makeText(DetailsActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                                return;
                            }

                            showImage();
                        }
                    });
        } else {
            showImage();
        }
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showImage(){
        mProgressBar.setVisibility(View.GONE);
        mSSImageView.setImage(ImageSource.uri(mImageCache.getPath()));
    }
}