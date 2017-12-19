package com.linsh.lshapp.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.linsh.TextViewHelper;
import com.linsh.lshapp.R;
import com.linsh.utilseverywhere.ResourceUtils;
import com.linsh.utilseverywhere.UnitConverseUtils;
import com.linsh.views.preference.TextPreference;

/**
 * <pre>
 *    author : Senh Linsh
 *    github : https://github.com/SenhLinsh
 *    date   : 2017/12/15
 *    desc   :
 * </pre>
 */
public class EditTextPreference extends TextPreference {

    public EditTextPreference(Context context) {
        super(context);
    }

    public EditTextPreference(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected TextViewHelper initTitleHelper() {
        TextViewHelper textViewHelper = super.initTitleHelper();
        textViewHelper.setTextSize(UnitConverseUtils.sp2px(18));
        textViewHelper.setTextColor(ResourceUtils.getColor(R.color.text_title));
        return textViewHelper;
    }

    @Override
    protected TextViewHelper initDetailHelper() {
        TextViewHelper textViewHelper = super.initDetailHelper();
        textViewHelper.setTextSize(UnitConverseUtils.sp2px(15));
        textViewHelper.setTextColor(ResourceUtils.getColor(R.color.text_detail));
        return textViewHelper;
    }
}
