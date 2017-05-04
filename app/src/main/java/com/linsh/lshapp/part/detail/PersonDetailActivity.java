package com.linsh.lshapp.part.detail;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshapp.model.bean.TypeLabel;
import com.linsh.lshapp.view.LshPopupWindow;
import com.linsh.lshutils.Rx.Action;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.utils.LshListUtils;
import com.linsh.lshutils.utils.LshScreenUtils;
import com.linsh.lshutils.view.LshColorDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmList;

public class PersonDetailActivity extends BaseToolbarActivity<PersonDetailContract.Presenter> implements PersonDetailContract.View {

    @BindView(R.id.iv_person_detail_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_person_detail_name)
    TextView tvName;
    @BindView(R.id.iv_person_detail_sex)
    ImageView ivSex;
    @BindView(R.id.tv_person_detail_desc)
    TextView tvDesc;
    @BindView(R.id.rcv_person_detail_content)
    RecyclerView rcvContent;

    private PersonDetailAdapter mDetailAdapter;

    @Override
    protected String getToolbarTitle() {
        return "详细信息";
    }

    @Override
    protected PersonDetailContract.Presenter initPresenter() {
        return new PersonDetailPresenter();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_person_detail;
    }

    @Override
    protected void initView() {
        rcvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDetailAdapter = new PersonDetailAdapter();
        rcvContent.setAdapter(mDetailAdapter);

        mDetailAdapter.setOnItemClickListener(new PersonDetailAdapter.OnItemClickListener<Type>() {
            @Override
            public void onItemClick(Type data, int firstLevelPosition, int secondLevelPosition) {
                showToast("onItemClick:" + data.getName() + "---" + secondLevelPosition);
            }
        });
        mDetailAdapter.setOnItemLongClickListener(new PersonDetailAdapter.OnItemLongClickListener<Type>() {
            @Override
            public void onItemLongClick(View view, final Type data, final int firstLevelPosition, final int secondLevelPosition) {
                int yOnScreen = LshScreenUtils.getLocationYOnScreen(view);
                View viewParent = (View) view.getParent();
                if (viewParent instanceof RecyclerView) {
                    viewParent = view;
                }

                LshPopupWindow popupWindow = new LshPopupWindow(getActivity()).BuildList()
                        .setItems(new String[]{"添加当前类型", "添加类型到前面", "添加类型到后面", "删除当前类型", "删除当前类型信息"}, new LshPopupWindow.OnItemClickListener() {
                            @Override
                            public void onClick(LshPopupWindow window, int index) {
                                window.dismiss();
                                switch (index) {
                                    case 0:
                                        // 添加当前类型
                                        mPresenter.addType(data.getName());
                                        break;
                                    case 1:
                                        // 添加类型到前面
                                        addType(firstLevelPosition);
                                        break;
                                    case 2:
                                        // 添加类型到后面
                                        addType(firstLevelPosition + 1);
                                        break;
                                    case 3:
                                        // 删除当前类型
                                        if (data.getTypeDetails().size() > 1) {
                                            showTextDialog("删除当前类型将会删除该类型的所有类型信息", "删除", new LshColorDialog.OnPositiveListener() {
                                                @Override
                                                public void onClick(LshColorDialog dialog) {
                                                    dialog.dismiss();
                                                    mPresenter.deleteType(data.getId());
                                                }
                                            }, null, null);
                                        } else {
                                            mPresenter.deleteType(data.getId());
                                        }
                                        break;
                                    case 4:
                                        // 删除当前类型信息
                                        mPresenter.deleteTypeDetail(data.getTypeDetails().get(secondLevelPosition).getId());
                                        break;
                                }
                            }
                        }).getPopupWindow();

                int xOff = viewParent.getWidth() / 2 - popupWindow.getWidth() / 2;
                int yOff = yOnScreen > LshScreenUtils.getScreenHeight() / 2 ? -popupWindow.getHeight() - view.getHeight() : 0;
                popupWindow.showAsDropDown(viewParent, xOff, yOff);
            }
        });
    }

    @Override
    public String getPersonId() {
        return LshActivityUtils.getStringExtra(getActivity());
    }

    @Override
    public void setData(Person person) {
        if (person != null) {
            tvName.setText(person.getName());
            tvDesc.setText(person.getDescribe());
            int gender = person.getIntGender();
            if (gender == 1) {
                ivSex.setImageResource(R.drawable.ic_sex_male);
            } else if (gender == 2) {
                ivSex.setImageResource(R.drawable.ic_sex_female);
            }
        }
    }

    @Override
    public void setData(PersonDetail personDetail) {
        if (personDetail != null) {
            mDetailAdapter.setData(personDetail.getTypes());
        }
    }

    private ArrayList<TypeDetail> getTypeDetails(PersonDetail personDetail) {
        ArrayList<TypeDetail> typeDetails = new ArrayList<>();

        RealmList<Type> types = personDetail.getTypes();
        for (Type type : types) {
            typeDetails.addAll(type.getTypeDetails());
        }
        return typeDetails;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_person_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_person_detail_add_type:
                if (addType(-1)) {
                    return false;
                }
                return true;
            case R.id.menu_person_detail_manage_type:

                return true;
            case R.id.menu_person_detail_delete_person:
                
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addType(int sort) {
        RealmList<TypeLabel> types = mPresenter.getTypes();
        if (types == null) {
            return true;
        }
        showTypesDialog(types, sort);
        return false;
    }

    private void showTypesDialog(RealmList<TypeLabel> types, final int sort) {
        List<String> stringList = LshListUtils.getStringList(types, new Action<String, TypeLabel>() {
            @Override
            public String call(TypeLabel typeLabel) {
                return typeLabel.getName();
            }
        });
        stringList.add(0, "添加新类型");
        showListDialog("添加类型", stringList, new LshColorDialog.OnItemClickListener() {
            @Override
            public void onClick(LshColorDialog dialog, String item, int index) {
                dialog.dismiss();
                if (index == 0) {
                    showAddTypeDialog();
                    return;
                }
                if (sort >= 0) {
                    mPresenter.addType(item, sort);
                } else {
                    mPresenter.addType(item);
                }
            }
        });
    }

    private void showAddTypeDialog() {
        new LshColorDialog(getActivity())
                .buildInput()
                .setTitle("添加新类型")
                .setPositiveButton("添加", new LshColorDialog.OnInputPositiveListener() {
                    @Override
                    public void onClick(LshColorDialog dialog, String inputText) {
                        dialog.dismiss();
                        mPresenter.addTypeLabel(inputText);
                    }
                })
                .setNegativeButton(null, null)
                .show();
    }
}