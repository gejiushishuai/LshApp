package com.linsh.lshapp.mvp.search;

import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.result.SearchResult;
import com.linsh.lshapp.tools.LshRxUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 17/5/5.
 */

public class SearchPresenter extends RealmPresenterImpl<SearchContract.View> implements SearchContract.Presenter {

    @Override
    protected void attachView() {
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    @Override
    public void search(String query) {
        Observable<List<SearchResult>> observable = LshRxUtils.getAsyncObservable(getRealm(), (realm, subscriber)
                -> subscriber.onNext(getSearchResults(realm, query)));
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchResults -> {
                    getView().showResults(searchResults);
                });
    }

    private List<SearchResult> getSearchResults(Realm realm, String query) {
        LinkedHashMap<Person, SearchResult> persons = new LinkedHashMap<>();

        RealmResults<Person> personsName = realm.where(Person.class).contains("name", query).findAll();
        for (Person person : personsName) {
            SearchResult value = new SearchResult(person.getName());
            persons.put(person, value);
        }

        RealmResults<Person> personsDesc = realm.where(Person.class).contains("describe", query).findAll();
        for (Person person : personsDesc) {
            if (persons.containsKey(person)) {
                SearchResult result = persons.get(person);
                result.personDesc = person.getDescribe();
            } else {
                persons.put(person, new SearchResult(person.getName(), person.getDescribe()));
            }
        }

        RealmResults<TypeDetail> typeDetailsDetail = realm.where(TypeDetail.class).contains("detail", query).findAll();
        RealmResults<TypeDetail> typeDetailsDesc = realm.where(TypeDetail.class).contains("describe", query).findAll();
        addTypeDetailsToSearchResult(realm, typeDetailsDetail, persons, true);
        addTypeDetailsToSearchResult(realm, typeDetailsDesc, persons, false);

        List<SearchResult> results = new ArrayList<>();
        results.addAll(persons.values());
        return results;
    }

    private void addTypeDetailsToSearchResult(Realm realm, List<TypeDetail> typeDetails, LinkedHashMap<Person, SearchResult> results, boolean showDetail) {
        for (TypeDetail typeDetail : typeDetails) {
            PersonDetail personDetail = realm.where(PersonDetail.class).contains("types.typeDetails.id", typeDetail.getId()).findFirst();
            if (personDetail != null) {
                Person person = realm.where(Person.class).contains("id", personDetail.getId()).findFirst();
                if (person != null) {
                    // 获取应该显示的字符串
                    String detail = showDetail ? typeDetail.getDetail() : ("...  " + typeDetail.getDescribe());
                    String detailToShow = getDetailToShow(realm, typeDetail.getId(), detail);
                    // 添加到 SearchResult 中
                    SearchResult result;
                    if (results.containsKey(person)) {
                        result = results.get(person);
                    } else {
                        result = new SearchResult(person.getName());
                        results.put(person, result);
                    }
                    if (result.typeDetail == null) {
                        result.typeDetail = new ArrayList<>();
                    }
                    result.typeDetail.add(detailToShow);
                }
            }
        }
    }

    private String getDetailToShow(Realm realm, String typeDetailId, String detail) {
        Type type = realm.where(Type.class).contains("typeDetails.id", typeDetailId).findFirst();
        String detailToShow;
        if (type != null) {
            detailToShow = type.getName() + " : " + detail; // TODO: 17/6/26 完善显示的文字(文字过长在合适的地方添加省略号)
        } else {
            detailToShow = detail;
        }
        return detailToShow;
    }
}
