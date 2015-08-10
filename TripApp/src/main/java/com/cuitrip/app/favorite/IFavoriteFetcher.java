package com.cuitrip.app.favorite;

import com.cuitrip.app.base.FetchCallback;
import com.cuitrip.app.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/10.
 */
public interface IFavoriteFetcher {
    void getFavoriteList(ListFetchCallback<FavoriteMode> itemListFetchCallback);
    void getFavoriteListWithMore(String pattern,ListFetchCallback<FavoriteMode> itemListFetchCallback);
    void deleteFavorite(FavoriteMode favoriteMode,FetchCallback callback);
}
