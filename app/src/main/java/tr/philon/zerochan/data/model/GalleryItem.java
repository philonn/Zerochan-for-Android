package tr.philon.zerochan.data.model;

public class GalleryItem {
    private String mImage;
    private String mPageLink;

    public GalleryItem(String image, String pageLink) {
        this.mImage = image;
        this.mPageLink = pageLink;
    }

    public String getPageLink() {
        return mPageLink;
    }

    public String getThumbnail() {
        return mImage;
    }

    public static String getFullImage(String thumbnail) {
        thumbnail = thumbnail.replace(".240.", ".full.");
        thumbnail = thumbnail.replace(".600.", ".full.");
        return thumbnail;
    }

    public static String getFileName(String image) {
        String baseUrl = image.substring(0, image.indexOf(".net/") + 5);
        image = image.replace(baseUrl, "");
        return image;
    }
}