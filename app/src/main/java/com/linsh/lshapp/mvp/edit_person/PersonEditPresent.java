package com.linsh.lshapp.mvp.edit_person;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.event.PersonChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshIdTools;
import com.linsh.lshapp.tools.ShiyiModelHelper;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.LshImageUtils;

import java.io.File;
import java.util.List;

import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class PersonEditPresent extends RealmPresenterImpl<PersonEditContract.View> implements PersonEditContract.Presenter {

    private RealmResults<Group> mGroups;
    private Person mPerson;

    @Override
    protected void attachView() {
        mGroups = ShiyiDbHelper.getGroups(getRealm());

        String personId = getView().getPersonId();
        if (personId != null) {
            mPerson = ShiyiDbHelper.getPerson(getRealm(), personId);
            mPerson.addChangeListener(element -> {
                if (mPerson.isValid()) {
                    getView().setData(mPerson);
                }
            });
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mPerson != null && mPerson.isValid()) {
            mPerson.removeAllChangeListeners();
        }
    }

    @Override
    public List<Group> getGroups() {
        return mGroups;
    }

    @Override
    public void addGroup(final String inputText) {
        ShiyiDbHelper.addGroup(getRealm(), inputText)
                .subscribe(Actions.empty(), new DefaultThrowableAction(), () -> {
                    getView().setGroup(inputText);
                    getView().onPersonModified();
                });
    }

    @Override
    public void savePerson(String group, String name, String desc, String sex, File avatarFile) {
        getView().showLoadingDialog();

        Observable<Void> observable;
        if (avatarFile != null) {
            String avatarName = "avatar_" + ShiyiModelHelper.getPersonId(name);
            String thumbName = "thumb_" + avatarName;
            File thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId());
            final String[] thumbUrl = {null};

            // 生成缩略图
            observable = Observable.unsafeCreate(subscriber -> {
                LshLogUtils.i("生成缩略图");
                // 宽高 256*256  最大尺寸 50Kb
                boolean success = LshImageUtils.compressImage(avatarFile, thumbFile, 256, 256, 50);
                if (success) {
                    subscriber.onNext(null);
                } else {
                    subscriber.onError(new RuntimeException("生成缩略图失败!"));
                }
                subscriber.onCompleted();
            }).subscribeOn(Schedulers.io())
                    .flatMap(uploadInfoHttpInfo -> {
                        LshLogUtils.i("上传缩略图");
                        return UrlConnector.uploadThumb(thumbName, thumbFile);
                    })
                    .flatMap(uploadInfoHttpInfo -> {
                        LshLogUtils.i("上传头像");
                        thumbUrl[0] = uploadInfoHttpInfo.data.source_url;
                        return UrlConnector.uploadAvatar(avatarName, avatarFile);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(uploadInfoHttpInfo -> {
                        LshLogUtils.i("保存联系人");
                        return getSavePersonObservable(group, name, desc,
                                uploadInfoHttpInfo.data.source_url, thumbUrl[0], sex);
                    });

        } else {
            observable = getSavePersonObservable(group, name, desc, null, null, sex);
        }
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(Actions.empty(), throwable -> {
                    getView().dismissLoadingDialog();
                    getView().showToast("保存失败(" + throwable.getMessage() + ")");
                }, () -> {
                    RxBus.getDefault().post(new PersonChangedEvent());
                    getView().dismissLoadingDialog();
                    getView().finishActivity();
                });
    }

    private Observable<Void> getSavePersonObservable(String group, String name, String desc, String avatarUrl, String avatarThumbUrl, String sex) {
        if (mPerson == null) {
            // 创建Person
            String avatar = avatarUrl == null ? "" : avatarUrl;
            return ShiyiDbHelper.addPerson(getRealm(), group, new Person(name, desc, avatar, avatarThumbUrl, sex));
        } else {
            // 属性有变化, 则修改Person属性
            Observable<Void> observable = null;
            if (!name.equals(mPerson.getName()) || !desc.equals(mPerson.getDescribe()) || !sex.equals(mPerson.getGender()) ||
                    (avatarUrl != null && !avatarUrl.equals(mPerson.getAvatar()))) {
                if (avatarUrl == null) {
                    observable = ShiyiDbHelper.editPerson(getRealm(), mPerson.getId(), name, desc, sex);
                } else {
                    observable = ShiyiDbHelper.editPerson(getRealm(), mPerson.getId(), name, desc, avatarUrl, avatarThumbUrl, sex);
                }
            }
            // 组别有变化, 则修改组别
            String primaryGroup = getView().getPrimaryGroup();
            if (!group.equals(primaryGroup)) {
                final Observable<Void> movePersonToGroup = ShiyiDbHelper.movePersonToGroup(getRealm(), mPerson.getId(), group);
                if (observable == null) {
                    observable = movePersonToGroup;
                } else {
                    observable.flatMap(new Func1<Void, Observable<?>>() {
                        @Override
                        public Observable<?> call(Void aVoid) {
                            return movePersonToGroup;
                        }
                    });
                }
            }
            return observable;
        }
    }
}
