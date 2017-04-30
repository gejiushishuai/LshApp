package com.linsh.lshapp.part.detail;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.model.bean.Type;
import com.linsh.lshapp.model.bean.TypeDetail;
import com.linsh.lshutils.utils.LshActivityUtils;

import java.util.ArrayList;

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
        mDetailAdapter = new PersonDetailAdapter();
        rcvContent.setAdapter(mDetailAdapter);
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
}
