package com.linsh.lshapp.mvp.album;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.bean.db.PersonAlbum;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;

import io.realm.RealmChangeListener;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class AlbumPresenter extends RealmPresenterImpl<AlbumContract.View> implements AlbumContract.Presenter {

    @Override
    protected void attachView() {
        String personId = getView().getPersonId();
        PersonAlbum personAlbum = ShiyiDbHelper.getPersonAlbum(getRealm(), personId);
        personAlbum.addChangeListener((RealmChangeListener<PersonAlbum>) element -> {
            if (element.isValid()) {
                getView().setData(element);
            }
        });

    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
