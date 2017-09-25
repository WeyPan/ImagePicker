package com.lenvar.android.imagepicker;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

class GridImageAdapter extends ImagePickerAdapter {

    private int mWindowWidth;

    public GridImageAdapter(Activity mContext, PickerConfig config) {
        super(mContext, config);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mWindowWidth = dm.widthPixels;
    }


    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageHolder mImageHolder = super.onCreateViewHolder(parent, viewType);
        mImageHolder.setSize(mWindowWidth);
        return mImageHolder;
    }
}
