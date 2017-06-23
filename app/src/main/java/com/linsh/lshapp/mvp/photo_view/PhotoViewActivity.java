package com.linsh.lshapp.mvp.photo_view;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseViewActivity;
import com.linsh.lshutils.utils.Basic.LshApplicationUtils;

import butterknife.BindView;

public class PhotoViewActivity extends BaseViewActivity<PhotoViewContract.Presenter> implements PhotoViewContract.View {

    public static final String EXTRA_URL = "STRING_ARRAY_LIST_EXTRA";
    public static final String EXTRA_URL_ARRAY_LIST = "STRING_ARRAY_LIST_EXTRA";

    @BindView(R.id.pv_photo_view)
    PhotoView mPhotoView;

    @Override
    protected int getLayout() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected void initView() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        Glide.with(LshApplicationUtils.getContext())
                .load(url)
                .dontAnimate()
                .into(mPhotoView);
    }

    @Override
    protected PhotoViewContract.Presenter initPresenter() {
        return new PhotoViewPresenter();
    }
}
