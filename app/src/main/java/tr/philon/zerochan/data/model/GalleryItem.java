package tr.philon.zerochan.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import tr.philon.zerochan.data.Service;

public class GalleryItem implements Parcelable{
    private String mImage;
    private String mPageLink;
    private boolean isPlaceHolder;

    protected GalleryItem(Parcel in) {
        mImage = in.readString();
        mPageLink = in.readString();
        isPlaceHolder = in.readByte() != 0x00;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mImage);
        dest.writeString(mPageLink);
        dest.writeByte((byte) (isPlaceHolder ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GalleryItem> CREATOR = new Parcelable.Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel in) {
            return new GalleryItem(in);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };
}