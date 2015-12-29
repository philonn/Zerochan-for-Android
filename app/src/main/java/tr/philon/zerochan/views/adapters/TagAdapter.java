package tr.philon.zerochan.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import tr.philon.zerochan.R;
import tr.philon.zerochan.data.model.SelectableItem;

public class TagAdapter extends ArrayAdapter<SelectableItem> {
    private Context mContext;
    private List<SelectableItem> mMainList;
    private List<SelectableItem> mSelectedItems;
    private List<SelectableItem> mBackupList;
    private int colorTextDefault;
    private int colorTextSelected;

    public TagAdapter(Context context, int resource, List<SelectableItem> items) {
        super(context, resource, items);
        mContext = context;
        mMainList = items;

        colorTextDefault = mContext.getResources().getColor(R.color.text_primary_white);
        colorTextSelected = mContext.getResources().getColor(R.color.app_accent);

        mSelectedItems = new ArrayList<>();
        mBackupList = new ArrayList<>();
        mBackupList.addAll(mMainList);
        mMainList.clear();
    }

    static class ViewHolder {
        @Bind(R.id.item_tag_text) TextView text;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tag, null);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.text.setText(mMainList.get(position).getTitle());
        if (mMainList.get(position).isSelected()) {
            mHolder.text.setTextColor(colorTextSelected);
        } else {
            mHolder.text.setTextColor(colorTextDefault);
        }
        return convertView;
    }

    public void updateSelection(int position) {
        SelectableItem item = mMainList.get(position);
        if (item.isSelected()) {
            mSelectedItems.remove(item);
            item.setSelected(false);
        } else {
            mSelectedItems.add(item);
            item.setSelected(true);
        }

        notifyDataSetChanged();
    }

    public void filter(String text) {
        text = text.toLowerCase(Locale.getDefault());
        mMainList.clear();

        if (text.isEmpty()) mMainList.clear();

        for (SelectableItem item : mSelectedItems) {
            mMainList.add(item);
        }

        if (!text.isEmpty()) {
            for (SelectableItem item : mBackupList) {
                if (item.getTitle().toLowerCase(Locale.getDefault()).contains(text)) {
                    if (!mSelectedItems.contains(item)) mMainList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public boolean isSelectionAvailable() {
        return !mSelectedItems.isEmpty();
    }
}