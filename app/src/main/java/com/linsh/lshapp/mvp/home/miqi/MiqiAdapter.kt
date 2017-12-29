package com.linsh.lshapp.mvp.home.miqi

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.linsh.lshapp.R
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.tools.ImageTools
import com.linsh.lshutils.adapter.LshHeaderFooterRcvAdapter
import com.linsh.lshutils.viewholder.BottomFooterViewHolder
import com.linsh.lshutils.viewholder.EmptyStatusViewHolder
import com.linsh.lshutils.viewholder.LshViewHolder
import com.linsh.utilseverywhere.LogUtils
import com.linsh.utilseverywhere.StringUtils

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/22
 *    desc   :
 * </pre>
 */
class MiqiAdapter : LshHeaderFooterRcvAdapter<Account, RecyclerView.ViewHolder>(false, true) {

    companion object {
        const val VIEW_TYPE_STATUS = 3
    }

    override fun setData(data: MutableList<Account>?) {
        setHasHeader(data != null && !data.isEmpty())
        super.setData(data)
    }

    override fun getItemViewType(position: Int): Int {
        val data = data
        return if (data == null || data.isEmpty()) {
            VIEW_TYPE_STATUS
        } else super.getItemViewType(position)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_STATUS) {
            EmptyStatusViewHolder(parent)
        } else MyViewHolder(parent)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MyHeaderViewHolder(parent)
    }

    override fun onCreateFooterViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BottomFooterViewHolder(parent)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, account: Account?, position: Int) {
        if (holder is MyViewHolder) {
            holder.tvWebName?.text = account?.website?.name ?: "未知"
            holder.tvName?.text = account?.name ?: "--"
            val webAvatar = account?.website?.avatar
            if (StringUtils.notEmpty(webAvatar)) {
                ImageTools.setImage(holder.ivWebAvatar, webAvatar)
            }
            val acAvatar = account?.avatar
            if (StringUtils.notEmpty(acAvatar)) {
                ImageTools.setImage(holder.ivAvatar, acAvatar)
            }
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
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
                LogUtils.i("BottomFooter 设置可见: " + (lastViewBottom >= parentBottom))
            }
        }
    }

    private inner class MyViewHolder(parent: ViewGroup) : LshViewHolder(R.layout.item_miqi, parent) {

        var ivWebAvatar: ImageView? = null
        var tvWebName: TextView? = null
        var tvName: TextView? = null
        var ivAvatar: ImageView? = null

        override fun initView(view: View) {
            ivWebAvatar = view.findViewById(R.id.iv_item_miqi_website_avatar)
            tvWebName = view.findViewById(R.id.tv_item_miqi_website_name)
            tvName = view.findViewById(R.id.tv_item_miqi_account_name)
            ivAvatar = view.findViewById(R.id.iv_item_miqi_account_avatar)
        }

    }

    private inner class MyHeaderViewHolder(parent: ViewGroup) : LshViewHolder(R.layout.item_miqi_header, parent) {
        override fun initView(itemView: View?) {
        }
    }

}