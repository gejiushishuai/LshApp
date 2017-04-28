package com.linsh.lshapp.part.detail;

import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailAdapter extends LshRecyclerViewAdapter<Type> {

    public PersonDetailAdapter() {
        super(R.layout.item_shiyi_group, null);
    }

    @Override
    protected void onBindViewHolder(LshViewHolder holder, Type data) {
    }
}
