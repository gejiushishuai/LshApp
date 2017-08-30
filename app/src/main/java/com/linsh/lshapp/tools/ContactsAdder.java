package com.linsh.lshapp.tools;

import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshutils.others.BitmapUtil;
import com.linsh.lshutils.tools.LshContactsEditor;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshContextUtils;
import com.linsh.lshutils.utils.LshImageUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.lshutils.utils.LshLunarCalendarUtils;

import java.util.List;

/**
 * Created by Senh Linsh on 17/8/14.
 */

public class ContactsAdder {

    private LshContactsEditor mEditor;

    public ContactsAdder() {
        mEditor = new LshContactsEditor(LshContextUtils.get().getContentResolver());
    }

    public void addContact(ContactsPerson person) {
        String name = person.getName();
        List<String> phoneNumbers = person.getPhoneNumbers();
        String birthday = person.getBirthday();
        String lunarBirthday = person.getLunarBirthday();
        String avatar = person.getAvatar();

        LshContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact();
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
                    .into(new BaseTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            byte[] bytes = BitmapUtil.getBytesFromBitmap(BitmapUtil.getBitmapFromDrawable(resource));
                            contactBuilder.insertPhoto(bytes);
                        }

                        @Override
                        public void getSize(SizeReadyCallback cb) {
                        }
                    });
        }
    }

    public void insertOrUpdatePersonId(ShiyiContact contact, String personId) {
        mEditor.buildContact(contact.getId())
                .delete(ShiyiContact.MIME_TYPE_PERSON_ID)
                .insert(ShiyiContact.MIME_TYPE_PERSON_ID, ShiyiContact.COLUMN_PERSON_ID, personId);
    }

    public void updateContact(ShiyiContact contact, ContactsPerson person) {
        String name = person.getName();
        List<String> phoneNumbers = person.getPhoneNumbers();
        String birthday = person.getBirthday();
        String lunarBirthday = person.getLunarBirthday();
        String avatar = person.getAvatar();
        String avatarThumb = person.getAvatarThumb();

        LshContactsEditor.ContactBuilder contactBuilder = mEditor.buildContact(contact.getId());
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
        if (!LshStringUtils.isAllEmpty(avatar, avatarThumb)) {
            avatar = LshStringUtils.isEmpty(avatar) ? avatarThumb : avatar;
            ImageTools.getGlide(avatar)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            byte[] bytes = LshImageUtils.bitmap2Bytes(resource, 1000000);
                            mEditor.buildContact(contact.getId())
                                    .deletePhoto()
                                    .insertPhoto(bytes);
                            Log.i("LshLog", "insertPhoto: ");
                        }
                    });
        }
    }
}
