package com.lab.utils;

import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;

import java.util.HashMap;

/**
 * Created by baziii on 15/8/1.
 */
public class CurrencyHelper {
    private static HashMap<String, String> currencyMap = new HashMap<>();
    private static CurrencyHelper currencyHelper;

    private CurrencyHelper() {
        currencyMap.put("cny", MainApplication.sContext.getString(R.string.ct_cny));
        currencyMap.put("wtd", MainApplication.sContext.getString(R.string.ct_twd));
    }

    public static CurrencyHelper getInstance() {
        if (currencyHelper == null) {
            synchronized (CurrencyHelper.class) {
                if (currencyHelper == null) {
                    currencyHelper = new CurrencyHelper();
                }
            }
        }
        return currencyHelper;
    }

    public String getCurrencyName(String code) {
        String lowerCaseCode = code.toLowerCase();
        if (currencyMap.containsKey(lowerCaseCode)){
            return currencyMap.get(lowerCaseCode);
        }
        return code;
    }


}
