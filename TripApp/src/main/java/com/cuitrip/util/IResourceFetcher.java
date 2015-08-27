package com.cuitrip.util;

/**
 * Created by baziii on 15/8/12.
 */
public interface IResourceFetcher {
    String getString(int id);
    String getString(int id,Object...objs);
    int getColor(int id);
}
