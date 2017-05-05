package com.linsh.lshapp.model.action;

import io.realm.RealmModel;
import io.realm.RealmResults;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ResultListFilterFunc<T extends RealmModel> implements Func1<RealmResults<T>, Boolean> {

    @Override
    public Boolean call(RealmResults<T> results) {
        return results.isLoaded();
    }
}
