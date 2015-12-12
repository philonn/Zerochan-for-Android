package tr.philon.zerochan.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.GalleryItem;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private Fragment mFragment;
    private List<GalleryItem> mDataset;
    private ClickListener mListener;
    private int mColumnWidth;

    public GalleryAdapter(Fragment fragment, List<GalleryItem> dataset, int columnWidth, ClickListener listener) {
        mFragment = fragment;
        mDataset = dataset;
        mColumnWidth = columnWidth;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_thumb, parent, false);

        if (mListener != null) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onItemClick(v);
                }
            });
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //noinspection SuspiciousNameCombination
        holder.layout.setLayoutParams(new LinearLayout.LayoutParams(mColumnWidth, mColumnWidth));

        int random = new Random().nextInt((5 - 1) + 1) + 1;
        float f = 1f;
        switch (random) {
            case 1:
                f = 0.6f;
                break;
            case 2:
                f = 0.5f;
                break;
            case 3:
                f = 0.4f;
                break;
            case 4:
                f = 0.3f;
                break;
            case 5:
                f = 0.2f;
                break;
        }

        holder.image.setAlpha(f);
        Glide.with(mFragment)
                .load(mDataset.get(position).getThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.image.setAlpha(1f);
                        return false;
                    }
                })
                .into(holder.image);

        if (mDataset.get(position).isPlaceHolder())
            holder.progressBar.setVisibility(View.VISIBLE);
        else holder.progressBar.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_thumb_layout) FrameLayout layout;
        @Bind(R.id.item_thumb_image) ImageView image;
        @Bind(R.id.item_thumb_progress_bar) ProgressBar progressBar;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public interface ClickListener {
        void onItemClick(View v);
    }
}