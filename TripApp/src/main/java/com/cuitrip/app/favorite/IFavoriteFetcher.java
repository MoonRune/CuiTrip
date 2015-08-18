package com.cuitrip.app.favorite;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.ListFetchCallback;

/**
 * Created by baziii on 15/8/10.
 */
public interface IFavoriteFetcher {
    void getFavoriteList(ListFetchCallback<FavoriteMode> itemListFetchCallback);
    void getFavoriteListWithMore(int pattern,ListFetchCallback<FavoriteMode> itemListFetchCallback);
    void deleteFavorite(FavoriteMode favoriteMode,CtApiCallback callback);
}
