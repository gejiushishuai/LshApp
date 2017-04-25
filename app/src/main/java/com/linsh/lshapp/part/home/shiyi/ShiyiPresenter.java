package com.linsh.lshapp.part.home.shiyi;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.Group;
import com.linsh.lshapp.model.Shiyi;
import com.linsh.lshapp.tools.RxUtils;
import com.linsh.lshapp.tools.ShiyiDataOperator;
import com.linsh.lshutils.utils.Basic.LshToastUtils;

import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/4/24.
 */

public class ShiyiPresenter extends BasePresenterImpl<ShiyiContract.View> implements ShiyiContract.Presenter {

    private RealmList<Group> groups;

    @Override
    protected void attachView() {
        Subscription subscription = ShiyiDataOperator.getGroups(getRealm())
                .flatMap(new Func1<RealmResults<Shiyi>, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(RealmResults<Shiyi> shiyis) {
                        if (shiyis.size() == 0) {
                            groups = new RealmList<>();
                            return ShiyiDataOperator.createShiyi(getRealm(), groups);
                        } else {
                            groups = shiyis.get(0).getGroups();
                            return RxUtils.getDoNothingObservable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getView().setData(groups);
                        LshToastUtils.showToast("setData");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        LshToastUtils.showToast(throwable.getMessage());
                    }
                });
        addSubscription(subscription);
    }

}
