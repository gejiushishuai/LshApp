package com.linsh.lshapp.task.shiyi;

import com.linsh.lshapp.model.bean.Type;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by linsh on 17/5/7.
 */

public class ShiyiDbUtils {

    public static void renewTypesSort(List<Type> types) {
        for (int i = 0; i < types.size(); i++) {
            types.get(i).setSort(i + 1);
        }
    }

    public static <T extends RealmModel> void sortToRealm(Realm realm, RealmList<T> list, String fiedName) {
        RealmResults<T> sorted = list.sort(fiedName);
        List<T> copySorted = realm.copyFromRealm(sorted);
        list.clear();
        list.addAll(copySorted);
    }
}
