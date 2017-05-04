package com.linsh.lshapp.model.result;

import com.linsh.lshutils.utils.Basic.LshStringUtils;

/**
 * Created by Senh Linsh on 17/5/4.
 */

public class Result {

    private String message;

    public Result() {
    }

    public Result(String message) {
        this.message = message;
    }

    public boolean isEmpty() {
        return LshStringUtils.isEmpty(message);
    }

    public String getMessage() {
        return message;
    }
}
