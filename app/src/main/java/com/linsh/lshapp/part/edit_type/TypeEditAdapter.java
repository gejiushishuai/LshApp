package com.linsh.lshapp.part.edit_type;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.Typable;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.lshutils.tools.LshItemDragHelper;

import java.util.Collections;

/**
 * Created by Senh Linsh on 17/5/8.
 */
public class TypeEditAdapter extends LshRecyclerViewAdapter<Typable, TypeEditAdapter.MyViewHolder> implements LshItemDragHelper.IItemDragCallback {

    @Override
    protected int getLayout() {
        return R.layout.item_type_edit;
    }

    @Override
    protected MyViewHolder getViewHolder(View view, int viewType) {
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(MyViewHolder holder, Typable data, int position) {
        holder.tvTypeName.setText(data.getName());
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, int fromPosition, int toPosition) {
        Collections.swap(getData(), fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(int position, int direction) {
        getData().remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, int fromPosition, int toPosition) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTypeName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTypeName = (TextView) itemView.findViewById(R.id.tv_item_type_edit_type_name);
        }
    }
}
