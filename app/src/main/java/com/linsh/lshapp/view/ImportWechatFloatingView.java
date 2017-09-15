package com.linsh.lshapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.linsh.lshapp.R;
import com.linsh.lshapp.model.bean.db.Group;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.service.Test4Service;
import com.linsh.lshapp.tools.ImportWechatHelper;
import com.linsh.lshutils.tools.LshXmlCreater;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshBackgroundUtils;
import com.linsh.lshutils.utils.LshScreenUtils;
import com.linsh.lshutils.utils.LshUnitConverseUtils;

import java.util.List;

public class ImportWechatFloatingView extends FrameLayout {
    private final Context mContext;
    private TextView mTvName;
    private TextView mTvWechatId;

    private RecyclerView mRcvPersons;
    private final ImportWechatHelper mHelper;
    private ImportTypeAdapter mAdapter;
    private TextView mTvSave;
    private FlexboxLayout mFlTypes;

    public ImportWechatFloatingView(Context paramContext) {
        super(paramContext);
        this.mContext = paramContext;
        initView();
        mHelper = new ImportWechatHelper();
    }

    private void initView() {
        inflate(this.mContext, R.layout.view_import_wechat_floating, this);

        mTvWechatId = (TextView) findViewById(R.id.tv_import_wechat_id);
        mTvName = (TextView) findViewById(R.id.tv_import_wechat_name);
        mRcvPersons = (RecyclerView) findViewById(R.id.rcv_import_wechat_list);
        mTvSave = (TextView) findViewById(R.id.tv_import_wechat_save);
        mFlTypes = (FlexboxLayout) findViewById(R.id.fl_import_wechat_flex);

        mRcvPersons.setLayoutManager(new LinearLayoutManager(mRcvPersons.getContext()));
        mAdapter = new ImportTypeAdapter();
        mRcvPersons.setAdapter(mAdapter);
        mRcvPersons.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        // 关闭按钮 点击事件
        View ivClose = findViewById(R.id.iv_import_wechat_close);
        ivClose.setOnClickListener(view -> {
            Toast.makeText(getContext(), "关闭悬浮框", Toast.LENGTH_SHORT).show();
            getContext().startService(new Intent(getContext(), Test4Service.class)
                    .putExtra(Test4Service.COMMAND, Test4Service.COMMAND_CLOSE));
        });
        // 保存按钮 点击事件
        mTvSave.setOnClickListener(view -> {
            String text = mTvSave.getText().toString();
            switch (text) {
                case "查找":
                    mHelper.findPersons(mTvName.getText().toString());
                    break;
                case "返回":
                    mAdapter.setState(mAdapter.mState - 1);
                    break;
                case "收起":
                    mAdapter.setState(0);
                    mTvSave.setText("查找");
                    break;
                case "保存":
                    LshToastUtils.show("保存");
                    break;
            }
        });
        LshBackgroundUtils.addPressedEffect(ivClose, mTvSave);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHelper.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHelper.detachView();
    }

    public void setPersons(List<Person> persons) {
        mAdapter.setPersons(persons);
    }

    public void setTypes(String name, List<Test4Service.Type> types) {
        if (types == null || types.size() == 0) {
            mTvName.setText("---");
            mTvSave.setText("查找");
            mTvSave.setEnabled(false);
            mAdapter.setState(0);
            mTvWechatId.setVisibility(VISIBLE);
            if (mFlTypes.getChildCount() > 1) {
                mFlTypes.removeViews(1, mFlTypes.getChildCount() - 1);
            }
            if (mFlTypes.getLayoutParams().width > 0) {
                mFlTypes.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } else {
            mTvName.setText(name);
            mTvSave.setEnabled(true);
            mTvSave.setText("查找");
            mTvWechatId.setVisibility(GONE);

            for (Test4Service.Type type : types) {
                TextView textView = (TextView) View.inflate(getContext(), R.layout.view_type, null);
                textView.setText(type.value);
                LshBackgroundUtils.addPressedEffect(textView);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!type.need) {
                            v.setSelected(!v.isSelected());
                        }
                    }
                });
                textView.setSelected(type.need);
                mFlTypes.addView(textView);
            }
            mFlTypes.measure(0, 0);
            int measuredWidth = mFlTypes.getMeasuredWidth();
            if (measuredWidth > LshScreenUtils.getScreenWidth() / 2) {
                mFlTypes.getLayoutParams().width = LshScreenUtils.getScreenWidth() / 2;
            }
        }
    }

    public void setGroups(List<Group> groups) {
        mAdapter.setGroups(groups);
    }

    private class ImportTypeAdapter extends RecyclerView.Adapter implements OnClickListener {

        private List<Person> mPersons;
        private List<Group> mGroups;

        private int mState; // 0 1 2 3

        private int curSelectedPos = -1;
        private int curGroupPos = -1;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int dp5 = LshUnitConverseUtils.dp2px(5);
            StateListDrawable selector = LshXmlCreater.createDrawableSelector()
                    .setSelectedColor(0x33333333).getSelector();

            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(dp5, dp5, dp5, dp5);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(selector);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            LshBackgroundUtils.addPressedEffect(textView);

            textView.setOnClickListener(this);
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView itemView = (TextView) holder.itemView;
            switch (mState) {
                case 1:
                    if (position == 0) {
                        itemView.setText("添加该联系人");
                    } else if (position == 1) {
                        itemView.setText("从拾意中选择联系人");
                    } else {
                        itemView.setText(mPersons.get(position - 2).getName());
                    }
                    break;
                case 2:
                    itemView.setText(mGroups.get(position).getName());
                    break;
                case 3:
                    itemView.setText(mGroups.get(curGroupPos).getPersons().get(position).getName());
                    break;
            }
            itemView.setTag(position);
            itemView.setSelected(position == curSelectedPos);
        }

        @Override
        public int getItemCount() {
            switch (mState) {
                case 1:
                    return mPersons == null ? 0 : 2 + mPersons.size();
                case 2:
                    return mGroups == null ? 0 : mGroups.size();
                case 3:
                    return curGroupPos < 0 ? 0 : mGroups.get(curGroupPos).getPersons().size();
            }
            return 0;
        }

        public void setState(int state) {
            if (state == 0 && mState == 0) return;

            mState = state;
            curSelectedPos = -1;
            switch (state) {
                case 0:
                    mPersons = null;
                    mRcvPersons.setVisibility(GONE);
                    break;
                case 1:
                    mTvSave.setText("收起");
                    mTvSave.setEnabled(true);
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                case 2:
                    if (mGroups == null) {
                        mHelper.getGroups();
                    }
                    mTvSave.setText("返回");
                    mTvSave.setEnabled(true);
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                case 3:
                    mTvSave.setText("返回");
                    mTvSave.setEnabled(true);
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
            notifyDataSetChanged();
        }

        public void setPersons(List<Person> persons) {
            mPersons = persons;
            setState(1);
        }

        public void setGroups(List<Group> groups) {
            mGroups = groups;
            setState(2);
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            switch (mState) {
                case 1:
                    if (position == 1) {
                        setState(2);
                        return;
                    }
                    mTvSave.setText(curSelectedPos != position ? "保存" : "收起");
                    break;
                case 2:
                    curGroupPos = position;
                    setState(3);
                    return;
                case 3:
                    mTvSave.setText(curSelectedPos != position ? "保存" : "返回");
                    break;
            }
            if (curSelectedPos != position) {
                View childAt = mRcvPersons.findViewWithTag(curSelectedPos);
                if (childAt != null) {
                    childAt.setSelected(false);
                }
                curSelectedPos = position;
                v.setSelected(true);
            } else {
                curSelectedPos = -1;
                v.setSelected(false);
            }
        }
    }

    private Point startP;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startP = new Point((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                int height = LshUnitConverseUtils.dp2px(28) + LshUnitConverseUtils.sp2px(32);
                if (startP.y > height) {
                    return false;
                }
                if (Math.abs(event.getX() - startP.x) > 10 || Math.abs(event.getY() - startP.y) > 10) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onInterceptTouchEvent(event);
    }
}