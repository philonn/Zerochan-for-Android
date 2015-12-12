package tr.philon.zerochan.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.data.Api;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.ui.activities.DetailsActivity;
import tr.philon.zerochan.ui.activities.MainActivity;
import tr.philon.zerochan.ui.activities.SearchActivity;
import tr.philon.zerochan.ui.adapters.GalleryAdapter;
import tr.philon.zerochan.ui.widget.EndlessScrollListener;
import tr.philon.zerochan.ui.widget.GridInsetDecoration;
import tr.philon.zerochan.util.PixelUtils;
import tr.philon.zerochan.util.SoupUtils;

public class GalleryFragment extends Fragment implements GalleryAdapter.ClickListener {
    @Bind(R.id.view_flipper) ViewFlipper mViewFlipper;
    @Bind(R.id.gallery_recycler) RecyclerView mRecycler;
    @Bind(R.id.gallery_error_button) TextView mErrorBtn;
    @BindString(R.string.transition_thumb) String mTransitionName;

    Context context;
    OkHttpClient mHttpClient;
    Api mApi;

    List<GalleryItem> mGridItems;
    GalleryAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, rootView);
        setHasOptionsMenu(true);
        context = getActivity();
        mHttpClient = new OkHttpClient();
        mApi = new Api();

        int id = getArguments().getInt(MainActivity.ARG_SECTION, 0);
        switch (id) {
            case MainActivity.ID_EVERYTHING:
                mApi.setQuery(Service.TAG_EVERYTHING);
                break;
            case MainActivity.ID_POPULAR:
                mApi.setQuery(Service.TAG_POPULAR);
                break;
            default:
                String query = getArguments().getString(SearchActivity.ARG_TAGS);
                mApi.setQuery(Uri.encode(query));
                break;
        }

        setUpRecyclerView();
        initErrorBtn();
        loadPage(mApi.getUrl());

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort:
                showSortDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setUpRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(context, getPossibleColumnsCount());
        GridInsetDecoration decoration = new GridInsetDecoration(PixelUtils.dpToPx(4));
        EndlessScrollListener listener = new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (mApi.hasNextPage())
                    loadPage(mApi.nextPage());
            }
        };
        listener.setVisibleThreshold(getPossibleColumnsCount() + 1);

        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(decoration);
        mRecycler.addOnScrollListener(listener);
    }

    private int getPossibleColumnsCount() {
        return PixelUtils.getScreenWidth(context) / PixelUtils.dpToPx(100);
    }

    private int getColumnWidth() {
        int screenWidth = PixelUtils.getScreenWidth(context);
        int columnCount = getPossibleColumnsCount();
        screenWidth = screenWidth - (2 * PixelUtils.dpToPx(2));

        return screenWidth / columnCount;
    }


    public void loadPage(String url) {
        if (mGridItems == null || mGridItems.size() == 0)
            showView("loading");

        Request request = new Request.Builder().url(url).build();
        Call call = mHttpClient.newCall(request);

        call.enqueue(new Callback() {
            Handler mainHandler = new Handler(context.getMainLooper());

            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showView("error");
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
                            showView("error");
                        } else {
                            displayImages(body);
                            mApi.hasNextPage(SoupUtils.hasNextPage(body));
                        }
                    }
                });
            }
        });
    }

    private void displayImages(String response) {
        if (mGridItems == null) {
            mGridItems = SoupUtils.exportGalleryItems(response);
            mAdapter = new GalleryAdapter(this, mGridItems, getColumnWidth(), this);
            mRecycler.setAdapter(mAdapter);
        } else {
            List<GalleryItem> newItems = SoupUtils.exportGalleryItems(response);
            int count = mAdapter.getItemCount();
            int newCount = count + newItems.size();

            mGridItems.addAll(newItems);
            mAdapter.notifyItemRangeInserted(count, newCount);
        }

        showView("grid");
    }


    private void showSortDialog() {
        LayoutInflater inflater = LayoutInflater.from(context);
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
                case Service.TIME_LAST_WEEK:
                    sortPopularWeek.setChecked(true);
                    break;
                case Service.TIME_LAST_THREE_MONTHS:
                    sortPopularMonth.setChecked(true);
                    break;
                case Service.TIME_ALL:
                    sortPopularAll.setChecked(true);
                    break;
            }
        }

        switch (mApi.getDimen()) {
            case Service.DIMEN_ALL:
                dimenAll.setChecked(true);
                break;
            case Service.DIMEN_LARGE_BETTER:
                dimenBetter.setChecked(true);
                break;
            case Service.DIMEN_LARGE:
                dimenLarge.setChecked(true);
                break;
        }

        new MaterialDialog.Builder(context)
                .customView(view, false)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.text_secondary_white)
                .positiveText(R.string.save)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        int orderID = rgOrder.getCheckedRadioButtonId();
                        int dimenID = rgDimen.getCheckedRadioButtonId();

                        switch (orderID) {
                            case R.id.rd_order_recent:
                                mApi.setSort(Service.ORDER_RECENT, Service.TIME_ALL);
                                break;
                            case R.id.rd_order_popular_week:
                                mApi.setSort(Service.ORDER_POPULAR, Service.TIME_LAST_WEEK);
                                break;
                            case R.id.rd_order_popular_month:
                                mApi.setSort(Service.ORDER_POPULAR, Service.TIME_LAST_THREE_MONTHS);
                                break;
                            case R.id.rd_order_popular_all:
                                mApi.setSort(Service.ORDER_POPULAR, Service.TIME_ALL);
                                break;
                        }

                        switch (dimenID) {
                            case R.id.rd_resolution_all:
                                mApi.setDimen(Service.DIMEN_ALL);
                                break;
                            case R.id.rd_resolution_better:
                                mApi.setDimen(Service.DIMEN_LARGE_BETTER);
                                break;
                            case R.id.rd_resolution_large:
                                mApi.setDimen(Service.DIMEN_LARGE);
                                break;
                        }

                        notifySortChanged();
                    }
                })
                .show();
    }

    private void notifySortChanged() {
        int count = mAdapter.getItemCount();
        mGridItems.clear();
        mAdapter.notifyItemRangeRemoved(0, count);
        loadPage(mApi.getUrl());
    }


    private void showView(String view) {
        switch (view) {
            case "loading":
                mViewFlipper.setDisplayedChild(0);
                break;
            case "grid":
                mViewFlipper.setDisplayedChild(1);
                break;
            case "error":
                mViewFlipper.setDisplayedChild(2);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(View v) {
        int position = mRecycler.getChildAdapterPosition(v);

        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(DetailsActivity.ARG_IMAGE, mGridItems.get(position).getThumbnail());

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation
                        (((Activity) context), v, mTransitionName);

        //ActivityCompat.startActivity(((Activity) context), intent, options.toBundle());
        if (Build.VERSION.SDK_INT >= 16)
            context.startActivity(intent, options.toBundle());
        else context.startActivity(intent);

    }

    private void initErrorBtn(){
        mErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPage(mApi.getUrl());
            }
        });
    }
}