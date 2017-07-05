package com.linsh.lshapp.model.action;


import io.reactivex.functions.Consumer;

/**
 * Created by Senh Linsh on 17/4/28.
 */

public class NothingConsumer<T> implements Consumer<T> {

    @Override
    public void accept(T t) {
    }
}
