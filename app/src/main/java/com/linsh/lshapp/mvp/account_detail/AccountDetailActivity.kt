package com.linsh.lshapp.mvp.account_detail

import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.linsh.dialog.LshColorDialog
import com.linsh.lshapp.R
import com.linsh.lshapp.base.BaseToolbarActivity
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.mvp.edit_account.AccountDetailContract
import com.linsh.lshapp.mvp.edit_account.AccountDetailPresent
import com.linsh.lshapp.tools.ImageTools
import com.linsh.lshutils.decoration.DividerItemDecoration
import com.linsh.utilseverywhere.ResourceUtils
import com.linsh.utilseverywhere.tools.IntentBuilder
import kotlinx.android.synthetic.main.activity_account_detail.*

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */
class AccountDetailActivity : BaseToolbarActivity<AccountDetailContract.Presenter>(), AccountDetailContract.View {

    private lateinit var mAdapter: AccountDetailAdapter

    override fun initPresenter(): AccountDetailContract.Presenter {
        return AccountDetailPresent()
    }

    override fun getToolbarTitle(): String {
        return "详情信息"
    }

    override fun getLayout(): Int {
        return R.layout.activity_account_detail
    }

    override fun initView() {
        mAdapter = AccountDetailAdapter()
        rcvContent.adapter = mAdapter
        rcvContent.layoutManager = LinearLayoutManager(this)
        rcvContent.addItemDecoration(DividerItemDecoration(1, ResourceUtils.getColor(R.color.line_whitebg)))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_account_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menu_account_detail_add_login_way -> {
                addLoginWay()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun addLoginWay() {
        LshColorDialog(this).buildList()
                .setList(listOf("帐号密码登录", "第三方登录"))
                .setOnItemClickListener({ dialog, item, index ->
                    dialog.dismiss()
                    if (index == 0) {
                        addPasswordLoginWay()
                    } else {
                        addOtherLoginWay()
                    }
                })
                .show()
    }

    private fun addPasswordLoginWay() {
        LshColorDialog(this).buildInput()
                .setTitle("输入帐号名")
                .setPositiveButton(null, { dialog, inputText ->
                    dialog.dismiss()
                    if (inputText.isEmpty()) {
                        showToast("不能为空")
                        return@setPositiveButton
                    }
                    mPresenter.addLoginWay(inputText)
                })
                .setNegativeButton(null, null)
                .show()
    }

    private fun addOtherLoginWay() {
        LshColorDialog(this).buildInput()
                .setTitle("请输入第三方帐号 id")
                .setHint("主页面长按帐号即可复制 id")
                .setPositiveButton(null, { dialog, inputText ->
                    dialog.dismiss()
                    if (inputText.isEmpty()) {
                        showToast("不能为空")
                        return@setPositiveButton
                    }
                    if (!inputText.matches(Regex("\\d+"))) {
                        showToast("id 必须是数字啊喂!")
                        return@setPositiveButton
                    }
                    mPresenter.addLoginWay(inputText.toLong())
                })
                .show()
    }

    override fun getAccountId(): Long {
        return IntentBuilder.getLongExtra(this)
    }

    override fun setData(account: Account) {
        ImageTools.setImage(ivAccountAvatar, account.avatar, R.drawable.ic_contact)
        ImageTools.setImage(ivAccountAvatar, account.website?.avatar, R.drawable.ic_website_default)
        tvAccountName.text = account.name
        tvWebsiteName.text = account.website?.name
        mAdapter.setData(account.loginName, account.loginAccounts)
    }
}
