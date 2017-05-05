package com.linsh.lshapp.part.type_detail;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.TypeDetail;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface TypeDetailContract {

    interface View extends BaseContract.BaseView {

        String getTypeDetailId();

        void setData(TypeDetail typeDetail);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void saveTypeDetail(String info, String desc);

        void deleteTypeDetail();

        TypeDetail getTypeDetail();
    }
}
