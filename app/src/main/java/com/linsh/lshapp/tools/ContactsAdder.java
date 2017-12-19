package com.linsh.lshapp.tools;

import android.graphics.Bitmap;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.tamir7.contacts.Contact;
import com.linsh.utilseverywhere.tools.ContactsEditor;
import com.linsh.utilseverywhere.BitmapUtils;
import com.linsh.utilseverywhere.ContextUtils;
import com.linsh.utilseverywhere.ListUtils;
import com.linsh.utilseverywhere.LunarCalendarUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.utilseverywhere.ToastUtils;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshapp.mvp.sync_contacts.ContactMixer;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/8/14.
 */

public class ContactsAdder {

    private ContactsEditor mEditor;

    public ContactsAdder() {
        mEditor = new ContactsEditor(ContextUtils.get().getContentResolver());
    }

    public Flowable<ShiyiContact> addContact(ContactsPerson person) {
        ContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact();
        return LshRxUtils.create((FlowableOnSubscribe<Object>) emitter -> {
            String name = person.getName();
            List<String> phoneNumbers = person.getPhoneNumbers();
            String birthday = person.getBirthday();
            String lunarBirthday = person.getLunarBirthday();
            String avatar = person.getAvatar();

            contactBuilder.insertDisplayName(name);
            contactBuilder.insert(ShiyiContact.MIME_TYPE_PERSON_ID, ShiyiContact.COLUMN_PERSON_ID, person.getId());
            if (!ListUtils.isEmpty(phoneNumbers)) {
                for (String number : phoneNumbers) {
                    contactBuilder.insertPhoneNumber(number);
                }
            }
            if (StringUtils.notEmpty(birthday)) {
                contactBuilder.insertBirthday(birthday);
            }
            if (StringUtils.notEmpty(lunarBirthday)) {
                if (lunarBirthday.matches(".*[\\u4e00-\\u9fa5]{1,2}月[\\u4e00-\\u9fa5]{1,3}日?")) {
                    lunarBirthday = LunarCalendarUtils.lunarStr2NormalStr(lunarBirthday);
                }
                contactBuilder.insertLunarBirthday(lunarBirthday);
            }
            if (StringUtils.notEmpty(avatar)) {
                ImageTools.getGlide(avatar)
                        .asBitmap()
                        .listener(new RequestListener<GlideUrl, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                                e.printStackTrace();
                                ToastUtils.show("导入头像失败");
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
                .observeOn(Schedulers.io())
                .map(result -> {
                    if (result instanceof Bitmap) {
                        byte[] bytes = BitmapUtils.toBytes((Bitmap) result, 1000000);
                        contactBuilder.insertPhoto(bytes);
                    }
                    return queryContact(contactBuilder.getContactId());
                });
    }

    public void insertOrUpdatePersonId(ShiyiContact contact, String personId) {
        mEditor.buildContact(contact.getId())
                .delete(ShiyiContact.MIME_TYPE_PERSON_ID)
                .insert(ShiyiContact.MIME_TYPE_PERSON_ID, ShiyiContact.COLUMN_PERSON_ID, personId);
    }

    public Flowable<ShiyiContact> updateContact(ShiyiContact contact, ContactsPerson person) {
        ContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact(contact.getId());
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
                if (!ListUtils.isEmpty(phoneNumbers)) {
                    for (String number : phoneNumbers) {
                        contactBuilder.insertPhoneNumber(number);
                    }
                }
                if (StringUtils.notEmpty(birthday)) {

                    contactBuilder.insertBirthday(birthday);
                }
                if (StringUtils.notEmpty(lunarBirthday)) {
                    if (lunarBirthday.matches(".*([\\u4e00-\\u9fa5]{1,2})月([\\u4e00-\\u9fa5]{1,3})日?")) {
                        lunarBirthday = LunarCalendarUtils.lunarStr2NormalStr(lunarBirthday);
                    }
                    contactBuilder.insertLunarBirthday(lunarBirthday);
                }
                if (StringUtils.isEmpty(contact.getPhotoUri()) && !StringUtils.isAllEmpty(avatar, avatarThumb)) {
                    avatar = StringUtils.isEmpty(avatar) ? avatarThumb : avatar;
                    ImageTools.getGlide(avatar)
                            .asBitmap()
                            .listener(new RequestListener<GlideUrl, Bitmap>() {
                                @Override
                                public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                                    e.printStackTrace();
                                    ToastUtils.show("导入头像失败");
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
                .observeOn(Schedulers.io())
                .map(result -> {
                    if (result instanceof Bitmap) {
                        byte[] bytes = BitmapUtils.toBytes((Bitmap) result, 1000000);
                        contactBuilder.insertPhoto(bytes);
                    }
                    return queryContact(contactBuilder.getContactId());
                });
    }

    public static ShiyiContact queryContact(long contactId) {
        ContactMixer.ShiyiQuery shiyiQuery = new ContactMixer.ShiyiQuery(ContextUtils.get());
        shiyiQuery.whereEqualTo(Contact.Field.RawContactId, contactId);
        List<ShiyiContact> shiyiContacts = shiyiQuery.find();
        if (!ListUtils.isEmpty(shiyiContacts)) {
            return shiyiContacts.get(0);
        }
        return null;
    }

    public Flowable<ShiyiContact> updatePhoto(ShiyiContact contact, String avatar) {
        ContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact(contact.getId());
        return LshRxUtils.create(new FlowableOnSubscribe<Object>() {

            @Override
            public void subscribe(FlowableEmitter<Object> emitter) throws Exception {
                ImageTools.getGlide(avatar)
                        .asBitmap()
                        .listener(new RequestListener<GlideUrl, Bitmap>() {
                            @Override
                            public boolean onException(Exception e, GlideUrl model, Target<Bitmap> target, boolean isFirstResource) {
                                e.printStackTrace();
                                ToastUtils.show("更新头像失败");
                                emitter.onNext(e);
                                emitter.onComplete();
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, GlideUrl model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                emitter.onNext(resource);
                                emitter.onComplete();
                                return true;
                            }
                        }).preload();
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(result -> {
                    if (result instanceof Bitmap) {
                        byte[] bytes = BitmapUtils.toBytes((Bitmap) result, 1000000);
                        contactBuilder.insertPhoto(bytes);
                        return queryContact(contactBuilder.getContactId());
                    }
                    return contact;
                });
    }
}
