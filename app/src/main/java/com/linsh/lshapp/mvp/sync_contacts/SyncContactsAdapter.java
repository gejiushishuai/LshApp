package com.linsh.lshapp.mvp.sync_contacts;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.ContactsPerson;
import com.linsh.lshapp.model.bean.ShiyiContact;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshLunarCalendarUtils;
import com.linsh.lshutils.utils.LshResourceUtils;

import java.util.List;

/**
 * Created by Senh Linsh on 17/6/19.
 */

class SyncContactsAdapter extends LshRecyclerViewAdapter<ContactMixer, SyncContactsAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    protected int getLayout() {
        return R.layout.item_import_contacts;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, ContactMixer mixer, int position) {
        ShiyiContact contact = mixer.getContact();
        ContactsPerson person = mixer.getPerson();

        // 比较联系人
        int drawableRes = 0;
        String text = null;
        if (mixer.getStatus() == ContactMixer.IMPORT_FROM_CONTACTS) {
            text = "导入";
            drawableRes = R.drawable.ic_sync_import;
            setLeftItemData(contact, null, holder);
            setRightItemData(null, person, holder);
        } else if (mixer.getStatus() == ContactMixer.EXPORT_TO_CONTACTS) {
            text = "导出";
            drawableRes = R.drawable.ic_sync_export;
            setLeftItemData(contact, null, holder);
            setRightItemData(null, person, holder);
        } else if (mixer.getStatus() == ContactMixer.LINK_TO_CONTACTS) {
            text = "关联";
            drawableRes = R.drawable.ic_sync_connect;
            setLeftItemData(contact, null, holder);
            setRightItemData(null, person, holder);
        } else if (mixer.getStatus() == ContactMixer.UPDATE_WITH_CONTACTS) {
            text = "更新";
            drawableRes = R.drawable.ic_sync_refresh;
            setLeftItemData(contact, null, holder);
            setRightItemData(null, person, holder);
        } else if (mixer.getStatus() == ContactMixer.FINISH_UPDATE) {
            text = "已同步";
            drawableRes = R.drawable.ic_sync_done;
            setLeftItemData(contact, person, holder);
            setRightItemData(contact, person, holder);
        }
        if (drawableRes != 0) {
            holder.tvStatus.setText(text);
            Drawable drawable = LshResourceUtils.getDrawable(drawableRes);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.tvStatus.setCompoundDrawables(null, drawable, null, null);
        }

        holder.llDetailLayout.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(this);
        holder.tvStatus.setOnClickListener(this);
        holder.itemView.setTag(holder);
        holder.tvStatus.setTag(position);
    }

    private void setLeftItemData(ShiyiContact contact, ContactsPerson person, MyViewHolder holder) {
        if (contact != null) {
            Uri uri = contact.getPhotoUri() == null ? null : Uri.parse(contact.getPhotoUri());
            setItemData(holder.llLeftContact, holder.tvLeftName, contact.getDisplayName(), holder.ivLeftAvatar, null
                    , uri, holder.tvLeftDetailText, getDetailText(contact, true));
        } else if (person != null) {
            setItemData(holder.llLeftContact, holder.tvLeftName, person.getName(), holder.ivLeftAvatar, person.getAvatarThumb()
                    , null, holder.tvLeftDetailText, getDetailText(person, false));
        } else {
            holder.llLeftContact.setVisibility(View.INVISIBLE);
        }
    }

    private void setRightItemData(ShiyiContact contact, ContactsPerson person, MyViewHolder holder) {
        if (person != null) {
            setItemData(holder.llRightContact, holder.tvRightName, person.getName(), holder.ivRightAvatar, person.getAvatarThumb()
                    , null, holder.tvRightDetailText, getDetailText(person, true));
        } else if (contact != null) {
            Uri uri = contact.getPhotoUri() == null ? null : Uri.parse(contact.getPhotoUri());
            setItemData(holder.llRightContact, holder.tvRightName, contact.getDisplayName(), holder.ivRightAvatar, null
                    , uri, holder.tvRightDetailText, getDetailText(contact, false));
        } else {
            holder.llRightContact.setVisibility(View.INVISIBLE);
        }
    }

    private String getDetailText(ShiyiContact contact, boolean isLeft) {
        List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        String birthday = contact.getBirthday() == null ? null : contact.getBirthday().getStartDate();
        String lunarBirthday = contact.getLunarBirthday() == null ?
                null : LshLunarCalendarUtils.normalStr2LunarStr(contact.getLunarBirthday().getStartDate());

        StringBuilder builder = new StringBuilder();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            appendInfo(builder, "电话", phoneNumber.getNormalizedNumber(), isLeft);
        }
        if (LshStringUtils.notEmpty(birthday)) {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            appendInfo(builder, "生日", birthday, isLeft);
        }
        if (LshStringUtils.notEmpty(lunarBirthday)) {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            appendInfo(builder, "生日", lunarBirthday, isLeft);
        }
        return builder.toString();
    }

    private String getDetailText(ContactsPerson person, boolean isRight) {
        List<String> phoneNumbers = person.getPhoneNumbers();
        String birthday = person.getBirthday();
        String lunarBirthday = person.getLunarBirthday();

        StringBuilder builder = new StringBuilder();
        if (phoneNumbers != null) {
            for (String phoneNumber : phoneNumbers) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                appendInfo(builder, "电话", phoneNumber, !isRight);
            }
        }
        if (LshStringUtils.notEmpty(birthday)) {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            appendInfo(builder, "生日", birthday, !isRight);
        }
        if (LshStringUtils.notEmpty(lunarBirthday)) {
            if (builder.length() != 0) {
                builder.append("\n");
            }
            appendInfo(builder, "生日", lunarBirthday, !isRight);
        }
        return builder.toString();
    }

    private StringBuilder appendInfo(StringBuilder builder, String name, String value, boolean isLeft) {
        if (isLeft) {
            builder.append(value).append(" : ").append(name);
        } else {
            builder.append(name).append(" : ").append(value);
        }
        return builder;
    }

    private void setItemData(View layout, TextView tvName, String name,
                             ImageView ivAvatar, String avatarUrl, Uri avatarUri, TextView tvDetailText, String detail) {
        tvName.setText(name);
        if (LshStringUtils.notEmpty(avatarUrl)) {
            ImageTools.setImage(ivAvatar, avatarUrl);
        } else if (avatarUri != null) {
            ImageTools.setImage(ivAvatar, avatarUri);
        } else {
            ImageTools.setImage(ivAvatar, R.drawable.ic_contact);
        }
        tvDetailText.setText(detail);
        layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_item_import_contacts_status) {
            int position = (int) v.getTag();
            if (mOnImportContactsListener != null) {
                mOnImportContactsListener.onClickStatus(getData().get(position), position);
            }
        } else {
            MyViewHolder holder = (MyViewHolder) v.getTag();
            boolean visible = holder.llDetailLayout.getVisibility() == View.VISIBLE;
            String detail = holder.tvLeftDetailText.getText().toString() + holder.tvRightDetailText.getText().toString();
            if (visible) {
                holder.llDetailLayout.setVisibility(View.GONE);
            } else if (LshStringUtils.notEmpty(detail)) {
                holder.llDetailLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private OnImportContactsListener mOnImportContactsListener;

    public void setOnImportContactsListener(OnImportContactsListener listener) {
        mOnImportContactsListener = listener;
    }

    public interface OnImportContactsListener {
        void onClickStatus(ContactMixer mixer, int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llLeftContact;
        private TextView tvLeftName;
        private ImageView ivLeftAvatar;
        private TextView tvLeftDetailText;
        private LinearLayout llRightContact;
        private ImageView ivRightAvatar;
        private TextView tvRightName;
        private TextView tvRightDetailText;
        private TextView tvStatus;
        private LinearLayout llDetailLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            llLeftContact = (LinearLayout) itemView.findViewById(R.id.ll_item_import_contacts_left_contact);
            tvLeftName = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_left_name);
            ivLeftAvatar = (ImageView) itemView.findViewById(R.id.iv_item_import_contacts_left_avatar);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_status);
            llRightContact = (LinearLayout) itemView.findViewById(R.id.ll_item_import_contacts_right_contact);
            ivRightAvatar = (ImageView) itemView.findViewById(R.id.iv_item_import_contacts_right_avatar);
            tvRightName = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_right_name);
            llDetailLayout = (LinearLayout) itemView.findViewById(R.id.ll_item_import_contacts_detail_layout);
            tvLeftDetailText = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_left_detail_text);
            tvRightDetailText = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_right_detail_text);
        }
    }
}
