package com.linsh.lshapp.part.setting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Shiyi;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshutils.utils.Basic.LshFileUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class SettingsPresenter extends BasePresenterImpl<SettingsContract.View> implements SettingsContract.Presenter {

    @Override
    protected void attachView() {

    }

    @Override
    public void outputDatabase() {
        final File destination = new File(LshFileFactory.getAppDir(), "shiyi.realm");
        LshFileUtils.delete(destination);

        Subscription subscribe = Observable
                .create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        getRealm().executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.writeCopyTo(destination);
                                subscriber.onNext(null);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        getView().showToast("导出成功");
                    }
                }, new DefaultThrowableAction());
        addSubscription(subscribe);
    }

    @Override
    public void importGson() {
        LshRxUtils.getAsyncTransactionObservable(getRealm(), new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, Subscriber<? super Void> subscriber) {
                boolean success = false;
                File importDir = new File(LshFileFactory.getJsonImportDir());
                if (importDir.exists() && importDir.isDirectory()) {
                    File[] files = importDir.listFiles();
                    for (File file : files) {
                        String name = file.getName();

                        if (name.equalsIgnoreCase("Shiyi.txt")) {
                            String json = LshFileUtils.readFile(file).toString();
                            Shiyi shiyi = new Gson().fromJson(json, Shiyi.class);

                            Shiyi realmShiyi = realm.where(Shiyi.class).findFirst();
                            realmShiyi.getGroups().addAll(shiyi.getGroups());
                            success = true;
                        } else if (name.equalsIgnoreCase("PersonDetail.txt") || name.equalsIgnoreCase("PersonDetails.txt")) {
                            String json = LshFileUtils.readFile(file).toString();
                            Type type = new TypeToken<ArrayList<PersonDetail>>() {
                            }.getType();
                            List<PersonDetail> personDetails = new Gson().fromJson(json, type);
                            realm.copyToRealmOrUpdate(personDetails);
                            success = true;
                        } else if (name.equalsIgnoreCase("TypeLabel.txt") || name.equalsIgnoreCase("TypeLabels.txt")) {
                            String json = LshFileUtils.readFile(file).toString();
                            Type type = new TypeToken<ArrayList<TypeLabel>>() {
                            }.getType();
                            List<TypeLabel> typeLabels = new Gson().fromJson(json, type);
                            realm.copyToRealmOrUpdate(typeLabels);
                            success = true;
                        }
                    }
                }
                if (!success) {
                    subscriber.onError(new RuntimeException("没有可导入的数据"));
                }
            }
        }).subscribe(Actions.empty(), new DefaultThrowableAction(), new Action0() {
            @Override
            public void call() {
                getView().showTextDialog("导入成功, 请重启应用以刷新数据");
            }
        });
    }
}
