package com.linsh.lshapp.task.db;

import com.linsh.lshapp.model.bean.Sortable;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;

/**
 * Created by linsh on 17/5/7.
 */

public class ShiyiDbUtils {

    /**
     * 根据集合顺序, 重新设置sort的排列顺序; 注: sort是从1开始计数
     */
    public static void renewSort(List<? extends Sortable> types) {
        for (int i = 0; i < types.size(); i++) {
            types.get(i).setSort(i + 1);
        }
    }

    /**
     * 根据字段, 重新排序, 并更新到数据库中
     */
    public static <T extends RealmModel> void sortToRealm(Realm realm, RealmList<T> list, String fiedName) {
        RealmResults<T> sorted = list.sort(fiedName);
        List<T> copySorted = realm.copyFromRealm(sorted);
        list.clear();
        list.addAll(copySorted);
    }
}
