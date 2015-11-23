package tr.philon.zerochan.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;

public class TagsFragment extends Fragment {
    private static final String TAGS_URL = "https://rawgit.com/philonn/Zerochan-Tags/master/tags.txt";

    Context mContext;

    File mTagListFile;
    Future<File> mIonFile;
    MaterialDialog mDownloadDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();

        checkTagsFile();

        return rootView;
    }

    private void checkTagsFile() {
        mTagListFile = new File(mContext.getExternalFilesDir(null), "tags.txt");

        if (!mTagListFile.exists()) {
            MyDialogs.askTagsDownload(mContext, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                    mDownloadDialog = MyDialogs.showDownloading(mContext);
                    mIonFile = Ion.with(mContext)
                            .load(TAGS_URL)
                            .write(mTagListFile)
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File result) {
                                    mDownloadDialog.dismiss();

                                    if (e != null) {
                                        Toast.makeText(mContext, "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                }
                            });
                }
            });
        }
    }
}