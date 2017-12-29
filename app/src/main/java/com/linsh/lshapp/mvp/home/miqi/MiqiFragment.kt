package com.linsh.lshapp.mvp.home.miqi

import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.linsh.lshapp.R
import com.linsh.lshapp.base.BaseMainFragment
import com.linsh.lshapp.model.bean.db.miqi.Account
import com.linsh.lshapp.mvp.edit_account.AccountEditActivity
import com.linsh.lshutils.decoration.DividerItemDecoration
import com.linsh.utilseverywhere.IntentUtils
import com.linsh.utilseverywhere.ResourceUtils
import kotlinx.android.synthetic.main.fragment_miqi.*

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/22
 *    desc   :
 * </pre>
 */
class MiqiFragment : BaseMainFragment<MiqiContract.Presenter>(), MiqiContract.View {

    lateinit var mAdapter: MiqiAdapter

    override fun getTitle(): String = "觅奇"

    override fun initPresenter(): MiqiContract.Presenter = MiqiPresenter()

    override fun getLayout(): Int = R.layout.fragment_miqi

    override fun initView(view: View) {
        mAdapter = MiqiAdapter()
        rcvContent.adapter = mAdapter
        rcvContent.layoutManager = LinearLayoutManager(activity)
        rcvContent.addItemDecoration(DividerItemDecoration(1, ResourceUtils.getColor(R.color.line_whitebg)))
    }

    override fun initData() {
        val account = Account()
        account.id
    }

    override fun getOptionsMenuItems(): Int = R.menu.fragment_miqi

    override fun onOptionsItemSelected(id: Int): Boolean {
        if (id == R.id.menu_fragment_miqi_add_account) {
            IntentUtils.buildIntent(AccountEditActivity::class.java)
                    .startActivity(activity)
            return true
        }
        return false
    }

    override fun setData(accounts: List<Account>) {
        mAdapter.data = accounts
    }
}