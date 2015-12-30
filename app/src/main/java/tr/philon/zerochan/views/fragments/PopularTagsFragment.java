package tr.philon.zerochan.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.okhttp.Response;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.RequestHandler;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.util.SoupUtils;
import tr.philon.zerochan.views.activities.SearchActivity;
import tr.philon.zerochan.views.adapters.PopularTagsAdapter;

public class PopularTagsFragment extends Fragment {
    public static final String TAG = "popularTagsFragment";

    private static final String STATE_DATASET = "dataset";
    private static final String STATE_VIEW = "view";

    private static final int VIEW_STATE_LOADING = 3;
    private static final int VIEW_STATE_ERROR = 4;
    private static final int VIEW_STATE_CONTENT = 6;

    private static final int VIEW_LOADING = 0;
    private static final int VIEW_ERROR = 2;
    private static final int VIEW_CONTENT = 1;

    private Context mContext;
    private ArrayList<String> mDataset;
    private PopularTagsAdapter mAdapter;
    private int mViewState;

    @Bind(R.id.view_flipper) ViewFlipper mViewFlipper;
    @Bind(R.id.tags_recycler) RecyclerView mRecycler;
    @Bind(R.id.tags_error_button) TextView mErrorBtn;

    public static PopularTagsFragment newInstance() {
        return new PopularTagsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tags, container, false);
        ButterKnife.bind(this, rootView);

        mContext = getActivity();
        //initGrid();
        initButtons();
        if (null != savedInstanceState)
            restoreState(savedInstanceState);
        else loadTags();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestHandler.getInstance().cancel();
    }

    //View

    private void showLoading() {
        saveViewState(VIEW_LOADING);
        mViewFlipper.setDisplayedChild(VIEW_LOADING);
    }

    private void showError() {
        saveViewState(VIEW_ERROR);
        mViewFlipper.setDisplayedChild(VIEW_ERROR);
    }

    private void showContent() {
        saveViewState(VIEW_STATE_CONTENT);
        mViewFlipper.setDisplayedChild(VIEW_CONTENT);
    }


    private void initRecycler(ArrayList<String> tags){
        mDataset = tags;
        mAdapter = new PopularTagsAdapter(mDataset, new PopularTagsAdapter.ClickListener() {
            @Override
            public void onItemClick(View v) {
                onClickTag(v);
            }
        });

        mRecycler.setAdapter(mAdapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mRecycler.setVerticalScrollBarEnabled(true);
    }

    private void initButtons(){
        mErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickErrorBtn();
            }
        });
    }


    private void onClickErrorBtn(){
        loadTags();
    }

    private void onClickTag(View v){
        int position = mRecycler.getChildAdapterPosition(v);
        startActivity(SearchActivity.newInstance(mContext, mDataset.get(position)));
    }

    private void onClickSearch(){}

    //Presenter

    private void loadTags(){
        showLoading();

        RequestHandler.getInstance().cancel();
        RequestHandler.getInstance().load(Service.getHomePage(), new RequestHandler.Callback() {
            Handler handler = new Handler(mContext.getMainLooper());

            @Override
            public void onSuccess(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        initRecycler(SoupUtils.getPopularTags(response));
                        showContent();
                    }
                });
            }

            @Override
            public void onFailure(Response response, Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showError();
                    }
                });
            }
        });
    }


    private void saveState(Bundle outState){
        outState.putStringArrayList(STATE_DATASET, mDataset);
        outState.putInt(STATE_VIEW, mViewState);
    }

    private void saveViewState(int state){
        mViewState = state;
    }

    private void restoreState(Bundle savedState){
        initRecycler(savedState.getStringArrayList(STATE_DATASET));
        mViewState = savedState.getInt(STATE_VIEW);

        switch (mViewState){
            case VIEW_STATE_LOADING: showLoading(); loadTags(); break;
            case VIEW_STATE_ERROR: showError(); break;
            case VIEW_STATE_CONTENT: showContent(); break;
        }
    }
}