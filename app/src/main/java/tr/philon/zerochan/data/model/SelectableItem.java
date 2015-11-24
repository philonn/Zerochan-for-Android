package tr.philon.zerochan.data.model;

public class SelectableItem {
    private String mTitle;
    private boolean isSelected;

    public SelectableItem(String title) {
        mTitle = title;
        isSelected = false;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}