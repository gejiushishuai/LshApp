package com.linsh.lshapp.part.detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshutils.adapter.LshNestedDataRcvAdapter;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailAdapter extends LshNestedDataRcvAdapter<Type, PersonDetailAdapter.MyViewHolder> {

    @Override
    protected int getLayout() {
        return R.layout.item_person_detail;
    }

    @Override
    protected MyViewHolder getViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, Type firstLevelData, int firstLevelPosition, int secondLevelPosition) {
        TypeDetail typeDetail = firstLevelData.getTypeDetails().get(secondLevelPosition);

        boolean hideName = firstLevelPosition != 0 && firstLevelData.equals(getData().get(firstLevelPosition).getName());
        holder.tvName.setText(firstLevelData.getName());
        holder.tvName.setVisibility(hideName ? View.GONE : View.VISIBLE);
        holder.tvInfo.setText(typeDetail.getDetail());
        holder.tvDetail.setText(typeDetail.getDescribe());
    }

    @Override
    protected int getSecondLevelDataSize(int firstLevelPosition) {
        return getData().get(firstLevelPosition).getTypeDetails().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final LinearLayout llLayout;
        private final TextView tvInfo;
        private final TextView tvDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_name);
            llLayout = (LinearLayout) itemView.findViewById(R.id.ll_item_person_detail_type_layout);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_info);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_detail);
        }
    }
}
