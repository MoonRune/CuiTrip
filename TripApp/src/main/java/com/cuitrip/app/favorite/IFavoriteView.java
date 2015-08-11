package com.cuitrip.app.favorite;

import com.cuitrip.app.base.IRefreshLoadMoreJumpDeleteView;

/**
 * Created by baziii on 15/8/11.
 */
public interface IFavoriteView extends IRefreshLoadMoreJumpDeleteView<FavoriteMode> {
    void jumpUnvaliable(FavoriteMode mode);
}
