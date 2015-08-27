package com.example.service.app;

import android.content.res.Resources;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;

import java.lang.reflect.Field;

/**
 * Created by baziii on 15/8/25.
 */
public class StringMapping {

    public static void main(String[] args) {
       Resources resources= MainApplication.getInstance().getResources();
       for(Field field: R.string.class.getFields()){
        System.out.println(field.getName());
       }
    }

}
