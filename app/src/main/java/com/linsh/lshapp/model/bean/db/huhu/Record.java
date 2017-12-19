package com.linsh.lshapp.model.bean.db.huhu;

import io.realm.RealmObject;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/06
 *    desc   :
 * </pre>
 */
public class Record extends RealmObject {

    private int id;
    // 状态, 包括: 正常打卡(0), 暂停(1), 开始(2), 频率变更(3)
    private int state;
    // 备注
    private String note;
    private long timestamp;

    public Record() {
    }

    public Record(int id) {
        this.id = id;
        timestamp = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
