package com.linsh.lshapp.mvp.home.shiyi;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshutils.adapter.LshExpandableRcvAdapter;
import com.linsh.lshutils.adapter.LshViewHolder;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

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
            ImageTools.loadAvatar(secondLevelHolder.ivAvatar, person.getAvatarThumb(), person.getAvatar());
        }
    }

    @Override
    public void setData(List<Group> firstLevelData) {
        if (firstLevelData == null) {
            super.setData(null);
            return;
        }
        ShiyiDiffCallBack callBack = new ShiyiDiffCallBack(getData(), firstLevelData);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        super.setData(firstLevelData, callBack.newExpandedPosition);
        diffResult.dispatchUpdatesTo(this);
    }

    private class ShiyiDiffCallBack extends DiffUtil.Callback {
        private List<Group> mOldData;
        private List<Group> mNewData;
        private int oldExpandedPosition = -1;
        private int newExpandedPosition = -1;

        public ShiyiDiffCallBack(List<Group> oldData, List<Group> newData) {
            mOldData = oldData;
            mNewData = newData;
            oldExpandedPosition = getExpandedPosition();
            if (mOldData != null && oldExpandedPosition >= 0) {
                Group group = mOldData.get(oldExpandedPosition);
                String id = group.getId();
                for (int i = 0; i < mOldData.size(); i++) {
                    Group forGroup = mOldData.get(i);
                    if (forGroup.getId().equals(id)) {
                        newExpandedPosition = i;
                        setExpandedPosition(newExpandedPosition);
                    }
                }
            }
        }

        @Override
        public int getOldListSize() {
            if (mOldData == null) return 0;

            int firstSize = mOldData.size();
            if (oldExpandedPosition >= 0) {
                return firstSize + mOldData.get(oldExpandedPosition).getPersons().size();
            } else {
                return firstSize;
            }
        }

        @Override
        public int getNewListSize() {
            if (mNewData == null) return 0;

            int firstSize = mNewData.size();
            if (newExpandedPosition >= 0) {
                return firstSize + mNewData.get(newExpandedPosition).getPersons().size();
            } else {
                return firstSize;
            }
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            int oldExpandedCount = oldExpandedPosition >= 0 ? mOldData.get(oldExpandedPosition).getPersons().size() : 0;
            int newExpandedCount = newExpandedPosition >= 0 ? mNewData.get(newExpandedPosition).getPersons().size() : 0;
            int oldGroupPosition = -1;
            int newGroupPosition = -1;
            int oldPersonPosition = -1;
            int newPersonPosition = -1;

            if (oldExpandedPosition < 0) {
                oldGroupPosition = oldItemPosition;
            } else if (oldItemPosition <= oldExpandedPosition) {
                oldGroupPosition = oldItemPosition;
            } else if (oldItemPosition <= oldExpandedPosition + oldExpandedCount) {
                oldPersonPosition = oldItemPosition - oldExpandedPosition - 1;
            } else {
                oldGroupPosition = oldItemPosition - oldExpandedCount;
            }
            if (newExpandedPosition < 0) {
                newGroupPosition = newItemPosition;
            } else if (newItemPosition <= newExpandedPosition) {
                newGroupPosition = newItemPosition;
            } else if (newItemPosition <= newExpandedPosition + newExpandedCount) {
                newPersonPosition = newItemPosition - newExpandedPosition - 1;
            } else {
                newGroupPosition = newItemPosition - newExpandedCount;
            }

            if (oldGroupPosition >= 0 && newGroupPosition >= 0) {
                return mOldData.get(oldGroupPosition).getId().equals(mNewData.get(newGroupPosition).getId());
            } else if (oldPersonPosition >= 0 && newPersonPosition >= 0) {
                return mOldData.get(oldExpandedPosition).getPersons().get(oldPersonPosition).getId()
                        .equals(mNewData.get(newExpandedPosition).getPersons().get(newPersonPosition).getId());
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            int oldExpandedCount = oldExpandedPosition >= 0 ? mOldData.get(oldExpandedPosition).getPersons().size() : 0;
            int newExpandedCount = newExpandedPosition >= 0 ? mNewData.get(newExpandedPosition).getPersons().size() : 0;
            int oldGroupPosition = -1;
            int newGroupPosition = -1;
            int oldPersonPosition = -1;
            int newPersonPosition = -1;

            if (oldExpandedPosition < 0) {
                oldGroupPosition = oldItemPosition;
            } else if (oldItemPosition <= oldExpandedPosition) {
                oldGroupPosition = oldItemPosition;
            } else if (oldItemPosition <= oldExpandedPosition + oldExpandedCount) {
                oldPersonPosition = oldItemPosition - oldExpandedPosition - 1;
            } else {
                oldGroupPosition = oldItemPosition - oldExpandedCount;
            }
            if (newExpandedPosition < 0) {
                newGroupPosition = newItemPosition;
            } else if (newItemPosition <= newExpandedPosition) {
                newGroupPosition = newItemPosition;
            } else if (newItemPosition <= newExpandedPosition + newExpandedCount) {
                newPersonPosition = newItemPosition - newExpandedPosition - 1;
            } else {
                newGroupPosition = newItemPosition - newExpandedCount;
            }

            if (oldGroupPosition >= 0 && newGroupPosition >= 0) {
                Group oldGroup = mOldData.get(oldGroupPosition);
                Group newGroup = mNewData.get(newGroupPosition);
                return oldGroup.getName().equals(newGroup.getName())
                        && oldGroup.getSort() == newGroup.getSort()
                        && oldGroup.getPersons().size() == newGroup.getPersons().size();
            } else if (oldPersonPosition >= 0 && newPersonPosition >= 0) {
                Person oldPerson = mOldData.get(oldExpandedPosition).getPersons().get(oldPersonPosition);
                Person newPerson = mNewData.get(newExpandedPosition).getPersons().get(newPersonPosition);
                return LshStringUtils.isEquals(oldPerson.getName(), newPerson.getName())
                        && LshStringUtils.isEquals(oldPerson.getDescribe(), newPerson.getDescribe())
                        && LshStringUtils.isEquals(oldPerson.getAvatar(), newPerson.getAvatar());
            }
            return false;
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
