package tr.philon.zerochan.data.model;

import tr.philon.zerochan.data.Service;

public class GalleryItem {
    private String mImage;
    private String mPageLink;
    private boolean isPlaceHolder;

    public GalleryItem() {
        this.isPlaceHolder = true;
    }

    public GalleryItem(String image, String pageLink) {
        this.mImage = image;
        this.mPageLink = pageLink;
        this.isPlaceHolder = false;
    }

    public String getPageLink() {
        return mPageLink;
    }

    public String getThumbnail() {
        return mImage;
    }

    public boolean isPlaceHolder() {
        return isPlaceHolder;
    }

    public static String getId(String imageUrl) {
        String s = imageUrl.substring(1, imageUrl.length() - 4);
        s = s.substring(s.lastIndexOf('.') + 1, s.length());
        return s;
    }

    public static String getPageLink(String imageUrl) {
        return Service.BASE_URL + Service.SLASH + getId(imageUrl);
    }

    public static String getFullImage(String thumbnail) {
        thumbnail = thumbnail.replace(".75.", ".full.");
        thumbnail = thumbnail.replace(".240.", ".full.");
        thumbnail = thumbnail.replace(".600.", ".full.");
        thumbnail = thumbnail.replace(thumbnail.substring(0, 9), "http://static");
        return thumbnail;
    }

    public static String getFileName(String image) {
        String baseUrl = image.substring(0, image.indexOf(".net/") + 5);
        image = image.replace(baseUrl, "");
        return image;
    }
}