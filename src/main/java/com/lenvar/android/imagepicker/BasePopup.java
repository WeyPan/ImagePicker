package com.lenvar.android.imagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.PopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

abstract class BasePopup extends PopupWindow {

    protected AppCompatActivity mContext;
    protected List<String> mPhotoList = new ArrayList<>();
    protected List<FolderAdapter.Folder> mFolderList = new ArrayList<>();
    protected boolean hasFolderScan = false;           // 是否扫描过

    public void getAllPhoto() {
        LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
            private final String[] IMAGE_PROJECTION = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.SIZE
            };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(mContext, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                            boolean showFlag = size > 1024 * 5;                           //是否大于5K
                            if (showFlag)
                                mPhotoList.add(path);
                            if (!hasFolderScan && showFlag) {
                                File photoFile = new File(path);                   // 获取图片文件
                                File folderFile = photoFile.getParentFile();      // 获取图片上一级文件夹
                                FolderAdapter.Folder folderInfo = new FolderAdapter.Folder();
                                folderInfo.path = folderFile.getAbsolutePath();
                                if (!contains(folderInfo)) {      // 判断是否是已经扫描到的图片文件夹
                                    List<String> photoInfoList = new ArrayList<>();
                                    photoInfoList.add(path);
                                    folderInfo.imgs = photoInfoList;
                                    mFolderList.add(folderInfo);
                                } else {
                                    FolderAdapter.Folder f = getFolder(folderInfo);
                                    if (f == null) {
                                        List<String> photoInfoList = new ArrayList<>();
                                        photoInfoList.add(path);
                                        folderInfo.imgs = photoInfoList;
                                        mFolderList.add(folderInfo);
                                    } else {
                                        f.imgs.add(path);
                                    }
                                }
                            }
                        } while (data.moveToNext());
                        setImageAdapter(mPhotoList);
                        mFolderList.add(0, new FolderAdapter.Folder("所有图片", mPhotoList));
                        setFolderAdapter(mFolderList);
                        hasFolderScan = true;
                    }
                }
            }

            private boolean contains(FolderAdapter.Folder folderInfo) {
                for (FolderAdapter.Folder folder : mFolderList) {
                    if (folder.path.equalsIgnoreCase(folderInfo.path)) {
                        return true;
                    }
                }
                return false;
            }

            private FolderAdapter.Folder getFolder(FolderAdapter.Folder folderInfo) {
                for (int i = 0; i < mFolderList.size(); i++) {
                    if (folderInfo.path.equalsIgnoreCase(mFolderList.get(i).path)) {
                        return mFolderList.get(i);
                    }
                }
                return null;
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
        mContext.getSupportLoaderManager().restartLoader(0, null, mLoaderCallback);   // 扫描手机中的图片
    }

    abstract void setImageAdapter(List<String> mPhotoList);

    abstract void setFolderAdapter(List<FolderAdapter.Folder> mFolderList);
}
