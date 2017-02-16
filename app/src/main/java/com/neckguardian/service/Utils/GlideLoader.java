package com.neckguardian.service.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Jiar on 2016/1/25.
 */
public class GlideLoader implements com.yancy.imageselector.ImageLoader {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(com.yancy.imageselector.R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }
}
