package com.linsh.lshapp.mvp.album;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.db.shiyi.PersonAlbum;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface AlbumContract {

    interface View extends BaseContract.BaseView {

        String getPersonId();

        void setData(PersonAlbum album);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

    }
}
