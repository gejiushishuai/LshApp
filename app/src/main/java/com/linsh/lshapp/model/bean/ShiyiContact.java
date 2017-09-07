package com.linsh.lshapp.model.bean;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Event;

import java.util.List;

/**
 * Created by Senh Linsh on 17/8/21.
 */

public class ShiyiContact extends Contact {

    public static final String MIME_TYPE_PERSON_ID = "vnd.com.lshapp.cursor.item/personId";
    public static final String COLUMN_PERSON_ID = "data1";

    private String personId;
    private String lookupKey;

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public String getLookupKey() {
        return lookupKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    public enum ShiyiField implements AbstractField {

        LshappId(MIME_TYPE_PERSON_ID, COLUMN_PERSON_ID);

        private String mMimeType;
        private String mColumn;

        ShiyiField(String mimeType, String column) {
            mMimeType = mimeType;
            mColumn = column;
        }

        @Override
        public String getMimeType() {
            return mMimeType;
        }

        @Override
        public String getColumn() {
            return mColumn;
        }
    }


    public Event getLunarBirthday() {
        List<Event> events = getEvents();
        for (Event event : events) {
            if (event.getType() == Event.Type.LUNAR_BIRTHDAY) {
                return event;
            }
        }
        return null;
    }
}
