package tr.philon.zerochan.data;

public class Api {
    String mQuery;
    String mSort;
    String mTime;
    String mDimen;
    int mPage;
    boolean hasNextPage;

    public Api(){
        mQuery = Service.TAG_EVERYTHING;
        mSort = Service.SORT_RECENT;
        mTime = Service.TIME_ALL;
        mDimen = Service.DIMEN_ALL;
        mPage = 1;
    }

    public String getUrl(){
        return Service.getUrl(mQuery, mSort, mTime, mDimen, mPage);
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

    public void setSort(String sort, String time) {
        mSort = sort;
        mTime = time;
    }

    public void setDimen(String dimen) {
        mDimen = dimen;
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void hasNextPage(boolean boo) {
        hasNextPage = boo;
    }
}