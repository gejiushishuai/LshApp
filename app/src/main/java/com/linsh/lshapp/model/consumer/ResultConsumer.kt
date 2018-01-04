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
class ResultConsumer(private val view: BaseContract.BaseView? = null, private val level: Int = 0) : Consumer<Result> {

    constructor(view: BaseContract.BaseView) : this(view, 1)

    override fun accept(result: Result) {
        if (!result.isSuccess) {
            when (level) {
                1 -> ToastUtils.show(result.message)
                2 -> view?.showTextDialog(result.message) ?: ToastUtils.show(result.message)
            }
            LogUtils.i("onNext -> ${result.message}")
        }
    }
}