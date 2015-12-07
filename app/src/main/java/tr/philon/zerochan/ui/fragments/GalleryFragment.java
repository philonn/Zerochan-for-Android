package tr.philon.zerochan.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Service;
import tr.philon.zerochan.data.Api;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.ui.activities.DetailsActivity;
import tr.philon.zerochan.ui.activities.MainActivity;
import tr.philon.zerochan.ui.activities.SearchActivity;
import tr.philon.zerochan.ui.adapters.GalleryAdapter;
import tr.philon.zerochan.ui.widget.GridInsetDecoration;
import tr.philon.zerochan.util.PixelUtils;
import tr.philon.zerochan.util.SoupUtils;

public class GalleryFragment extends Fragment implements GalleryAdapter.ClickListener {
    Context context;
    OkHttpClient mHttpClient = new OkHttpClient();
    Api mApi = new Api();

    List<GalleryItem> mGridItems;
    GalleryAdapter mAdapter;

    @Bind(R.id.recyclerGrid) RecyclerView mRecycler;
    @Bind(R.id.view_flipper) ViewFlipper mViewFlipper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, rootView);
        context = getActivity();

        int id = getArguments().getInt(MainActivity.ARG_SECTION, 0);
        switch (id){
            case MainActivity.ID_EVERYTHING:
                mApi.setQuery(Service.TAG_EVERYTHING);
                break;
            case MainActivity.ID_POPULAR:
                mApi.setQuery(Service.TAG_POPULAR);
                break;
            default:
                String query = getArguments().getString(SearchActivity.ARG_TAGS);
                try {
                    query = URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                mApi.setQuery(query);
                break;
        }

        setUpRecyclerView();
        loadPage(mApi.getUrl());

        return rootView;
    }

    private void setUpRecyclerView() {
        mRecycler.setLayoutManager(new GridLayoutManager(context, getPossibleColumnsCount()));
        mRecycler.addItemDecoration(new GridInsetDecoration(PixelUtils.dpToPx(4)));
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
                        }
                    }
                });
            }
        });
    }

    private void displayImages(String response){
        mGridItems = SoupUtils.exportGalleryItems(response);
        mAdapter = new GalleryAdapter(this, mGridItems, getColumnWidth(), this);
        mRecycler.setAdapter(mAdapter);
        showView("grid");
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
        startActivity(intent);
    }
}