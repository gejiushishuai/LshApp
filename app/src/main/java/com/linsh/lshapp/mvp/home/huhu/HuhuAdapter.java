package com.linsh.lshapp.mvp.home.huhu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.huhu.Task;
import com.linsh.lshutils.adapter.LshHeaderFooterRcvAdapter;
import com.linsh.lshutils.viewholder.BottomFooterViewHolder;
import com.linsh.lshutils.viewholder.EmptyStatusViewHolder;
import com.linsh.lshutils.viewholder.LshViewHolder;
import com.linsh.utilseverywhere.LogUtils;

import java.util.List;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class HuhuAdapter extends LshHeaderFooterRcvAdapter<Task, RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_STATUS = 3;

    public HuhuAdapter() {
        super(false, true);
    }

    @Override
    public int getItemViewType(int position) {
        List<Task> data = getData();
        if (data == null || data.isEmpty()) {
            return VIEW_TYPE_STATUS;
        }
        return super.getItemViewType(position);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_STATUS) {
            return new EmptyStatusViewHolder(parent);
        }
        MyViewHolder viewHolder = new MyViewHolder(parent);
        viewHolder.tvFinish.setOnClickListener(this);
        viewHolder.tvStop.setOnClickListener(this);
        viewHolder.tvDelete.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        MyViewHolder viewHolder = new MyViewHolder(parent);
        ((SwipeLayout) viewHolder.itemView).setSwipeEnabled(false);
        viewHolder.tvTitle.setText("任务名称");
        viewHolder.tvFrequency.setText("频率");
        viewHolder.tvStatus.setText("状态");
        return viewHolder;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent) {
        return new BottomFooterViewHolder(parent);
    }

    @Override
    protected void onBindItemViewHolder(RecyclerView.ViewHolder holder, Task task, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.tvTitle.setText(task.getTitle());
            viewHolder.tvFrequency.setText(task.getFrequency());
            viewHolder.tvStatus.setText(task.getStatus());
            ((ViewGroup) viewHolder.tvFinish.getParent()).setTag(position);
        }
    }

    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
    }

    @Override
    protected void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof BottomFooterViewHolder) {
            ViewGroup parent = (ViewGroup) holder.itemView.getParent();
            if (parent != null) {
                int parentBottom = parent.getBottom();
                int childCount = parent.getChildCount();
                if (childCount > 1) {
                    int lastViewBottom = parent.getChildAt(childCount - 2).getBottom();
                    if (lastViewBottom >= parentBottom) {
                        holder.itemView.setVisibility(View.VISIBLE);
                    } else {
                        holder.itemView.setVisibility(View.GONE);
                    }
                    LogUtils.i("BottomFooter 设置可见: " + (lastViewBottom >= parentBottom));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_item_huhu_finish:
                if (mAdapterListener != null) {
                    Object tag = ((ViewGroup) v.getParent()).getTag();
                    if (tag != null && tag instanceof Integer) {
                        SwipeLayout swipeLayout = (SwipeLayout) v.getParent().getParent();
                        swipeLayout.close();
                        mAdapterListener.onSignInClick((Integer) tag);
                    }
                }
                break;
            case R.id.tv_item_huhu_stop:
                if (mAdapterListener != null) {
                    Object tag = ((ViewGroup) v.getParent()).getTag();
                    if (tag != null && tag instanceof Integer) {
                        SwipeLayout swipeLayout = (SwipeLayout) v.getParent().getParent();
                        swipeLayout.close();
                        mAdapterListener.onStopClick((Integer) tag);
                    }
                }
                break;
            case R.id.tv_item_huhu_delete:
                if (mAdapterListener != null) {
                    Object tag = ((ViewGroup) v.getParent()).getTag();
                    if (tag != null && tag instanceof Integer) {
                        SwipeLayout swipeLayout = (SwipeLayout) v.getParent().getParent();
                        swipeLayout.close();
                        mAdapterListener.onDeleteClick((Integer) tag);
                    }
                }
                break;
            default:
                super.onClick(v);
        }
    }

    @Override
    public void setData(List<Task> data) {
        setHasHeader(data != null && !data.isEmpty());
        super.setData(data);
    }

    private class MyViewHolder extends LshViewHolder {

        private TextView tvFinish;
        private TextView tvStop;
        private TextView tvDelete;
        private TextView tvTitle;
        private TextView tvFrequency;
        private TextView tvStatus;

        public MyViewHolder(ViewGroup parent) {
            super(R.layout.item_huhu, parent);
        }

        @Override
        public void initView(View view) {
            tvFinish = (TextView) view.findViewById(R.id.tv_item_huhu_finish);
            tvStop = (TextView) view.findViewById(R.id.tv_item_huhu_stop);
            tvDelete = (TextView) view.findViewById(R.id.tv_item_huhu_delete);
            tvTitle = (TextView) view.findViewById(R.id.tv_item_huhu_title);
            tvFrequency = (TextView) view.findViewById(R.id.tv_item_huhu_frequency);
            tvStatus = (TextView) view.findViewById(R.id.tv_item_huhu_status);
        }
    }

    private AdapterListener mAdapterListener;

    public void setAdapterListener(AdapterListener listener) {
        mAdapterListener = listener;
    }

    public interface AdapterListener {
        void onSignInClick(int position);

        void onStopClick(int position);

        void onDeleteClick(int position);
    }
}
