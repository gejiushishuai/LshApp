package com.linsh.lshapp.mvp.type_detail;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.linsh.utilseverywhere.module.SimpleDate;
import com.linsh.utilseverywhere.EditTextUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.view.TimePickerDialog;

/**
 * Created by Senh Linsh on 17/9/7.
 */

public class TypeInfoHelper {

    public static TypeInfo getHelper(String title) {
        switch (title) {
            case "电话":
            case "电话号码":
                return new TypeNumber();
            case "生日":
                return new TypeBirthday();
            default:
                return new TypeOther();
        }
    }

    private static class TypeNumber implements TypeInfo {

        @Override
        public void setDisplayMode(EditText etInfo) {
            EditTextUtils.disableEditState(etInfo);
            etInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = etInfo.getText().toString();
                    if (!StringUtils.isEmpty(number) && number.matches("^\\d[\\d\\s-]+\\d$")) {
                        number = number.replaceAll("\\s", "").replaceAll("-", "");
                        Uri uri = Uri.parse("tel:" + number);
                        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                        etInfo.getContext().startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void setEditMode(EditText etInfo) {
            etInfo.setOnClickListener(null);
            etInfo.setInputType(EditorInfo.TYPE_CLASS_PHONE);
            EditTextUtils.enableEditState(etInfo, true);
        }
    }

    private static class TypeBirthday implements TypeInfo {

        @Override
        public void setDisplayMode(EditText etInfo) {
            EditTextUtils.disableEditState(etInfo);
        }

        @Override
        public void setEditMode(EditText etInfo) {
            EditTextUtils.disableEditState(etInfo);
            etInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String dateStr = etInfo.getText().toString();
                    SimpleDate date = null;
                    if (StringUtils.notEmpty(dateStr)) {
                        date = SimpleDate.parseDateString(dateStr);
                    }
                    if (date == null) {
                        date = new SimpleDate(1990, 1, 1);
                    }
                    TimePickerDialog pickerDialog = new TimePickerDialog(etInfo.getContext())
                            .setDate(date)
                            .setOnPositiveClickListener(dialog -> {
                                dialog.dismiss();
                                String title = dialog.getDate().getDisplayString();
                                etInfo.setText(title);
                            });
                    pickerDialog.show();
                }
            });
        }
    }

    public static class TypeOther implements TypeInfo {

        @Override
        public void setDisplayMode(EditText etInfo) {
            EditTextUtils.disableEditState(etInfo);
        }

        @Override
        public void setEditMode(EditText etInfo) {
            EditTextUtils.enableEditState(etInfo, true);
        }
    }

    interface TypeInfo {

        void setDisplayMode(EditText etInfo);

        void setEditMode(EditText etInfo);
    }
}
