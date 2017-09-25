package com.lenvar.android.imagepicker;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

abstract class ImagePickerAdapter extends RecyclerView.Adapter<ImageHolder> {

    protected Activity mContext;
    protected LayoutInflater mLayoutInflater;
    protected List<String> mImgList;
    protected List<String> mPickList;
    protected OnPickListener mOnPickListener;
    protected PickerConfig mConfig;

    protected ImagePickerAdapter(Activity mContext, PickerConfig config) {
        this.mContext = mContext;
        this.mLayoutInflater = mContext.getLayoutInflater();
        this.mConfig = config;
        this.mImgList = new ArrayList<>();
        this.mPickList = new ArrayList<>();
    }

    public void set(List<String> imgList) {
        mImgList.clear();
        mImgList.addAll(imgList);
        notifyDataSetChanged();
    }

    public void setOnPickListener(OnPickListener mOnPickListener) {
        this.mOnPickListener = mOnPickListener;
    }

    public List<String> getPickList() {
        return mPickList;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageHolder mImageHolder = new ImageHolder(mLayoutInflater.inflate(R.layout.item_image, parent, false));
        mImageHolder.setConfig(mConfig);
        return mImageHolder;
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, final int position) {
        final String path = mImgList.get(position);

        mConfig.getImageLoader().displayImage(mContext, holder.mImg, path);

        if (mPickList.contains(path)) {
            holder.mRadio.setBackgroundResource(R.drawable.circle);
            holder.mRadio.setText((mPickList.indexOf(path) + 1) + "");
            if (holder.mRadio.getBackground() != null) {
                ((GradientDrawable) holder.mRadio.getBackground()).setColor(mConfig.getStyleColor());
            }
        } else {
            holder.mRadio.setBackgroundResource(mConfig.getTheme() == PickerConfig.ImagePickerTheme.THEME_DRAK ? R.drawable.ring_drak : R.drawable.ring_light);
            holder.mRadio.setText(null);
        }
        holder.setConfig(mConfig);

        holder.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPickList.contains(path)) {
                    holder.mRadio.setBackground(null);
                    mPickList.remove(path);
                    if (mOnPickListener != null)
                        mOnPickListener.onPicker(mPickList.size());
                    holder.mRadio.setBackgroundResource(mConfig.getTheme() == PickerConfig.ImagePickerTheme.THEME_DRAK ? R.drawable.ring_light : R.drawable.ring_drak);
                    holder.mRadio.setText(null);
                    notifyDataSetChanged();
                } else {
                    if (mPickList.size() < mConfig.getMaxPick()) {
                        mPickList.add(path);
                        if (mOnPickListener != null)
                            mOnPickListener.onPicker(mPickList.size());
                        holder.mRadio.setBackgroundResource(R.drawable.circle);
                        holder.mRadio.setText((mPickList.indexOf(path) + 1) + "");
                        if (holder.mRadio.getBackground() != null) {
                            ((GradientDrawable) holder.mRadio.getBackground()).setColor(mConfig.getStyleColor());
                        }
                    } else {
                        Toast.makeText(mContext, "最多选择" + mConfig.getMaxPick() + "张图片", Toast.LENGTH_SHORT).show();
                    }
                }
                holder.setConfig(mConfig);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImgList.size();
    }
}
