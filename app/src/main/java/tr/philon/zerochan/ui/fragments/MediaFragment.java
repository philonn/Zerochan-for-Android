package tr.philon.zerochan.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ViewFlipper;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.Api;
import tr.philon.zerochan.data.Page;
import tr.philon.zerochan.data.model.GalleryItem;
import tr.philon.zerochan.ui.activities.DetailsActivity;
import tr.philon.zerochan.ui.adapters.GalleryAdapter;
import tr.philon.zerochan.util.PixelUtils;
import tr.philon.zerochan.util.SoupUtils;

public class MediaFragment extends Fragment {
    Context context;
    OkHttpClient mHttpClient = new OkHttpClient();

    Page mPage = new Page();
    List<GalleryItem> mGridItems;

    @Bind(R.id.grid) GridView mGrid;
    @Bind(R.id.view_flipper) ViewFlipper mViewFlipper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.view_grid_gallery, container, false);
        context = getActivity();
        ButterKnife.bind(this, rootView);

        mGrid.setNumColumns(getPossibleColumnsCount());
        mPage.setQuery(Api.TAG_POPULAR);
        loadPage(mPage.getUrl());

        return rootView;
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
                            mGridItems = SoupUtils.exportItems(body);
                            mGrid.setAdapter(new GalleryAdapter(context, R.layout.item_gallery, mGridItems, getColumnWidth()));
                            mGrid.setOnItemClickListener(myListener);
                            showView("grid");
                        }
                    }
                });
            }
        });
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

    private int getPossibleColumnsCount() {
        return PixelUtils.getScreenWidth(context) / PixelUtils.dpToPx(100);
    }

    private int getColumnWidth() {
        int screenWidth = PixelUtils.getScreenWidth(context);
        int columnCount = getPossibleColumnsCount();
        screenWidth = screenWidth - (2 * PixelUtils.dpToPx(2));

        return screenWidth / columnCount;
    }

    private AdapterView.OnItemClickListener myListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra(DetailsActivity.ARG_IMAGE, mGridItems.get(i).getThumbnail());
            startActivity(intent);
        }
    };
}