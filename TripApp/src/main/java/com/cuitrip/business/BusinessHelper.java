package com.cuitrip.business;


import com.lab.network.LabResponse;

public class BusinessHelper {
//    public static final String BASE_URL = "http://api.cuitrip.com/baseservice/";
    //public static final String BASE_URL = "http://42.121.16.186:9999/baseservice/";
    public static final String BASE_URL = "http://58.96.175.29:8080/baseservice/";

    public static String getApiUrl(String api){
        return BASE_URL + api;
    }

    public static boolean isTokenInvalided(LabResponse response){
        return response != null && response.code == 1;
    }
}
