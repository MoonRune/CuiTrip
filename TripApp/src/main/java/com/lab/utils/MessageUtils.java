
package com.lab.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class MessageUtils {

    private static Toast mToast = null;

    public interface DateCheckListener {
        void onDataSelect(String s);
    }

    public static boolean isAfter5() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
    }

    public static void showDateCheck(FragmentActivity context, final DateCheckListener dateCheckListener) {
        AlertDialog.Builder builder = MessageUtils.createHoloBuilder(context);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+800"));
        if (isAfter5())
        {
            final DatePicker datePicker = new DatePicker(context);
            int year = calendar.get(Calendar.YEAR);
            int monthOfYear = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            datePicker.init(year, monthOfYear, dayOfMonth, new DatePicker.OnDateChangedListener() {

                public void onDateChanged(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                    LogHelper.e("omg", "onDateChanged");
                }

            });
            builder.setView(datePicker);
            builder.setPositiveButton(R.string.ct_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (dateCheckListener != null) {
                        dateCheckListener.onDataSelect(datePicker.getYear() + "-" +
                                fillToTwo(datePicker.getMonth() + 1)
                                + "-" + fillToTwo(datePicker.getDayOfMonth()));
                    }
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.show();
            dialog.show();
        }
        else
        {

            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePickerDialog datePickerDialog, int i, int i1, int i2) {
                            if (dateCheckListener != null) {
                                dateCheckListener.onDataSelect(i + "-" +
                                        fillToTwo(i1+1)
                                        + "-" + fillToTwo(i2));
                                datePickerDialog.dismiss();
                            }
                        }
                    },
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
//            dpd.setThemeDark(modeDarkDate.isChecked());
//            dpd.vibrate(vibrateDate.isChecked());
//            dpd.dismissOnPause(dismissDate.isChecked());
//            if (modeCustomAccentDate.isChecked()) {
//                dpd.setAccentColor(Color.parseColor("#9C27B0"));
//            }
            dpd.setAccentColor(PlatformUtil.getInstance().getColor(R.color.ct_main_color_light));
            dpd.show(context.getFragmentManager(), "Datepickerdialog");

//            DatePickerDialog dpd = DatePickerDialog.newInstance(
//                    context,
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH)
//            );
//            dpd.show(getFragmentManager(), "Datepickerdialog");
        }
    }

    public static String fillToTwo(int value) {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(value);
    }

    public static void showToast(int resId) {
        showToast(null, MainApplication.getInstance().getString(resId));
    }

    public static void showToast(String tips) {
        showToast(null, (null == tips || TextUtils.isEmpty(tips.trim())) ? "unknow" : tips);
    }

    public static void showToast(Context context, String toastString) {
        if (context == null) {
            context = MainApplication.getInstance();
        }
        if (mToast == null) {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToastCenter(String toastString) {
        if (mToast == null) {
            mToast = Toast.makeText(MainApplication.getInstance(), "", Toast.LENGTH_SHORT);
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

    public static AlertDialog.Builder createBuilder(Context context) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
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

    public static void dialogBuilderInput(Context context, boolean cancelable, String url, String title,
                                          String okString, final setMessageListener setMessageListener) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_centent_input, null);
        builder.setView(view);
        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_image_iv);
        TextView headerTv = (TextView) view.findViewById(R.id.dialog_name_tv);
        final TextView contentTv = (TextView) view.findViewById(R.id.dialog_content_tv);
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

        btnV.setText(okString);
        final AlertDialog alertDialog = builder.show();
        btnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(contentTv.getText().toString())) {
                    showToast(R.string.please_input);
                    return;
                }
                if (setMessageListener != null) {
                    setMessageListener.setMessage(contentTv.getText().toString());
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(cancelable);
        alertDialog.setCancelable(cancelable);
        alertDialog.show();
    }

    public interface setMessageListener {
        void setMessage(String s);
    }
}
