package tr.philon.zerochan.data;

public class ApiHelper {
    String mQuery;
    int mPage;
    String mSort;
    String mDimen;

    public ApiHelper(){
        mQuery = null;
        mPage = 1;
        mSort = Api.SORT_RECENT;
        mDimen = Api.DIMEN_ALL;
    }

    public void setQuery(String query) {
        mQuery = query;
    }

    public void setPage(int page) {
        mPage = page;
    }

    public void setSort(String sort) {
        mSort = sort;
    }

    public void setDimen(String dimen) {
        mDimen = dimen;
    }

    public String getUrl(){
        if(mQuery != null && mQuery.equals(Api.TAG_POPULAR)){
            return Api.BASE_URL + Api.TAG_POPULAR;
        }

        return Api.getUrl(mQuery, mPage, mSort, mDimen);
    }

    public String nextPage(){
        mPage++;
        return Api.getUrl(mQuery, mPage, mSort, mDimen);
    }
}