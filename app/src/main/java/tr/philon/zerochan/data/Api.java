package tr.philon.zerochan.data;

import tr.philon.zerochan.data.prefs.PrefManager;

public class Api {
    String mQuery;
    String mOrder;
    String mTime;
    String mDimen;
    int mPage;
    boolean hasNextPage;

    public Api() {
        mQuery = Service.TAG_EVERYTHING;
        mOrder = PrefManager.getInstance().getString(PrefManager.KEY_ORDER);
        mTime = PrefManager.getInstance().getString(PrefManager.KEY_TIME);
        mDimen = PrefManager.getInstance().getString(PrefManager.KEY_DIMEN);
        mPage = 1;

        if (mOrder == null && mTime == null && mDimen == null) {
            setSort(Service.ORDER_RECENT, Service.TIME_ALL);
            setDimen(Service.DIMEN_ALL);
        }
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
        PrefManager.getInstance().putString(PrefManager.KEY_ORDER, mOrder);
        PrefManager.getInstance().putString(PrefManager.KEY_TIME, mTime);
    }

    public String getDimen() {
        return mDimen;
    }

    public void setDimen(String dimen) {
        mPage = 1;
        mDimen = dimen;
        PrefManager.getInstance().putString(PrefManager.KEY_DIMEN, mDimen);
    }

    public boolean hasNextPage() {
        return hasNextPage;
    }

    public void hasNextPage(boolean boo) {
        hasNextPage = boo;
    }
}