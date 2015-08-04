package com.lab.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.cuitrip.service.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created on 7/11.
 */
public class ImageHelper {
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        //config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public static DisplayImageOptions getDefaultDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_stub)
//                .showImageForEmptyUri(R.drawable.ic_empty)
//                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                //.displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    public static DisplayImageOptions getPersonDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ct_default_user)
                .showImageForEmptyUri(R.drawable.ct_default_user)
                .showImageOnFail(R.drawable.ct_default_user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    public static DisplayImageOptions getCtDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ct_default)
                .showImageForEmptyUri(R.drawable.ct_default)
                .showImageOnFail(R.drawable.ct_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    public static void displayImage(String url, ImageView imageView, DisplayImageOptions options){
        ImageLoader.getInstance().displayImage(url,imageView,
                options == null ? getDefaultDisplayImageOptions() : options);
    }

    public static void displayPersonImage(String url, ImageView imageView, DisplayImageOptions options){
        ImageLoader.getInstance().displayImage(url,imageView,
                options == null ? getPersonDisplayImageOptions() : options);
    }

    public static void displayCtImage(String url, ImageView imageView, DisplayImageOptions options){
        ImageLoader.getInstance().displayImage(url,imageView,
                options == null ? getCtDisplayImageOptions() : options);
    }

    public static void displayImage(String url, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener){
        ImageLoader.getInstance().displayImage(url, imageView,
                options == null ? getDefaultDisplayImageOptions() : options, listener);
    }

}
