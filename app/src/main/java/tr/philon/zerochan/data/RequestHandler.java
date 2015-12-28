package tr.philon.zerochan.data;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class RequestHandler {
    private static RequestHandler sInstance = null;
    private OkHttpClient mHttpClient;
    private Request mRequest;
    private Call mCall;
    private Callback mCallback;

    protected RequestHandler() {
    }

    public static RequestHandler getInstance() {
        if (sInstance == null) {
            sInstance = new RequestHandler();
            sInstance.mHttpClient = new OkHttpClient();
        }
        return sInstance;
    }

    public void load(String url, Callback callback) {
        cancel();

        mCallback = callback;
        mRequest = new Request.Builder().url(url).build();
        mCall = mHttpClient.newCall(mRequest);
        mCall.enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mCallback.onFailure(null, e);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                if (response.isSuccessful())
                    mCallback.onSuccess(response.body().string());
                else mCallback.onFailure(response, null);
            }
        });
    }

    public void cancel() {
        if (mCall != null && !mCall.isCanceled()) {
            mCall.cancel();
            mCall = null;
        }
    }

    public interface Callback {

        void onSuccess(String response);

        void onFailure(Response response, Throwable throwable);
    }
}