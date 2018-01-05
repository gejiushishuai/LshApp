package com.linsh.lshapp.mvp.edit_account

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.google.gson.Gson
import com.linsh.dialog.LshColorDialog
import com.linsh.lshapp.R
import com.linsh.lshapp.base.BaseToolbarActivity
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.model.bean.db.miqi.AccountAvatar
import com.linsh.lshapp.mvp.avatarSelect.AccountAvatarSelectPresenter
import com.linsh.lshapp.mvp.avatarSelect.AvatarSelectActivity
import com.linsh.lshapp.tools.ImageTools
import com.linsh.utilseverywhere.ListUtils
import com.linsh.utilseverywhere.StringUtils
import com.linsh.utilseverywhere.ToastUtils
import com.linsh.utilseverywhere.tools.IntentBuilder
import kotlinx.android.synthetic.main.activity_account_edit.*
import java.io.File

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/23
 *    desc   :
 * </pre>
 */
class AccountEditActivity : BaseToolbarActivity<AccountEditContract.Presenter>(), AccountEditContract.View {

    private var mAccountId: Long? = null
    private var mConfirmItem: MenuItem? = null
    private val emptyText = "---"
    private var mCurPickedFile: File? = null
    private var mCurSelectedFile: File? = null

    override fun getToolbarTitle(): String {
        return if (getAccountId() == 0L) "添加帐号" else "编辑帐号"
    }

    override fun getLayout(): Int {
        return R.layout.activity_account_edit
    }

    override fun initView() {
        ipAvatar.setOnClickListener { editAvatar() }
        tpName.setOnClickListener { editName() }
        tpWebsite.setOnClickListener { editWebsite() }
    }

    // 点击添加头像
    private fun editAvatar() {
        LshColorDialog(activity)
                .buildList()
                .setList(listOf("添加或更换头像"))
                .setOnItemClickListener({ dialog, _, _ ->
                    dialog.dismiss()
                    IntentBuilder(AvatarSelectActivity::class.java)
                            .putExtra(AccountAvatarSelectPresenter::class.java.name, "class")
                            .startActivityForResult(activity, 100)
                })
                .show()
    }

    // 点击修改帐号名称
    private fun editName() {
        val lastName = tpName.detail().text.toString()
        LshColorDialog(activity)
                .buildInput()
                .setTitle("任务名称")
                .setText(emptyToEmpty(lastName))
                .setPositiveButton(null, LshColorDialog.OnInputPositiveListener { dialog, inputText ->
                    if (isEmpty(inputText)) {
                        ToastUtils.show("不要偷懒啥也不填~")
                        return@OnInputPositiveListener
                    }
                    if (inputText != lastName) {
                        tpName.detail().text = inputText
                        onInfoModified()
                    }
                    dialog.dismiss()
                })
                .setNegativeButton(null, null)
                .show()
    }

    // 点击选择网站
    private fun editWebsite() {
        val websites = mPresenter.getWebsites()
        val list = ListUtils.toStringList(websites, { it.name })
        list.add(0, "+ 新增网站")
        LshColorDialog(activity)
                .buildList()
                .setList(list)
                .setOnItemClickListener({ dialog, item, index ->
                    dialog.dismiss()
                    if (index == 0) {
                        addWebsite()
                    } else {
                        tpWebsite.detail().text = item
                        onInfoModified()
                    }
                })
                .show()
    }

    // 添加网站
    private fun addWebsite() {
        LshColorDialog(activity).buildInput()
                .setTitle("添加网址")
                .setPositiveButton("添加", { dialog, inputText ->
                    if (StringUtils.notEmpty(inputText)) {
                        dialog.dismiss()
                        mPresenter.addWebsite(inputText)
                        tpWebsite.detail().text = inputText
                        onInfoModified()
                    } else {
                        showToast("输入不能为空")
                    }
                })
                .setNegativeButton(null, null)
                .show()
    }

    // 信息被修改
    private fun onInfoModified() {
        // 只有名字和组别填写之后才设置确认修改按钮为可用
        if (mConfirmItem == null) {
            mConfirmItem = toolbar.menu.findItem(R.id.menu_shiyi_edit_task_confirm)
        }
        if (mConfirmItem == null) {
            showToast("Maybe there is something wrong with confirm menu item, please note!")
        } else if (!mConfirmItem!!.isEnabled && !isEmpty(tpName.detail().text.toString())
                && !isEmpty(tpWebsite.detail().text.toString())) {
            mConfirmItem!!.isEnabled = true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_task_edit, menu)
        mConfirmItem = menu.findItem(R.id.menu_shiyi_edit_task_confirm)
        mConfirmItem?.isEnabled = false // 默认开始时确认修改按钮不可用
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.menu_shiyi_edit_task_confirm) {
            saveAccount()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initPresenter(): AccountEditContract.Presenter {
        return AccountEditPresent()
    }

    private fun getName(): String {
        return tpName.detail().text.toString()
    }

    private fun getWebsite(): String {
        return tpWebsite.detail().text.toString()
    }

    private fun setAvatar(accountAvatar: AccountAvatar?) {
        ipAvatar.detail().view.tag = accountAvatar
        ImageTools.setImage(ipAvatar.detail().view, accountAvatar?.thumbFirst())
    }

    private fun getAvatar(): AccountAvatar {
        return ipAvatar.detail().view.tag as AccountAvatar
    }

    private fun isEmpty(text: String): Boolean {
        return StringUtils.isEmpty(text) || emptyText == text
    }

    private fun emptyToEmpty(text: String): String {
        return if (isEmpty(text)) "" else text
    }

    private fun emptyToEmptyText(text: String): String {
        return if (isEmpty(text)) emptyText else text
    }

    override fun getAccountId(): Long {
        if (mAccountId == null) {
            mAccountId = IntentBuilder.getLongExtra(activity)
        }
        return mAccountId!!
    }

    //  保存帐号信息
    private fun saveAccount() {
        mPresenter.saveAccount(getName(), getWebsite(), getAvatar())
    }

    override fun setData(mAccount: Account) {
        tpName.detail().text = mAccount.name
        tpWebsite.detail().text = mAccount.website?.name
        setAvatar(mAccount.avatar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            101 -> if (data != null) {
                val json = data.getStringExtra("avatar")
                val avatar = Gson().fromJson(json, AccountAvatar::class.java)
                if (avatar.url?.isNotEmpty() == true) {
                    ImageTools.setImage(ipAvatar.detail().imageView, avatar.thumbFirst())
                    ipAvatar.detail().imageView.tag = avatar
                }
            }
        }
    }
}
