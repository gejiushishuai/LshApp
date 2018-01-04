package com.linsh.lshapp.mvp.account_detail;

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.linsh.lshapp.R
import com.linsh.lshapp.common.BottomFooterEmptyStatusAdapter
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.tools.ImageTools
import com.linsh.lshutils.viewholder.LshViewHolder
import kotlinx.android.synthetic.main.item_account_detail_login_account.view.*
import kotlinx.android.synthetic.main.item_account_detail_login_name.view.*

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/03
 *    desc   :
 * </pre>
 */
class AccountDetailAdapter : BottomFooterEmptyStatusAdapter<Account>(false) {

    var loginName: String? = null

    fun setData(loginName: String?, data: MutableList<Account>?) {
        this.loginName = loginName
        super.setData(data)
    }

    override fun getItemCount(): Int {
        if (loginName?.isNotEmpty() == true) {
            return if (data?.isNotEmpty() == true)
                super.getItemCount() + 1
            else 1
        }
        return super.getItemCount()
    }

    override fun getItemViewType(position: Int): Int {
        if (loginName?.isNotEmpty() == true && position == 0) {
            return 4
        }
        return super.getItemViewType(position)
    }

    override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 4) {
            return LoginNameViewHolder(parent)
        }
        return LoginAccountViewHolder(parent)
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder? {
        return null
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        super.onBindViewHolder(holder, position - if (holder is LoginAccountViewHolder && loginName?.isNotEmpty() == true) 1 else 0)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder?, account: Account, position: Int) {
        if (holder is LoginAccountViewHolder) {
            ImageTools.setImage(holder.itemView.ivAccountAvatar, account.avatar, R.drawable.ic_contact)
            ImageTools.setImage(holder.itemView.ivWebsiteAvatar, account.website?.avatar, R.drawable.ic_website_default)
            holder.itemView.tvAccountName.text = account.name
            holder.itemView.tvWebsiteName.text = account.website?.name
        } else if (holder is LoginNameViewHolder) {
            if (loginName?.isNotEmpty() == true) {
                val loginNames = loginName!!.split("::")
                if (loginNames.isNotEmpty()) {
                    val names = loginNames[0].replace("++", "\n")
                    holder.itemView.tvLoginName.text = names
                    if (loginNames.size > 1) {
                        holder.itemView.tvLoginPassword.text = loginNames[1]
                    }
                }
                holder.itemView.flLoginPasswordLayout.visibility =
                        if (loginNames.size > 1) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
    }

    private inner class LoginAccountViewHolder(parent: ViewGroup) : LshViewHolder(R.layout.item_account_detail_login_account, parent)

    private inner class LoginNameViewHolder(parent: ViewGroup) : LshViewHolder(R.layout.item_account_detail_login_name, parent)
}
