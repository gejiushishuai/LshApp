package com.linsh.lshapp.mvp.home.yingmao;

import com.linsh.lshapp.model.bean.Client;
import com.linsh.lshapp.model.bean.SignIn;
import com.linsh.lshapp.tools.SharedPreferenceTools;
import com.linsh.lshutils.module.SimpleDate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *    author : Senh Linsh
 *    date   : 2017/11/01
 *    desc   :
 * </pre>
 */
public class SignInHelper {

    public static Flowable<List<SignIn>> checkSign(Client[] clients) {
        ArrayList<SignIn> signIns = new ArrayList<>();
        return Flowable.fromArray(clients)
                .subscribeOn(Schedulers.io())
                .flatMap(client -> {
                    long lastSignInTime = SharedPreferenceTools.getClientLastSignInTime(client.name());
                    if (lastSignInTime < 0) {
                        signIns.add(new SignIn(client, -1));
                    } else if (lastSignInTime == 0 || !new SimpleDate(System.currentTimeMillis()).isSameDay(new SimpleDate(lastSignInTime))) {
                        signIns.add(new SignIn(client, 0));
                    } else {
                        signIns.add(new SignIn(client, 1));
                    }
                    return Flowable.just(signIns);
                });
    }

    public static void setState(SignIn signIn, int state) {
        if (signIn.getState() == state) return;
        signIn.setState(state);
        refreshState(signIn);
    }

    public static void refreshState(SignIn signIn) {
        switch (signIn.getState()) {
            case SignIn.STATE_IGNORED:
                SharedPreferenceTools.refreshClientSignedIn(signIn.getClient().name(), -1);
                break;
            case SignIn.STATE_UNSIGNED:
                SharedPreferenceTools.refreshClientSignedIn(signIn.getClient().name(), 0);
                break;
            case SignIn.STATE_SIGNED:
                SharedPreferenceTools.refreshClientSignedIn(signIn.getClient().name());
                break;
        }
    }
}
