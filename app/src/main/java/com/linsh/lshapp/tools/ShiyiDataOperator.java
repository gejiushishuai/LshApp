package com.linsh.lshapp.tools;

import com.linsh.lshapp.model.Group;
import com.linsh.lshapp.model.Shiyi;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshThreadUtils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ShiyiDataOperator {

    public static rx.Observable<Void> createShiyi(final Realm realm, final RealmList<Group> groups) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Shiyi shiyi = realm.createObject(Shiyi.class);
                        shiyi.setGroups(groups);
                        subscriber.onNext(null);
                        LshLogUtils.i("getCurrentThreadName -- " + LshThreadUtils.getCurrentThreadName());
                    }
                });
            }
        });
    }

    public static rx.Observable<RealmResults<Shiyi>> getGroups(Realm realm) {
        return realm.where(Shiyi.class).findAllAsync().asObservable();
    }


}
