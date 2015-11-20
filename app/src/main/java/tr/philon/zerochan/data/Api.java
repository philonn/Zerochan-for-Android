package tr.philon.zerochan.data;

public class Api {
    public final static String BASE_URL = "http://www.zerochan.net/";
    public final static String TAG_SEARCH = "search?";
    public final static String TAG_POPULAR = "popular";
    public final static String PARAM_QUERY = "q=";
    public final static String PARAM_PAGE = "p=";
    public final static String SORT_RECENT = "s=id";
    public final static String SORT_POPULAR_ALL = "s=fav&t=0";
    public final static String SORT_POPULAR_LAST_WEEK = "s=fav&t=1";
    public final static String SORT_POPULAR_THREE_WEEKS = "s=fav&t=2";
    public final static String DIMEN_ALL = "d=0";
    public final static String DIMEN_LARGE_BETTER = "d=1";
    public final static String DIMEN_LARGE = "d=2";
    public final static String AND = "&";

    public static String getUrl(String query, int page, String sort, String dimensions){
        String s = BASE_URL;

        if(query == null) s += "?";
        else  s += addParam(TAG_SEARCH, query);

        s += addParam(PARAM_PAGE, page + "")
                + AND + addParam(sort, null)
                + AND + addParam(dimensions, null);
        return s;
    }

    private static String addParam(String param, String value){
        return (value == null) ? param : param + value;
    }
}