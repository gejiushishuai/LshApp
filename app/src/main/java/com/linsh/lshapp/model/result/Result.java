package com.linsh.lshapp.model.result;


import com.linsh.utilseverywhere.StringUtils;

/**
 * Created by Senh Linsh on 17/5/4.
 */

public class Result {

    private boolean success;
    private String message;

    public Result() {
        this(true, null);
    }

    public Result(String message) {
        this(false, null);
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(message);
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
