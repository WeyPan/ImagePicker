package com.lenvar.android.imagepicker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class AlbumPicker extends BasePopup implements View.OnClickListener, OnPickListener, FolderAdapter.OnFolderClickListener {

    public static final int GALLERY_GRID_COUNT = 4;

    private Button mTitle, mCancel, mComplete;
    private RecyclerView mImageRv, mFolderRv;
    private GridImageAdapter mAdapter;
    private View mMask;
    private FolderAdapter mFolderAdapter;
    private Animation mShow, mHide;
    private TextView mNoImage;
    private PickerConfig mConfig;

    static void open(AppCompatActivity context, PickerConfig config) {
        AlbumPicker picker = new AlbumPicker(context, config);
        picker.getAllPhoto();
        int y = 0;
        if (Utils.hasNavigationBar(context)) {
            if (Utils.checkDeviceHasNavigationBar(context)) {
                y = Utils.getBottomStatusHeight(context);
            }
        }
        picker.showAtLocation(context.getWindow().getDecorView(), Gravity.BOTTOM, 0, y);
    }

    private AlbumPicker(AppCompatActivity context, PickerConfig config) {
        super.mContext = context;
        this.mConfig = config;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        View contentView = context.getLayoutInflater().inflate(R.layout.popup_image_picker, null);
        setAnimationStyle(R.style.image_picker_anim_style);
        setContentView(contentView);
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());

        mTitle = (Button) contentView.findViewById(R.id.image_title);
        mTitle.setOnClickListener(this);
        mCancel = (Button) contentView.findViewById(R.id.image_cancel);
        mCancel.setOnClickListener(this);
        mComplete = (Button) contentView.findViewById(R.id.image_complete);
        mComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getPickList().size() > 0) {
                    mConfig.getOnPickCompleteListener().onPickComplete(mAdapter.getPickList());
                    dismiss();
                }
            }
        });
        mComplete.setText("发送(0/" + mConfig.getMaxPick() + ")");

        mImageRv = (RecyclerView) contentView.findViewById(R.id.gallery_picker);
        mImageRv.setHasFixedSize(true);
        mImageRv.setLayoutManager(new GridLayoutManager(context, GALLERY_GRID_COUNT));
        mAdapter = new GridImageAdapter(context, mConfig);
        mAdapter.setOnPickListener(this);
        mImageRv.setAdapter(mAdapter);
        mNoImage = (TextView) contentView.findViewById(R.id.no_image);
        mMask = contentView.findViewById(R.id.mask);
        mMask.setOnClickListener(this);
        mFolderRv = (RecyclerView) contentView.findViewById(R.id.folder_picker);
        mFolderRv.setHasFixedSize(true);
        mFolderRv.setLayoutManager(new LinearLayoutManager(context));
        mFolderAdapter = new FolderAdapter(context, mConfig);
        mFolderAdapter.setOnFolderClickListener(this);
        mFolderRv.setAdapter(mFolderAdapter);

        mShow = AnimationUtils.loadAnimation(context, R.anim.folder_card_input);
        mShow.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mMask.setVisibility(View.VISIBLE);
                mFolderRv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mHide = AnimationUtils.loadAnimation(context, R.anim.folder_card_output);
        mHide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMask.setVisibility(View.GONE);
                mFolderRv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        contentView.findViewById(R.id.image_title_bar).setBackgroundColor(mConfig.getStyleColor());
        if (PickerConfig.ImagePickerTheme.THEME_LIGHT == mConfig.getTheme()) {
            mTitle.setTextColor(Color.parseColor("#FFFFFF"));
            mCancel.setTextColor(Color.parseColor("#FFFFFF"));
            mComplete.setTextColor(Color.parseColor("#FFFFFF"));
            mTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down_light, 0);
        } else {
            mTitle.setTextColor(Color.parseColor("#333333"));
            mCancel.setTextColor(Color.parseColor("#333333"));
            mComplete.setTextColor(Color.parseColor("#333333"));
            mTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down_drak, 0);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_title) {
            if (mFolderRv.getVisibility() == View.GONE)
                mFolderRv.startAnimation(mShow);
            else
                mFolderRv.startAnimation(mHide);
        } else if (view.getId() == R.id.image_cancel) {
            dismiss();
        } else if (view.getId() == R.id.mask) {
            mFolderRv.startAnimation(mHide);
        }
    }

    @Override
    public void onPicker(int option) {
        mComplete.setText("发送(" + option + "/" + mConfig.getMaxPick() + ")");
        mFolderAdapter.setPickList(mAdapter.getPickList());
    }

    @Override
    public void onFolderClick(FolderAdapter.Folder folder) {
        mFolderRv.startAnimation(mHide);
        if (folder.path.contains("/"))
            mTitle.setText(folder.path.substring(folder.path.lastIndexOf("/") + 1));
        else
            mTitle.setText(folder.path);

        if (folder.imgs.size() == 0) {
            mNoImage.setVisibility(View.VISIBLE);
        }
        mAdapter.set(folder.imgs);
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
        mFolderAdapter.set(mFolderList);
    }
}
