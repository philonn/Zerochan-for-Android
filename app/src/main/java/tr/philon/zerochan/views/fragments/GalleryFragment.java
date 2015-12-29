package tr.philon.zerochan.views.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Api;
import tr.philon.zerochan.data.RequestHandler;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.util.PixelUtils;
import tr.philon.zerochan.util.SoupUtils;
import tr.philon.zerochan.views.activities.DetailsActivity;
import tr.philon.zerochan.views.activities.SearchActivity;
import tr.philon.zerochan.views.adapters.GalleryAdapter;
import tr.philon.zerochan.widget.EndlessScrollListener;
import tr.philon.zerochan.widget.GridInsetDecoration;

public class GalleryFragment extends Fragment {
    @Bind(R.id.view_flipper) ViewFlipper mViewFlipper;
    @Bind(R.id.gallery_coordinator) CoordinatorLayout mCoordinator;
    @Bind(R.id.gallery_recycler) RecyclerView mRecycler;
    @Bind(R.id.gallery_error_image) ImageView mErrorImage;
    @Bind(R.id.gallery_error_message) TextView mErrorMessage;
    @Bind(R.id.gallery_error_button) TextView mErrorBtn;
    @BindString(R.string.transition_thumb) String mTransitionName;

    private static final int VIEW_LOADING = 0;
    private static final int VIEW_CONTENT = 1;
    private static final int VIEW_ERROR = 2;

    private Context mContext;
    private Api mApi;
    private List<GalleryItem> mDataset;
    private List<String> mRelatedTags;
    private GalleryAdapter mAdapter;
    private Drawer mRelatedTagsDrawer;

    private boolean isPlaceHolderVisible;

    public static GalleryFragment newInstance(String tags, boolean isUser) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();

        args.putString(SearchActivity.ARG_TAGS, tags);
        args.putBoolean(SearchActivity.ARG_IS_SINGLE_TAG, !tags.contains(",") && !tags.isEmpty());
        args.putBoolean(SearchActivity.ARG_USER, isUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        setHasOptionsMenu(true);

        mContext = getContext();
        mApi = new Api(Uri.encode(getArguments().getString(SearchActivity.ARG_TAGS)),
                getArguments().getBoolean(SearchActivity.ARG_IS_SINGLE_TAG),
                getArguments().getBoolean(SearchActivity.ARG_USER));


        ButterKnife.bind(this, rootView);
        initGrid();
        initButtons();
        //initRelatedTagsDrawer();

        loadPage();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem relatedTags = menu.findItem(R.id.action_related_tags_toggle);
        if (mApi.isSingleTag()) relatedTags.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort:
                showSortDialog();
                break;
            case R.id.action_related_tags_toggle:
                showRelatedTagsDrawer();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RequestHandler.getInstance().cancel();
    }

    //View

    private void showLoading() {
        mViewFlipper.setDisplayedChild(VIEW_LOADING);
    }

    private void showError() {
        mViewFlipper.setDisplayedChild(VIEW_ERROR);
    }

    private void showEmpty() {
        mErrorImage.setVisibility(View.GONE);
        mErrorBtn.setVisibility(View.GONE);
        mErrorMessage.setText("No results found");
        mViewFlipper.setDisplayedChild(VIEW_ERROR);
    }

    private void showContent() {
        mViewFlipper.setDisplayedChild(VIEW_CONTENT);
    }

    private void showRelatedTagsDrawer() {
        mRelatedTagsDrawer.openDrawer();
    }

    private  void showSortDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_sort, null);

        final RadioGroup rgOrder = (RadioGroup) view.findViewById(R.id.rd_group_order);
        final RadioButton sortRecent = (RadioButton) view.findViewById(R.id.rd_order_recent);
        final RadioButton sortPopularWeek = (RadioButton) view.findViewById(R.id.rd_order_popular_week);
        final RadioButton sortPopularMonth = (RadioButton) view.findViewById(R.id.rd_order_popular_month);
        final RadioButton sortPopularAll = (RadioButton) view.findViewById(R.id.rd_order_popular_all);
        final RadioGroup rgDimen = (RadioGroup) view.findViewById(R.id.rd_group_resolution);
        final RadioButton dimenAll = (RadioButton) view.findViewById(R.id.rd_resolution_all);
        final RadioButton dimenBetter = (RadioButton) view.findViewById(R.id.rd_resolution_better);
        final RadioButton dimenLarge = (RadioButton) view.findViewById(R.id.rd_resolution_large);

        if (mApi.getOrder().equals(Service.ORDER_RECENT)) {
            sortRecent.setChecked(true);
        } else if (mApi.getOrder().equals(Service.ORDER_POPULAR)) {
            switch (mApi.getTime()) {
                case Service.TIME_LAST_WEEK: sortPopularWeek.setChecked(true); break;
                case Service.TIME_LAST_THREE_MONTHS: sortPopularMonth.setChecked(true); break;
                case Service.TIME_ALL: sortPopularAll.setChecked(true); break;
            }
        }

        switch (mApi.getDimen()) {
            case Service.DIMEN_ALL: dimenAll.setChecked(true); break;
            case Service.DIMEN_LARGE_BETTER: dimenBetter.setChecked(true); break;
            case Service.DIMEN_LARGE: dimenLarge.setChecked(true); break;
        }

        new MaterialDialog.Builder(mContext)
                .customView(view, false)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.text_secondary_white)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        int orderId = rgOrder.getCheckedRadioButtonId();
                        int dimenId = rgDimen.getCheckedRadioButtonId();

                        onSortChanged(orderId, dimenId);
                    }
                })
                .show();
    }

    private void showLoadingMore(boolean show) {
        if (show) {
            if (isLoading()) return;

            isLoading(true);
            mDataset.add(new GalleryItem());
            mAdapter.notifyItemInserted(mDataset.size());
        } else {
            if (!isLoading()) return;

            isLoading(false);
            mDataset.remove(mDataset.size() - 1);
            mAdapter.notifyItemRemoved(mDataset.size());
        }
    }

    private void showLoadingMoreError() {
        Snackbar.make(mCoordinator, "Unable to load more", Snackbar.LENGTH_INDEFINITE)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        retryLoadMore();
                    }
                }).show();
    }

    private void addOlderItems(List<GalleryItem> items) {
        int count = mAdapter.getItemCount();
        int newCount = count + items.size();

        mDataset.addAll(items);
        mAdapter.notifyItemRangeInserted(count, newCount);
    }


    private void initGrid() {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, getColumnsCount());
        GridInsetDecoration decoration = new GridInsetDecoration(PixelUtils.dpToPx(4));
        EndlessScrollListener listener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                GalleryFragment.this.onLoadMore();
            }
        };
        listener.setVisibleThreshold(getColumnsCount() + 1);

        mDataset = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, mDataset, getColumnWidth(), new GalleryAdapter.ClickListener() {
            @Override
            public void onItemClick(View v) {
                onImageClick(v);
            }
        });

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(decoration);
        mRecycler.addOnScrollListener(listener);
        mRecycler.setAdapter(mAdapter);
    }

    private void initButtons() {
        mErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onErrorBtnClick();
            }
        });
    }

    private void initRelatedTagsDrawer() {
        mRelatedTagsDrawer = new DrawerBuilder()
                .withActivity(getActivity())
                .withDrawerWidthDp(280)
                .withDrawerGravity(Gravity.END)
                .withCloseOnClick(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        onRelatedTagsDrawerItemClick(position);
                        return false;
                    }
                })
                .build();

        for (String tag : mRelatedTags) {
            mRelatedTagsDrawer.addItem(new PrimaryDrawerItem().withName(tag).withSelectable(false));
        }
    }


    private int getColumnsCount() {
        return PixelUtils.getScreenWidth() / PixelUtils.dpToPx(100);
    }

    private int getColumnWidth() {
        int screenWidth = PixelUtils.getScreenWidth() - (2 * PixelUtils.dpToPx(2));
        int columnCount = getColumnsCount();
        return screenWidth / columnCount;
    }


    private void onErrorBtnClick() {
        retryLoadMore();
    }

    private void onImageClick(View v) {
        int position = mRecycler.getChildAdapterPosition(v);
        if (isPlaceHolderVisible && mDataset.get(position).isPlaceHolder()) return;

        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra(DetailsActivity.ARG_IMAGE, mDataset.get(position).getThumbnail());

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation
                        (((Activity) mContext), v.findViewById(R.id.item_thumb_image), mTransitionName);

        ActivityCompat.startActivity(((Activity) mContext), intent, options.toBundle());
    }

    private void onRelatedTagsDrawerItemClick(int position) {
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(SearchActivity.ARG_TAGS, mRelatedTags.get(position));
        intent.putExtra(SearchActivity.ARG_USER, false);
        startActivity(intent);
    }

    //Presenter

    private void loadPage() {
        loadPage(mApi.getUrl());
    }

    private void loadPage(String url) {
        if (isFirstPage())
            showLoading();
        else showLoadingMore(true);

        RequestHandler.getInstance().load(url, new RequestHandler.Callback() {
            Handler handler = new Handler(mContext.getMainLooper());

            @Override
            public void onSuccess(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (loadRelatedTags()) {
                            mRelatedTags = SoupUtils.getRelatedTags(response, mApi.getQuery());
                            initRelatedTagsDrawer();
                        }

                        if (isFirstPage())
                            showContent();
                        showLoadingMore(false);
                        addOlderItems(SoupUtils.exportGalleryItems(response));
                        mApi.hasNextPage(SoupUtils.hasNextPage(response));
                    }
                });
            }

            @Override
            public void onFailure(final Response response, Throwable throwable) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (response != null && response.code() == 404)
                            showEmpty();
                        else if (isFirstPage())
                            showError();
                        else showLoadingMoreError();
                    }
                });
            }
        });
    }

    private void loadNextPage() {
        loadPage(mApi.nextPage());
    }

    private void retryLoadMore() {
        loadPage(mApi.getUrl());
    }

    private void refreshPage() {
        if (isFirstPage()) {
            loadPage(mApi.getUrl());
        } else {
            mApi.setPage(1);
            int count = mAdapter.getItemCount();
            mDataset.clear();
            mAdapter.notifyItemRangeRemoved(0, count);
            loadPage(mApi.getUrl());
        }
    }

    private void onSortChanged(int orderId, int dimenId){
        switch (orderId) {
            case R.id.rd_order_recent: mApi.setSort(Service.ORDER_RECENT, Service.TIME_ALL); break;
            case R.id.rd_order_popular_week: mApi.setSort(Service.ORDER_POPULAR, Service.TIME_LAST_WEEK); break;
            case R.id.rd_order_popular_month: mApi.setSort(Service.ORDER_POPULAR, Service.TIME_LAST_THREE_MONTHS); break;
            case R.id.rd_order_popular_all: mApi.setSort(Service.ORDER_POPULAR, Service.TIME_ALL); break;
        }

        switch (dimenId) {
            case R.id.rd_resolution_all: mApi.setDimen(Service.DIMEN_ALL); break;
            case R.id.rd_resolution_better: mApi.setDimen(Service.DIMEN_LARGE_BETTER); break;
            case R.id.rd_resolution_large: mApi.setDimen(Service.DIMEN_LARGE); break;
        }

        refreshPage();
    }

    private void onLoadMore(){

    }


    private boolean isFirstPage() {
        return mDataset.size() == 0;
    }

    private boolean loadRelatedTags() {
        return mApi.isSingleTag() && mRelatedTags == null;
    }

    private boolean isLoading() {
        return isPlaceHolderVisible;
    }

    private void isLoading(boolean boo) {
        isPlaceHolderVisible = boo;
    }
}
