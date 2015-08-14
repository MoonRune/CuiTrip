package com.lab.utils.imageupload;

import android.os.Environment;
import android.text.TextUtils;

import com.cuitrip.app.MainApplication;
import com.lab.utils.LogHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by bqf on 15/1/30.
 */
public class CacheDirManager {
    public static final String TAG = "CacheDirManager";
    private static final String APP_BASE_DIR = "/.cuitrip";
    private static String APP_CACHE_DIR;
    private static volatile CacheDirManager instance;

    private CacheDirManager() {
    }

    public static CacheDirManager getInstance() {
        if (instance == null) {
            synchronized (CacheDirManager.class) {
                if (instance == null) {
                    instance = new CacheDirManager();
                }
            }
        }
        return instance;
    }

    private String getBaseDir() {
        if (TextUtils.isEmpty(APP_CACHE_DIR)) {
            APP_CACHE_DIR = validateBaseDir();
        }
        return APP_CACHE_DIR;
    }

    private String validateBaseDir() {
        String nowPath = null;
        File tmpFile = null;
        tmpFile = MainApplication.sContext.getExternalCacheDir();
        if (tmpFile != null && validataPath(nowPath = tmpFile.getAbsolutePath())) {
            LogHelper.e(TAG, "getExternalCacheDir=" + nowPath);
            return nowPath;
        }
        tmpFile = MainApplication.sContext.getExternalCacheDir();
        if (tmpFile != null && validataPath(nowPath = tmpFile.getAbsolutePath())) {
            LogHelper.e(TAG, "getCacheDir=" + nowPath);
            return nowPath;
        }
        LogHelper.e(TAG, "getExternalStorageDirectory="
                + Environment.getExternalStorageDirectory().getAbsolutePath()
                + APP_BASE_DIR
                + "/data");
        return Environment.getExternalStorageDirectory().getAbsolutePath() + APP_BASE_DIR + "/data";
    }

    protected boolean validataPath(String path) {
        File noMedia = new File(path + "/.nomedia");
        if (noMedia.exists()) {
            return true;
        }
        new File(path).mkdirs();
        try {
            if (noMedia.createNewFile()) {
                return true;
            }
        } catch (IOException e) {

        }
        return false;
    }

    public static void clearDir(String dirname) {
        File dir = new File(dirname);
        if (dir.exists()) {
            if (dir.listFiles() != null) {
                for (File temp : dir.listFiles()) {
                    if (temp.isDirectory()) {
                        clearDir(temp);
                    } else {
                        temp.delete();
                    }
                }
            }
        }
    }

    public static void clearDir(File dir) {
        if (dir.listFiles() != null) {
            for (File temp : dir.listFiles()) {
                if (temp.isDirectory()) {
                    clearDir(temp);
                } else {
                    temp.delete();
                }
            }
        }
    }

    public String getAppCacheDir() {
        return APP_CACHE_DIR;
    }

    public String picDir() {
        return getFileDir("/pic");
    }
    public String cameraDir() {
        return getFileDir("/camera")+"/temp";
    }

    private String getFileDir(String fileName) {
        String dir = getBaseDir() + fileName;
        if (!new File(dir).exists()) {
            new File(dir).mkdirs();
        }
        return dir;
    }
}
