package tr.philon.zerochan.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Api implements Parcelable{
    boolean isSingleTag;
    boolean isUser;
    String mQuery;
    String mOrder;
    String mTime;
    String mDimen;
    int mPage;
    boolean hasNextPage;

    protected Api(Parcel in) {
        isSingleTag = in.readByte() != 0x00;
        isUser = in.readByte() != 0x00;
        mQuery = in.readString();
        mOrder = in.readString();
        mTime = in.readString();
        mDimen = in.readString();
        mPage = in.readInt();
        hasNextPage = in.readByte() != 0x00;
    }

    public Api(String query, boolean isSingleTag, boolean isUser) {
        this.isSingleTag = isSingleTag;
        this.isUser = isUser;
        this.mQuery = !query.isEmpty() ? query : Service.TAG_EVERYTHING;
        this.mOrder = Service.ORDER_RECENT;
        this.mTime = Service.TIME_ALL;
        this.mDimen = Service.DIMEN_ALL;
        this.mPage = 1;
    }

    public String getUrl() {
        return Service.getUrl(isUser, mQuery, mOrder, mTime, mDimen, mPage);
    }

    public String nextPage() {
        mPage++;
        return getUrl();
    }

    public boolean isSingleTag() {
        return isSingleTag;
    }

    public String getQuery() {
        return mQuery;
    }

    public String getOrder() {
        return mOrder;
    }

    public String getTime() {
        return mTime;
    }

    public void setSort(String order, String time) {
        mPage = 1;
        mOrder = order;
        mTime = time;
    }

    public String getDimen() {
        return mDimen;
    }

    public void setDimen(String dimen) {
        mPage = 1;
        mDimen = dimen;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void hasNextPage(boolean boo) {
        hasNextPage = boo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSingleTag ? 0x01 : 0x00));
        dest.writeByte((byte) (isUser ? 0x01 : 0x00));
        dest.writeString(mQuery);
        dest.writeString(mOrder);
        dest.writeString(mTime);
        dest.writeString(mDimen);
        dest.writeInt(mPage);
        dest.writeByte((byte) (hasNextPage ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Api> CREATOR = new Parcelable.Creator<Api>() {
        @Override
        public Api createFromParcel(Parcel in) {
            return new Api(in);
        }

        @Override
        public Api[] newArray(int size) {
            return new Api[size];
        }
    };
}