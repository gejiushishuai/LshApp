package com.linsh.lshapp.tools;

import android.graphics.Bitmap;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.tamir7.contacts.Contact;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshapp.model.throwabes.CustomThrowable;
import com.linsh.lshapp.mvp.sync_contacts.ContactMixer;
import com.linsh.lshutils.tools.LshContactsEditor;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshContextUtils;
import com.linsh.lshutils.utils.LshImageUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.lshutils.utils.LshLunarCalendarUtils;

import org.reactivestreams.Publisher;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/8/14.
 */

public class ContactsAdder {

    private LshContactsEditor mEditor;

    public ContactsAdder() {
        mEditor = new LshContactsEditor(LshContextUtils.get().getContentResolver());
    }

    public Flowable<ShiyiContact> addContact(ContactsPerson person) {
        LshContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact();
        return LshRxUtils.create((FlowableOnSubscribe<Object>) emitter -> {
            String name = person.getName();
            List<String> phoneNumbers = person.getPhoneNumbers();
            String birthday = person.getBirthday();
            String lunarBirthday = person.getLunarBirthday();
            String avatar = person.getAvatar();

            contactBuilder.insertDisplayName(name);
            contactBuilder.insert(ShiyiContact.MIME_TYPE_PERSON_ID, ShiyiContact.COLUMN_PERSON_ID, person.getId());
            if (!LshListUtils.isEmpty(phoneNumbers)) {
                for (String number : phoneNumbers) {
                    contactBuilder.insertPhoneNumber(number);
                }
            }
            if (LshStringUtils.notEmpty(birthday)) {
                contactBuilder.insertBirthday(birthday);
            }
            if (LshStringUtils.notEmpty(lunarBirthday)) {
                if (lunarBirthday.matches(".*[\\u4e00-\\u9fa5]{1,2}月[\\u4e00-\\u9fa5]{1,3}日?")) {
                    lunarBirthday = LshLunarCalendarUtils.lunarStr2NormalStr(lunarBirthday);
                }
                contactBuilder.insertLunarBirthday(lunarBirthday);
            }
            if (LshStringUtils.notEmpty(avatar)) {
                ImageTools.getGlide(avatar)
                        .asBitmap()
                        .listener(new RequestListener<GlideUrl, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                                e.printStackTrace();
                                LshToastUtils.show("导入头像失败");
                                emitter.onNext(e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, GlideUrl model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                emitter.onNext(resource);
                                return false;
                            }
                        }).preload();
            } else {
                emitter.onNext(true);
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    if (result instanceof Bitmap) {
                        byte[] bytes = LshImageUtils.bitmap2Bytes((Bitmap) result, 1000000);
                        contactBuilder.insertPhoto(bytes);
                    }
                    return contactBuilder.getContactId();
                })
                .flatMap(new Function<Long, Publisher<ShiyiContact>>() {
                    @Override
                    public Publisher<ShiyiContact> apply(Long contactId) throws Exception {
                        return queryContact(contactId);
                    }
                });
    }

    public void insertOrUpdatePersonId(ShiyiContact contact, String personId) {
        mEditor.buildContact(contact.getId())
                .delete(ShiyiContact.MIME_TYPE_PERSON_ID)
                .insert(ShiyiContact.MIME_TYPE_PERSON_ID, ShiyiContact.COLUMN_PERSON_ID, personId);
    }

    public Flowable<ShiyiContact> updateContact(ShiyiContact contact, ContactsPerson person) {
        LshContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact(contact.getId());
        return LshRxUtils.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {
                String name = person.getName();
                List<String> phoneNumbers = person.getPhoneNumbers();
                String birthday = person.getBirthday();
                String lunarBirthday = person.getLunarBirthday();
                String avatar = person.getAvatar();
                String avatarThumb = person.getAvatarThumb();

                contactBuilder.deleteDisplayName();
                contactBuilder.deleteAllPhoneNumbers();
                contactBuilder.deleteBirthday();
                contactBuilder.deleteLunarBirthday();
                contactBuilder.insertDisplayName(name);
                if (!LshListUtils.isEmpty(phoneNumbers)) {
                    for (String number : phoneNumbers) {
                        contactBuilder.insertPhoneNumber(number);
                    }
                }
                if (LshStringUtils.notEmpty(birthday)) {
                    contactBuilder.insertBirthday(birthday);
                }
                if (LshStringUtils.notEmpty(lunarBirthday)) {
                    if (lunarBirthday.matches(".*([\\u4e00-\\u9fa5]{1,2})月([\\u4e00-\\u9fa5]{1,3})日?")) {
                        lunarBirthday = LshLunarCalendarUtils.lunarStr2NormalStr(lunarBirthday);
                    }
                    contactBuilder.insertLunarBirthday(lunarBirthday);
                }
                if (LshStringUtils.isEmpty(contact.getPhotoUri()) && !LshStringUtils.isAllEmpty(avatar, avatarThumb)) {
                    avatar = LshStringUtils.isEmpty(avatar) ? avatarThumb : avatar;
                    ImageTools.getGlide(avatar)
                            .asBitmap()
                            .listener(new RequestListener<GlideUrl, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                                    e.printStackTrace();
                                    LshToastUtils.show("导入头像失败");
                                    emitter.onNext(e);
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, GlideUrl model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    emitter.onNext(resource);
                                    return true;
                                }
                            }).preload();
                } else {
                    emitter.onNext(true);
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    if (result instanceof Bitmap) {
                        byte[] bytes = LshImageUtils.bitmap2Bytes((Bitmap) result, 1000000);
                        contactBuilder.insertPhoto(bytes);
                    }
                    return contactBuilder.getContactId();
                })
                .flatMap(new Function<Long, Publisher<ShiyiContact>>() {
                    @Override
                    public Publisher<ShiyiContact> apply(Long contactId) throws Exception {
                        return queryContact(contactId);
                    }
                });
    }

    public static Flowable<ShiyiContact> queryContact(long contactId) {
        return LshRxUtils.create(new FlowableOnSubscribe<ShiyiContact>() {
            @Override
            public void subscribe(FlowableEmitter<ShiyiContact> emitter) throws Exception {
                ContactMixer.ShiyiQuery shiyiQuery = new ContactMixer.ShiyiQuery(LshContextUtils.get());
                shiyiQuery.whereEqualTo(Contact.Field.RawContactId, contactId);
                List<ShiyiContact> shiyiContacts = shiyiQuery.find();
                if (!LshListUtils.isEmpty(shiyiContacts)) {
                    emitter.onNext(shiyiContacts.get(0));
                } else {
                    emitter.onError(new CustomThrowable("没有该联系人"));
                }
            }
        });
    }
}
