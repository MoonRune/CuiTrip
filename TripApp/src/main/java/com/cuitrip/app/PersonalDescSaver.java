package com.cuitrip.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lab.utils.LogHelper;
import com.lab.utils.imageupload.CacheDirManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by baziii on 15/7/28.
 */
public class PersonalDescSaver {
    public static final String TAG = "PersonalDescSaver";

    public static void saveAll(final List<Bitmap> bitmapList, final String content) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserConfig.getInstance().setPersonalDesc(content);
                if (bitmapList != null) {
                    for (Bitmap bitmap : bitmapList) {
                        try {
                            save(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static Bitmap getBitmap(String name) {
        return BitmapFactory.decodeFile(CacheDirManager.getInstance().picDir() + "/" + name);
    }

    public static void clean() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserConfig.getInstance().setPersonalDesc("");
                CacheDirManager.clearDir(CacheDirManager.getInstance().picDir()
                );
            }
        }).start();
    }

    private static void save(Bitmap bitmap) throws IOException {
        File var6 = new File(CacheDirManager.getInstance().picDir(), String.valueOf(bitmap.hashCode()));
        FileOutputStream var7 = new FileOutputStream(var6);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, var7);
        var7.flush();
        var7.close();
        LogHelper.e(TAG, "save " + var6.getAbsolutePath());
    }
}
