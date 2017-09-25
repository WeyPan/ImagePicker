package com.lenvar.android.imagepicker;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

class ImageHolder extends RecyclerView.ViewHolder {

    public ImageView mImg;
    public TextView mRadio;
    public View click;

    public ImageHolder(View itemView) {
        super(itemView);
        mImg = (ImageView) itemView.findViewById(R.id.image);
        mRadio = (TextView) itemView.findViewById(R.id.number);
        click = itemView.findViewById(R.id.click);
    }

    public void setSize(int windowWidth) {
        if (itemView != null) {
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = windowWidth / AlbumPicker.GALLERY_GRID_COUNT;
            params.height = params.width - (int) (params.width * 0.2);
            itemView.setLayoutParams(params);
        }
    }

    public void setConfig(PickerConfig config) {
        if (PickerConfig.ImagePickerTheme.THEME_LIGHT == config.getTheme()) {
            mRadio.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            mRadio.setTextColor(Color.parseColor("#333333"));
        }
    }
}
