package com.lab.network;

import android.text.TextUtils;
import com.alibaba.fastjson.JSONObject;
import com.lab.utils.LogHelper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public class LabResponse {

//    {
//        "code": 200,
//        "msg": "服务器忙",
//        "result": null
//    }

    public int code;
    public String msg;
    public Object result;
    public String  moneyType;
    public String  balance;
    public String  rate;
//    "moneyType": "CNY",		// 余额显示币种
//            "balance": "2000.00",		// 账户余额
//            "rate": "640.08",		// 100 美元换算成moneyType 的价值

    public static LabResponse parseResponse(String response) {
        LogHelper.d("LabResponse", "response: " + response);
        JSONObject json = JSONObject.parseObject(response);
        Integer code = json.getInteger("code");
        LabResponse resp = new LabResponse();
        resp.code = code != null ? code : -1;
        resp.msg = json.getString("msg");
        resp.moneyType = json.getString("moneyType");
        resp.balance = json.getString("balance");
        resp.rate = json.getString("rate");
        try{
            resp.result = json.getJSONObject("result");
        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof ClassCastException){
                try{
                    resp.result = json.getJSONArray("result");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
        return resp;
    }

    public static LabResponse parseResponse(byte[] bytedata)
            throws UnsupportedEncodingException {
        return parseResponse(new String(bytedata, "UTF-8"));
    }

    public Object getDataValue(String valueKey, Class clazz) {
        if (TextUtils.isEmpty(valueKey)) throw new NullPointerException("valueKey is null");
        if (result == null) throw new LabResponseException(msg, code);
        if (!result.getClass().equals(clazz)) return null;
        try {
            Method method = clazz != null
                ? clazz.getMethod("get"
                    + new String(new char[]{valueKey.charAt(0)}).toUpperCase(Locale.US)
                    + (valueKey.length() > 1 ? valueKey.substring(1) : ""))
                : result.getClass().getMethod("get"
                    + new String(new char[]{valueKey.charAt(0)}).toUpperCase(Locale.US)
                    + (valueKey.length() > 1 ? valueKey.substring(1) : ""));
            Object value = method.invoke(result);
            return value;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class LabResponseException extends RuntimeException {
        public int code;
        LabResponseException(String msg, int code) {
            super(msg);
            this.code = code;
        }
    }
}
