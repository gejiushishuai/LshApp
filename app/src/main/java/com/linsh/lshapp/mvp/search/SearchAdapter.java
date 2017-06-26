package com.linsh.lshapp.mvp.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.result.SearchResult;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.lshutils.utils.LshListUtils;

/**
 * Created by Senh Linsh on 17/6/26.
 */

class SearchAdapter extends LshRecyclerViewAdapter<SearchResult, SearchAdapter.MyViewHolder> {


    @Override
    protected int getLayout() {
        return R.layout.item_search;
    }

    @Override
    protected MyViewHolder createViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, SearchResult data, int position) {
        holder.tvName.setText(data.personName);
        holder.tvDesc.setText(LshStringUtils.nullStrToEmpty(data.personDesc));
        if (data.typeDetail != null && data.typeDetail.size() > 0) {
            holder.tvDetail.setVisibility(View.VISIBLE);
            holder.tvDetail.setText(LshListUtils.joint(data.typeDetail, "\r\n"));
        } else {
            holder.tvDetail.setVisibility(View.GONE);
            holder.tvDetail.setText("");
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvDesc;
        private final TextView tvDetail;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_search_name);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_item_search_desc);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_item_search_detail);
        }
    }
}
