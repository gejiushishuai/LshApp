package com.linsh.lshapp.model.action;

import io.reactivex.functions.Function;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ResultListFilterFunc<T extends RealmModel> implements Function<RealmResults<T>, Boolean> {

    @Override
    public Boolean apply(RealmResults<T> results) {
        return results.isLoaded();
    }
}
