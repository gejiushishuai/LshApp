package com.linsh.lshapp.tools;

import com.linsh.lshapp.model.Group;
import com.linsh.lshapp.model.Person;
import com.linsh.lshapp.model.Shiyi;
import com.linsh.lshapp.model.throwabes.DeleteUnemptyGroupThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnnameGroupThrowable;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshThreadUtils;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ShiyiDataOperator {

    public static void createShiyi(final Realm realm) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Shiyi shiyi = realm.createObject(Shiyi.class);
                shiyi.setGroups(new RealmList<Group>());
                LshLogUtils.v("createShiyi", "getCurrentThreadName -- " + LshThreadUtils.getCurrentThreadName());
            }
        });
    }

    public static rx.Observable<Void> addGroup(final Realm realm, final String groupName) {
        return realm.where(Group.class).equalTo("name", groupName).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<Group>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Group> results) {
                        return results.isLoaded();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<RealmResults<Group>, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(final RealmResults<Group> results) {
                        LshLogUtils.v("query groupName", "size : " + results.size());
                        if (results.size() != 0) {
                            // 已经有该组的名称
                            return RxUtils.getDoNothingObservable();
                        }
                        return Observable.create(new Observable.OnSubscribe<Void>() {
                            @Override
                            public void call(final Subscriber<? super Void> subscriber) {
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        RealmResults<Shiyi> shiyiResults = realm.where(Shiyi.class).findAll();
                                        RealmList<Group> groups = shiyiResults.get(0).getGroups();

                                        Group group = ShiyiModelHelper.newGroup(groupName, groups.size() + 1);

                                        groups.add(group);
                                        LshLogUtils.v("add group", "groupName : " + groupName);
                                        LshLogUtils.v("add group", "getCurrentThreadName -- " + LshThreadUtils.getCurrentThreadName());
                                    }
                                });
                            }
                        }).observeOn(AndroidSchedulers.mainThread());
                    }
                });
    }

    public static rx.Observable<RealmResults<Shiyi>> getGroups(Realm realm) {
        return realm.where(Shiyi.class).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<Shiyi>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Shiyi> shiyis) {
                        return shiyis.isLoaded();
                    }
                });
    }

    public static rx.Observable<Void> deleteGroup(final Realm realm, final String groupId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Group> results = realm.where(Group.class).equalTo("id", groupId).findAll();

                        for (Group group : results) {
                            RealmList<Person> persons = group.getPersons();
                            if (persons != null && persons.size() > 0) {
                                if ("未分组".equals(group.getName())) {
                                    subscriber.onError(new DeleteUnnameGroupThrowable("未分组里的联系人必须移至其他分组后才能删除"));
                                } else {
                                    subscriber.onError(new DeleteUnemptyGroupThrowable("分组中的联系人不会被删除，是否继续删除该分组？"));
                                }
                                return;
                            }
                        }

                        results.deleteAllFromRealm();
                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public static rx.Observable<Void> moveToUnnameGroup(final Realm realm, final String groupId) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Group> results = realm.where(Group.class).equalTo("id", groupId).findAll();
                        if (results.size() > 0) {
                            Group group = results.get(0);
                            RealmResults<Group> unnameGroupResults = realm.where(Group.class).equalTo("name", "未分组").findAll();
                            if (unnameGroupResults.size() > 0) {
                                unnameGroupResults.get(0).getPersons().addAll(group.getPersons());
                            } else {
                                ShiyiModelHelper.newGroup("未分组", 0);
                            }
                        }
                        results.deleteAllFromRealm();
                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public static rx.Observable<Void> renameGroup(final Realm realm, final String groupId, final String newGroupName) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Group> results = realm.where(Group.class).equalTo("id", groupId).findAll();
                        for (Group group : results) {
                            group.setName(newGroupName);
                        }
                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

}
