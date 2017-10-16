package com.lenvar.android.imagepicker;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ImagePicker extends BasePopup implements View.OnClickListener, OnPickListener {

    public static final int REQUEST_CAMERA = 65533;

    private RecyclerView mImageRv;
    private HorizontalImageAdapter mAdapter;
    private TextView mNoImage;
    private Button mAlbumBtn, mShotBtn;
    private PickerConfig mConfig;
    private static File mCameraFile;

    public static void open(AppCompatActivity context, PickerConfig config) {
        ImagePicker picker = new ImagePicker(context, config);
        picker.getAllPhoto();
        int y = 0;
        if (Utils.hasNavigationBar(context)) {
            if (Utils.checkDeviceHasNavigationBar(context)) {
                y = Utils.getBottomStatusHeight(context);
            }
        }
        picker.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, y);
    }

    private ImagePicker(AppCompatActivity context, PickerConfig config) {
        super.mContext = context;
        this.mConfig = config;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View conentView = context.getLayoutInflater().inflate(R.layout.popup_gallery, null);
        setAnimationStyle(R.style.image_picker_anim_style);
        setContentView(conentView);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());

        mImageRv = (RecyclerView) conentView.findViewById(R.id.gallery_picker);
        mImageRv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mImageRv.setLayoutManager(linearLayoutManager);
        mAdapter = new HorizontalImageAdapter(context, mConfig);
        mAdapter.setOnPickListener(this);
        mImageRv.setAdapter(mAdapter);
        mNoImage = (TextView) conentView.findViewById(R.id.no_image);
        mAlbumBtn = (Button) conentView.findViewById(R.id.gallery_album);
        mAlbumBtn.setOnClickListener(this);
        mShotBtn = (Button) conentView.findViewById(R.id.gallery_shot);
        mShotBtn.setOnClickListener(this);
        conentView.findViewById(R.id.gallery_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.gallery_album) {
            AlbumPicker.open(mContext, mConfig);
            dismiss();
        } else if (view.getId() == R.id.gallery_shot) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCameraFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/DCIM/temp/" + System.currentTimeMillis() + ".jpg");
            mCameraFile.getParentFile().mkdirs();
            Uri uri = FileProvider.getUriForFile(mContext, super.mContext.getApplicationInfo().processName + ".imagePathProvider", mCameraFile);
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            mContext.startActivityForResult(intent, REQUEST_CAMERA);
            dismiss();
        } else if (view.getId() == R.id.gallery_cancel) {
            dismiss();
        }
    }

    @Override
    public void onPicker(int option) {
        if (option > 0) {
            mAlbumBtn.setText(R.string.send);
            mShotBtn.setText("已经选择 " + option + "/" + mConfig.getMaxPick() + " 图片");
            mShotBtn.setClickable(false);
            if (option == 1) {
                mAlbumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mConfig.getOnPickCompleteListener().onPickComplete(mAdapter.getPickList());
                        dismiss();
                    }
                });
                mShotBtn.setOnClickListener(null);
            }
        } else {
            mAlbumBtn.setText(R.string.album);
            mShotBtn.setText(R.string.shot);
            mShotBtn.setClickable(true);
            mAlbumBtn.setOnClickListener(this);
            mShotBtn.setOnClickListener(this);
        }
    }

    @Override
    void setImageAdapter(List<String> mPhotoList) {
        if (mPhotoList.size() == 0) {
            mNoImage.setVisibility(View.VISIBLE);
        }
        mAdapter.set(mPhotoList);
    }

    @Override
    void setFolderAdapter(List<FolderAdapter.Folder> mFolderList) {
    }

    public static String onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            if (mCameraFile != null) {
                return mCameraFile.getAbsolutePath();
            }
        }
        return "";
    }
}
