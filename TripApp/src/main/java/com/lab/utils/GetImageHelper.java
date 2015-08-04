package com.lab.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.cuitrip.app.MainApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created on 7/25.
 */
public class GetImageHelper {

    public static final int MAX_WIDTH = 1000;
    public static final int MAX_HEIGHT = 1980;

    public static int getInSampleSize(BitmapFactory.Options options) {
        return getInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
    }

    public static int getInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > width){ //控制高度
            if(width > reqWidth){
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }else{
            if(width > reqWidth){
                inSampleSize = (int) Math.floor((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static String getResizedBitmapString(Activity activity, Uri uri){
        Bitmap bp = getResizedBitmap(activity, uri);
        if(bp == null){
            return null;
        }else {
            String tmp = bitmapToBase64String(bp);
            bp.recycle();
            return tmp;
        }
    }

    public static Bitmap getResizedBitmap(Activity activity, Uri uri) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = activity.getContentResolver().openInputStream(uri);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            is.close();
            is = activity.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options1 = new BitmapFactory.Options();
            options1.inSampleSize = getInSampleSize(options);
            options1.inJustDecodeBounds = false;
            Bitmap temp = BitmapFactory.decodeStream(is, null, options1);
            int mExifRotation = getExifRotation(getFromMediaUri(activity.getContentResolver(), uri));
            if (mExifRotation != 0) {
                Matrix mtx = new Matrix();
                mtx.postRotate(mExifRotation);
                bitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(),
                        mtx, true);
                temp.recycle();
            } else {
                bitmap = temp;
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return bitmap;
    }

    //将Base64转换成bitmap
    public static Bitmap base64StringToBitmap(String str) {
        if(str == null){
            return null;
        }
        byte[] bytes;
        bytes = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String bitmapToBase64String(Bitmap bm) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        byte[] b = bos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static File getFromMediaUri(ContentResolver resolver, Uri uri) {
        if (uri == null) return null;
        if ("file".equals(uri.getScheme())) {
            return new File(uri.getPath());
        } else if ("content".equals(uri.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uri.toString().startsWith("content://com.google.android.gallery3d")) ?
                            cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME) :
                            cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return new File(filePath);
                        }
                    }
                }
            } catch (SecurityException ignored) {
            } finally {
                if (cursor != null) cursor.close();
            }
        }
        return null;
    }

    public static int getExifRotation(File imageFile) {
        if (imageFile == null) return 0;
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            LogHelper.e("Error getting Exif data", e.getMessage());
            return 0;
        }
    }
    public static Bitmap resizebitmapToFitScreen(Bitmap bm) {
        try {
            int width = MainApplication.sContext.getApplicationContext()
                    .getResources().getDisplayMetrics().widthPixels;
            return resizeBitmapToFitWidth(bm, width);
        }catch (Exception e){
            e.printStackTrace();
            return bm;
        }
    }

    public static Bitmap resizeBitmapToFitWidth(Bitmap bm, int width) {
        int oriWidth = bm.getWidth();
        int oriHeight = bm.getHeight();
        if(oriWidth < width){
            return bm;
        }else{
            int height = (int)((float)width / oriWidth * oriHeight);
            Bitmap tmp = Bitmap.createScaledBitmap(bm, width, height, false);
            bm.recycle();
            return tmp;
        }
    }
}
