package com.linsh.lshapp.mvp.import_contacts;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.tamir7.contacts.Address;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Event;
import com.github.tamir7.contacts.PhoneNumber;
import com.linsh.lshapp.R;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshListUtils;

import java.util.List;

/**
 * Created by Senh Linsh on 17/6/19.
 */

class ImportContactsAdapter extends LshRecyclerViewAdapter<Contact, ImportContactsAdapter.MyViewHolder> implements View.OnClickListener {

    @Override
    protected int getLayout() {
        return R.layout.item_import_contacts;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, Contact data, int position) {
        String name = data.getDisplayName();
        List<PhoneNumber> phoneNumbers = data.getPhoneNumbers();
        String number = LshListUtils.joint(LshListUtils.getStringList(phoneNumbers,
                PhoneNumber::getNormalizedNumber), " & ");

        holder.tvName.setText(name);
        if (LshStringUtils.notEmpty(number)) {
            holder.tvNumber.setText("电话: " + number);
        } else {
            holder.tvNumber.setText("");
        }
        holder.rlDetailLayout.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(this);
        holder.tvAdd.setOnClickListener(this);
        holder.itemView.setTag(holder);
        holder.tvAdd.setTag(holder);
    }

    @Override
    public void onClick(View v) {
        MyViewHolder holder = (MyViewHolder) v.getTag();
        if (v.getId() == R.id.tv_item_import_contacts_add) {
            if (mOnImportContactsListener != null) {
                mOnImportContactsListener.onAddContact(getData().get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        } else {
            boolean visible = holder.rlDetailLayout.getVisibility() == View.VISIBLE;
            if (visible) {
                holder.rlDetailLayout.setVisibility(View.GONE);
                holder.tvDetailText.setText("");
            } else {
                String detail = getDetail(getData().get(holder.getAdapterPosition()));
                if (!LshStringUtils.isEmpty(detail)) {
                    holder.rlDetailLayout.setVisibility(View.VISIBLE);
                    holder.tvDetailText.setText(detail);
                }
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
        void onAddContact(Contact contact, int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvNumber;
        private final TextView tvAdd;
        private final TextView tvDetailText;
        private final RelativeLayout rlDetailLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_name);
            tvNumber = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_number);
            tvAdd = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_add);
            tvDetailText = (TextView) itemView.findViewById(R.id.tv_item_import_contacts_detail_text);
            rlDetailLayout = (RelativeLayout) itemView.findViewById(R.id.rl_item_import_contacts_detail);
        }
    }
}
