package com.linsh.lshapp.tools;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by linsh on 17/2/2.
 */

public class LshIdTools {

    /**
     * 通过名字获取拼音Id
     */
    public static String getPinYinId(String name) {
        return getStringPinYin(name);
    }

    //转换一个字符串
    private static String getStringPinYin(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            String tempPinyin = getCharacterPinYin(str.charAt(i));
            if (tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    //转换单个字符
    private static String getCharacterPinYin(char c) {
        String[] pinyin = null;
        try {
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null || pinyin.length == 0) return null;
        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }

    public static String getTimeId() {
        return new SimpleDateFormat("yyMMddHHmmss").format(new Date());
    }
}
