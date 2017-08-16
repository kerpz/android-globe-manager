package com.globe.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

public class PreferenceDate extends DialogPreference {
    private int lastYear = 1970;
    private int lastMonth = 0; // 0 - 11
    private int lastDay = 1;
    
    private DatePicker picker = null;

    public static int getYear(String date) {
        String[] pieces = date.split("-");
        return(Integer.parseInt(pieces[0]));
    }

    public static int getMonth(String date) {
        String[] pieces = date.split("-");
        return(Integer.parseInt(pieces[1]));
    }

    public static int getDay(String date) {
        String[] pieces = date.split("-");
        return(Integer.parseInt(pieces[2]));
    }

    public String getText() {
        return String.format("%04d", lastYear)+"-"+String.format("%02d", lastMonth + 1)+"-"+String.format("%02d", lastDay);
    }

    public PreferenceDate(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        picker = new DatePicker(getContext());
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.updateDate(lastYear, lastMonth, lastDay);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastYear = picker.getYear();
            lastMonth = picker.getMonth();
            lastDay = picker.getDayOfMonth();

            String date = String.valueOf(lastYear) + "-"
                    + String.valueOf(lastMonth) + "-"
                    + String.valueOf(lastDay);

            if (callChangeListener(date)) {
                persistString(date);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String date = null;

        if (restoreValue) {
            if (defaultValue == null) {
                date = getPersistedString("1970-01-01");
            }
            else {
                date = getPersistedString(defaultValue.toString());
            }
        }
        else {
            date = defaultValue.toString();
        }

        lastYear = getYear(date);
        lastMonth = getMonth(date);
        lastDay = getDay(date);
    }
}