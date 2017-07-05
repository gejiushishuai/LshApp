package com.linsh.lshapp.model.action;

import io.reactivex.functions.Function;
import io.realm.RealmObject;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ResultObjectFilterFunc<T extends RealmObject> implements Function<T, Boolean> {

    @Override
    public Boolean apply(T t) {
        return t.isLoaded();
    }
}
