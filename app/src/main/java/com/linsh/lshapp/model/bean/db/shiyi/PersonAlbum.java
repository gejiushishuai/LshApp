package com.linsh.lshapp.model.bean.db.shiyi;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Senh Linsh on 17/6/9.
 */

public class PersonAlbum extends RealmObject {
    @PrimaryKey
    private String id;
    private RealmList<ImageUrl> pictures;
    private RealmList<ImageUrl> avatars;

    public PersonAlbum() {
    }

    public PersonAlbum(String personId) {
        this.id = personId;
        pictures = new RealmList<>();
        avatars = new RealmList<>();
    }

    public PersonAlbum(String personId, ImageUrl avatar) {
        this.id = personId;
        pictures = new RealmList<>();
        avatars = new RealmList<>();
        addAvatar(avatar);
    }

    public PersonAlbum(String personId, RealmList<ImageUrl> pictures, RealmList<ImageUrl> avatars) {
        this.id = personId;
        this.pictures = pictures;
        this.avatars = avatars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<ImageUrl> getPictures() {
        return pictures;
    }

    public void addPicture(ImageUrl picture) {
        this.pictures.add(picture);
    }

    public RealmList<ImageUrl> getAvatars() {
        return avatars;
    }

    public void addAvatar(ImageUrl avatar) {
        if (avatar != null) {
            this.avatars.add(avatar);
        }
    }
}
