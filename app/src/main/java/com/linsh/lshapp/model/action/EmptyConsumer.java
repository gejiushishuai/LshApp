package com.linsh.lshapp.model.action;


import io.reactivex.functions.Consumer;

/**
 * Created by Senh Linsh on 17/7/5.
 */

public class EmptyConsumer<T> implements Consumer<T> {

    @Override
    public void accept(T t) {
    }
}
