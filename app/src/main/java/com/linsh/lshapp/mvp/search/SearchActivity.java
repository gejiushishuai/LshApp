package com.linsh.lshapp.mvp.search;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.result.SearchResult;
import com.linsh.lshapp.mvp.person_detail.PersonDetailActivity;
import com.linsh.lshutils.adapter.LshRecyclerViewAdapter;
import com.linsh.utilseverywhere.HandlerUtils;
import com.linsh.utilseverywhere.IntentUtils;
import com.linsh.utilseverywhere.StringUtils;

import java.util.List;

public class SearchActivity extends BaseToolbarActivity<SearchContract.Presenter> implements SearchContract.View {

    RecyclerView mRecyclerView;
    private SearchAdapter mAdapter;

    @Override
    protected String getToolbarTitle() {
        return "搜索";
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.rcv_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new LshRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SearchResult searchResult = mAdapter.getData().get(position);
                IntentUtils.buildIntent(PersonDetailActivity.class)
                        .putExtra(searchResult.personId)
                        .startActivity(getActivity());
            }
        });
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchPresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private String curNewText;
            private Runnable mRunnable = () -> {
                mPresenter.search(curNewText);
            };

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals(curNewText)) {
                    curNewText = query;
                    mPresenter.search(curNewText);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                curNewText = newText;
                HandlerUtils.removeRunnable(mRunnable);
                if (StringUtils.notEmpty(curNewText)) {
                    HandlerUtils.postRunnable(mRunnable, 500);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void showResults(List<SearchResult> results) {
        mAdapter.setData(results);
    }
}
