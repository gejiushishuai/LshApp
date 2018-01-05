package com.linsh.lshapp.model.consumer

import com.linsh.lshapp.base.BaseContract
import com.linsh.lshapp.model.result.Result
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
class ResultConsumer(private val view: BaseContract.BaseView? = null, private val level: Int = 1) : Consumer<Result> {

    override fun accept(result: Result) {
        consumeFailure(result, view, level)
    }

    companion object {
        fun consumeFailure(result: Result, view: BaseContract.BaseView? = null, level: Int = 0): Boolean {
            if (!result.isSuccess) {
                when (level) {
                    1 -> ToastUtils.show(result.message)
                    2 -> view?.showTextDialog(result.message) ?: ToastUtils.show(result.message)
                }
                LogUtils.i("onNext -> ${result.message}")
                return true
            }
            return false
        }
    }
}