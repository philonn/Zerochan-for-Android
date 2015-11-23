package tr.philon.zerochan.data;

public class ApiHelper {
    String mQuery;
    int mPage;
    String mSort;
    String mDimen;
    boolean isTagPage;

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

    public void setIsTagPage(boolean isTagPage) {
        this.isTagPage = isTagPage;
    }

    public String getUrl(){
        if(mQuery != Api.TAG_EVERYTHING
                && mQuery.equals(Api.TAG_POPULAR)){
            return Api.BASE_URL + Api.TAG_POPULAR;
        }

        if(isTagPage){
            return Api.BASE_URL + mQuery + "?s=tag"+ Api.AND + Api.PARAM_PAGE + mPage;
        }

        return Api.getUrl(mQuery, mPage, mSort, mDimen);
    }

    public int getPage(){
        return mPage;
    }

    public String nextPage(){
        mPage++;

        if(isTagPage) return Api.BASE_URL + mQuery + "?s=tag"+ Api.AND + Api.PARAM_PAGE + mPage;
        return Api.getUrl(mQuery, mPage, mSort, mDimen);
    }
}