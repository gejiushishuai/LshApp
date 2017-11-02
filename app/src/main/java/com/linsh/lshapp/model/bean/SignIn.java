package com.linsh.lshapp.model.bean;

import java.io.Serializable;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/11/01
 *    desc   :
 * </pre>
 */
public class SignIn implements Serializable {

    public static final int STATE_IGNORED = -1;
    public static final int STATE_UNSIGNED = 0;
    public static final int STATE_SIGNED = 1;

    private Client client;
    private int state;

    public SignIn() {
    }

    public SignIn(Client client) {
        this.client = client;
    }

    public SignIn(Client client, int state) {
        this.client = client;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Client getClient() {
        return client;
    }
}
