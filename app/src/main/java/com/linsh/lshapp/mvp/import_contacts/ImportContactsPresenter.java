package com.linsh.lshapp.mvp.import_contacts;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.action.DismissLoadingThrowableAction;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.bean.http.HttpInfo;
import com.linsh.lshapp.model.bean.http.UploadInfo;
import com.linsh.lshapp.model.throwabes.CustomThrowable;
import com.linsh.lshapp.model.throwabes.PersonRepeatThrowable;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshapp.tools.HttpErrorCatcher;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshIdTools;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.NameTool;
import com.linsh.lshapp.tools.ShiyiModelHelper;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshLogUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshBitmapUtils;
import com.linsh.lshutils.utils.LshDateUtils;
import com.linsh.lshutils.view.LshColorDialog;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class ImportContactsPresenter extends RealmPresenterImpl<ImportContactsContract.View> implements ImportContactsContract.Presenter {

    @Override
    protected void attachView() {
        getView().showLoadingDialog();
        Observable<List<Contact>> observable = Observable.unsafeCreate(new Observable.OnSubscribe<List<Contact>>() {
            @Override
            public void call(Subscriber<? super List<Contact>> subscriber) {
                Contacts.initialize(LshApplicationUtils.getContext());
                subscriber.onNext(Contacts.getQuery().find());
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    getView().dismissLoadingDialog();
                    if (contacts.size() > 0) {
                        getView().setData(contacts);
                    } else {
                        getView().showTextDialog("手机通讯录中没有联系人哦");
                    }
                }, throwable -> {
                    getView().dismissLoadingDialog();
                    DefaultThrowableAction.showThrowableMsg(throwable);
                });
    }

    @Override
    public void addContact(Contact contact) {
        getView().showLoadingDialog();
        LshLogUtils.i("添加联系人");

        Person person = getPerson(contact);
        PersonDetail personDetail = getPersonDetail(contact, person.getId());
        Subscription addPersonAddDetailSub = ShiyiDbHelper.addPerson(getRealm(), ShiyiModelHelper.UNNAME_GROUP_NAME, person, personDetail)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(Actions.empty(), throwable -> {
                    getView().dismissLoadingDialog();
                    if (throwable instanceof PersonRepeatThrowable) {
                        LshLogUtils.i("已经存在该联系人");
                        getView().showTextDialog("已经存在该联系人, 如果添加将会覆盖重复的属性", "添加", dialog -> {
                            dialog.dismiss();
                            getView().showLoadingDialog();
                            addSubscription(ShiyiDbHelper.coverPersonAddDetail(getRealm(), person, personDetail)
                                    .subscribe(Actions.empty(), throwable2 -> {
                                        getView().dismissLoadingDialog();
                                        DefaultThrowableAction.showThrowableMsg(throwable2);
                                    }, () -> {
                                        getView().dismissLoadingDialog();
                                        checkUploadAvatar(person, contact);
                                    }));
                        }, null, null);
                    } else {
                        DefaultThrowableAction.showThrowableMsg(throwable);
                    }
                }, () -> {
                    getView().dismissLoadingDialog();
                    checkUploadAvatar(person, contact);
                });
        addSubscription(addPersonAddDetailSub);
    }

    private void checkUploadAvatar(Person person, Contact contact) {
        if (LshStringUtils.notEmpty(contact.getPhotoUri())) {
            LshLogUtils.i("通讯录中有该联系人的头像");
            getView().showTextDialog("是否继续上传该联系人的头像?", "上传", new LshColorDialog.OnPositiveListener() {
                @Override
                public void onClick(LshColorDialog dialog) {
                    LshLogUtils.i("上传头像");
                    dialog.dismiss();
                    getView().showLoadingDialog();
                    addSubscription(uploadAvatar(person, contact)
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(new Func1<ImageUrl, Observable<Void>>() {
                                @Override
                                public Observable<Void> call(ImageUrl imageUrl) {
                                    if (imageUrl != null) {
                                        LshLogUtils.i("上传头像成功, 保存联系人");
                                        return ShiyiDbHelper.editPerson(getRealm(), person, imageUrl);
                                    }
                                    LshLogUtils.i("上传头像失败");
                                    return LshRxUtils.getDoNothingObservable();
                                }
                            })
                            .subscribe(Actions.empty(), new DismissLoadingThrowableAction(getView()), () -> {
                                getView().dismissLoadingDialog();
                                getView().removeCurrentItem();
                            }));
                }
            }, "不上传", dialog -> {
                dialog.dismiss();
                getView().removeCurrentItem();
            });
        } else {
            getView().removeCurrentItem();
        }
    }

    public Observable<ImageUrl> uploadAvatar(Person person, Contact contact) {

        return Observable.unsafeCreate(new Observable.OnSubscribe<ImageUrl>() {
            @Override
            public void call(Subscriber<? super ImageUrl> subscriber) {
                // 判断是否有联系人头像
                String photoUri = contact.getPhotoUri();
                if (LshStringUtils.notEmpty(photoUri)) {
                    try {
                        Uri uri = Uri.parse(contact.getPhotoUri());
                        ContentResolver resolver = LshApplicationUtils.getContext().getContentResolver();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(resolver, uri);

                        String avatarName = NameTool.getAvatarName(person.getName());
                        String thumbName = NameTool.getAvatarThumbName(avatarName);
                        File avatarFile = LshFileFactory.getUploadAvatarFile(LshIdTools.getTimeId());
                        File thumbFile = LshFileFactory.getUploadThumbFile(LshIdTools.getTimeId());

                        if (bitmap.getByteCount() > 500 * 500) {
                            // 生成原图和缩略图
                            if (LshBitmapUtils.saveBitmap(bitmap, avatarFile, true) && avatarFile.exists()) {
                                LshBitmapUtils.compressBitmap(avatarFile, thumbFile, 256, 256, 50);
                            }
                        } else if (bitmap.getByteCount() > 256 * 256) {
                            // 生成缩略图
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                            bitmap.recycle();
                            LshBitmapUtils.saveBitmap(scaledBitmap, thumbFile, 50, true);
                        } else {
                            // 直接用作缩略图
                            LshBitmapUtils.saveBitmap(bitmap, thumbFile, 50, true);
                        }

                        ImageUrl imageUrl = new ImageUrl();
                        Observable<HttpInfo<UploadInfo>> observable = null;
                        if (avatarFile.exists()) {
                            observable = UrlConnector.uploadAvatar(avatarName, avatarFile);
                        }
                        if (thumbFile.exists()) {
                            if (observable == null) {
                                observable = UrlConnector.uploadThumb(thumbName, thumbFile);
                            } else {
                                observable = observable.flatMap(new Func1<HttpInfo<UploadInfo>, Observable<HttpInfo<UploadInfo>>>() {
                                    @Override
                                    public Observable<HttpInfo<UploadInfo>> call(HttpInfo<UploadInfo> uploadInfoHttpInfo) {
                                        imageUrl.setUrl(uploadInfoHttpInfo.data.source_url);
                                        return UrlConnector.uploadThumb(thumbName, thumbFile);
                                    }
                                });
                            }
                        }

                        if (observable != null) {
                            observable.subscribe(uploadInfoHttpInfo -> {
                                imageUrl.setThumbUrl(uploadInfoHttpInfo.data.source_url);
                                person.setAvatar(imageUrl.getUrl(), imageUrl.getThumbUrl());
                                subscriber.onNext(imageUrl);
                            }, throwable -> {
                                subscriber.onError(new CustomThrowable(HttpErrorCatcher.dispatchError(throwable)));
                            }, subscriber::onCompleted);
                        } else {
                            subscriber.onNext(null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).observeOn(Schedulers.io());
    }

    public Person getPerson(Contact contact) {
        return new Person(contact.getDisplayName(), "", "", "", "");
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
                        type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, event.getStartDate(), null));
                        break;
                    case UNKNOWN:
                        // 农历生日, 目前魅族可以, 小米不行
                        String birthday = null;
                        try {
                            String startDate = event.getStartDate();
                            if (startDate.matches("\\d{2}-\\d{2}")) {
                                Date date = new SimpleDateFormat("MM-dd").parse(startDate);
                                birthday = LshDateUtils.getLunarDate(date, false);
                            } else if (startDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
                                birthday = LshDateUtils.getLunarDate(date, false);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (birthday != null) {
                            type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, birthday, null));
                        }
                        break;
                }
            }
            types.add(type);
        }
        // 添加备注
        String note = contact.getNote();
        if (!LshStringUtils.isEmpty(note)) {
            Type type = new Type(personId, "备注", types.size() + 1);
            types.add(type);
            type.getTypeDetails().add(new TypeDetail(type.getId(), type.getTypeDetails().size() + 1, note, null));
        }
        return personDetail;
    }
}
