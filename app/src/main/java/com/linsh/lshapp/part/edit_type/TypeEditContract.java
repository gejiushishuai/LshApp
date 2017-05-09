package com.linsh.lshapp.part.edit_type;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.bean.Typable;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface TypeEditContract {

    interface View extends BaseContract.BaseView {

        <T extends Typable> void setData(List<T> typeLabels);

        String getPersonId();

        void deletedTypeFromRealm(boolean isSuccess, int position);
    }

    interface Presenter<T extends Typable> extends BaseContract.BasePresenter<View> {

        void saveTypes(List<T> data);

        void removeType(String typeName, int position);
    }
}
