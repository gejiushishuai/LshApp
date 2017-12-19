package com.linsh.lshapp.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linsh.utilseverywhere.module.SimpleDate;
import com.linsh.utilseverywhere.DateUtils;
import com.linsh.utilseverywhere.LunarCalendarUtils;
import com.linsh.utilseverywhere.UnitConverseUtils;
import com.linsh.lshapp.R;

import java.util.Date;

import cn.carbswang.android.numberpickerview.library.NumberPickerView;

/**
 * Created by Senh Linsh on 17/9/7.
 */

public class TimePickerDialog extends Dialog implements NumberPickerView.OnValueChangeListener, View.OnClickListener {

    private View mView;

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
    private int curYearIndex = DateUtils.getCurYear() - startYear;
    private int curMonthIndex = 0;
    private int curDayIndex = 0;


    public TimePickerDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog);
    }

    public TimePickerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        mView = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_date_picker, null);

        tvTitle = (TextView) mView.findViewById(R.id.tv_dialog_date_picker_title);
        pvYear = (NumberPickerView) mView.findViewById(R.id.pv_dialog_date_picker_year);
        pvMonth = (NumberPickerView) mView.findViewById(R.id.pv_dialog_date_picker_month);
        pvDay = (NumberPickerView) mView.findViewById(R.id.pv_dialog_date_picker_day);
        llType = (LinearLayout) mView.findViewById(R.id.ll_dialog_date_picker_type);
        ivTypeToggle = (ImageView) mView.findViewById(R.id.iv_dialog_date_picker_type_toggle);
        llYear = (LinearLayout) mView.findViewById(R.id.ll_dialog_date_picker_year);
        ivYearToggle = (ImageView) mView.findViewById(R.id.iv_dialog_date_picker_year_toggle);
        tvCancel = (TextView) mView.findViewById(R.id.tv_dialog_date_picker_cancel);
        tvConfirm = (TextView) mView.findViewById(R.id.tv_dialog_date_picker_confirm);

        pvYear.setOnValueChangedListener(this);
        pvMonth.setOnValueChangedListener(this);
        pvDay.setOnValueChangedListener(this);
        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        llType.setOnClickListener(this);
        llYear.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        // 设置 Margin, 在 inflate() 时丢失该属性
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
        int dp10 = UnitConverseUtils.dp2px(10);
        layoutParams.setMargins(dp10, dp10, dp10, dp10);
        // 设置 MatchParent 及在底部弹出窗口
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);

        refreshDatesAndTitle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_dialog_date_picker_type:
                ivTypeToggle.setSelected(!ivTypeToggle.isSelected());
                refreshDatesAndTitle();
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

    private void refreshDatesAndTitle() {
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
            months = LunarCalendarUtils.getLunarMonths(false);
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
            days = LunarCalendarUtils.getLunarDays();
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
        } else if (picker == pvDay) {
            curDayIndex = newVal;
        }
        refreshTitle();
    }

    public void setIsLunar(boolean isLunar) {
        ivTypeToggle.setSelected(isLunar);
    }

    public boolean isLunar() {
        return ivTypeToggle.isSelected();
    }

    public void setHasYear(boolean hasYear) {
        ivYearToggle.setSelected(hasYear);
    }

    public boolean hasYear() {
        return ivYearToggle.isSelected();
    }

    public TimePickerDialog setDate(Date date, boolean hasYear, boolean isLunar) {
        SimpleDate simpleDate = new SimpleDate(date, isLunar);
        if (!hasYear) {
            simpleDate.setYear(0);
        }
        return setDate(simpleDate);
    }

    public TimePickerDialog setDate(SimpleDate date) {
        if (date != null) {
            int year = date.getYear();
            int month = date.getMonth();
            int day = date.getDay();
            if (year > 0) {
                curYearIndex = year - startYear;
            }
            curMonthIndex = month - 1;
            curDayIndex = day - 1;

            setHasYear(year > 0);
            setIsLunar(date.isLunar());
        }
        refreshDatesAndTitle();
        return this;
    }

    public SimpleDate getDate() {
        int year = years.length <= 1 ? 0 : curYearIndex + startYear;
        return new SimpleDate(year, curMonthIndex + 1, curDayIndex + 1, isLunar());
    }

    public TimePickerDialog setOnPositiveClickListener(OnClickListener listener) {
        mOnPositiveClickListener = listener;
        return this;
    }

    public TimePickerDialog setOnNegativeClickListener(OnClickListener listener) {
        mOnNegativeClickListener = listener;
        return this;
    }

    public interface OnClickListener {
        void onClick(TimePickerDialog dialog);
    }
}
