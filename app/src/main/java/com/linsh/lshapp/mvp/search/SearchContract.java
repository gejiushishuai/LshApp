package com.linsh.lshapp.mvp.search;

import com.linsh.lshapp.base.BaseContract;
import com.linsh.lshapp.model.result.SearchResult;

import java.util.Collection;

/**
 * Created by Senh Linsh on 17/4/25.
 */

public interface SearchContract {

    interface View extends BaseContract.BaseView {

        void showResults(Collection<SearchResult> results);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {

        void search(String query);
    }
}
