package com.linsh.lshapp.model.event;

import com.linsh.lshapp.model.bean.SignIn;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/11/02
 *    desc   :
 * </pre>
 */
public class SignInEvent {

    private String Client;
    private int state;

    public SignInEvent(SignIn signIn) {
        Client = signIn.getClient().name();
        state = signIn.getState();
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
