package com.linsh.lshapp.model.consumer

import com.linsh.lshapp.base.BaseContract
import com.linsh.utilseverywhere.LogUtils
import com.linsh.utilseverywhere.ToastUtils
import io.reactivex.functions.Consumer

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2018/01/03
 *    desc   :
 * </pre>
 */
class ThrowableConsumer(private val view: BaseContract.BaseView? = null, private val level: Int = 1) : Consumer<Throwable> {

    override fun accept(thr: Throwable) {
        when (level) {
            1 -> ToastUtils.show(thr.message)
            2 -> view?.showTextDialog(thr.message) ?: ToastUtils.show(thr.message)
        }
        LogUtils.e(thr)
    }
}