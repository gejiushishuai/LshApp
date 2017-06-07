package com.linsh.lshapp.mvp.home.shiyi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.Group;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshutils.adapter.LshExpandableRcvAdapter;
import com.linsh.lshutils.adapter.LshViewHolder;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class ShiyiAdapter extends LshExpandableRcvAdapter<Group, Person> {

    @Override
    public List<Person> getSecondData(int position) {
        return getFirstLevelData().get(position).getPersons();
    }

    @Override
    protected RecyclerView.ViewHolder getFirstLevelHolder(ViewGroup parent) {
        return new ShiyiFirstLevelHolder(parent);
    }

    @Override
    protected RecyclerView.ViewHolder getSecondLevelHolder(ViewGroup parent) {
        return new ShiyiSecondLevelHolder(parent);
    }

    @Override
    protected void onBindExpandableViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ShiyiFirstLevelHolder) {
            ShiyiFirstLevelHolder firstLevelHolder = (ShiyiFirstLevelHolder) holder;
            Group group = getFirstLevelData().get(getFirstPosition(position));
            int count = group.getPersons() == null ? 0 : group.getPersons().size();
            firstLevelHolder.tvName.setText(group.getName());
            firstLevelHolder.tvCount.setText(String.valueOf(count));
        } else if (holder instanceof ShiyiSecondLevelHolder) {
            ShiyiSecondLevelHolder secondLevelHolder = (ShiyiSecondLevelHolder) holder;
            Person person = getSecondLevelData().get(getSecondPosition(position));
            secondLevelHolder.tvName.setText(person.getName());
            secondLevelHolder.tvDetail.setText(person.getDescribe());
            ImageTools.setImage(secondLevelHolder.ivAvatar, person.getAvatar());
        }
    }


    private class ShiyiFirstLevelHolder extends LshViewHolder {
        private TextView tvName;
        private TextView tvCount;

        public ShiyiFirstLevelHolder(ViewGroup parent) {
            super(R.layout.item_shiyi_group, parent);
        }

        @Override
        public void initView(View itemView) {
            tvName = (TextView) itemView.findViewById(R.id.tv_item_group_name);
            tvCount = (TextView) itemView.findViewById(R.id.tv_item_group_count);
        }
    }

    private class ShiyiSecondLevelHolder extends LshViewHolder {
        private ImageView ivAvatar;
        private TextView tvName;
        private TextView tvDetail;

        public ShiyiSecondLevelHolder(ViewGroup parent) {
            super(R.layout.item_shiyi_person, parent);
        }

        @Override
        public void initView(View itemView) {
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_item_contacts);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_contacts_name);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_item_contacts_detail);
        }
    }
}
