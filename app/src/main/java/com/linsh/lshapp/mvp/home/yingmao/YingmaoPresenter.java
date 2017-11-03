package com.linsh.lshapp.mvp.home.yingmao;

import android.content.Intent;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.bean.Client;
import com.linsh.lshapp.model.bean.SignIn;
import com.linsh.lshapp.model.event.SignInEvent;
import com.linsh.lshapp.service.SignInService4;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshAccessibilityUtils;
import com.linsh.lshutils.utils.LshContextUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class YingmaoPresenter extends BasePresenterImpl<YingmaoContract.View> implements YingmaoContract.Presenter {

    private List<SignIn> mSignIns;

    @Override
    protected void attachView() {
        Disposable disposable1 = RxBus.getDefault().toObservable(SignInEvent.class)
                .subscribe(event -> {
                    String client = event.getClient();
                    LshLogUtils.i("刷新签到成功状态", client);
                    for (SignIn in : mSignIns) {
                        if (in.getClient().name().equals(client)) {
                            in.setState(event.getState());
                            SignInHelper.refreshState(in);
                            break;
                        }
                    }
                    getView().setData(mSignIns);
                });
        addDisposable(disposable1);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void refreshData() {
        Disposable disposable = SignInHelper.checkSign(Client.values())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signIns -> {
                    mSignIns = signIns;
                    getView().setData(mSignIns);
                });
        addDisposable(disposable);
    }

    @Override
    public void signIn(SignIn signIn) {
        if (LshAccessibilityUtils.checkAccessibility()) {
            Intent intent = new Intent(LshContextUtils.get(), SignInService4.class)
                    .putExtra("sign_in", signIn);
            LshContextUtils.startService(intent);
        }
    }

    @Override
    public void switchIgnore(SignIn signIn) {
        SignInHelper.setState(signIn,
                signIn.getState() == SignIn.STATE_IGNORED ? SignIn.STATE_UNSIGNED : SignIn.STATE_IGNORED);
    }

    @Override
    public void signInAll() {

    }
}
