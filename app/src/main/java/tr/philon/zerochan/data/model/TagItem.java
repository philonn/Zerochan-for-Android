package tr.philon.zerochan.data.model;

public class TagItem {
    public String mTagName;
    public String mTagLink;
    private boolean isTheme;

    public TagItem(String tagName, String tagLink, boolean isTheme) {
        this.mTagName = tagName;
        this.mTagLink = tagLink;
        this.isTheme = isTheme;
    }

    public String getTagName() {
        return mTagName;
    }

    public String getTagLink() {
        return mTagLink;
    }

    public boolean isTheme() {
        return isTheme;
    }
}