package tr.philon.zerochan.data;

public class Api {
    boolean isUser;
    String mQuery;
    String mOrder;
    String mTime;
    String mDimen;
    int mPage;
    boolean hasNextPage;

    public Api(String query, boolean isUser) {
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

    public boolean isUser() {
        return isUser;
    }

    public void setIsUser(boolean boo) {
        this.isUser = boo;
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
}