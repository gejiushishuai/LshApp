package com.linsh.lshapp.mvp.sync_contacts;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.dialog.LshColorDialog;
import com.linsh.utilseverywhere.ContextUtils;
import com.linsh.utilseverywhere.module.SimpleDate;
import com.linsh.utilseverywhere.BitmapUtils;
import com.linsh.utilseverywhere.LogUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.DismissLoadingThrowableConsumer;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshapp.model.bean.db.shiyi.ImageUrl;
import com.linsh.lshapp.model.bean.db.shiyi.Person;
import com.linsh.lshapp.model.bean.db.shiyi.PersonDetail;
import com.linsh.lshapp.model.bean.db.shiyi.Type;
import com.linsh.lshapp.model.bean.db.shiyi.TypeDetail;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;
import com.linsh.lshapp.model.throwabes.CustomThrowable;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshapp.tools.ContactsAdder;
import com.linsh.lshapp.tools.HttpErrorCatcher;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshIdTools;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.NameTool;
import com.linsh.lshapp.tools.ShiyiModelHelper;

import org.reactivestreams.Publisher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class SyncContactsPresenter extends RealmPresenterImpl<SyncContactsContract.View> implements SyncContactsContract.Presenter {


    private ContactsAdder mContactsAdder;

    @Override
    protected void attachView() {
        mContactsAdder = new ContactsAdder();
        getView().showLoadingDialog();
        // 获取打开同步的拾意联系人
        ShiyiDbHelper.getSyncContactsPersons()
                // 获取手机联系人, 并合并
                .flatMap(new Function<List<ContactsPerson>, Publisher<TreeMap<String, ContactMixer>>>() {
                    @Override
                    public Publisher<TreeMap<String, ContactMixer>> apply(List<ContactsPerson> contactsPersons) throws Exception {
                        return LshRxUtils.create((FlowableOnSubscribe<TreeMap<String, ContactMixer>>) emitter -> {

                            Contacts.initialize(ContextUtils.get());
                            List<ShiyiContact> contacts = ContactMixer.getContacts();
                            TreeMap<String, ContactMixer> mixers = ContactMixer.mix(contactsPersons, contacts);
                            emitter.onNext(mixers);

                        }).subscribeOn(Schedulers.io());
                    }
                })
                // 补全拾意联系人中没有打开同步但可以和手机联系人匹配的拾意联系人
                .flatMap(new Function<TreeMap<String, ContactMixer>, Publisher<TreeMap<String, ContactMixer>>>() {
                    @Override
                    public Publisher<TreeMap<String, ContactMixer>> apply(TreeMap<String, ContactMixer> mixers) throws Exception {
                        return ShiyiDbHelper.fixContactMixer(mixers);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    getView().dismissLoadingDialog();
                    getView().setData(new ArrayList<>(map.values()));

                }, throwable -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableConsumer.showThrowableMsg(throwable);
                });
    }

    @Override
    public void onClickStatus(ContactMixer mixer) {
        int status = mixer.getStatus();
        switch (status) {
            case ContactMixer.IMPORT_FROM_CONTACTS:
                importFromContact(mixer);
                break;
            case ContactMixer.EXPORT_TO_CONTACTS:
                exportToContact(mixer);
                break;
            case ContactMixer.LINK_TO_CONTACTS:
                linkToContact(mixer);
                break;
            case ContactMixer.UPDATE_WITH_CONTACTS:
                updateWithContact(mixer);
                break;
            case ContactMixer.FINISH_UPDATE:
            default:
                break;
        }
    }

    @Override
    public void refreshAvatar(ContactMixer mixer) {
        Disposable disposable = mContactsAdder.updatePhoto(mixer.getContact(), mixer.getPerson().getAvatar())
                .doOnSubscribe(onSubscribe -> getView().showLoadingDialog())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new DefaultThrowableConsumer())
                .doOnTerminate(() -> getView().dismissLoadingDialog())
                .subscribe(contact -> getView().updateItem());
        addDisposable(disposable);
    }

    private void updateWithContact(ContactMixer mixer) {
        getView().showTextDialog("以『拾意联系人』的数据进行覆盖或以『手机联系人』的数据进行覆盖？", "拾意联系人", new LshColorDialog.OnPositiveListener() {
            @Override
            public void onClick(LshColorDialog dialog) {
                dialog.dismiss();
                mContactsAdder.updateContact(mixer.getContact(), mixer.getPerson())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(contact -> {
                            getView().dismissLoadingDialog();
                            mixer.setContact(contact);
                            getView().updateItem();
                        }, error -> {
                            error.printStackTrace();
                            getView().dismissLoadingDialog();
                            getView().showToast(error.getMessage());
                            getView().updateItem();
                        });
            }
        }, "手机联系人", new LshColorDialog.OnNegativeListener() {
            @Override
            public void onClick(LshColorDialog dialog) {
                dialog.dismiss();
                importFromContact(mixer);
            }
        });
    }

    private void linkToContact(ContactMixer mixer) {
        String personId = mixer.getContact().getPersonId();
        if (StringUtils.isEmpty(personId)) {
            mContactsAdder.insertOrUpdatePersonId(mixer.getContact(), mixer.getPerson().getId());
        } else {
            mContactsAdder.insertOrUpdatePersonId(mixer.getContact(), mixer.getPerson().getId());
        }
        mixer.getContact().setPersonId(mixer.getPerson().getId());
        mixer.refreshStatus();
        getView().updateItem();
    }

    private void exportToContact(ContactMixer mixer) {
        getView().showLoadingDialog();
        Disposable disposable = mContactsAdder.addContact(mixer.getPerson())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contact -> {
                    getView().dismissLoadingDialog();
                    mixer.setContact(contact);
                    getView().updateItem();
                }, error -> {
                    error.printStackTrace();
                    getView().dismissLoadingDialog();
                    getView().showToast(error.getMessage());
                    getView().updateItem();
                });
        addDisposable(disposable);
    }

    public void importFromContact(ContactMixer mixer) {
        getView().showLoadingDialog();
        LogUtils.i("添加联系人");

        ShiyiContact contact = mixer.getContact();
        Person person = getPerson(contact);
        PersonDetail personDetail = getPersonDetail(contact, person.getId());

        Disposable disposable = ShiyiDbHelper
                // 判断是否存在该联系人
                .hasPersonName(person.getName())
                .observeOn(AndroidSchedulers.mainThread())
                // 添加或覆盖联系人
                .flatMap(has -> {
                    if (has) {
                        return LshRxUtils.create(new FlowableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                                LogUtils.i("已经存在该联系人");
                                getView().dismissLoadingDialog();
                                getView().showTextDialog("已经存在该联系人, 如果添加将会覆盖重复的属性", "添加", dialog -> {
                                    dialog.dismiss();
                                    getView().showLoadingDialog();
                                    emitter.onNext(true);
                                }, null, null);
                            }
                        }).flatMap(needCover -> {
                            return ShiyiDbHelper.coverPersonAddDetail(getRealm(), person, personDetail);
                        });
                    } else {
                        return ShiyiDbHelper.addPerson(getRealm(), ShiyiModelHelper.UNNAME_GROUP_NAME, person, personDetail);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                // 判断是否需要上传头像
                .flatMap(personId -> {
                    person.setId(personId);
                    return LshRxUtils.create(new FlowableOnSubscribe<Boolean>() {
                        @Override
                        public void subscribe(FlowableEmitter<Boolean> emitter) throws Exception {
                            if (StringUtils.isEmpty(contact.getPhotoUri())) {
                                // 不上传
                                emitter.onNext(false);
                            } else {
                                // 弹窗询问
                                getView().dismissLoadingDialog();
                                getView().showTextDialog("是否继续上传该联系人的头像?", "上传", new LshColorDialog.OnPositiveListener() {
                                    @Override
                                    public void onClick(LshColorDialog dialog) {
                                        LogUtils.i("上传头像");
                                        dialog.dismiss();
                                        getView().showLoadingDialog();
                                        emitter.onNext(true);
                                    }
                                }, "不上传", dialog -> {
                                    dialog.dismiss();
                                    emitter.onNext(false);
                                });
                            }
                        }
                    });
                })
                .observeOn(Schedulers.io())
                // 上传头像
                .flatMap(uploadNeeded -> {
                    if (uploadNeeded) {
                        return uploadAvatar(person, contact)
                                .observeOn(AndroidSchedulers.mainThread())
                                // 上传成功, 保存联系人
                                .flatMap(imageUrl -> {
                                    if (imageUrl.getUrl() != null) {
                                        LogUtils.i("上传头像成功, 保存联系人");
                                        return ShiyiDbHelper.editPerson(getRealm(), person, imageUrl)
                                                .map(personId -> true);
                                    }
                                    LogUtils.i("上传头像失败");
                                    return Flowable.just(false);
                                });
                    } else {
                        return Flowable.just(true);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success -> {
                    getView().dismissLoadingDialog();
                    mContactsAdder.insertOrUpdatePersonId(contact, person.getId());
                    contact.setPersonId(person.getId());
                    mixer.setStatus(ContactMixer.FINISH_UPDATE);
                    getView().updateItem();
                }, new DismissLoadingThrowableConsumer(getView()));
        addDisposable(disposable);
    }

    public Flowable<ImageUrl> uploadAvatar(Person person, Contact contact) {

        return LshRxUtils.create((FlowableOnSubscribe<ImageUrl>) emitter -> {
            // 判断是否有联系人头像
            String photoUri = contact.getPhotoUri();
            if (StringUtils.notEmpty(photoUri)) {
                try {
                    Uri uri = Uri.parse(contact.getPhotoUri());
                    ContentResolver resolver = ContextUtils.get().getContentResolver();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);

                    String avatarName = NameTool.getAvatarName(person.getName());
                    String thumbName = NameTool.getAvatarThumbName(avatarName);
                    File avatarFile = LshFileFactory.getUploadAvatarFile(LshIdTools.getTimeId());
                    File thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId());

                    if (bitmap.getByteCount() > 500 * 500) {
                        // 生成原图和缩略图
                        if (BitmapUtils.saveBitmap(bitmap, avatarFile, true) && avatarFile.exists()) {
                            BitmapUtils.compressBitmap(avatarFile, thumbFile, 256, 256, 50);
                        }
                    } else if (bitmap.getByteCount() > 256 * 256) {
                        // 生成缩略图
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                        bitmap.recycle();
                        BitmapUtils.saveBitmap(scaledBitmap, thumbFile, 50, true);
                    } else {
                        // 直接用作缩略图
                        BitmapUtils.saveBitmap(bitmap, thumbFile, 50, true);
                    }

                    ImageUrl imageUrl = new ImageUrl();
                    Flowable<HttpInfo<UploadInfo>> observable = null;
                    if (avatarFile.exists()) {
                        observable = UrlConnector.uploadAvatar(avatarName, avatarFile);
                    }
                    if (thumbFile.exists()) {
                        if (observable == null) {
                            observable = UrlConnector.uploadThumb(thumbName, thumbFile);
                        } else {
                            observable = observable.flatMap(uploadInfoHttpInfo -> {
                                imageUrl.setUrl(uploadInfoHttpInfo.data.source_url);
                                return UrlConnector.uploadThumb(thumbName, thumbFile);
                            });
                        }
                    }

                    if (observable != null) {
                        observable.subscribe(uploadInfoHttpInfo -> {
                            imageUrl.setThumbUrl(uploadInfoHttpInfo.data.source_url);
                            person.setAvatar(imageUrl.getUrl(), imageUrl.getThumbUrl());
                            emitter.onNext(imageUrl);
                        }, throwable -> {
                            emitter.onError(new CustomThrowable(HttpErrorCatcher.dispatchError(throwable)));
                        }, emitter::onComplete);
                    } else {
                        emitter.onNext(new ImageUrl());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).observeOn(Schedulers.io());
    }

    public Person getPerson(Contact contact) {
        return new Person(contact.getDisplayName(), "", "", "", "", true);
    }

    public PersonDetail getPersonDetail(Contact contact, String personId) {
        PersonDetail personDetail = new PersonDetail(personId);
        RealmList<Type> types = personDetail.getTypes();

        // 添加电话
        List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers.size() > 0) {
            List<String> list = new ArrayList<>();
            Type type = new Type(personId, "电话", types.size() + 1);
            types.add(type);
            for (PhoneNumber phoneNumber : phoneNumbers) {
                String number = phoneNumber.getNormalizedNumber();
                if (!list.contains(number)) {
                    type.getTypeDetails().add(new TypeDetail(type.getId(), phoneNumbers.size() + 1, number, null));
                    list.add(number);
                }
            }
        }
        // 添加地址
        List<Address> addresses = contact.getAddresses();
        if (addresses.size() > 0) {
            Type type = new Type(personId, "住址", types.size() + 1);
            types.add(type);
            for (Address address : addresses) {
                type.getTypeDetails().add(new TypeDetail(type.getId(), addresses.size() + 1, address.getFormattedAddress(), null));
            }
        }
        // 添加生日
        List<Event> events = contact.getEvents();
        if (events != null && events.size() > 0) {
            Type type = new Type(personId, "生日", types.size() + 1);
            for (Event event : events) {
                switch (event.getType()) {
                    case BIRTHDAY:
                        // 生日
                        String birthday = event.getStartDate();
                        SimpleDate simpleDate1 = SimpleDate.parseDateString(birthday);
                        birthday = simpleDate1 == null ? birthday : simpleDate1.getDisplayString();
                        type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, birthday, null));
                        break;
                    case UNKNOWN:
                    case LUNAR_BIRTHDAY:
                        // 农历生日, 目前魅族可以, 小米不行
                        String lunarBirthday = event.getStartDate();
                        SimpleDate simpleDate = SimpleDate.parseDateString(lunarBirthday);
                        if (simpleDate != null) {
                            simpleDate.setLunar(true);
                            lunarBirthday = simpleDate.getDisplayString();
                            type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, lunarBirthday, null));
                        }
                        break;
                }
            }
            types.add(type);
        }
        // 添加备注
        String note = contact.getNote();
        if (!StringUtils.isEmpty(note)) {
            Type type = new Type(personId, "备注", types.size() + 1);
            types.add(type);
            type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, note, null));
        }
        return personDetail;
    }
}
