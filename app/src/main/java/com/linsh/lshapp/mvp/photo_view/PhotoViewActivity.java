package com.linsh.lshapp.mvp.photo_view;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseViewActivity;
import com.linsh.lshutils.adapter.LshViewPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

public class PhotoViewActivity extends BaseViewActivity<PhotoViewContract.Presenter> implements PhotoViewContract.View {

    public static final String EXTRA_URL = "string_array_list_extra";
    public static final String EXTRA_URL_ARRAY_LIST = "string_array_list_extra";
    public static final String EXTRA_DISPLAY_ITEM = "extra_display_item";
    private List<String> mUrls = new ArrayList<>();

    @BindView(R.id.vp_photo_view)
    ViewPager mViewPager;

    @Override
    protected int getLayout() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void initView() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (StringUtils.notEmpty(url)) {
            mUrls.add(url);
        } else {
            String[] extras = getIntent().getStringArrayExtra(EXTRA_URL_ARRAY_LIST);
            if (extras != null) {
                Collections.addAll(mUrls, extras);
            }
        }
        PhotoViewAdapter adapter = new PhotoViewAdapter();
        adapter.setData(mUrls);
        mViewPager.setAdapter(adapter);

        int curItem = getIntent().getIntExtra(EXTRA_DISPLAY_ITEM, 0);
        if (curItem > 0 && curItem < mUrls.size()) {
            mViewPager.setCurrentItem(curItem);
        }
    }

    @Override
    protected PhotoViewContract.Presenter initPresenter() {
        return new PhotoViewPresenter();
    }

    private class PhotoViewAdapter extends LshViewPagerAdapter<String> {

        @Override
        protected View getView(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(PhotoViewActivity.this);
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Glide.with(PhotoViewActivity.this)
                    .load(getData().get(position))
                    .dontAnimate()
                    .into(photoView);
            return photoView;
        }
    }
}
