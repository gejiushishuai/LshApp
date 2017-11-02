package com.linsh.lshapp.mvp.home.yingmao;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.SignIn;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.LshResourceUtils;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public class YingmaoAdapter extends LshRecyclerViewAdapter<SignIn, YingmaoAdapter.MyViewHolder> {


    @Override
    protected int getLayout() {
        return R.layout.item_yingmao;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, SignIn signIn, int position) {
        holder.tvName.setText(signIn.getClient().getAppName());
        switch (signIn.getState()) {
            case SignIn.STATE_IGNORED:
                holder.tvStatus.setText("已忽略");
                holder.tvStatus.setBackgroundColor(LshResourceUtils.getColor(R.color.color_disable));
                holder.tvStatus.setTag(null);
                break;
            case SignIn.STATE_UNSIGNED:
                holder.tvStatus.setText(signIn.getClient().getAction());
                holder.tvStatus.setBackgroundColor(LshResourceUtils.getColor(R.color.color_theme_yellow));
                holder.tvStatus.setTag(position);
                break;
            case SignIn.STATE_SIGNED:
                holder.tvStatus.setText("已签到");
                holder.tvStatus.setBackgroundColor(LshResourceUtils.getColor(R.color.color_theme_dark_blue));
                holder.tvStatus.setTag(null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_item_yingmao_status:
                Object tag = v.getTag();
                if (tag != null && tag instanceof Integer) {
                    mAdapterListener.onSignInClick((Integer) tag);
                }
                break;
            default:
                super.onClick(v);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvStatus;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_yingmao_name);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_item_yingmao_status);
            tvStatus.setOnClickListener(YingmaoAdapter.this);
        }
    }

    private AdapterListener mAdapterListener;

    public void setAdapterListener(AdapterListener listener) {
        mAdapterListener = listener;
    }

    public interface AdapterListener {
        void onSignInClick(int position);
    }
}
