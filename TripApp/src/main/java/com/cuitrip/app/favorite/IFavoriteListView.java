package com.cuitrip.app.favorite;

import com.cuitrip.app.base.IRefreshLoadMoreJumpDeleteListView;

/**
 * Created by baziii on 15/8/11.
 */
public interface IFavoriteListView extends IRefreshLoadMoreJumpDeleteListView<FavoriteMode> {
    void jumpUnvaliable(FavoriteMode mode);
}
