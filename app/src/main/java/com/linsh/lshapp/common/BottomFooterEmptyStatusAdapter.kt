package com.linsh.lshapp.common

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.linsh.lshutils.adapter.LshHeaderFooterRcvAdapter
import com.linsh.lshutils.viewholder.BottomFooterViewHolder
import com.linsh.lshutils.viewholder.EmptyStatusViewHolder

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/03
 *    desc   :
 * </pre>
 */
abstract class BottomFooterEmptyStatusAdapter<T>(hasHeader: Boolean) : LshHeaderFooterRcvAdapter<T>(hasHeader, false) {

    companion object {
        const val VIEW_TYPE_EMPTY_STATUS = 3
    }

    override fun getItemCount(): Int {
        if (data == null || data.isEmpty()) {
            return 1
        }
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        if (data == null || data.isEmpty()) {
            return VIEW_TYPE_EMPTY_STATUS
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_EMPTY_STATUS) {
            return EmptyStatusViewHolder(parent)
        }
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onCreateFooterViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder {
        return BottomFooterViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder !is EmptyStatusViewHolder) {
            super.onBindViewHolder(holder, position)
        }
    }

    override fun onBindFooterViewHolder(holder: RecyclerView.ViewHolder) {
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder?) {
        super.onViewAttachedToWindow(holder)
        if (holder is BottomFooterViewHolder) {
            val parent = holder.itemView.parent as ViewGroup
            val parentBottom = parent.bottom
            val childCount = parent.childCount
            if (childCount > 1) {
                val lastViewBottom = parent.getChildAt(childCount - 2).bottom
                if (lastViewBottom >= parentBottom) {
                    holder.itemView.visibility = View.VISIBLE
                } else {
                    holder.itemView.visibility = View.GONE
                }
            }
        }
    }
}