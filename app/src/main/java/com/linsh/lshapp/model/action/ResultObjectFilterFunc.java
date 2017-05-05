package com.linsh.lshapp.model.action;

import io.realm.RealmObject;
import rx.functions.Func1;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class ResultObjectFilterFunc<T extends RealmObject> implements Func1<T, Boolean> {

    @Override
    public Boolean call(T t) {
        return t.isLoaded();
    }
}
