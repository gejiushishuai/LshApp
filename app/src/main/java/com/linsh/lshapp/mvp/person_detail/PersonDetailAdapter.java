package com.linsh.lshapp.mvp.person_detail;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshutils.adapter.LshNestedDataRcvAdapter;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshResourceUtils;
import com.linsh.lshutils.utils.LshUnitConverseUtils;

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
        return new MyViewHolder(view) {

            @Override
            public boolean onLongClick(View v) {
                int adapterPosition = getAdapterPosition();
                int firstLevelPosition = getFirstLevelPosition(adapterPosition);
                int secondLevelPosition = getSecondLevelPosition(adapterPosition);
                mOnItemLongClickListener.onItemLongClick(v, getData().get(firstLevelPosition), firstLevelPosition, secondLevelPosition);
                return true;
            }

            @Override
            public void onClick(View v) {
                int adapterPosition = getAdapterPosition();
                int firstLevelPosition = getFirstLevelPosition(adapterPosition);
                int secondLevelPosition = getSecondLevelPosition(adapterPosition);
                mOnItemClickListener.onItemClick(getData().get(firstLevelPosition), firstLevelPosition, secondLevelPosition);
            }
        };
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, Type type, int firstLevelPosition, int secondLevelPosition) {
        TypeDetail typeDetail = type.getTypeDetails().get(secondLevelPosition);


        boolean hideName = secondLevelPosition != 0;
        holder.tvName.setText(type.getName());
        holder.tvName.setVisibility(hideName ? View.INVISIBLE : View.VISIBLE);
        int leftMargin = 0;
        if (hideName) leftMargin = LshUnitConverseUtils.dp2px(100);
        ((LinearLayout.LayoutParams) holder.vDivider.getLayoutParams()).setMargins(leftMargin, 0, 0, 0);

        String detail = typeDetail.getDetail();
        boolean empty = LshStringUtils.isEmpty(detail);
        holder.tvInfo.setText(empty ? "未填写" : detail);
        holder.tvInfo.setTextColor(LshResourceUtils.getColor(empty ? R.color.color_text_disabled : R.color.text_title));

        holder.tvDetail.setText(typeDetail.getDescribe());
    }

    @Override
    protected int getSecondLevelDataSize(int firstLevelPosition) {
        return getData().get(firstLevelPosition).getTypeDetails().size();
    }

    private OnItemClickListener<Type> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<Type> listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(T data, int firstLevelPosition, int secondLevelPosition);
    }

    private OnItemLongClickListener<Type> mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener<Type> listener) {
        mOnItemLongClickListener = listener;
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View view, T data, int firstLevelPosition, int secondLevelPosition);
    }

    public abstract class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final TextView tvName;
        private final TextView tvInfo;
        private final TextView tvDetail;
        private final View llTypeLayout;
        private final View vDivider;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_name);
            tvInfo = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_info);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_item_person_detail_type_detail);
            llTypeLayout = itemView.findViewById(R.id.ll_item_person_detail_type_layout);
            vDivider = itemView.findViewById(R.id.v_item_person_detail_divider);

            llTypeLayout.setOnClickListener(this);
            llTypeLayout.setOnLongClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public abstract boolean onLongClick(View v);

        @Override
        public abstract void onClick(View v);
    }
}
