package com.linsh.lshapp.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshutils.utils.LshDateUtils;
import com.linsh.lshutils.utils.LshLunarCalendarUtils;

import java.util.Calendar;
import java.util.Date;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Senh Linsh on 17/9/7.
 */

public class TimePickerDialog extends Dialog implements NumberPickerView.OnValueChangeListener, View.OnClickListener {

    private TextView tvTitle;
    private LinearLayout llType;
    private ImageView ivTypeToggle;
    private LinearLayout llYear;
    private ImageView ivYearToggle;
    private TextView tvCancel;
    private TextView tvConfirm;
    private NumberPickerView pvYear;
    private NumberPickerView pvMonth;
    private NumberPickerView pvDay;
    private OnClickListener mOnPositiveClickListener;
    private OnClickListener mOnNegativeClickListener;

    private String[] years;
    private String[] months;
    private String[] days;

    private int startYear = 1900;
    private int endYear = 2100;
    private int curYearIndex = LshDateUtils.getCurYear() - startYear;
    private int curMonthIndex = 0;
    private int curDayIndex = 0;

    public TimePickerDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
    }

    public TimePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_date_picker);
        // 设置 MatchParent 及在底部弹出窗口
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        tvTitle = (TextView) findViewById(R.id.tv_dialog_date_picker_title);
        pvYear = (NumberPickerView) findViewById(R.id.pv_dialog_date_picker_year);
        pvMonth = (NumberPickerView) findViewById(R.id.pv_dialog_date_picker_month);
        pvDay = (NumberPickerView) findViewById(R.id.pv_dialog_date_picker_day);
        llType = (LinearLayout) findViewById(R.id.ll_dialog_date_picker_type);
        ivTypeToggle = (ImageView) findViewById(R.id.iv_dialog_date_picker_type_toggle);
        llYear = (LinearLayout) findViewById(R.id.ll_dialog_date_picker_year);
        ivYearToggle = (ImageView) findViewById(R.id.iv_dialog_date_picker_year_toggle);
        tvCancel = (TextView) findViewById(R.id.tv_dialog_date_picker_cancel);
        tvConfirm = (TextView) findViewById(R.id.tv_dialog_date_picker_confirm);

        ivYearToggle.setSelected(true);
        refreshDates();

        pvYear.setOnValueChangedListener(this);
        pvMonth.setOnValueChangedListener(this);
        pvDay.setOnValueChangedListener(this);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        llType.setOnClickListener(this);
        llYear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dialog_date_picker_type:
                ivTypeToggle.setSelected(!ivTypeToggle.isSelected());
                refreshDates();
                break;
            case R.id.ll_dialog_date_picker_year:
                ivYearToggle.setSelected(!ivYearToggle.isSelected());
                setYears(hasYear());
                refreshTitle();
                break;
            case R.id.tv_dialog_date_picker_cancel:
                if (mOnNegativeClickListener != null) {
                    mOnNegativeClickListener.onClick(this);
                } else {
                    dismiss();
                }
                break;
            case R.id.tv_dialog_date_picker_confirm:
                if (mOnPositiveClickListener != null) {
                    mOnPositiveClickListener.onClick(this);
                } else {
                    dismiss();
                }
                break;
        }
    }

    private void refreshTitle() {
        StringBuilder builder = new StringBuilder();
        if (years.length > 1) {
            builder.append(years[curYearIndex]).append("年");
        }
        builder.append(months[curMonthIndex]).append("月")
                .append(days[curDayIndex]);
        if (!ivTypeToggle.isSelected()) {
            builder.append("日");
        }
        tvTitle.setText(builder.toString());
    }

    private void refreshDates() {
        setDates(hasYear(), curMonthIndex + 1, isLunar());
        refreshTitle();
    }

    private void setDates(boolean hasYear, int month, boolean isLunar) {
        setYears(hasYear);
        setMonths(isLunar);
        setDays(month, isLunar);
    }

    private void setYears(boolean hasYear) {
        if (hasYear) {
            if (years == null || years.length == 1) {
                int length = endYear - startYear + 1;
                years = new String[length];
                for (int i = 0; i < length; i++) {
                    years[i] = String.valueOf(startYear + i);
                }
            }
        } else {
            years = new String[]{"----"};
        }
        pvYear.refreshByNewDisplayedValues(years);
        pvYear.setValue(years.length == 1 ? 0 : curYearIndex);
    }

    private void setMonths(boolean isLunar) {
        if (isLunar) {
            months = LshLunarCalendarUtils.getLunarMonths(false);
        } else {
            months = new String[12];
            for (int i = 0; i < 12; i++) {
                months[i] = String.valueOf(i + 1);
            }
        }
        pvMonth.refreshByNewDisplayedValues(months);
        pvMonth.setValue(curMonthIndex);
    }

    private void setDays(int month, boolean isLunar) {
        if (isLunar) {
            days = LshLunarCalendarUtils.getLunarDays();
            pvDay.setHintText("");
        } else {
            int num = 30;
            if (month == 2) {
                num = 29;
            } else if ((month <= 7 && month % 2 == 1) || (month >= 8 && month % 2 == 0)) {
                num = 31;
            }
            days = new String[num];
            for (int i = 0; i < num; i++) {
                days[i] = String.valueOf(i + 1);
            }
            pvDay.setHintText("日");
        }
        pvDay.refreshByNewDisplayedValues(days);
        curDayIndex = curDayIndex > days.length - 1 ? days.length - 1 : curDayIndex;
        pvDay.setValue(curDayIndex);
    }

    @Override
    public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
        if (picker == pvYear) {
            curYearIndex = newVal;
        } else if (picker == pvMonth) {
            curMonthIndex = newVal;
            setDays(curMonthIndex + 1, isLunar());
            refreshTitle();
        } else if (picker == pvDay) {
            curDayIndex = newVal;
        }
        refreshTitle();
    }

    public boolean isLunar() {
        return ivTypeToggle.isSelected();
    }

    public boolean hasYear() {
        return ivYearToggle.isSelected();
    }

    public String getDateAsNormalString() {
        StringBuilder builder = new StringBuilder();
        if (hasYear()) {
            builder.append(years[curYearIndex]).append("-");
        }
        int month = curMonthIndex + 1;
        if (month < 10) {
            builder.append('0');
        }
        builder.append(month).append("-");
        int day = curDayIndex + 1;
        if (day < 10) {
            builder.append('0');
        }
        builder.append(day);
        return builder.toString();
    }

    public String getDateAsTitleString() {
        if (tvTitle != null) {
            return tvTitle.getText().toString();
        }
        return null;
    }

    public int[] getDateAsArray() {
        int year = years.length <= 1 ? 0 : curYearIndex + startYear;
        return new int[]{year, curMonthIndex + 1, curDayIndex + 1};
    }

    public Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, curMonthIndex, curDayIndex + 1);
        return calendar.getTime();
    }

    public void setOnPositiveClickListener(OnClickListener listener) {
        mOnPositiveClickListener = listener;
    }

    public void setOnNegativeClickListener(OnClickListener listener) {
        mOnNegativeClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(DialogInterface dialog);
    }
}
