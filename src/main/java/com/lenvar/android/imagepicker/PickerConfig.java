package com.lenvar.android.imagepicker;

import android.support.annotation.ColorInt;

public class PickerConfig {

    public enum ImagePickerTheme {
        THEME_DRAK, THEME_LIGHT;
    }

    private ImageLoader mImageLoader;

    private OnPickCompleteListener mOnPickCompleteListener;

    @ColorInt
    private int mStyleColor = -1;

    private ImagePickerTheme mTheme = ImagePickerTheme.THEME_DRAK;

    private int mMaxPick = 9;

    private PickerConfig(ImageLoader mImageLoader) {
        this.mImageLoader = mImageLoader;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public int getStyleColor() {
        return mStyleColor;
    }

    public ImagePickerTheme getTheme() {
        return mTheme;
    }

    public OnPickCompleteListener getOnPickCompleteListener() {
        return mOnPickCompleteListener;
    }

    public int getMaxPick() {
        return mMaxPick;
    }

    public static class Builder {

        private PickerConfig mPickerConfig;

        public Builder(ImageLoader mImageLoader) {
            mPickerConfig = new PickerConfig(mImageLoader);
        }

        public Builder setStyleColor(@ColorInt int color) {
            mPickerConfig.mStyleColor = color;
            return this;
        }

        public Builder setTheme(ImagePickerTheme theme) {
            mPickerConfig.mTheme = theme;
            return this;
        }

        public Builder setMaxPick(int maxPick) {
            if (maxPick > 9)
                maxPick = 9;
            if (maxPick <= 0)
                maxPick = 1;
            mPickerConfig.mMaxPick = maxPick;
            return this;
        }

        public Builder setOnPickCompleteListener(OnPickCompleteListener listener) {
            mPickerConfig.mOnPickCompleteListener = listener;
            return this;
        }

        public PickerConfig build() {
            return mPickerConfig;
        }
    }
}
