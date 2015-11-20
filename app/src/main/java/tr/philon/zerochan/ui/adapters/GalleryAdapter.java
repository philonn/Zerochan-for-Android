package tr.philon.zerochan.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.GalleryItem;

import java.util.List;

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

        Glide.with(context)
                .load(items.get(position).getThumbnail())
                .centerCrop()
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