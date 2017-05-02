package com.linsh.lshapp.tools;

import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Shiyi;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeLabel;
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

    /**
     * 创建拾意数据表
     */
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

    public static void createPersonDetail(final Realm realm, final String personId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonDetail detail = realm.createObject(PersonDetail.class);
                detail.setId(personId);
                detail.setTypes(new RealmList<Type>());
                LshLogUtils.v("createShiyi", "getCurrentThreadName -- " + LshThreadUtils.getCurrentThreadName());
            }
        });
    }

    /**
     * 添加分组
     */
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
                            return LshRxUtils.getDoNothingObservable();
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

    /**
     * 获取所有分组信息
     */
    public static rx.Observable<RealmList<Group>> getGroups(final Realm realm) {
        return realm.where(Shiyi.class).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<Shiyi>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Shiyi> shiyis) {
                        return shiyis.isLoaded();
                    }
                })
                .map(new Func1<RealmResults<Shiyi>, RealmList<Group>>() {
                    @Override
                    public RealmList<Group> call(RealmResults<Shiyi> shiyis) {
                        LshLogUtils.v("getGroups Shiyis", "size = " + shiyis.size());
                        if (shiyis.size() == 0) {
                            ShiyiDataOperator.createShiyi(realm);
                            return null;
                        } else {
                            return shiyis.get(0).getGroups();
                        }
                    }
                });
    }

    /**
     * 删除分组
     */
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

    /**
     * 删除该分组并将分组里的数据转移到"未分组"分组
     */
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

    /**
     * 重命名分组
     */
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

    public static rx.Observable<Void> addPerson(final Realm realm, final String group, final String name, final String desc, final String sex) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Group> groups = realm.where(Group.class).equalTo("name", group).findAll();
                        if (groups.size() > 0) {
                            Person person = ShiyiModelHelper.newPerson(name, desc, sex);
                            groups.get(0).getPersons().add(person);
                        }
                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public static rx.Observable<Person> getPerson(final Realm realm, String personId) {
        return realm.where(Person.class).equalTo("id", personId).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<Person>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<Person> shiyis) {
                        return shiyis.isLoaded();
                    }
                })
                .map(new Func1<RealmResults<Person>, Person>() {
                    @Override
                    public Person call(RealmResults<Person> persons) {
                        LshLogUtils.v("getPerson persons", "size = " + persons.size());
                        if (persons.size() == 0) {
                            return null;
                        }
                        return persons.get(0);
                    }
                });
    }

    public static rx.Observable<PersonDetail> getPersonDetail(final Realm realm, final String personId) {
        return realm.where(PersonDetail.class).equalTo("id", personId).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<PersonDetail>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<PersonDetail> shiyis) {
                        return shiyis.isLoaded();
                    }
                })
                .map(new Func1<RealmResults<PersonDetail>, PersonDetail>() {
                    @Override
                    public PersonDetail call(RealmResults<PersonDetail> details) {
                        LshLogUtils.v("getPersonDetail details", "size = " + details.size());
                        if (details.size() == 0) {
                            createPersonDetail(realm, personId);
                            return null;
                        }
                        return details.get(0);
                    }
                });
    }

    public static rx.Observable<RealmResults<TypeLabel>> getTypeLabels(Realm realm) {
        return realm.where(TypeLabel.class).findAllAsync().asObservable()
                .filter(new Func1<RealmResults<TypeLabel>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<TypeLabel> typeLabels) {
                        return typeLabels.isLoaded();
                    }
                });
    }

    public static Observable<Void> addTypeLabel(final Realm realm, final String labelName, final int size) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<TypeLabel> results = realm.where(TypeLabel.class).equalTo("name", labelName).findAll();
                        if (results.size() == 0) {
                            TypeLabel newTypeLabel = ShiyiModelHelper.newTypeLabel(size + 1, labelName);
                            realm.copyToRealm(newTypeLabel);
                        }
                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Void> addType(final Realm realm, final String personId, final String typeName) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<PersonDetail> results = realm.where(PersonDetail.class).equalTo("id", personId).findAll();
                        PersonDetail personDetail;
                        if (results.size() > 0) {
                            personDetail = results.get(0);
                        } else {
                            personDetail = ShiyiModelHelper.newPersonDetail(personId);
                            realm.copyToRealm(personDetail);
                        }
                        RealmList<Type> types = personDetail.getTypes();
                        types.add(ShiyiModelHelper.newType(personDetail.getId(), types.size() + 1, typeName));

                        subscriber.onNext(null);
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread());
    }
}
