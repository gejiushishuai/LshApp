package com.linsh.lshapp.part.detail;

import com.linsh.lshapp.base.BasePresenterImpl;
import com.linsh.lshapp.model.action.DefaultThrowableAction;
import com.linsh.lshapp.model.bean.Person;
import com.linsh.lshapp.model.bean.PersonDetail;
import com.linsh.lshapp.tools.ShiyiDataOperator;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by Senh Linsh on 17/4/28.
 */
public class PersonDetailPresenter extends BasePresenterImpl<PersonDetailContract.View> implements PersonDetailContract.Presenter {

    private Person mPerson;
    private PersonDetail mPersonDetail;

    @Override
    protected void attachView() {
        Subscription getPersonSub = ShiyiDataOperator.getPerson(getRealm(), getView().getPersonId())
                .subscribe(new Action1<Person>() {
                    @Override
                    public void call(Person person) {
                        if (person != null) {
                            mPerson = person;
                            getView().setData(mPerson);
                        }
                    }
                }, new DefaultThrowableAction());

        Subscription getPersonDetailSub = ShiyiDataOperator.getPersonDetail(getRealm(), getView().getPersonId())
                .subscribe(new Action1<PersonDetail>() {
                    @Override
                    public void call(PersonDetail personDetail) {
                        if (personDetail != null) {
                            mPersonDetail = personDetail;
                            getView().setData(mPersonDetail);
                        }
                    }
                }, new DefaultThrowableAction());
        addSubscription(getPersonSub, getPersonDetailSub);
    }

}
