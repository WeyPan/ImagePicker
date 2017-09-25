package com.lenvar.android.imagepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ImageView;

public interface ImageLoader {
    void displayImage(@NonNull Context context, @NonNull ImageView imageView, @NonNull String path);
}
