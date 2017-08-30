package com.linsh.lshapp.mvp.import_contacts;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Event;
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

class ImportContactsAdapter extends LshRecyclerViewAdapter<ContactMixer, ImportContactsAdapter.MyViewHolder> implements View.OnClickListener {

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

        // 设置左侧手机联系人信息
        if (contact != null) {
            String photoUri = contact.getPhotoUri();
            String name = contact.getDisplayName();
            List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
            String birthday = contact.getBirthday() == null ? null : contact.getBirthday().getStartDate();
            String lunarBirthday = contact.getLunarBirthday() == null ?
                    null : LshLunarCalendarUtils.normalStr2LunarStr(contact.getLunarBirthday().getStartDate());

            if (LshStringUtils.notEmpty(photoUri)) {
                ImageTools.setImage(holder.ivLeftAvatar, Uri.parse(photoUri));
            } else {
                ImageTools.setImage(holder.ivLeftAvatar, R.drawable.ic_contact);
            }
            holder.tvLeftName.setText(name);

            StringBuilder builder = new StringBuilder();
            for (PhoneNumber phoneNumber : phoneNumbers) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append(phoneNumber.getNormalizedNumber()).append(" : 电话");
            }
            if (LshStringUtils.notEmpty(birthday)) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append(birthday).append(" : 生日");
            }
            if (LshStringUtils.notEmpty(lunarBirthday)) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append(lunarBirthday).append(" : 生日");
            }
            holder.tvLeftDetailText.setText(builder.toString());
            holder.llLeftContact.setVisibility(View.VISIBLE);
        } else {
            holder.llLeftContact.setVisibility(View.INVISIBLE);
        }

        // 设置右侧拾意联系人信息
        if (person != null) {
            String name = person.getName();
            String avatarThumb = person.getAvatarThumb();
            List<String> phoneNumbers = person.getPhoneNumbers();
            String birthday = person.getBirthday();
            String lunarBirthday = person.getLunarBirthday();

            holder.tvRightName.setText(name);
            if (LshStringUtils.notEmpty(avatarThumb)) {
                ImageTools.setImage(holder.ivRightAvatar, avatarThumb);
            } else {
                ImageTools.setImage(holder.ivRightAvatar, R.drawable.ic_contact);
            }

            StringBuilder builder = new StringBuilder();
            if (phoneNumbers != null) {
                for (String phoneNumber : phoneNumbers) {
                    if (builder.length() != 0) {
                        builder.append("\n");
                    }
                    builder.append("电话 : ").append(phoneNumber);
                }
            }
            if (LshStringUtils.notEmpty(birthday)) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append("生日 : ").append(birthday);
            }
            if (LshStringUtils.notEmpty(lunarBirthday)) {
                if (builder.length() != 0) {
                    builder.append("\n");
                }
                builder.append("生日 : ").append(lunarBirthday);
            }
            holder.tvRightDetailText.setText(builder.toString());
            holder.llRightContact.setVisibility(View.VISIBLE);
        } else {
            holder.llRightContact.setVisibility(View.INVISIBLE);
        }

        // 比较联系人
        int drawableRes = 0;
        String text = null;
        if (mixer.getStatus() == ContactMixer.IMPORT_FROM_CONTACTS) {
            text = "导入";
            drawableRes = R.drawable.ic_sync_import;
        } else if (mixer.getStatus() == ContactMixer.EXPORT_TO_CONTACTS) {
            text = "导出";
            drawableRes = R.drawable.ic_sync_export;
        } else if (mixer.getStatus() == ContactMixer.LINK_TO_CONTACTS) {
            text = "关联";
            drawableRes = R.drawable.ic_sync_connect;
        } else if (mixer.getStatus() == ContactMixer.UPDATE_WITH_CONTACTS) {
            text = "更新";
            drawableRes = R.drawable.ic_sync_refresh;
        } else if (mixer.getStatus() == ContactMixer.FINISH_UPDATE) {
            text = "已同步";
            drawableRes = R.drawable.ic_sync_done;
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

    private String getDetail(Contact contact) {
        StringBuilder builder = new StringBuilder();
        Event birthday = contact.getBirthday();
        if (birthday != null) {
            builder.append("生日:").append(birthday.getStartDate());
        }
        List<Address> addresses = contact.getAddresses();
        if (addresses != null && addresses.size() > 0) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append("地址:");
            for (Address address : addresses) {
                builder.append(address.getFormattedAddress());
                builder.append(" ");
            }
        }
        String note = contact.getNote();
        if (!LshStringUtils.isEmpty(note)) {
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append("备注:").append(note);
        }
        return builder.toString();
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
