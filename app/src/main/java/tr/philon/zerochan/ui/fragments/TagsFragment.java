package tr.philon.zerochan.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
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
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.SelectableItem;
import tr.philon.zerochan.ui.activities.SearchActivity;
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

    @Bind(R.id.tags_grid) GridView mGrid;
    @Bind(R.id.tags_fab) FloatingActionButton mFAB;
    @Bind(R.id.search_edit) EditText mSearchText;
    @Bind(R.id.search_clear) ImageButton mSearchClearBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        ButterKnife.bind(this, rootView);
        mContext = getActivity();

        initFAB();
        initSearchBar();
        downloadTags();
        //readTags();

        return rootView;
    }

    private void updateFABVisibility() {
        if (mAdapter != null) {
            if (mAdapter.isSelectionAvailable()) showFAB();
            else hideFAB();
        }
    }

    private void showFAB() {
        mFAB.animate().cancel();
        mFAB.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(200)
                .start();
    }

    private void hideFAB() {
        mFAB.animate().cancel();
        mFAB.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(200)
                .start();
    }

    private void initFAB() {
        hideFAB();
        mFAB.setOnClickListener(onFABClickListener);
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

                updateFABVisibility();
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
            updateFABVisibility();

            SelectableItem item = mAdapter.getItem(position);
            if (item.isSelected()) mSelectedTags.add(item);
            else mSelectedTags.remove(item);
        }
    };

    private View.OnClickListener onFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String tags = mSelectedTags.toString();
            tags = tags.substring(1, tags.length() - 1).replaceAll(", ", ",");

            Intent intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra(SearchActivity.ARG_TAGS, tags);
            startActivity(intent);
        }
    };
}