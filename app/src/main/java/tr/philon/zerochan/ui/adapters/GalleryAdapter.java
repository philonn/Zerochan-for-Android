package tr.philon.zerochan.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.GalleryItem;

import java.util.List;
import java.util.Random;

public class GalleryAdapter extends ArrayAdapter<GalleryItem> {
    private Context context;

    private List<GalleryItem> items;
    private int columnWidth;

    public GalleryAdapter(Context context, int resource, List<GalleryItem> items, int columnWidth) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
        this.columnWidth = columnWidth;
    }

    static class ViewHolder {
        @Bind(R.id.item_gallery_layout) FrameLayout layout;
        @Bind(R.id.item_gallery_image) ImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_gallery, null);
            mHolder = new ViewHolder(convertView);
            mHolder.layout.setLayoutParams(new FrameLayout.LayoutParams(columnWidth, columnWidth));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

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

        mHolder.image.setAlpha(f);
        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mHolder.image.setAlpha(1f);
                        return false;
                    }
                })
                .into(mHolder.image);

        return convertView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public GalleryItem getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}