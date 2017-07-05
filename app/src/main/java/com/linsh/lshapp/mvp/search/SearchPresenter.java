package com.linsh.lshapp.mvp.search;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.RealmPresenterImpl;
import com.linsh.lshapp.model.bean.db.Person;
import com.linsh.lshapp.model.bean.db.PersonDetail;
import com.linsh.lshapp.model.bean.db.Type;
import com.linsh.lshapp.model.bean.db.TypeDetail;
import com.linsh.lshapp.model.result.SearchResult;
import com.linsh.lshapp.tools.LshRxUtils;
import com.linsh.lshutils.utils.LshRecourseUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

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
        Flowable<List<SearchResult>> observable = LshRxUtils.getAsyncFlowable((realm, subscriber)
                -> subscriber.onNext(getSearchResults(realm, query)));
        Disposable disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchResults -> {
                    getView().showResults(searchResults);
                });
        addDisposable(disposable);
    }

    private List<SearchResult> getSearchResults(Realm realm, String query) {
        LinkedHashMap<Person, SearchResult> persons = new LinkedHashMap<>();

        RealmResults<Person> personsName = realm.where(Person.class).contains("name", query).findAll();
        for (Person person : personsName) {
            CharSequence name = getSpannableString(person.getName(), query, 0);
            SearchResult value = new SearchResult(person.getId(), name);
            persons.put(person, value);
        }

        RealmResults<Person> personsDesc = realm.where(Person.class).contains("describe", query).findAll();
        for (Person person : personsDesc) {
            CharSequence describe = getSpannableString(person.getDescribe(), query, 0);
            if (persons.containsKey(person)) {
                SearchResult result = persons.get(person);
                result.personDesc = describe;
            } else {
                persons.put(person, new SearchResult(person.getId(), person.getName(), describe));
            }
        }

        RealmResults<TypeDetail> typeDetailsDetail = realm.where(TypeDetail.class).contains("detail", query).findAll();
        RealmResults<TypeDetail> typeDetailsDesc = realm.where(TypeDetail.class).contains("describe", query).findAll();
        addTypeDetailsToSearchResult(realm, typeDetailsDetail, persons, true, query);
        addTypeDetailsToSearchResult(realm, typeDetailsDesc, persons, false, query);

        List<SearchResult> results = new ArrayList<>();
        results.addAll(persons.values());
        return results;
    }

    private void addTypeDetailsToSearchResult(Realm realm, List<TypeDetail> typeDetails, LinkedHashMap<Person, SearchResult> results, boolean showDetail, String query) {
        for (TypeDetail typeDetail : typeDetails) {
            // 通过 TypeDetail 获取 PersonDetail
            PersonDetail personDetail = realm.where(PersonDetail.class).contains("types.typeDetails.id", typeDetail.getId()).findFirst();
            if (personDetail != null) {
                // 通过 PersonDetail 获取 Person
                Person person = realm.where(Person.class).contains("id", personDetail.getId()).findFirst();
                if (person != null) {
                    // 获取应该显示的字符串
                    String detail = showDetail ? typeDetail.getDetail() : ("...  " + typeDetail.getDescribe());
                    CharSequence detailToShow = getDetailToShow(realm, typeDetail.getId(), detail, query);
                    // 添加到 SearchResult 中
                    SearchResult result;
                    if (results.containsKey(person)) {
                        result = results.get(person);
                    } else {
                        result = new SearchResult(person.getId(), person.getName());
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

    private CharSequence getDetailToShow(Realm realm, String typeDetailId, String detail, String query) {
        Type type = realm.where(Type.class).contains("typeDetails.id", typeDetailId).findFirst();
        String detailToShow;
        int startIndex = 0;
        if (type != null) {
            String prefix = type.getName() + " : ";
            startIndex = prefix.length();
            detailToShow = prefix + detail; // TODO: 17/6/26 完善显示的文字(文字过长在合适的地方添加省略号)
        } else {
            detailToShow = detail;
        }
        return getSpannableString(detailToShow, query, startIndex);
    }

    // 使用富文本来给需要查询的文字添加颜色
    private CharSequence getSpannableString(String text, String query, int startIndex) {
        SpannableString spannable = new SpannableString(text);
        int length = query.length();
        for (int i = startIndex; i <= text.length() - length; i++) {
            String substring = text.substring(i, i + length);
            if (substring.equals(query)) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(LshRecourseUtils.getColor(R.color.color_theme_dark_blue));
                spannable.setSpan(colorSpan, i, i + length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                i += length - 1;
            }
        }
        return spannable;
    }
}
