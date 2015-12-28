package tr.philon.zerochan.data;

public class Api {
    String mQuery;
    String mOrder;
    String mTime;
    String mDimen;
    int mPage;
    boolean hasNextPage;

    public Api() {
        mQuery = Service.TAG_EVERYTHING;
        mOrder = Service.ORDER_RECENT;
        mTime = Service.TIME_ALL;
        mDimen = Service.DIMEN_ALL;
        mPage = 1;
    }

    public Api(String query) {
        mQuery = !query.isEmpty() ? query : Service.TAG_EVERYTHING;
        mOrder = Service.ORDER_RECENT;
        mTime = Service.TIME_ALL;
        mDimen = Service.DIMEN_ALL;
        mPage = 1;
    }

    public String getUrl() {
        return Service.getUrl(mQuery, mOrder, mTime, mDimen, mPage);
    }

    public String nextPage() {
        mPage++;
        return getUrl();
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    public int getPage() {
        return mPage;
    }

    public void setPage(int page) {
        mPage = page;
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

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void hasNextPage(boolean boo) {
        hasNextPage = boo;
    }
}