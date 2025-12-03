package com.example.mymovies.util

import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(
    private val linearLayoutManager: LinearLayoutManager? = null,
    private val gridLayoutManager: GridLayoutManager? = null,
) : RecyclerView.OnScrollListener() {
    private var firstVisibleItemPosition: Int = 0
    private var lastVisibleItemPosition: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    private var isNotLoadingAndNotLastPage: Boolean = false
    private var isNotAtBeginning: Boolean = false
    private var isAtLastItem: Boolean = false
    private var isTotalMoreThanVisible: Boolean = false

    private var isScrolling: Boolean = false

    abstract fun isLoading(): Boolean
    abstract fun isLastPage(): Boolean

    abstract fun totalItems(): Int?
    abstract fun loadMoreItems()

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            isScrolling = true
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (linearLayoutManager != null) {
            firstVisibleItemPosition =
                (linearLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            visibleItemCount =
                (linearLayoutManager as LinearLayoutManager).childCount
            totalItemCount =
                (linearLayoutManager as LinearLayoutManager ).itemCount

            if (shouldPaginate()) {
                loadMoreItems()
                isScrolling = false
            }
        }

        if (gridLayoutManager != null) {
            lastVisibleItemPosition =
                (gridLayoutManager as GridLayoutManager).findLastVisibleItemPosition() + 1
            totalItemCount =
                (gridLayoutManager as GridLayoutManager).itemCount

            if (shouldPaginate()) {
                loadMoreItems()
                isScrolling = false
            }
        }

    }

    private fun shouldPaginate(): Boolean {
        if(linearLayoutManager != null){
            isNotLoadingAndNotLastPage = !isLoading() && !isLastPage()
            isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            isNotAtBeginning = firstVisibleItemPosition >= 0
            isTotalMoreThanVisible = totalItemCount >= (totalItems() ?: 0)

            return isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
        }

        if(gridLayoutManager != null){
            isNotLoadingAndNotLastPage = !isLoading() && !isLastPage()
            isAtLastItem = lastVisibleItemPosition == totalItemCount
            isTotalMoreThanVisible = totalItemCount >= (totalItems() ?: 0)

            return isNotLoadingAndNotLastPage && isAtLastItem
                    && isTotalMoreThanVisible && isScrolling
        }


        return false
    }

}