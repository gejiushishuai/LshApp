package com.linsh.lshapp.mvp.avatarSelect

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.linsh.lshapp.R
import com.linsh.lshapp.base.BaseToolbarActivity
import com.linsh.lshapp.model.bean.Avatar
import com.linsh.lshapp.mvp.photo_view.PhotoViewActivity
import com.linsh.lshapp.tools.ImageTools
import com.linsh.lshapp.tools.LshFileFactory
import com.linsh.lshapp.tools.LshIdTools
import com.linsh.utilseverywhere.ClassUtils
import com.linsh.utilseverywhere.IntentUtils
import com.linsh.utilseverywhere.tools.IntentBuilder
import com.linsh.views.album.Image
import kotlinx.android.synthetic.main.activity_album_select.*
import java.io.File

class AvatarSelectActivity : BaseToolbarActivity<AvatarSelectContract.Presenter>(), AvatarSelectContract.View {

    private var curPosition: Int = -1
    private var confirmItem: MenuItem? = null
    private var mCurPickedFile: File? = null

    companion object {
        const val REQUEST_CODE_PICK_PHOTO = 100
        const val REQUEST_CODE_CROP_PHOTO = 101
    }

    override fun getToolbarTitle(): String {
        return "选择头像"
    }

    override fun getLayout(): Int {
        return R.layout.activity_album_select
    }

    override fun initView() {
        asvAlbum.setLimitSelectedNum(1)
        asvAlbum.setOnItemSelectedListener({ selected, position ->
            if (selected) {
                confirmItem?.isEnabled = true
                curPosition = position
            } else {
                curPosition = -1
                confirmItem?.isEnabled = false
            }
        })
        asvAlbum.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val photos = asvAlbum.photos
            val array = arrayOfNulls<String>(photos.size)
            for (i in array.indices) {
                val avatar = photos[i] as Avatar
                array[i] = ImageTools.getSignedUrl(avatar.imageUrl.url)
            }
            IntentUtils.buildIntent(PhotoViewActivity::class.java)
                    .putExtra(array, PhotoViewActivity.EXTRA_URL_ARRAY)
                    .putExtra(position, PhotoViewActivity.EXTRA_DISPLAY_ITEM_INDEX)
                    .startActivity(activity)
            return@OnItemLongClickListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_album_select, menu)
        confirmItem = menu?.findItem(R.id.menu_album_select_confirm)
        confirmItem?.isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_album_select_confirm -> {
                val selectedPhotos = asvAlbum.selectedPhotos
                if (selectedPhotos.size > 0) {
                    val intent = intent.putExtra("avatar", mPresenter.getJson(selectedPhotos[0]))
                    setResult(101, intent)
                    finishActivity()
                }
            }
            R.id.menu_album_select_add_avatar -> {
                IntentUtils.gotoPickPhoto(this, REQUEST_CODE_PICK_PHOTO)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
        // 选择照片后返回
            REQUEST_CODE_PICK_PHOTO ->
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mCurPickedFile = LshFileFactory.getUploadAvatarFile(LshIdTools.getTimeId())
                    IntentUtils.gotoCropPhoto(this, REQUEST_CODE_CROP_PHOTO,
                            data.data, Uri.fromFile(mCurPickedFile), 1, 1, 1600, 1600)
                }
        // 剪裁照片后返回
            REQUEST_CODE_CROP_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    mPresenter.addAvatar(mCurPickedFile!!)
                }
        }
    }

    override fun initPresenter(): AvatarSelectContract.Presenter {
        val clazz = IntentBuilder.getStringExtra(activity, "class")
        val instance = ClassUtils.getInstance(clazz)
        if (instance is AvatarSelectContract.Presenter) {
            return instance
        }
        throw RuntimeException("请反思一下你是不是忘记传指定 Presenter 的类名过来")
    }

    override fun setData(avatars: List<Image>) {
        val list = if (curPosition >= 0) listOf(curPosition) else null
        asvAlbum.setPhotos(avatars, list)
    }
}
