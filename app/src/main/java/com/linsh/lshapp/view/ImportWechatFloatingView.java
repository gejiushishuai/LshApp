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
import com.linsh.lshapp.mvp.person_detail.PersonDetailActivity;
import com.linsh.lshapp.service.ImportAppDataService;
import com.linsh.lshapp.tools.ImportWechatHelper;
import com.linsh.lshutils.tools.LshXmlCreater;
import com.linsh.lshutils.utils.Basic.LshToastUtils;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshBackgroundUtils;
import com.linsh.lshutils.utils.LshIntentUtils;
import com.linsh.lshutils.utils.LshScreenUtils;
import com.linsh.lshutils.utils.LshUnitConverseUtils;

import java.util.List;

import io.reactivex.functions.Action;
import io.realm.Realm;

public class ImportWechatFloatingView extends FrameLayout {
    private final Context mContext;
    private TextView mTvName;
    private TextView mTvWechatId;

    private RecyclerView mRcvPersons;
    private final ImportWechatHelper mHelper;
    private ImportTypeAdapter mAdapter;
    private TextView mTvSave;
    private FlexboxLayout mFlTypes;
    private List<ImportAppDataService.Type> mTypes;
    private Realm mRealm;

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
            getContext().startService(new Intent(getContext(), ImportAppDataService.class)
                    .putExtra(ImportAppDataService.COMMAND, ImportAppDataService.COMMAND_CLOSE));
        });
        // 保存按钮 点击事件
        mTvSave.setOnClickListener(view -> {
            String text = mTvSave.getText().toString();
            switch (text) {
                case "跳转":
                    mAdapter.setState(0);
                    break;
                case "查找":
                    mHelper.findPersons(mTvName.getText().toString());
                    break;
                case "返回":
                    mAdapter.setState(mAdapter.mState - 1);
                    break;
                case "收起":
                    mAdapter.setState(-1);
                    mTvSave.setText("查找");
                    break;
                case "保存":
                    mHelper.savePerson(mRealm, mAdapter.getCurPersonId(), mTvName.getText().toString(), mTypes)
                            .doOnComplete(new Action() {
                                @Override
                                public void run() throws Exception {
                                    mAdapter.setState(-1);
                                    mTvSave.setText("查找");
                                    LshToastUtils.show("已保存");
                                }
                            }).subscribe();
                    break;
            }
        });
        LshBackgroundUtils.addPressedEffect(ivClose, mTvSave);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRealm = Realm.getDefaultInstance();
        mHelper.attachView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mRealm.close();
        mHelper.detachView();
    }

    public void setPersons(List<Person> persons) {
        mAdapter.setPersons(persons);
    }

    public void setTypes(String name, List<ImportAppDataService.Type> types) {
        mTypes = types;
        if (name == null) {
            mTvName.setText("---");
            mAdapter.setState(-1);
            mTvWechatId.setVisibility(VISIBLE);
            if (mFlTypes.getChildCount() > 1) {
                mFlTypes.removeViews(1, mFlTypes.getChildCount() - 1);
            }
            if (mFlTypes.getLayoutParams().width > 0) {
                mFlTypes.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } else {
            mTvName.setText(name);
            mAdapter.setState(-2);
            mTvWechatId.setVisibility(GONE);

            if (mFlTypes.getChildCount() > 1) {
                mFlTypes.removeViews(1, mFlTypes.getChildCount() - 1);
            }
            for (ImportAppDataService.Type type : types) {
                TextView textView = (TextView) View.inflate(getContext(), R.layout.view_type, null);
                textView.setText(type.typeDetail);
                LshBackgroundUtils.addPressedEffect(textView);
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!type.need) {
                            v.setSelected(!v.isSelected());
                            type.selected = v.isSelected();
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

    private class ImportTypeAdapter extends RecyclerView.Adapter implements OnClickListener, OnLongClickListener {

        private List<Person> mPersons;
        private List<Group> mGroups;

        private int mState = -1; // 0 1 2 3

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
            textView.setOnLongClickListener(this);
            return new RecyclerView.ViewHolder(textView) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TextView itemView = (TextView) holder.itemView;
            switch (mState) {
                case 0:
                    String[] texts = new String[]{"跳转至微信", "跳转至 QQ", "跳转至钉钉"};
                    itemView.setText(texts[position]);
                    break;
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
                case 0:
                    return 3;
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
            if (state == -1 || state == 0) {
                if (state == 0 && mState == -1) state = 0;
                else state = -1;
            }
            mState = state;
            curSelectedPos = -1;
            switch (state) {
                case -2:
                    mPersons = null;
                    mTvSave.setText("查找");
                    mRcvPersons.setVisibility(GONE);
                    break;
                case -1:
                    mPersons = null;
                    mTvSave.setText("跳转");
                    mRcvPersons.setVisibility(GONE);
                    break;
                case 0:
                    mPersons = null;
                    mTvSave.setText("跳转");
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                case 1:
                    mTvSave.setText("收起");
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                case 2:
                    if (mGroups == null) {
                        mHelper.getGroups();
                    }
                    mTvSave.setText("返回");
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                case 3:
                    mTvSave.setText("返回");
                    mRcvPersons.setVisibility(VISIBLE);
                    break;
                default:
                    break;
            }
            mTvSave.setEnabled(true);
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
                case 0:
                    if (mAdapter.mState == 0) {
                        switch (position) {
                            case 0:
                                LshIntentUtils.gotoApp("com.tencent.mm");
                                break;
                            case 1:
                                LshIntentUtils.gotoApp("com.tencent.mobileqq");
                                break;
                            case 2:
                                LshIntentUtils.gotoApp("com.alibaba.android.rimet");
                                break;
                        }
                        setState(-1);
                    }
                    break;
                case 1:
                    if (position == 1) {
                        setState(2);
                        return;
                    }
                    mTvSave.setText(curSelectedPos != position ? "保存" : "收起");
                    if (mTypes == null || mTypes.size() == 0) {
                        mTvSave.setEnabled(curSelectedPos == position);
                    }
                    break;
                case 2:
                    curGroupPos = position;
                    setState(3);
                    return;
                case 3:
                    mTvSave.setText(curSelectedPos != position ? "保存" : "返回");
                    if (mTypes == null || mTypes.size() == 0) {
                        mTvSave.setEnabled(curSelectedPos == position);
                    }
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

        @Override
        public boolean onLongClick(View v) {
            int position = (int) v.getTag();
            switch (mState) {
                case 1:
                    if (position > 1) {
                        LshActivityUtils.newIntent(PersonDetailActivity.class)
                                .putExtra(mPersons.get(position - 2).getId())
                                .newTask()
                                .startActivity(getContext());
                    }
                    break;
                case 3:
                    LshActivityUtils.newIntent(PersonDetailActivity.class)
                            .putExtra(mGroups.get(curGroupPos).getPersons().get(position).getId())
                            .newTask()
                            .startActivity(getContext());
                    break;
            }


            return false;
        }

        public String getCurPersonId() {
            switch (mState) {
                case 1:
                    if (curSelectedPos == 0) {
                        return null;
                    } else if (curSelectedPos > 1) {
                        return mPersons.get(curSelectedPos - 2).getId();
                    }
                    break;
                case 3:
                    return mGroups.get(curGroupPos).getPersons().get(curSelectedPos).getId();
            }
            return null;
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