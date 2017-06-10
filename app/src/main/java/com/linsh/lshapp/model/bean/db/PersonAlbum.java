package com.linsh.lshapp.model.bean.db;

import io.realm.RealmList;
import io.realm.RealmModel;

/**
 * Created by Senh Linsh on 17/6/9.
 */

public class PersonAlbum implements RealmModel {

    private String id;
    private RealmList<ImageUrl> pictures;
    private RealmList<ImageUrl> avatars;
}
