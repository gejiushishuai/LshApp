package com.linsh.lshapp.mvp.edit_person;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonAlbum;
import com.linsh.lshapp.model.bean.db.PersonDetail;
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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

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
            Disposable disposable = LshRxUtils.getAsyncFlowable(new AsyncConsumer<String>() {
                @Override
                public void call(Realm realm, FlowableEmitter<? super String> subscriber) {
                    Group group = realm.where(Group.class).equalTo("persons.id", personId).findFirst();
                    subscriber.onNext(group.getName());
                }
            }).subscribe(group -> {
                mPrimaryGroupName = group;
                getView().setGroup(group);
            });
            addDisposable(disposable);

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
        Disposable disposable = ShiyiDbHelper.addGroup(getRealm(), inputText)
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
                    getView().setGroup(inputText);
                    getView().onPersonModified();
                });
        addDisposable(disposable);
    }

    @Override
    public void savePerson(String group, String name, String desc, String sex, File avatarFile) {
        getView().showLoadingDialog();

        Flowable<Void> flowable;
        if (avatarFile != null) {
            String avatarName = NameTool.getAvatarName(name);
            String thumbName = NameTool.getAvatarThumbName(avatarName);
            File thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId());
            final String[] thumbUrl = {null};

            // 生成缩略图
            flowable = Flowable.create(emitter -> {
                LshLogUtils.i("生成缩略图");
                // 宽高 256*256  最大尺寸 50Kb
                boolean success = LshImageUtils.compressImage(avatarFile, thumbFile, 256, 256, 50);
                if (success) {
                    emitter.onNext(true);
                } else {
                    emitter.onError(new RuntimeException("生成缩略图失败!"));
                }
                emitter.onComplete();
            }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
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
            flowable = getSavePersonObservable(group, name, desc, null, null, sex);
        }
        Disposable disposable = flowable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptyConsumer<>(), throwable -> {
                    getView().dismissLoadingDialog();
                    getView().showToast("保存失败(" + throwable.getMessage() + ")");
                }, () -> {
                    getView().dismissLoadingDialog();
                    getView().finishActivity();
                });
        addDisposable(disposable);
    }

    private Flowable<Void> getSavePersonObservable(String group, String name, String desc, String avatarUrl, String avatarThumbUrl, String sex) {
        ImageUrl imageUrl = null;
        if (!LshStringUtils.isEmpty(avatarUrl) && LshRegexUtils.isURL(avatarUrl)) {
            imageUrl = new ImageUrl(avatarUrl, avatarThumbUrl);
        }
        if (mPerson == null) {
            Person person = new Person(name, desc, avatarUrl, avatarThumbUrl, sex);
            return ShiyiDbHelper.addPerson(getRealm(), group, person, new PersonDetail(person.getId()), new PersonAlbum(person.getId(), imageUrl));
        } else {
            Flowable<Void> flowable;
            Person person = getRealm().copyFromRealm(mPerson);
            person.setName(name);
            person.setDescribe(desc);
            person.setGender(sex);
            if (avatarUrl != null) {
                person.setAvatar(avatarUrl, avatarThumbUrl);
            }
            if (group.equals(mPrimaryGroupName)) {
                flowable = ShiyiDbHelper.editPerson(getRealm(), person, imageUrl);
            } else {
                flowable = ShiyiDbHelper.editPerson(getRealm(), group, person, imageUrl);
            }
            return flowable;
        }
    }
}
