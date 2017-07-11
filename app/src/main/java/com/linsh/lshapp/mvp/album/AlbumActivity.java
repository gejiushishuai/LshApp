package com.linsh.lshapp.mvp.album;

import android.view.View;
import android.widget.AdapterView;

import com.linsh.lshapp.R;
import com.linsh.lshapp.base.BaseToolbarActivity;
import com.linsh.lshapp.model.bean.Avatar;
import com.linsh.lshapp.model.bean.db.ImageUrl;
import com.linsh.lshapp.model.bean.db.PersonAlbum;
import com.linsh.lshapp.mvp.photo_view.PhotoViewActivity;
import com.linsh.lshapp.tools.ImageTools;
import com.linsh.lshutils.module.Image;
import com.linsh.lshutils.utils.LshActivityUtils;
import com.linsh.lshutils.view.albumview.AlbumView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.RealmList;

public class AlbumActivity extends BaseToolbarActivity<AlbumContract.Presenter> implements AlbumContract.View {

    public static final String EXTRA_URL_ARRAY_LIST = "STRING_ARRAY_LIST_EXTRA";

    @BindView(R.id.pv_album)
    AlbumView mAlbumView;

    @Override
    protected String getToolbarTitle() {
        return "查看头像";
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_album;
    }

    @Override
    protected void initView() {
        mAlbumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<? extends Image> photos = mAlbumView.getPhotos();
                String[] array = new String[photos.size()];
                for (int i = 0; i < array.length; i++) {
                    Avatar avatar = (Avatar) photos.get(i);
                    array[i] = ImageTools.getSignedUrl(avatar.imageUrl.getUrl());
                }
                LshActivityUtils.newIntent(PhotoViewActivity.class)
                        .putExtra(array, PhotoViewActivity.EXTRA_URL_ARRAY_LIST)
                        .putExtra(position, PhotoViewActivity.EXTRA_DISPLAY_ITEM)
                        .startActivity(getActivity());
            }
        });
    }

    @Override
    protected AlbumContract.Presenter initPresenter() {
        return new AlbumPresenter();
    }

    @Override
    public String getPersonId() {
        return LshActivityUtils.getStringExtra(this);
    }

    @Override
    public void setData(PersonAlbum album) {
        RealmList<ImageUrl> avatars = album.getAvatars();
        List<Avatar> urls = new ArrayList<>();
        for (ImageUrl avatar : avatars) {
            urls.add(new Avatar(avatar));
        }
        mAlbumView.setPhotos(urls);
    }
}
