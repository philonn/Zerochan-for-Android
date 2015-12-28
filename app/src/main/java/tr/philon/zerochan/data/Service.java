package tr.philon.zerochan.data;

/**
 * param : query,
 * param : page, max 100.
 * param : sort, 'id' recent, `fav` popular by time,
 * [only for tag pages] `tag` name, `count` popular / count.
 * param : time, `0` all-time, `1` last-week, `2` last-3-mo.
 * param : dimension, `0` all-sizes, `1` large-and-better, `2` only-very-large.
 */
public class Service {
    public final static String BASE_URL = "http://www.zerochan.net";
    public final static String PATH_USER = "/user";
    public final static String SLASH = "/";
    public final static String QMARK = "?";
    public final static String AND = "&";
    /* Special Queries */
    public final static String TAG_EVERYTHING = "";
    public final static String TAG_POPULAR = "/popular";
    public final static String TAG_META_TAGS = "/Meta+Tags";
    /* Parameters */
    public final static String PARAM_QUERY = "";
    public final static String PARAM_PAGE = "p=";
    public final static String PARAM_ORDER = "s=";
    public final static String PARAM_TIME = "t=";
    public final static String PARAM_DIMEN = "d=";
    /* Values */
    public final static String ORDER_RECENT = "id";
    public final static String ORDER_POPULAR = "fav";
    public final static String ORDER_NAME = "tag";
    public final static String ORDER_COUNT = "count";
    public final static String TIME_ALL = "0";
    public final static String TIME_LAST_WEEK = "1";
    public final static String TIME_LAST_THREE_MONTHS = "2";
    public final static String DIMEN_ALL = "0";
    public final static String DIMEN_LARGE_BETTER = "1";
    public final static String DIMEN_LARGE = "2";

    public static String getUrl(boolean isUser, String query, String order, String time, String dimension, int page) {
        if (query.equals(TAG_POPULAR))
            return BASE_URL + TAG_POPULAR;

        String pathUser = isUser ? PATH_USER : "";
        return BASE_URL + pathUser +
                SLASH + query + QMARK +
                PARAM_ORDER + order + AND +
                PARAM_TIME + time + AND +
                PARAM_DIMEN + dimension + AND +
                PARAM_PAGE + page;
    }
}