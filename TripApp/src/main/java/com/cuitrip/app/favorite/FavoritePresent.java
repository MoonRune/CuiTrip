package com.cuitrip.app.favorite;

import android.os.AsyncTask;

import com.cuitrip.app.base.FetchCallback;
import com.cuitrip.app.base.ListFetchCallback;
import com.lab.utils.LogHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoritePresent<T extends FavoriteMode> {
    public static final String TAG = "FavoritePresent";
    IFavoriteFetcher mFavoriteFetcher = new TestFavoriteFetcher();
    IFavoriteView mFavoriteView;

    public FavoritePresent(IFavoriteView iFavoriteView) {
        this.mFavoriteView = iFavoriteView;
    }

    protected class TestFavoriteFetcher implements IFavoriteFetcher {
        int i;

        @Override
        public void getFavoriteList(final ListFetchCallback<FavoriteMode> itemListFetchCallback) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    List<FavoriteMode> result = new ArrayList<>();
                    for (i = 0; i < 10; i++) {
                        result.add(new FavoriteMode("id" + i, "name" + i, "headPic" + i, "address" + i,
                                "author name", "aurhot ava", "author career", false));
                    }
                    itemListFetchCallback.onSuc(result);
                    super.onPostExecute(o);
                }
            }.execute();
        }

        @Override
        public void getFavoriteListWithMore(String pattern, final ListFetchCallback<FavoriteMode> itemListFetchCallback) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    List<FavoriteMode> result = new ArrayList<>();
                    int max = i + 10;
                    for (; i < max; i++) {
                        result.add(new FavoriteMode("id" + i, "name" + i, "headPic" + i, "address" + i,
                                "author name", "aurhot ava", "author career", true));
                    }
                    itemListFetchCallback.onSuc(result);
                    super.onPostExecute(o);
                }
            }.execute();
        }

        @Override
        public void deleteFavorite(FavoriteMode favoriteMode, final FetchCallback callback) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    callback.onSuc();
                    super.onPostExecute(o);
                }
            }.execute();

        }
    }

    ;

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

    public void onClick(T messageMode) {
        if (messageMode.isAvaliable()) {
            mFavoriteView.jump(messageMode);
        } else {
            mFavoriteView.jumpUnvaliable(messageMode);
//            onMove(messageMode);
        }
    }

    public void onMove(final T messageMode) {
        mFavoriteFetcher.deleteFavorite(messageMode, new FetchCallback() {
            @Override
            public void onSuc() {
                mFavoriteView.delete(messageMode);
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
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
