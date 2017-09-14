package com.linsh.lshapp.tools;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.Shiyi;
import com.linsh.lshapp.service.Test4Service;
import com.linsh.lshapp.view.ImportWechatFloatingView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/9/13.
 */

public class ImportWechatHelper {

    private ImportWechatFloatingView mView;
    private Disposable mDisposable;

    public void attachView(ImportWechatFloatingView view) {
        mView = view;
        mDisposable = RxBus.getDefault().toObservable(Test4Service.WechatContactEvent.class)
                .subscribe(wechatContactEvent -> {
                    mView.setTypes(wechatContactEvent.getName(), wechatContactEvent.getTypes());
                });
    }

    public void detachView() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void findPersons(String name) {
        LshRxUtils.getAsyncFlowable(new AsyncConsumer<List<Person>>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super List<Person>> emitter) {
                RealmResults<Person> persons = realm.where(Person.class).contains("name", name).findAll();
                emitter.onNext(realm.copyFromRealm(persons));
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnError(new DefaultThrowableConsumer())
                .subscribe(persons -> {
                    mView.setPersons(persons);
                });
    }

    public void getGroups() {
        LshRxUtils.getAsyncFlowable(new AsyncConsumer<List<Group>>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super List<Group>> emitter) {
                List<Group> result = new ArrayList<Group>();
                Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                if (shiyi != null) {
                    result = realm.copyFromRealm(shiyi.getGroups());
                }
                emitter.onNext(result);
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .doOnError(new DefaultThrowableConsumer())
                .subscribe(groups -> {
                    mView.setGroups(groups);
                });
    }
}
