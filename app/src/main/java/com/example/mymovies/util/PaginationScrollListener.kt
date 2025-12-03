package com.example.mymovies.util

import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.mymovies.util.LayoutManagerType.*

abstract class PaginationScrollListener(
    private val layoutManagerType: LayoutManagerType,
    private val layoutManager: LayoutManager
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

        when (layoutManagerType) {
            LINEAR_LAYOUT -> {
                firstVisibleItemPosition =
                    (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                visibleItemCount =
                    (layoutManager as LinearLayoutManager).childCount
                totalItemCount =
                    (layoutManager as LinearLayoutManager).itemCount

                if (shouldPaginate()) {
                    loadMoreItems()
                    isScrolling = false
                }
            }

            GRID_LAYOUT -> {
                lastVisibleItemPosition =
                    (layoutManager as GridLayoutManager).findLastVisibleItemPosition() + 1
                totalItemCount =
                    (layoutManager as GridLayoutManager).itemCount

                if (shouldPaginate()) {
                    loadMoreItems()
                    isScrolling = false
                }
            }

            STAGGERED_LAYOUT -> Unit
        }
    }

    private fun shouldPaginate(): Boolean {
        when (layoutManagerType) {
            LINEAR_LAYOUT -> {
                isNotLoadingAndNotLastPage = !isLoading() && !isLastPage()
                isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                isNotAtBeginning = firstVisibleItemPosition >= 0
                isTotalMoreThanVisible = totalItemCount >= (totalItems() ?: 0)

                return isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                        && isTotalMoreThanVisible && isScrolling
            }

            GRID_LAYOUT -> {
                isNotLoadingAndNotLastPage = !isLoading() && !isLastPage()
                isAtLastItem = lastVisibleItemPosition == totalItemCount
                isTotalMoreThanVisible = totalItemCount >= (totalItems() ?: 0)

                return isNotLoadingAndNotLastPage && isAtLastItem
                        && isTotalMoreThanVisible && isScrolling
            }

            STAGGERED_LAYOUT -> Unit

        }

        return false
    }

}

enum class LayoutManagerType {
    LINEAR_LAYOUT, GRID_LAYOUT, STAGGERED_LAYOUT
}