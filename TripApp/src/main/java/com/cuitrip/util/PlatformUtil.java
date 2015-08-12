package com.cuitrip.util;

/**
 * Created by baziii on 15/8/12.
 */
public class PlatformUtil implements IResourceFetcher{
    public static  PlatformUtil sPlatformUtil =new PlatformUtil();
    private PlatformUtil(){

    }
    private IResourceFetcher mResourceFetcher;

    public IResourceFetcher getmResourceFetcher() {
        return mResourceFetcher;
    }

    public void setmResourceFetcher(IResourceFetcher mResourceFetcher) {
        this.mResourceFetcher = mResourceFetcher;
    }

    public static PlatformUtil getInstance() {
        return sPlatformUtil;
    }

    @Override
    public String getString(int id) {
        return mResourceFetcher.getString(id);
    }
}
