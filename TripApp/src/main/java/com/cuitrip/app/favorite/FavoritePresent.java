package com.cuitrip.app.favorite;

import com.cuitrip.app.base.FetchCallback;
import com.cuitrip.app.base.IRefreshLoadMoreJumpDeleteView;
import com.cuitrip.app.base.ListFetchCallback;
import com.lab.utils.LogHelper;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoritePresent<T extends FavoriteMode> {
    public static final String TAG = "FavoritePresent";
    IFavoriteFetcher mFavoriteFetcher = new TestFavoriteFetcher();
    IRefreshLoadMoreJumpDeleteView<FavoriteMode> mFavoriteView;

    public FavoritePresent(IRefreshLoadMoreJumpDeleteView<FavoriteMode> iFavoriteView) {
        this.mFavoriteView = iFavoriteView;
    }

    protected class TestFavoriteFetcher implements IFavoriteFetcher {
        @Override
        public void getFavoriteList(ListFetchCallback<FavoriteMode> itemListFetchCallback) {

        }

        @Override
        public void getFavoriteListWithMore(String pattern, ListFetchCallback<FavoriteMode> itemListFetchCallback) {

        }

        @Override
        public void deleteFavorite(FavoriteMode favoriteMode, FetchCallback callback) {

        }
    };

    public void requestLoadMore() {
        mFavoriteView.uiShowLoadMore();
        mFavoriteFetcher.getFavoriteListWithMore("", new ListFetchCallback<FavoriteMode>() {
            @Override
            public void onSuc(List<FavoriteMode> t) {
                LogHelper.e(TAG, "requestLoadMore onscu" + t.size());
                mFavoriteView.renderUIWithAppendData(t);
                mFavoriteView.uiHideLoadMore();
            }

            @Override
            public void onFailed(Throwable throwable) {

                LogHelper.e(TAG, "requestLoadMore onfailed");
                mFavoriteView.uiHideLoadMore();
            }
        });
    }

    public void onClick(T messageMode){
        mFavoriteView.jump(messageMode);
    }

    public void onMove(T messageMode){
        mFavoriteView.jump(messageMode);
    }

    public void requestRefresh() {
        mFavoriteView.uiShowRefreshLoading();
        mFavoriteFetcher.getFavoriteList(new ListFetchCallback<FavoriteMode>() {
            @Override
            public void onSuc(List<FavoriteMode> t) {
                LogHelper.e(TAG, "requestRefresh onsuc" + t.size());
                mFavoriteView.renderUIWithData(t);
                mFavoriteView.uiHideRefreshLoading();
            }

            @Override
            public void onFailed(Throwable throwable) {
                LogHelper.e(TAG, "requestRefresh onfailed");
                mFavoriteView.uiHideRefreshLoading();
            }
        });
    }
}
