package com.lab.utils;

/**
 * Created on 7/17.
 */
public class Constants {

    public static final String CT_HTML_HEAD = "<html><head><meta http-equiv=\"Content-Type\" content=\"charset=utf-8\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"> <style type=\"text/css\">body{margin:24px 15px 15px 15px;font-size:15px;line-height:1.5;background-color:#fff;font-size:15px;color:rgb(124,124,124)}p.maintitle{font-size:21px;color:rgb(60,60,60);text-align:center;}p.subtitle{font-size:17px;color:rgb(60,60,60);text-align:center;}img{margin-top:5px;margin-bottom:5px}</style></head><body>";

    public static final String CT_HTML_TITLE = "<p class=\"maintitle\">";

    public static final String CT_HTML_NO_TITLE = "<p>";

    public static final String CT_HTML_MID = "</p><p>";

    public static final String CT_HTML_END = "</p></body></html>";

    public static final String IMAGE_URL_HEAD = "<div><img src=\"";

    public static final String IMAGE_URL_END = "\" width=\"100%\"/></div>";

    public static final String IMAGE_PATTERN = "<div>\\s*<img\\s+src=\\s*\"([^\"]*)\"\\s+width=[^/>]*/>\\s*</div>";

    public static String dealHtmlLine(String input){
        return input == null ? null : input.replaceAll("\n", "</p><p>");
    }
}
