package tr.philon.zerochan.data;

public class ApiHelper {
    String mQuery;
    String mSort;
    String mTime;
    String mDimen;
    int mPage;

    public ApiHelper(){
        mQuery = Api.TAG_EVERYTHING;
        mSort = Api.SORT_RECENT;
        mTime = Api.TIME_ALL;
        mDimen = Api.DIMEN_ALL;
        mPage = 1;
    }

    public String getUrl(){
        return Api.getUrl(mQuery, mSort, mTime, mDimen, mPage);
    }

    public String nextPage() {
        mPage++;
        return getUrl();
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public int getPage() {
        return mPage;
    }

    public void setSort(String sort, String time) {
        mSort = sort;
        mTime = time;
    }

    public void setDimen(String dimen) {
        mDimen = dimen;
    }
}