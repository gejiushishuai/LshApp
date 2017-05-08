package com.linsh.lshapp.task.shiyi;

import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.ResultListFilterFunc;
import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Shiyi;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.model.result.Result;
import com.linsh.lshapp.model.throwabes.DeleteUnemptyGroupThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnnameGroupThrowable;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.ShiyiModelHelper;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ShiyiDbHelper {

    /**
     * 创建拾意数据表
     */
    public static void createShiyi(final Realm realm) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Shiyi shiyi = realm.createObject(Shiyi.class);
                shiyi.setGroups(new RealmList<Group>());
            }
        });
    }

    public static void createPersonDetail(final Realm realm, final String personId) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PersonDetail personDetail = ShiyiModelHelper.newPersonDetail(personId);
                realm.copyToRealm(personDetail);
            }
        });
    }

    /**
     * 获取所有分组信息
     */
    public static Observable<RealmList<Group>> getGroups(final Realm realm) {
        return realm.where(Shiyi.class).findAllAsync().asObservable()
                .filter(new ResultListFilterFunc<Shiyi>())
                .map(new Func1<RealmResults<Shiyi>, RealmList<Group>>() {
                    @Override
                    public RealmList<Group> call(RealmResults<Shiyi> shiyis) {
                        if (shiyis.size() == 0) {
                            createShiyi(realm);
                            return null;
                        } else {
                            return shiyis.get(0).getGroups();
                        }
                    }
                });
    }

    public static Observable<Person> getPerson(final Realm realm, String personId) {
        return realm.where(Person.class).equalTo("id", personId).findAllAsync().asObservable()
                .filter(new ResultListFilterFunc<Person>())
                .map(new Func1<RealmResults<Person>, Person>() {
                    @Override
                    public Person call(RealmResults<Person> persons) {
                        if (persons.size() == 0) {
                            return null;
                        }
                        return persons.get(0);
                    }
                });
    }

    public static Observable<PersonDetail> getPersonDetail(final Realm realm, final String personId) {
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
                        if (details.size() == 0) {
                            createPersonDetail(realm, personId);
                            return null;
                        }
                        return details.get(0);
                    }
                });
    }

    public static Observable<RealmResults<TypeLabel>> getTypeLabels(Realm realm) {
        return realm.where(TypeLabel.class).findAllSortedAsync("sort").asObservable()
                .filter(new Func1<RealmResults<TypeLabel>, Boolean>() {
                    @Override
                    public Boolean call(RealmResults<TypeLabel> typeLabels) {
                        return typeLabels.isLoaded();
                    }
                });
    }

    public static Observable<TypeDetail> getTypeDetail(final Realm realm, String typeDetailId) {
        return realm.where(TypeDetail.class).equalTo("id", typeDetailId).findAllAsync().asObservable()
                .filter(new ResultListFilterFunc<TypeDetail>())
                .map(new Func1<RealmResults<TypeDetail>, TypeDetail>() {
                    @Override
                    public TypeDetail call(RealmResults<TypeDetail> details) {
                        if (details.size() == 0) {
                            return null;
                        }
                        return details.get(0);
                    }
                });
    }


    /**
     * 添加分组
     */
    public static Observable<Void> addGroup(final Realm realm, final String groupName) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                // 获取shiyi表格
                Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                RealmList<Group> groups = shiyi.getGroups();
                // 判断是否有该分组
                Group sameNameGroup = groups.where().equalTo("name", groupName).findFirst();
                if (sameNameGroup == null) {
                    // 没有该分组则创建
                    Group group = ShiyiModelHelper.newGroup(groupName, groups.size() + 1);
                    groups.add(group);
                }
            }
        });
    }

    /**
     * 删除分组
     */
    public static Observable<Void> deleteGroup(final Realm realm, final String groupId) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                // 获取该分组
                Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();
                if (group != null) {
                    // 获取分组中联系人, 如果分组中有联系人, 则提示是否删除
                    RealmList<Person> persons = group.getPersons();
                    if (persons != null && persons.size() > 0) {
                        if ("未分组".equals(group.getName())) {
                            subscriber.onError(new DeleteUnnameGroupThrowable("未分组里的联系人必须移至其他分组后才能删除"));
                        } else {
                            subscriber.onError(new DeleteUnemptyGroupThrowable("分组中的联系人不会被删除，是否继续删除该分组？"));
                        }
                    } else {
                        // 没有联系人, 直接删除
                        group.deleteFromRealm();
                    }
                }
            }
        });
    }

    /**
     * 删除该分组并将分组里的数据转移到"未分组"分组
     */
    public static Observable<Void> moveToUnnameGroup(final Realm realm, final String groupId) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                if (shiyi != null) {
                    RealmList<Group> groups = shiyi.getGroups();
                    // 获取分组
                    Group group = groups.where().equalTo("id", groupId).findFirst();
                    if (group != null) {
                        // 获取未分组分组
                        Group unnameGroup = groups.where().equalTo("name", "未分组").findFirst();
                        if (unnameGroup == null) {
                            // 没有未分组, 则创建
                            unnameGroup = ShiyiModelHelper.newGroup("未分组", 0);
                            groups.add(unnameGroup);
                        }
                        RealmList<Person> persons = unnameGroup.getPersons();
                        persons.addAll(realm.copyFromRealm(group.getPersons()));
                        group.deleteFromRealm();
                        if (unnameGroup.isManaged()) {
                            // 对联系人进行排序并保存
                            ShiyiDbUtils.sortToRealm(realm, persons, "id");
                        }
                    }
                }
            }
        });
    }

    /**
     * 重命名分组
     */
    public static Observable<Void> renameGroup(final Realm realm, final String groupId, final String newGroupName) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();
                if (group != null) {
                    group.setName(newGroupName);
                }
            }
        });
    }

    public static Observable<Void> addPerson(final Realm realm, final String groupName, final String personName, final String desc, final String sex) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Group group = realm.where(Group.class).equalTo("name", groupName).findFirst();
                if (group != null) {
                    Person person = ShiyiModelHelper.newPerson(personName, desc, sex);
                    RealmList<Person> persons = group.getPersons();
                    persons.add(person);
                    // 对联系人进行排序并保存
                    ShiyiDbUtils.sortToRealm(realm, persons, "id");
                }
            }
        });
    }

    public static Observable<Void> addTypeLabel(final Realm realm, final String labelName, final int size) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                TypeLabel typeLabel = realm.where(TypeLabel.class).equalTo("name", labelName).findFirst();
                if (typeLabel == null) {
                    TypeLabel newTypeLabel = ShiyiModelHelper.newTypeLabel(size + 1, labelName);
                    realm.copyToRealm(newTypeLabel);
                }
            }
        });
    }

    public static Observable<Void> addType(final Realm realm, final String personId, final String typeName) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                // 通过Id查询该PersonDetail
                PersonDetail personDetail = realm.where(PersonDetail.class).equalTo("id", personId).findFirst();
                if (personDetail != null) {
                    // 有该PersonDetail, 获取Types, 查询是否有同名的Type
                    RealmResults<Type> typeResults = personDetail.getTypes().where().equalTo("name", typeName).findAll();
                    if (typeResults.size() > 0) {
                        // 有同名的Type, 则在该Type的TypeDetails最后加上空的TypeDetail
                        RealmList<TypeDetail> typeDetails = typeResults.get(0).getTypeDetails();
                        typeDetails.add(ShiyiModelHelper.newTypeDetail(typeDetails.size() + 1, personDetail.getId()));
                    } else {
                        // 没有同名的Type, 则在types里面加上一个Type (该Type的TypeDetails里面默认有一个空的TypeDetail)
                        RealmList<Type> types = personDetail.getTypes();
                        types.add(ShiyiModelHelper.newType(personDetail.getId(), types.size() + 1, typeName));
                    }
                } else {
                    // 没有该PersonDetail, 则创建一个
                    personDetail = ShiyiModelHelper.newPersonDetail(personId);
                    realm.copyToRealm(personDetail);
                    // 然后添加一个Type到Types里面 (该Type的TypeDetails里面默认有一个空的TypeDetail)
                    RealmList<Type> types = personDetail.getTypes();
                    types.add(ShiyiModelHelper.newType(personDetail.getId(), types.size() + 1, typeName));
                }
            }
        });
    }

    public static Observable<Result> addType(final Realm realm, final String personId, final String typeName, final int sort) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Result> subscriber) {
                // 通过Id查询该PersonDetail
                PersonDetail personDetail = realm.where(PersonDetail.class).equalTo("id", personId).findFirst();
                if (personDetail != null) {
                    // 有该PersonDetail, 获取Types, 查询是否有同名的Type
                    RealmResults<Type> typeResults = personDetail.getTypes().where().equalTo("name", typeName).findAll();
                    if (typeResults.size() > 0) {
                        // 有同名的Type, 则在该Type的TypeDetails最后加上空的TypeDetail
                        RealmList<TypeDetail> typeDetails = typeResults.get(0).getTypeDetails();
                        typeDetails.add(ShiyiModelHelper.newTypeDetail(typeDetails.size() + 1, personDetail.getId()));

                        subscriber.onNext(new Result("已存在该类型, 添加至该类型处"));
                    } else {
                        // 没有同名的Type, 则在types里面加上一个Type (该Type的TypeDetails里面默认有一个空的TypeDetail)
                        RealmList<Type> types = personDetail.getTypes();

                        // 添加类型到指定的地方
                        int saftySort = sort;
                        if (saftySort < 0) {
                            saftySort = 0;
                        } else if (saftySort >= types.size()) {
                            saftySort = types.size();
                        }
                        types.add(saftySort, ShiyiModelHelper.newType(personDetail.getId(), types.size() + 1, typeName));
                        ShiyiDbUtils.renewSort(types);
                    }
                } else {
                    // 没有该PersonDetail, 则创建一个
                    personDetail = ShiyiModelHelper.newPersonDetail(personId);
                    realm.copyToRealm(personDetail);
                    // 然后添加一个Type到Types里面 (该Type的TypeDetails里面默认有一个空的TypeDetail)
                    RealmList<Type> types = personDetail.getTypes();
                    types.add(ShiyiModelHelper.newType(personDetail.getId(), types.size() + 1, typeName));
                }
            }
        });
    }

    public static Observable<Void> deleteType(final Realm realm, final String typeId) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Type type = realm.where(Type.class).equalTo("id", typeId).findFirst();
                if (type != null) {
                    type.deleteFromRealm();
                }
            }
        });
    }

    public static Observable<Void> deleteTypeDetail(final Realm realm, final String typeDetailId) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                RealmResults<TypeDetail> typeResults = realm.where(TypeDetail.class).equalTo("id", typeDetailId).findAll();
                typeResults.deleteAllFromRealm();
            }
        });
    }

    public static Observable<Result> deletePerson(final Realm realm, final String personId) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Result> subscriber) {
                // 删除Person
                RealmResults<Person> personResults = realm.where(Person.class).equalTo("id", personId).findAll();
                personResults.deleteAllFromRealm();
                // 删除PersonDetail
                RealmResults<PersonDetail> personDetailResults = realm.where(PersonDetail.class).equalTo("id", personId).findAll();
                personDetailResults.deleteAllFromRealm();
            }
        });
    }

    public static Observable<Result> editTypeDetail(final Realm realm, final String typeDetailId, final String info, final String desc) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Result>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Result> subscriber) {
                TypeDetail typeDetail = realm.where(TypeDetail.class).equalTo("id", typeDetailId).findFirst();
                if (typeDetail != null) {
                    typeDetail.setDetail(info);
                    typeDetail.setDescribe(desc);
                    typeDetail.refreshTimestamp();
                }
            }
        });
    }

    public static Observable<Void> editPerson(Realm realm, final String personId, final String name, final String desc, final String sex) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Person person = realm.where(Person.class).equalTo("id", personId).findFirst();
                if (person != null) {
                    person.setName(name);
                    person.setDescribe(desc);
                    person.setGender(sex);
                }
            }
        });
    }

    public static Observable<Void> movePersonToGroup(Realm realm, final String personId, final String newGroupName) {
        return LshRxUtils.getAsyncTransactionObservable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                Person person = realm.where(Person.class).equalTo("id", personId).findFirst();
                if (person != null) {
                    Group newGroup = realm.where(Group.class).equalTo("name", newGroupName).findFirst();
                    if (newGroup != null) {
                        // 获取person的copy
                        Person copy = realm.copyFromRealm(person);
                        // 删除原来的person
                        person.deleteFromRealm();
                        // 添加copy到新分组去
                        RealmList<Person> persons = newGroup.getPersons();
                        persons.add(copy);
                        // 对联系人进行排序并保存
                        ShiyiDbUtils.sortToRealm(realm, persons, "id");
                    }
                }
            }
        });
    }
}
