package com.linsh.lshapp.model.bean.db.huhu;

import java.util.Calendar;
import java.util.TimeZone;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/06
 *    desc   :
 * </pre>
 */
public class Task extends RealmObject {

    @PrimaryKey
    private long id;
    // 标题
    private String title;
    // 频率
    private int frequency;
    // 频率单位: 年月日时周 // 暂无: 分秒 旬
    private String unit;
    // 开始时间
    private long startTime;
    // 上次打卡时间
    private long lastUpdateTime;
    // 计次
    private int progress;
    // 当前状态
    @Ignore
    private String status;

    @Deprecated
    public Task() {
    }

    public Task(long id, String title, int frequency, char unit) {
        this(id, title, frequency, unit, System.currentTimeMillis());
    }

    public Task(long id, String title, int frequency, char unit, long startTime) {
        this(id, title, frequency, unit, startTime, 0, 0);
    }

    public Task(long id, String title, int frequency, char unit, long startTime, long lastUpdateTime, int progress) {
        this.id = id;
        this.title = title;
        this.frequency = frequency;
        this.unit = String.valueOf(unit);
        this.startTime = startTime;
        if (lastUpdateTime == 0) {
            lastUpdateTime = adjustTime(startTime, this.unit).getTimeInMillis();
        }
        this.lastUpdateTime = lastUpdateTime;
        this.progress = progress;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFrequency() {
        String result;
        switch (unit) {
            case "日":
                result = frequency + "天";
                break;
            case "时":
                result = frequency + "小时";
                break;
            default:
                result = frequency + unit;
                break;
        }
        return result;
    }

    public void setFrequency(int frequency, char unit) {
        this.frequency = frequency;
        this.unit = String.valueOf(unit);
    }

    public String getStatus() {
        if (status == null) {
            refreshStatus();
        }
        return status;
    }

    public void finishTaskOneTime() {
        progress++;
    }

    public void finishTask(int times) {
        progress += times;
    }

    /**
     * 更新数据
     */
    public boolean update() {
        if ("年".equals(unit)) {
            Calendar calendar = adjustTime(unit);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis != lastUpdateTime) {
                int curYear = calendar.get(Calendar.YEAR);
                calendar.setTimeInMillis(lastUpdateTime);
                int lastYear = calendar.get(Calendar.YEAR);
                progress -= curYear - lastYear;
                lastUpdateTime = timeInMillis;
                return true;
            }
        } else if ("月".equals(unit)) {
            Calendar calendar = adjustTime(unit);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis != lastUpdateTime) {
                int curYear = calendar.get(Calendar.YEAR);
                int curMonth = calendar.get(Calendar.MONTH);
                calendar.setTimeInMillis(lastUpdateTime);
                int lastYear = calendar.get(Calendar.YEAR);
                int lastMonth = calendar.get(Calendar.MONTH);
                progress -= (curYear - lastYear) * 12 + curMonth - lastMonth;
                lastUpdateTime = timeInMillis;
                return true;
            }
        } else if ("周".equals(unit)) {
            Calendar calendar = adjustTime(unit);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis != lastUpdateTime) {
                long diff = timeInMillis - lastUpdateTime;
                int weekDiff = (int) (diff / (1000L * 60 * 60 * 24 * 7));
                progress -= weekDiff;
                lastUpdateTime = timeInMillis;
                return true;
            }
        } else if ("日".equals(unit)) {
            Calendar calendar = adjustTime(unit);
            long timeInMillis = calendar.getTimeInMillis();
            if (timeInMillis != lastUpdateTime) {
                long diff = timeInMillis - lastUpdateTime;
                int dayDiff = (int) (diff / (1000L * 60 * 60 * 24));
                progress -= dayDiff;
                lastUpdateTime = timeInMillis;
                return true;
            }
        } else if ("时".equals(unit)) {
            long timeInMillis = System.currentTimeMillis();
            long diff = timeInMillis - lastUpdateTime;
            long hourDiff = (int) (diff / (1000L * 60 * 60));
            if (hourDiff > 0) {
                progress -= hourDiff;
                lastUpdateTime = timeInMillis;
                return true;
            }
        }
        return false;
    }

    private static Calendar adjustTime(String unit) {
        return adjustTime(System.currentTimeMillis(), unit);
    }

    /**
     * 根据时间单位进行校准 Calendar
     */
    private static Calendar adjustTime(long time, String unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        switch (unit) {
            case "年":
                calendar.set(Calendar.MONTH, 0);
            case "月":
                calendar.set(Calendar.DAY_OF_MONTH, 0);
            case "周":
                calendar.set(Calendar.DAY_OF_WEEK, 0);
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
            case "日":
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
        }
        return calendar;
    }

    public void refreshStatus() {
        if (progress == 0) {
            status = "完成度刚刚好哦";
        } else if (progress < 0) {
            status = "缺打" + Math.abs(progress) + "次";
        } else {
            status = "超前" + progress + "次";
        }
    }
}
