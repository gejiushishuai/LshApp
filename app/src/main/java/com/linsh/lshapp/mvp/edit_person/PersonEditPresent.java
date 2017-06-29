package com.linsh.lshapp.mvp.edit_person;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.AsyncAction;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonAlbum;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.event.PersonChangedEvent;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshIdTools;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.NameTool;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshImageUtils;
import com.linsh.lshutils.utils.LshRegexUtils;

import java.io.File;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class PersonEditPresent extends RealmPresenterImpl<PersonEditContract.View> implements PersonEditContract.Presenter {

    private RealmResults<Group> mGroups;
    private String mPrimaryGroupName;
    private Person mPerson;

    @Override
    protected void attachView() {
        mGroups = ShiyiDbHelper.getGroups(getRealm());

        String personId = getView().getPersonId();
        if (personId != null) {
            // 获取该联系人所在组别
            LshRxUtils.getAsyncObservable(new AsyncAction<String>() {
                @Override
                public void call(Realm realm, Subscriber<? super String> subscriber) {
                    Group group = realm.where(Group.class).equalTo("persons.id", personId).findFirst();
                    subscriber.onNext(group.getName());
                }
            }).subscribe(group -> {
                mPrimaryGroupName = group;
                getView().setGroup(group);
            });

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
            String avatarName = NameTool.getAvatarName(name);
            String thumbName = NameTool.getAvatarThumbName(avatarName);
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
        ImageUrl imageUrl = null;
        if (!LshStringUtils.isEmpty(avatarUrl) && LshRegexUtils.isURL(avatarUrl)) {
            imageUrl = new ImageUrl(avatarUrl, avatarThumbUrl);
        }
        if (mPerson == null) {
            Person person = new Person(name, desc, avatarUrl, avatarThumbUrl, sex);
            return ShiyiDbHelper.addPerson(getRealm(), group, person, new PersonDetail(person.getId()), new PersonAlbum(person.getId(), imageUrl));
        } else {
            Observable<Void> observable;
            Person person = getRealm().copyFromRealm(mPerson);
            person.setName(name);
            person.setDescribe(desc);
            person.setGender(sex);
            if (avatarUrl != null) {
                person.setAvatar(avatarUrl, avatarThumbUrl);
            }
            if (group.equals(mPrimaryGroupName)) {
                observable = ShiyiDbHelper.editPerson(getRealm(), person, imageUrl);
            } else {
                observable = ShiyiDbHelper.editPerson(getRealm(), group, person, imageUrl);
            }
            return observable;
        }
    }
}
