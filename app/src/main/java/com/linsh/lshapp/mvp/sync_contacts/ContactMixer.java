package com.linsh.lshapp.mvp.sync_contacts;

import android.content.Context;
import android.database.Cursor;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.CustomQuery;
import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshArrayUtils;
import com.linsh.lshutils.utils.LshContextUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.lshutils.utils.LshLunarCalendarUtils;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Senh Linsh on 17/8/21.
 */

public class ContactMixer {

    private ContactsPerson mPerson;
    private ShiyiContact mContact;

    private int status;
    public static final int IMPORT_FROM_CONTACTS = 1;
    public static final int EXPORT_TO_CONTACTS = 2;
    public static final int LINK_TO_CONTACTS = 3;
    public static final int UPDATE_WITH_CONTACTS = 4;
    public static final int FINISH_UPDATE = 5;


    public ContactMixer(ContactsPerson person, ShiyiContact contact) {
        mPerson = person;
        mContact = contact;
        refreshStatus();
    }

    public ContactsPerson getPerson() {
        return mPerson;
    }

    public void setPerson(ContactsPerson person) {
        mPerson = person;
        refreshStatus();
    }

    public void refreshStatus() {
        if (mPerson == null && mContact == null) {
            status = 0;
        } else if (mPerson != null && mContact == null) {
            status = EXPORT_TO_CONTACTS;
        } else if (mPerson == null) {
            status = IMPORT_FROM_CONTACTS;
        } else if (!mPerson.getId().equals(mContact.getPersonId())) {
            status = LINK_TO_CONTACTS;
        } else {
            // 名字 生日
            String birthdayOfPerson = mPerson.getBirthday();
            String birthdayOfContact = mContact.getBirthday() == null ? null : mContact.getBirthday().getStartDate();
            if (!LshStringUtils.isEquals(mPerson.getName(), mContact.getDisplayName())
                    || !LshStringUtils.isEquals(birthdayOfPerson, birthdayOfContact)) {
                status = UPDATE_WITH_CONTACTS;
                return;
            }
            // 农历生日
            String lunarBirthdayOfPerson = LshLunarCalendarUtils.lunarStr2NormalStr(mPerson.getLunarBirthday());
            String lunarBirthdayOfContact = mContact.getLunarBirthday() == null ? null : mContact.getLunarBirthday().getStartDate();
            if (!LshStringUtils.isEquals(lunarBirthdayOfPerson, lunarBirthdayOfContact)) {
                status = UPDATE_WITH_CONTACTS;
                return;
            }
            // 电话
            List<String> phoneNumbersOfPerson = mPerson.getPhoneNumbers();
            List<PhoneNumber> phoneNumbersOfContact = mContact.getPhoneNumbers();
            boolean emptyOfPerson = LshListUtils.isEmpty(phoneNumbersOfPerson);
            boolean emptyOfContact = LshListUtils.isEmpty(phoneNumbersOfContact);
            if (emptyOfPerson != emptyOfContact) {
                status = UPDATE_WITH_CONTACTS;
                return;
            }
            if (!emptyOfPerson) {
                if (phoneNumbersOfPerson.size() != phoneNumbersOfContact.size()) {
                    status = UPDATE_WITH_CONTACTS;
                    return;
                }
                outside:
                for (String phoneNumberOfPerson : phoneNumbersOfPerson) {
                    for (PhoneNumber phoneNumberOfContact : phoneNumbersOfContact) {
                        if (phoneNumberOfPerson.equals(phoneNumberOfContact.getNormalizedNumber()))
                            continue outside;
                    }
                    status = UPDATE_WITH_CONTACTS;
                    return;
                }
            }
            // 头像
            String avatarThumb = mPerson.getAvatarThumb();
            String avatar = mPerson.getAvatar();
            String photoUri = mContact.getPhotoUri();
            if (LshStringUtils.isAllEmpty(avatarThumb, avatar) != LshStringUtils.isEmpty(photoUri)) {
                status = UPDATE_WITH_CONTACTS;
                return;
            }
            status = FINISH_UPDATE;
        }
    }

    public ShiyiContact getContact() {
        return mContact;
    }

    public void setContact(ShiyiContact contact) {
        mContact = contact;
        refreshStatus();
    }

    public static TreeMap<String, ContactMixer> mix(List<ContactsPerson> persons, List<ShiyiContact> contacts) {
        TreeMap<String, ContactMixer> map = new TreeMap<>();

        // 先找出 ShiyiContact 中有与 ContactsPerson 的 id 相同的, 添加到 ContactMixer 中, 以 Id 为 key
        for (int i = 0; i < contacts.size(); i++) {
            ShiyiContact contact = contacts.get(i);
            String contactId = contact.getPersonId();
            if (LshStringUtils.notEmpty(contactId)) {
                for (int j = 0; j < persons.size(); j++) {
                    ContactsPerson person = persons.get(j);
                    String personId = person.getId();
                    if (contactId.equals(personId)) {
                        // 两个 personId 相同则放到同一组里去
                        map.put(person.getName(), new ContactMixer(person, contact));
                        // 移除
                        contacts.remove(i);
                        persons.remove(j);
                        i--;
                        j--;
                    }
                }
            }
        }
        // 然后将同步的拾意联系人添加到 ContactMixer 中, 以 名字 为 key
        for (ContactsPerson person : persons) {
            putExitPerson(map, person, 0);
        }
        // 然后将手机联系人添加到 ContactMixer 中, 以 名字 为 key
        for (ShiyiContact contact : contacts) {
            String key = contact.getDisplayName();
            if (map.containsKey(key)) {
                ContactMixer mixer = map.get(key);
                if (mixer.getContact() == null) {
                    mixer.setContact(contact);
                }
            } else {
                map.put(key, new ContactMixer(null, contact));
            }
        }
        return map;
    }

    private static void putExitPerson(TreeMap<String, ContactMixer> map, ContactsPerson person, int index) {
        String key = index == 0 ? person.getName() : person.getName() + index;
        if (map.containsKey(key)) {
            ContactMixer mixer = map.get(key);
            if (mixer.getPerson() == null) {
                mixer.setPerson(person);
            } else {
                putExitPerson(map, person, ++index);
            }
        } else {
            map.put(key, new ContactMixer(person, null));
        }
    }

    public static List<ShiyiContact> getContacts() {
        ShiyiQuery shiyiQuery = new ShiyiQuery(LshContextUtils.get());
        return shiyiQuery.find();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class ShiyiQuery extends CustomQuery<ShiyiContact> {

        public ShiyiQuery(Context context) {
            super(context);
            Contact.Field[] values = Contact.Field.values();
            ShiyiContact.ShiyiField[] values1 = ShiyiContact.ShiyiField.values();
            Contact.AbstractField[] fields = new Contact.AbstractField[values.length + values1.length];
            LshArrayUtils.addArrays(fields, values, values1);
            include(fields);
        }

        @Override
        protected ShiyiContact getCustomContact() {
            return new ShiyiContact();
        }

        @Override
        protected void buildCustomFieldToContact(Cursor cursor, String mimeType, ShiyiContact shiyiContact) {
            if (mimeType.equals(ShiyiContact.MIME_TYPE_PERSON_ID)) {
                int columnIndex = cursor.getColumnIndex(ShiyiContact.COLUMN_PERSON_ID);
                if (columnIndex >= 0) {
                    String personId = cursor.getString(columnIndex);
                    shiyiContact.setPersonId(personId);
                }
            }
        }
    }


}
