package com.linsh.lshapp.mvp.search;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.result.SearchResult;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

import java.util.List;

import butterknife.BindView;

public class SearchActivity extends BaseToolbarActivity<SearchContract.Presenter> implements SearchContract.View {

    @BindView(R.id.rcv_search)
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchAdapter();
        mRecyclerView.setAdapter(mAdapter);
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
                LshApplicationUtils.removeRunnable(mRunnable);
                if (LshStringUtils.notEmpty(curNewText)) {
                    LshApplicationUtils.postRunnable(mRunnable, 500);
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
