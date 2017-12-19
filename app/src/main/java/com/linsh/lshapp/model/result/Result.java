package com.linsh.lshapp.model.result;


import com.linsh.utilseverywhere.StringUtils;

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
        return StringUtils.isEmpty(message);
    }

    public String getMessage() {
        return message;
    }
}
