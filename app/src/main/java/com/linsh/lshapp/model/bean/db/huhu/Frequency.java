package com.linsh.lshapp.model.bean.db.huhu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/15
 *    desc   :
 * </pre>
 */
public class Frequency {

    public int frequency;
    public char unit;

    public Frequency(int frequency, char unit) {
        this.frequency = frequency;
        this.unit = unit;
    }

    public String getFrequency() {
        return "" + frequency + unit;
    }

    @Override
    public String toString() {
        return "" + frequency + unit;
    }

    public static Frequency parse(String frequency) {
        if (frequency == null) return null;
        frequency = frequency.trim();

        Matcher matcher = Pattern.compile("(\\d+)(([年月日时周])|[天]|小时|星期)").matcher(frequency);
        if (matcher.find()) {
            String group1 = matcher.group(1);
            String group3 = matcher.group(3);
            int num = Integer.parseInt(group1);
            if (group3 != null) {
                return new Frequency(num, group3.charAt(0));
            } else {
                String group2 = matcher.group(2);
                char unit;
                switch (group2) {
                    case "天":
                        unit = '日';
                        break;
                    case "小时":
                        unit = '时';
                        break;
                    case "星期":
                        unit = '周';
                        break;
                    default:
                        return null;
                }
                return new Frequency(num, unit);
            }
        }
        return null;
    }

}
