package com.linsh.lshapp.part.edit_type;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.TypeLabel;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface TypeEditContract {

    interface View extends BaseContract.BaseView {

        void setData(List<TypeLabel> typeLabels);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void saveTypes(List<TypeLabel> data);
    }
}
