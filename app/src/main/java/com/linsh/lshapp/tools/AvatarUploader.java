package com.linsh.lshapp.tools;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.linsh.lshutils.utils.Basic.LshApplicationUtils;
import com.linsh.lshutils.utils.Basic.LshIOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

/**
 * Created by Senh Linsh on 16/12/7.
 */
public class AvatarUploader {

    // 使用照相机拍照获取图片
    private static final int SELECT_PIC_BY_TAKE_PHOTO = 1;
    // 使用相册中的图片
    private static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    // 使用裁剪的图片
    private static final int SELECT_PIC_BY_RESIZE = 3;

    private static String filePath = LshApplicationUtils.getContext().getCacheDir().getAbsolutePath() + "/header.jpg";

    /***
     * 从相册中取图片
     */
    public void pickPhoto(Activity activity) {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
        // 如果要限制上传到服务器的图片类型时可以直接写如：image/jpeg 、 image/png等的类型
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(pickIntent, SELECT_PIC_BY_PICK_PHOTO);
    }

    /**
     * 拍照获取图片
     */
    public void takePhoto(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //下面这句指定调用相机拍照后的照片存储的路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(filePath)));
        activity.startActivityForResult(intent, SELECT_PIC_BY_TAKE_PHOTO);// 采用ForResult打开


    }

    /**
     * 调用系统的裁剪
     */

    private void resizePhoto(Activity activity, Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, SELECT_PIC_BY_RESIZE);
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, final Intent data) {

        switch (requestCode) {
            case SELECT_PIC_BY_PICK_PHOTO:// 直接从相册获取
                try {
                    resizePhoto(activity, data.getData());
                } catch (NullPointerException e) {
                    e.printStackTrace();// 用户点击取消操作
                }
                break;
            case SELECT_PIC_BY_TAKE_PHOTO:// 调用相机拍照
                File temp = new File(filePath);
                resizePhoto(activity, Uri.fromFile(temp));
                break;
            case SELECT_PIC_BY_RESIZE:// 取得裁剪后的图片
                if (data != null) {
                    Observable.unsafeCreate((Observable.OnSubscribe<Void>) subscriber -> saveToSdCard(data))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(Actions.empty(), throwable -> mOnUploadListener.onFailed(throwable.getMessage()),
                                    () -> mOnUploadListener.onPictureSaved(new File(filePath)));
                }
                break;
        }
    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void saveToSdCard(Intent picData) {
        Bundle extras = picData.getExtras();
        if (extras != null) {
            // 取得SDCard图片路径做显示
            final Bitmap photo = extras.getParcelable("data");
            if (photo != null) {
                if (mOnUploadListener != null) {
                    LshApplicationUtils.getMainHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            mOnUploadListener.onPictureSelected(photo);
                        }
                    });
                }

                FileOutputStream b = null;
                try {
                    b = new FileOutputStream(new File(filePath));
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    LshIOUtils.close(b);
                }
            }
        }
    }

    private OnUploadListener mOnUploadListener;

    public void setOnUploadListener(OnUploadListener listener) {
        mOnUploadListener = listener;
    }

    public interface OnUploadListener {
        void onPictureSelected(Bitmap head);

        void onPictureSaved(File file);

        void onFailed(String errorMsg);
    }
}
