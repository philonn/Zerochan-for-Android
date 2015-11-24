package tr.philon.zerochan.ui.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.SelectableItem;
import tr.philon.zerochan.ui.adapters.TagAdapter;

public class TagsFragment extends Fragment {
    private static final String TAGS_URL = "https://rawgit.com/philonn/Zerochan-Tags/master/tags.txt";

    Context mContext;

    File mTagListFile;
    Future<File> mIonFile;
    MaterialDialog mLoadingDialog;

    List<SelectableItem> mTagList;
    List<SelectableItem> mSelectedTags;
    TagAdapter mAdapter;

    @Bind(R.id.grid_tags) GridView mGrid;
    @Bind(R.id.search_edit) EditText mSearchText;
    @Bind(R.id.search_clear) ImageButton mSearchClearBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();

        initSearchBar();
        downloadTags();
        //readTags();

        return rootView;
    }

    public void initSearchBar() {
        //Show text cursor
        mSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sbEditText.requestFocus();
                //sbEditText.performClick();
                mSearchText.setCursorVisible(true);
            }
        });

        //Show/Hide clear button
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* empty */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    mSearchClearBtn.setVisibility(View.VISIBLE);
                    if (mAdapter != null) mAdapter.filter(s.toString());
                } else {
                    mSearchClearBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                /* empty */
            }
        });

        //Clear text
        mSearchClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchText != null) {
                    mSearchText.setText("");
                    mSearchText.performClick();
                    mAdapter.filter("");
                }
            }
        });
    }

    private void downloadTags() {
        mTagListFile = new File(mContext.getExternalFilesDir(null), "tags.txt");
        mLoadingDialog = MyDialogs.showLoading(mContext);

        if (!mTagListFile.exists()) {
            mIonFile = Ion.with(mContext)
                    .load(TAGS_URL)
                    .write(mTagListFile)
                    .setCallback(new FutureCallback<File>() {
                        @Override
                        public void onCompleted(Exception e, File result) {
                            if (e != null) {
                                Toast.makeText(mContext, "Something went wrong, please try again later", Toast.LENGTH_LONG).show();
                                return;
                            }

                            readTags();
                        }
                    });
        } else {
            readTags();
        }
    }

    private void readTags() {
        new readTask().execute();
    }

    private class readTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Scanner scanner;
            try {
                scanner = new Scanner(mTagListFile);
                mTagList = new ArrayList<>();
                mSelectedTags = new ArrayList<>();

                while (scanner.hasNextLine()) {
                    mTagList.add(new SelectableItem(scanner.nextLine()));
                }

                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mTagList != null) {
                mAdapter = new TagAdapter(mContext, R.layout.item_tag, mTagList);
                mGrid.setAdapter(mAdapter);
                mGrid.setOnItemClickListener(onItemClick);
                mLoadingDialog.dismiss();
            }
        }
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            mAdapter.updateSelection(position);

            SelectableItem item = mAdapter.getItem(position);
            if (item.isSelected()) mSelectedTags.add(item);
            else mSelectedTags.remove(item);

        }
    };
}