package com.linsh.lshapp.part.setting;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshutils.utils.Basic.LshFileUtils;

import java.io.File;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class SettingsPresenter extends BasePresenterImpl<SettingsContract.View> implements SettingsContract.Presenter {

    @Override
    protected void attachView() {

    }

    @Override
    public void outputDatabase() {
        final File destination = new File(LshFileFactory.getAppDir(), "shiyi.realm");
        LshFileUtils.delete(destination);

        Subscription subscribe = Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        getRealm().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.writeCopyTo(destination);
                                subscriber.onNext(null);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getView().showToast("导出成功");
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscribe);
    }
}
