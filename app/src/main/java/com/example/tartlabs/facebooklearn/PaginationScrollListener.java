package com.example.tartlabs.facebooklearn;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private boolean isGridLayoutManager;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public PaginationScrollListener(GridLayoutManager gridLayoutManager, boolean isGridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
        this.isGridLayoutManager = isGridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!isGridLayoutManager) {
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        } else {
            int visibleItemCount = gridLayoutManager.getChildCount();
            int totalItemCount = gridLayoutManager.getItemCount();
            int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0) {
                    loadMoreItems();
                }
            }
        }
    }

    public abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
