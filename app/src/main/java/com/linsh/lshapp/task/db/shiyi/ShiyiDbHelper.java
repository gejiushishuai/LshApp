package com.linsh.lshapp.task.db.shiyi;

import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonAlbum;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Shiyi;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.bean.db.TypeLabel;
import com.linsh.lshapp.model.throwabes.CustomThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnemptyGroupThrowable;
import com.linsh.lshapp.model.throwabes.DeleteUnnameGroupThrowable;
import com.linsh.lshapp.model.throwabes.PersonRepeatThrowable;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.ShiyiModelHelper;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshRegexUtils;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ShiyiDbHelper {

    public static RealmResults<Group> getGroups(Realm realm) {
        return realm.where(Group.class).findAllSortedAsync("sort");
    }

    public static Person getPerson(Realm realm, String personId) {
        return realm.where(Person.class).equalTo("id", personId).findFirstAsync();
    }

    public static PersonDetail getPersonDetail(Realm realm, String personId) {
        return realm.where(PersonDetail.class).equalTo("id", personId).findFirstAsync();
    }

    public static RealmResults<TypeLabel> getTypeLabels(Realm realm) {
        return realm.where(TypeLabel.class).findAllSortedAsync("sort");
    }

    public static TypeDetail getTypeDetail(final Realm realm, String typeDetailId) {
        return realm.where(TypeDetail.class).equalTo("id", typeDetailId).findFirstAsync();
    }

    public static PersonAlbum getPersonAlbum(Realm realm, String personId) {
        return realm.where(PersonAlbum.class).equalTo("id", personId).findFirstAsync();
    }

    public static Flowable<List<Group>> getGroupsCopy() {
        return LshRxUtils.getAsyncFlowable(new AsyncConsumer<List<Group>>() {
            @Override
            public void call(Realm realm, FlowableEmitter<? super List<Group>> emitter) {
                RealmResults<Group> groups = realm.where(Group.class).findAll();
                emitter.onNext(realm.copyFromRealm(groups));
            }
        });
    }

    /**
     * 添加分组
     */
    public static Flowable<Void> addGroup(final Realm realm, final String groupName) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 获取shiyi表格
                Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                if (shiyi == null) {
                    shiyi = realm.createObject(Shiyi.class);
                    shiyi.setGroups(new RealmList<Group>());
                }
                RealmList<Group> groups = shiyi.getGroups();
                // 判断是否有该分组
                Group sameNameGroup = groups.where().equalTo("name", groupName).findFirst();
                if (sameNameGroup == null) {
                    // 没有该分组则创建
                    groups.add(new Group(groupName, groups.size() + 1));
                }
            }
        });
    }

    /**
     * 删除分组
     */
    public static Flowable<Void> deleteGroup(final Realm realm, final String groupId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 获取该分组
                Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();
                if (group != null) {
                    // 获取分组中联系人, 如果分组中有联系人, 则提示是否删除
                    RealmList<Person> persons = group.getPersons();
                    if (persons != null && persons.size() > 0) {
                        if ("未分组".equals(group.getName())) {
                            emitter.onError(new DeleteUnnameGroupThrowable("未分组里的联系人必须移至其他分组后才能删除"));
                        } else if ("删除".equals(group.getName())) {
                            group.deleteFromRealm();
                        } else {
                            emitter.onError(new DeleteUnemptyGroupThrowable("分组中的联系人不会被删除，是否继续删除该分组？"));
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
    public static Flowable<Void> moveToUnnameGroup(final Realm realm, final String groupId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                if (shiyi != null) {
                    RealmList<Group> groups = shiyi.getGroups();
                    // 获取分组
                    Group group = groups.where().equalTo("id", groupId).findFirst();
                    if (group != null) {
                        // 获取未分组分组
                        Group unnameGroup = groups.where().equalTo("name", "未分组").findFirst();
                        List<Person> copyPersons = realm.copyFromRealm(group.getPersons());
                        if (unnameGroup == null) {
                            // 没有未分组, 则创建
                            unnameGroup = new Group("未分组", 0);
                            unnameGroup.getPersons().addAll(copyPersons);
                            groups.add(0, unnameGroup);
                        } else {
                            RealmList<Person> persons = unnameGroup.getPersons();
                            persons.addAll(copyPersons);
                            // 对联系人进行排序并保存
                            ShiyiDbUtils.sortToRealm(realm, persons, "id");
                        }
                        group.getPersons().clear();
                        group.deleteFromRealm();
                    }
                }
            }
        });
    }

    /**
     * 重命名分组
     */
    public static Flowable<Void> renameGroup(final Realm realm, final String groupId, final String newGroupName) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                Group group = realm.where(Group.class).equalTo("id", groupId).findFirst();
                if (group != null) {
                    group.setName(newGroupName);
                }
            }
        });
    }

    public static Flowable<Void> addTypeLabel(final Realm realm, final String labelName, final int size) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                TypeLabel typeLabel = realm.where(TypeLabel.class).equalTo("name", labelName).findFirst();
                if (typeLabel == null) {
                    TypeLabel newTypeLabel = new TypeLabel(labelName, size + 1);
                    realm.copyToRealm(newTypeLabel);
                }
            }
        });
    }

    public static Flowable<Void> addType(final Realm realm, final String personId, final String typeName) {
        return addType(realm, personId, typeName, -1);
    }

    public static Flowable<Void> addType(final Realm realm, final String personId, final String typeName, final int sort) {
        return addTypeDetail(realm, personId, typeName, sort, "", "");
    }

    public static Flowable<Void> addTypeDetail(final Realm realm, final String personId, final String typeName,
                                               String typeDetail, String typeDetailDesc) {
        return addTypeDetail(realm, personId, typeName, -1, typeDetail, typeDetailDesc);
    }

    private static Flowable<Void> addTypeDetail(final Realm realm, final String personId, final String typeName,
                                                final int sort, String typeDetail, String typeDetailDesc) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 通过Id查询该PersonDetail
                PersonDetail personDetail = realm.where(PersonDetail.class).equalTo("id", personId).findFirst();
                if (personDetail != null) {
                    // 有该PersonDetail, 获取Types, 查询是否有同名的Type
                    RealmResults<Type> typeResults = personDetail.getTypes().where().equalTo("name", typeName).findAll();
                    if (typeResults.size() > 0) {
                        // 有同名的Type, 则在该Type的TypeDetails最后加上空的TypeDetail
                        Type type = typeResults.get(0);
                        RealmList<TypeDetail> typeDetails = type.getTypeDetails();
                        typeDetails.add(new TypeDetail(type.getId(), typeDetails.size() + 1, typeDetail, typeDetailDesc));
                    } else {
                        // 没有同名的Type, 则在types里面加上一个Type (该Type的TypeDetails里面默认有一个空的TypeDetail)
                        RealmList<Type> types = personDetail.getTypes();
                        // 创建一个新的 Type, 在里面添加一个空的 TypeDetail
                        Type newType = new Type(personDetail.getId(), typeName, types.size() + 1);
                        newType.getTypeDetails().add(new TypeDetail(newType.getId(), 1, typeDetail, typeDetailDesc));
                        // 添加类型到指定的地方
                        if (sort >= 0 && sort < types.size()) {
                            types.add(sort, newType);
                        } else {
                            types.add(newType);
                        }
                        // 重新排序
                        ShiyiDbUtils.renewSort(types);
                    }
                } else {
                    // 没有该PersonDetail, 则创建一个
                    personDetail = new PersonDetail(personId);
                    realm.copyToRealm(personDetail);
                    // 然后添加一个Type到Types里面 (该Type的TypeDetails里面默认有一个空的TypeDetail)
                    RealmList<Type> types = personDetail.getTypes();
                    Type newType = new Type(personDetail.getId(), typeName, types.size() + 1);
                    types.add(newType);
                    newType.getTypeDetails().add(new TypeDetail(newType.getId(), 1, typeDetail, typeDetailDesc));
                }
            }
        });
    }

    public static Flowable<Void> deleteType(final Realm realm, final String typeId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                Type type = realm.where(Type.class).equalTo("id", typeId).findFirst();
                if (type != null) {
                    type.deleteFromRealm();
                }
            }
        });
    }

    public static Flowable<Void> deleteTypeDetail(final Realm realm, final String typeDetailId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                RealmResults<TypeDetail> typeResults = realm.where(TypeDetail.class).equalTo("id", typeDetailId).findAll();
                typeResults.deleteAllFromRealm();
            }
        });
    }

    public static Flowable<Void> deletePerson(final Realm realm, final String personId) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 删除Person
                RealmResults<Person> personResults = realm.where(Person.class).equalTo("id", personId).findAll();
                personResults.deleteAllFromRealm();
                // 删除PersonDetail
                RealmResults<PersonDetail> personDetailResults = realm.where(PersonDetail.class).equalTo("id", personId).findAll();
                personDetailResults.deleteAllFromRealm();
            }
        });
    }

    public static Flowable<Void> editTypeDetail(final Realm realm, final String typeDetailId, final String info, final String desc) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                TypeDetail typeDetail = realm.where(TypeDetail.class).equalTo("id", typeDetailId).findFirst();
                if (typeDetail != null) {
                    typeDetail.setDetail(info);
                    typeDetail.setDescribe(desc);
                    typeDetail.refreshTimestamp();
                }
            }
        });
    }

    public static Flowable<Void> editPerson(Realm realm, Person person) {
        return editPerson(realm, null, person, null);
    }

    public static Flowable<Void> editPerson(Realm realm, Person person, ImageUrl avatar) {
        return editPerson(realm, null, person, avatar);
    }

    public static Flowable<Void> editPerson(Realm realm, String newGroupName, Person person) {
        return editPerson(realm, newGroupName, person, null);
    }

    public static Flowable<Void> editPerson(Realm realm, String newGroupName, Person person, ImageUrl avatar) {
        if (person.isManaged())
            throw new IllegalArgumentException("无法处理被 Realm 所管理的对象");
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 查找 Person
                Person realmPerson = realm.where(Person.class).equalTo("id", person.getId()).findFirst();
                if (realmPerson != null) {
                    // 更新 Person
                    realm.copyToRealmOrUpdate(person);
                    // 在相册中保存头像
                    if (avatar != null && !LshStringUtils.isEmpty(avatar.getUrl()) && LshRegexUtils.isURL(avatar.getUrl())) {
                        PersonAlbum personAlbum = realm.where(PersonAlbum.class).equalTo("id", person.getId()).findFirst();
                        if (personAlbum == null) {
                            personAlbum = realm.copyToRealm(new PersonAlbum(person.getId()));
                        }
                        personAlbum.addAvatar(avatar);
                    }
                    // 分组名不为空时, 更改分组
                    if (newGroupName != null) {
                        Group oldGroup = realm.where(Group.class).equalTo("persons.id", realmPerson.getId()).findFirst();
                        // 组名不一样, 确定更改
                        if (oldGroup != null && !oldGroup.getName().equals(newGroupName)) {
                            Group newGroup = realm.where(Group.class).equalTo("name", newGroupName).findFirst();
                            if (newGroup != null) {
                                oldGroup.getPersons().remove(realmPerson);
                                newGroup.getPersons().add(realmPerson);
                                // 对联系人进行排序并保存
                                ShiyiDbUtils.sortToRealm(realm, newGroup.getPersons(), "id");
                            }
                        }
                    }
                }
            }
        });
    }

    public static Flowable<Void> savePersonTypes(Realm realm, final String personId, final List<Type> types) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                ShiyiDbUtils.renewSort(types);
                realm.copyToRealmOrUpdate(types);

                PersonDetail personDetail = realm.where(PersonDetail.class).equalTo("id", personId).findFirst();
                if (personDetail != null) {
                    RealmList<Type> types = personDetail.getTypes();
                    ShiyiDbUtils.sortToRealm(realm, types, "sort");
                }
            }
        });
    }

    public static Flowable<Void> removePersonType(Realm realm, final String personId, final String typeName) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                PersonDetail personDetail = realm.where(PersonDetail.class).equalTo("id", personId).findFirst();
                if (personDetail != null) {
                    Type type = personDetail.getTypes().where().equalTo("name", typeName).findFirst();
                    if (type != null) {
                        type.deleteFromRealm();
                    }
                }
            }
        });
    }

    public static Flowable<Void> removeTypeLabel(Realm realm, final String typeName) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                TypeLabel typeLabel = realm.where(TypeLabel.class).equalTo("name", typeName).findFirst();
                if (typeLabel != null) {
                    typeLabel.deleteFromRealm();
                }
            }
        });
    }

    public static Flowable<Void> saveGroups(Realm realm, final List<Group> groups) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 重新设置sort字段
                ShiyiDbUtils.renewSort(groups);
                realm.copyToRealmOrUpdate(groups);
            }
        });
    }

    public static Flowable<Void> addPerson(Realm realm, String group, Person person) {
        return addPerson(realm, group, person, new PersonDetail(person.getId()), new PersonAlbum(person.getId()));
    }

    public static Flowable<Void> addPerson(Realm realm, String group, Person person, PersonDetail personDetail) {
        return addPerson(realm, group, person, personDetail, new PersonAlbum(person.getId()));
    }

    public static Flowable<Void> addPerson(Realm realm, String group, Person person, PersonDetail personDetail, PersonAlbum personAlbum) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 查找联系人
                Person realmPerson = realm.where(Person.class).equalTo("name", person.getName()).findFirst();
                // 无法保存已经存在的联系人
                if (realmPerson != null) {
                    emitter.onError(new PersonRepeatThrowable("已经存在该联系人"));
                    return;
                }
                // 没有指定组名, 则保存在"未分组"分组里面
                String groupName = group;
                if (LshStringUtils.isEmpty(groupName)) {
                    groupName = ShiyiModelHelper.UNNAME_GROUP_NAME;
                }
                Group realmGroup = realm.where(Group.class).equalTo("name", groupName).findFirst();
                if (realmGroup == null && ShiyiModelHelper.UNNAME_GROUP_NAME.equals(groupName)) {
                    Shiyi shiyi = realm.where(Shiyi.class).findFirst();
                    realmGroup = new Group(ShiyiModelHelper.UNNAME_GROUP_NAME, 1);
                    RealmList<Group> groups = shiyi.getGroups();
                    groups.add(0, realmGroup);
                    ShiyiDbUtils.renewSort(groups);
                }
                // 保存联系人
                if (realmGroup != null) {
                    realmGroup.getPersons().add(person);
                    realm.copyToRealmOrUpdate(personDetail);
                    realm.copyToRealmOrUpdate(personAlbum);
                    // 对联系人进行排序并保存
                    ShiyiDbUtils.sortToRealm(realm, realmGroup.getPersons(), "id");
                } else {
                    emitter.onError(new CustomThrowable("没有创建该分组, 无法添加"));
                }
            }
        });
    }

    public static Flowable<Void> coverPersonAddDetail(Realm realm, Person person, PersonDetail personDetail) {
        return LshRxUtils.getAsyncTransactionFlowable(realm, new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                // 获取 Person
                Person realmPerson = realm.where(Person.class).equalTo("name", person.getName()).findFirst();
                if (realmPerson != null) {
                    person.setId(realmPerson.getId());
                    personDetail.setId(realmPerson.getId());
                    // 覆盖添加 Person 字段
                    if (LshStringUtils.isEmpty(person.getDescribe())) {
                        realmPerson.setDescribe(person.getDescribe());
                    }
                    if (!LshStringUtils.isEmpty(person.getAvatar())) {
                        realmPerson.setAvatar(person.getAvatar(), person.getAvatarThumb());
                    }
                    if (person.getIntGender() != 0) {
                        realmPerson.setGender(person.getIntGender());
                    }
                    // 获取 PersonDetail
                    PersonDetail realmPersonDetail = realm.where(PersonDetail.class).equalTo("id", realmPerson.getId()).findFirst();
                    if (realmPersonDetail != null) {
                        RealmList<Type> types = personDetail.getTypes();
                        RealmList<Type> realmTypes = realmPersonDetail.getTypes();
                        for (Type type : types) {
                            Type realmType = realmTypes.where().equalTo("name", type.getName()).findFirst();
                            if (realmType != null) {
                                RealmList<TypeDetail> realmTypeDetails = realmType.getTypeDetails();
                                RealmList<TypeDetail> typeDetails = type.getTypeDetails();
                                for (TypeDetail typeDetail : typeDetails) {
                                    String detail = typeDetail.getDetail();
                                    String describe = typeDetail.getDescribe();
                                    if (realmTypeDetails.where().equalTo("detail", detail).findFirst() == null) {
                                        realmTypeDetails.add(new TypeDetail(realmType.getId(), realmTypeDetails.size() + 1, detail, describe));
                                    }
                                }
                            } else {
                                realmType = new Type(realmPersonDetail.getId(), type.getName(), realmTypes.size() + 1);
                                realmTypes.add(realmType);
                                RealmList<TypeDetail> realmTypeDetails = realmType.getTypeDetails();
                                RealmList<TypeDetail> typeDetails = type.getTypeDetails();
                                for (TypeDetail typeDetail : typeDetails) {
                                    String detail = typeDetail.getDetail();
                                    String describe = typeDetail.getDescribe();
                                    realmTypeDetails.add(new TypeDetail(realmType.getId(), realmTypeDetails.size() + 1, detail, describe));
                                }
                            }
                        }
                    } else {
                        realm.copyToRealmOrUpdate(personDetail);
                    }
                } else {
                    emitter.onError(new CustomThrowable("该联系人不存在"));
                }
            }
        });
    }
}
