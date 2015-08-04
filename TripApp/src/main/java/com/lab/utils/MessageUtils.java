
package com.lab.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;

public class MessageUtils {

    private static Toast mToast = null;

    public static void showToast(int resId) {
        showToast(null, MainApplication.sContext.getString(resId));
    }

    public static void showToast(String tips) {
        showToast(null, (null == tips || TextUtils.isEmpty(tips.trim())) ? "unknow" : tips);
    }

    public static void showToast(Context context, String toastString) {
        if (context == null) {
            context = MainApplication.sContext;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToastCenter(String toastString) {
        if (mToast == null) {
            mToast = Toast.makeText(MainApplication.sContext, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(toastString);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static ProgressDialog getProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("");
        progressDialog.setTitle("");
        return progressDialog;
    }

    public static ProgressDialog getProgressDialog(Context context, int stringId) {
        ProgressDialog progressDialog = getProgressDialog(context);
        progressDialog.setMessage(context.getString(stringId));
        return progressDialog;
    }

    public static ProgressDialog getNoCancelProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = getProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static AlertDialog.Builder createHoloBuilder(Context context) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context, R.style.cuiAppDialog);
        return builder;
    }
//
//    public static AlertDialog.Builder createHoloTranBuilder(Context context) {
//        AlertDialog.Builder builder;
//        builder = new AlertDialog.Builder(context, com.cuitrip.service.R.style.ctDialog);
//        return builder;
//    }


    public static void dialogBuilder(Context context, boolean cancelable, String url, String title, String content,
                                     String okString, final View.OnClickListener onClickListener) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_centent, null);
        builder.setView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_image_iv);
        TextView headerTv = (TextView) view.findViewById(R.id.dialog_name_tv);
        TextView contentTv = (TextView) view.findViewById(R.id.dialog_content_tv);
        TextView btnV = (TextView) view.findViewById(R.id.dialog_click_v);
        if (!TextUtils.isEmpty(url)) {
            imageView.setVisibility(View.VISIBLE);
            ImageHelper.displayCtImage(url, imageView, null);
        } else {
            imageView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(title)) {
            headerTv.setVisibility(View.VISIBLE);
            headerTv.setText(title);
        } else {
            headerTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(content)) {
            contentTv.setVisibility(View.VISIBLE);
            contentTv.setText(content);
        } else {
            contentTv.setVisibility(View.GONE);
        }
        btnV.setText(okString);
        final AlertDialog alertDialog = builder.show();
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v);
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(cancelable);
        alertDialog.setCancelable(cancelable);
        alertDialog.show();
    }
}
