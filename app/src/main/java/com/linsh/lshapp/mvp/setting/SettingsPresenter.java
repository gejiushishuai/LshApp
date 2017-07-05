package com.linsh.lshapp.mvp.setting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.action.AsyncConsumer;
import com.linsh.lshapp.model.action.AsyncTransaction;
import com.linsh.lshapp.model.action.DefaultThrowableConsumer;
import com.linsh.lshapp.model.action.EmptyConsumer;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Shiyi;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.bean.db.TypeLabel;
import com.linsh.lshapp.task.db.shiyi.ShiyiDbHelper;
import com.linsh.lshapp.task.network.UrlConnector;
import com.linsh.lshapp.tools.LshFileFactory;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshapp.tools.RealmTool;
import com.linsh.lshapp.tools.SharedPreferenceTools;
import com.linsh.lshapp.tools.VersionChecker;
import com.linsh.lshutils.utils.Basic.LshFileUtils;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshClickUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.FlowableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Senh Linsh on 17/5/2.
 */

public class SettingsPresenter extends RealmPresenterImpl<SettingsContract.View> implements SettingsContract.Presenter {

    @Override
    protected void attachView() {

    }

    @Override
    public void outputDatabase() {
        final File destination = new File(LshFileFactory.getAppDir(), "shiyi.realm");
        LshFileUtils.delete(destination);

        Disposable disposable = LshRxUtils
                .getAsyncFlowable(new AsyncConsumer<Void>() {
                    @Override
                    public void call(Realm realm, FlowableEmitter<? super Void> subscriber) {
                        realm.writeCopyTo(destination);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> {
                    getView().showToast("导出成功");
                });
        addDisposable(disposable);
    }

    @Override
    public void importGson() {
        LshRxUtils.getAsyncTransactionFlowable(getRealm(), new AsyncTransaction<Void>() {
            @Override
            protected void execute(Realm realm, FlowableEmitter<? super Void> emitter) {
                boolean success = false;
                File importDir = new File(LshFileFactory.getJsonImportDir());
                LshFileUtils.makeDirs(importDir);
                if (importDir.exists() && importDir.isDirectory()) {
                    File[] files = importDir.listFiles();
                    if (files != null && files.length > 0) {
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
                                java.lang.reflect.Type typeOfT = new TypeToken<ArrayList<PersonDetail>>() {
                                }.getType();
                                List<PersonDetail> personDetails = new Gson().fromJson(json, typeOfT);
                                // 因为生成导入文件时, PersonDetail 中的 Type 中的 TypeDetail 有很大的几率 id 是一样的,
                                // 会导致 TypeDetail 被重复 id 的覆盖的情况, 所以需要先把 id 给去重复了
                                for (PersonDetail personDetail : personDetails) {
                                    for (Type type : personDetail.getTypes()) {
                                        RealmList<TypeDetail> typeDetails = type.getTypeDetails();
                                        for (int i = 0; i < typeDetails.size(); i++) {
                                            TypeDetail typeDetail = typeDetails.get(i);
                                            // 把末尾的数字去掉, 并拼上序号 i
                                            typeDetail.setId(typeDetail.getId().substring(0, typeDetail.getId().length() - 1) + i);
                                        }
                                    }
                                }
                                realm.copyToRealmOrUpdate(personDetails);
                                success = true;
                            } else if (name.equalsIgnoreCase("TypeLabel.txt") || name.equalsIgnoreCase("TypeLabels.txt")) {
                                String json = LshFileUtils.readFile(file).toString();
                                java.lang.reflect.Type type = new TypeToken<ArrayList<TypeLabel>>() {
                                }.getType();
                                List<TypeLabel> typeLabels = new Gson().fromJson(json, type);
                                realm.copyToRealmOrUpdate(typeLabels);
                                success = true;
                            }
                        }
                    }
                }
                if (!success) {
                    emitter.onError(new RuntimeException("没有可导入的数据"));
                }
            }
        }).subscribe(new EmptyConsumer<>(), new DefaultThrowableConsumer(), () -> getView().showTextDialog("导入成功, 请重启应用以刷新数据"));
    }

    @Override
    public void checkUpdate() {
        Disposable checkUpdateSub = VersionChecker.checkUpdate(getView());
        addDisposable(checkUpdateSub);
    }

    @Override
    public void backupDatabase() {
        if (!RealmTool.checkBackupRealm()) {
            LshToastUtils.showToast("数据库没有发生更改, 无须备份");
            return;
        }
        UrlConnector.uploadRealmData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uploadInfoHttpInfo -> {
                    if (uploadInfoHttpInfo != null && uploadInfoHttpInfo.data != null) {
                        LshToastUtils.showToast("已成功备份至云端");
                        SharedPreferenceTools.refreshLastBackupRealmTime();
                    }
                }, new DefaultThrowableConsumer());
    }

    @Override
    public void outputWordRepo() {
        if (LshClickUtils.isFastDoubleClick()) {
            return;
        }
        long realmModifiedTime = new File(getRealm().getPath()).lastModified();
        File file = LshFileFactory.getOutputWordRepoFile();
        if (file.exists() && realmModifiedTime < file.lastModified()) {
            getView().showToast("数据库没有发生更改, 无须重复导出");
            return;
        }
        ShiyiDbHelper.getGroupsCopy()
                .observeOn(Schedulers.io())
                .map(groups -> {
                    List<String> personNames = new ArrayList<>();
                    for (Group group : groups) {
                        for (Person person : group.getPersons()) {
                            String name = person.getName();
                            if (name.contains("-")) {
                                String[] splits = name.split("-");
                                Collections.addAll(personNames, splits);
                            } else {
                                personNames.add(name);
                            }
                        }
                    }
                    LshFileUtils.writeFile(file.getAbsolutePath(), personNames);
                    return true;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(groups -> {
                    getView().showToast("导出成功");
                }, new DefaultThrowableConsumer());
    }
}
