package com.linsh.lshapp.tools;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.Shiyi;
import com.linsh.lshapp.service.Test5Service;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.view.ImportWechatFloatingView;
import com.linsh.lshutils.utils.LshArrayUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
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
        mDisposable = RxBus.getDefault().toObservable(Test5Service.WechatContactEvent.class)
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

    public Flowable<Void> savePerson(String personId, String personName, List<Test5Service.Type> types) {
        if (personId != null) {
            Test5Service.Type[] array = LshArrayUtils.toArray(types, new Test5Service.Type[types.size()]);
            return LshRxUtils.getMainThreadFlowable(new AsyncConsumer<Realm>() {
                @Override
                public void call(Realm realm, FlowableEmitter<? super Realm> emitter) {
                    emitter.onNext(realm);
                    emitter.onComplete();
                }
            }).flatMap(realm -> Flowable.fromArray(array)
                    .flatMap(type -> ShiyiDbHelper.addTypeDetail(realm, personId, type.type, type.value, ""))
            ).observeOn(AndroidSchedulers.mainThread());
        } else {
            Test5Service.Type[] array = LshArrayUtils.toArray(types, new Test5Service.Type[types.size()]);
            return LshRxUtils.getMainThreadFlowable(new AsyncConsumer<Realm>() {
                @Override
                public void call(Realm realm, FlowableEmitter<? super Realm> emitter) {
                    emitter.onNext(realm);
                    emitter.onComplete();
                }
            }).flatMap(realm -> {
                Person person = new Person(personName);
                return ShiyiDbHelper.addPerson(realm, ShiyiModelHelper.UNNAME_GROUP_NAME, person)
                                .flatMap(personId1 -> Flowable.fromArray(array))
                                .flatMap(type -> ShiyiDbHelper.addTypeDetail(realm, person.getId(), type.type, type.value, ""));
                    }
            ).observeOn(AndroidSchedulers.mainThread());
        }
    }
}
