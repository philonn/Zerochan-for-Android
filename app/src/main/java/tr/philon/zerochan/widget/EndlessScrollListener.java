package tr.philon.zerochan.widget;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/*
 * From https://guides.codepath.com/android/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    private int startingPageIndex = 1;
    private int currentPage = 1;
    private int visibleThreshold = 3;
    private int previousTotalItemCount = 0;
    private boolean loading = true;

    private GridLayoutManager mLayoutManager;

    public EndlessScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = view.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore();
            loading = true;
        }
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore();

    public void setVisibleThreshold(int visibleThreshold){
        this.visibleThreshold = visibleThreshold;
    }
}