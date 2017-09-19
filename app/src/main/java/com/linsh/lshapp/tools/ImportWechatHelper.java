package com.linsh.lshapp.tools;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.Shiyi;
import com.linsh.lshapp.service.ImportAppDataService;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.view.ImportWechatFloatingView;
import com.linsh.lshutils.utils.LshArrayUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.realm.Case;
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
        mDisposable = RxBus.getDefault().toObservable(ImportAppDataService.WechatContactEvent.class)
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
        Matcher matcher = Pattern.compile("^([\\u4e00-\\u9fa5]+)-?(\\w+)|(\\w+)-?([\\u4e00-\\u9fa5]+)$").matcher(name);
        ArrayList<String> list = new ArrayList<>();
        list.add(name);
        if (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            String group3 = matcher.group(3);
            String group4 = matcher.group(4);
            if (group1 != null && group1.length() >= 2) list.add(group1);
            if (group2 != null && group2.length() >= 3) list.add(group2);
            if (group3 != null && group3.length() >= 3) list.add(group3);
            if (group4 != null && group4.length() >= 2) list.add(group4);
        }
        String[] names = LshArrayUtils.toArray(list, new String[list.size()]);
        Flowable.fromArray(names)
                // 匹配名字
                .flatMap(eachName -> LshRxUtils.getAsyncFlowable((AsyncConsumer<List<Person>>) (realm, emitter) -> {
                    RealmResults<Person> persons = realm.where(Person.class).contains("name", eachName, Case.INSENSITIVE).findAll();
                    emitter.onNext(realm.copyFromRealm(persons));
                    emitter.onComplete();
                }))
                .flatMap(Flowable::fromIterable)
                // 合并名字
                .collect((Callable<HashMap<String, Person>>) HashMap::new,
                        (map, person) -> map.put(person.getId(), person))
                // 去重
                .map(map -> {
                    ArrayList<Person> arrayList = new ArrayList<>();
                    arrayList.addAll(map.values());
                    return arrayList;
                })
                .observeOn(AndroidSchedulers.mainThread())
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

    public Flowable<Void> savePerson(String personId, String personName, List<ImportAppDataService.Type> types) {
        int length = 0;
        for (ImportAppDataService.Type type : types) {
            if (type.need || type.selected) length++;
        }
        ImportAppDataService.Type[] neededTypes = new ImportAppDataService.Type[length];
        for (int i = 0; i < types.size(); i++) {
            ImportAppDataService.Type type = types.get(i);
            if (type.need || type.selected) neededTypes[i] = type;
        }
        if (personId != null) {
            return LshRxUtils.getMainThreadFlowable(new AsyncConsumer<Realm>() {
                @Override
                public void call(Realm realm, FlowableEmitter<? super Realm> emitter) {
                    emitter.onNext(realm);
                    emitter.onComplete();
                }
            }).flatMap(realm -> Flowable.fromArray(neededTypes)
                    .flatMap(type -> ShiyiDbHelper.addTypeDetail(realm, personId, type.type, type.value, ""))
            ).observeOn(AndroidSchedulers.mainThread());
        } else {
            return LshRxUtils.getMainThreadFlowable(new AsyncConsumer<Realm>() {
                @Override
                public void call(Realm realm, FlowableEmitter<? super Realm> emitter) {
                    emitter.onNext(realm);
                    emitter.onComplete();
                }
            }).flatMap(realm -> {
                        Person person = new Person(personName);
                        return ShiyiDbHelper.addPerson(realm, ShiyiModelHelper.UNNAME_GROUP_NAME, person)
                                .flatMap(personId1 -> Flowable.fromArray(neededTypes))
                                .flatMap(type -> ShiyiDbHelper.addTypeDetail(realm, person.getId(), type.type, type.value, ""));
                    }
            ).observeOn(AndroidSchedulers.mainThread());
        }
    }
}
