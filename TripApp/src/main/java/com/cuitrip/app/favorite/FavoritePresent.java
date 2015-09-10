package com.cuitrip.app.favorite;

import android.text.TextUtils;

import com.cuitrip.app.base.CtApiCallback;
import com.cuitrip.app.base.CtException;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.RecommendItem;
import com.cuitrip.model.RecommendOutData;
import com.cuitrip.service.R;
import com.cuitrip.util.PlatformUtil;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.loopj.android.http.AsyncHttpClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoritePresent<T extends FavoriteMode> {
    public static final String TAG = "FavoritePresent";
    IFavoriteFetcher mFavoriteFetcher = new FavoriteFetcher();
    IFavoriteListView mFavoriteView;

    public FavoritePresent(IFavoriteListView iFavoriteView) {
        this.mFavoriteView = iFavoriteView;
    }

    protected class FavoriteFetcher implements IFavoriteFetcher {
        int defaultSize = 10;
        AsyncHttpClient mClient = new AsyncHttpClient();

        @Override
        public void getFavoriteList(final ListFetchCallback<FavoriteMode> itemListFetchCallback) {
            ServiceBusiness.getLikes(((FavoriteListActivity) mFavoriteView), mClient, new LabAsyncHttpResponseHandler(RecommendOutData.class) {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    ArrayList<FavoriteMode> result = new ArrayList<>();
                    if (data != null) {
                        RecommendOutData recommendOutData = ((RecommendOutData) data);
                        for (RecommendItem item : recommendOutData.getLists()) {
                            result.add(FavoriteMode.getInstance(item));
                        }
                    }
                    itemListFetchCallback.onSuc(result);
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        msg = response.msg;
                    } else {
                        msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    itemListFetchCallback.onFailed(new CtException(msg));

                }
            }, 0, defaultSize);
        }

        @Override
        public void getFavoriteListWithMore(int pattern, final ListFetchCallback<FavoriteMode> itemListFetchCallback) {
            ServiceBusiness.getLikes(((FavoriteListActivity) mFavoriteView), mClient, new LabAsyncHttpResponseHandler(RecommendOutData.class) {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    LogHelper.e("omg", "result " + response.result);
                    if (data != null) {
                        RecommendOutData recommendOutData = ((RecommendOutData) data);
                        LogHelper.e("omg", "size " + recommendOutData.getLists().size());
                        ArrayList<FavoriteMode> result = new ArrayList<>();
                        for (RecommendItem item : recommendOutData.getLists()) {
                            result.add(FavoriteMode.getInstance(item));
                        }
                        itemListFetchCallback.onSuc(result);
                    }
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg;
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        msg = response.msg;
                    } else {
                        msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    itemListFetchCallback.onFailed(new CtException(msg));

                }
            }, pattern, pattern + defaultSize);
        }

        @Override
        public void deleteFavorite(FavoriteMode favoriteMode, final CtApiCallback callback) {
            //  先删
            callback.onSuc();
            ServiceBusiness.unikeService(((FavoriteListActivity) mFavoriteView), mClient, new LabAsyncHttpResponseHandler() {
                @Override
                public void onSuccess(LabResponse response, Object data) {
                    LogHelper.e("omg", " suc " + String.valueOf(response.result));
                }

                @Override
                public void onFailure(LabResponse response, Object data) {
                    String msg;
                    LogHelper.e("omg", "failed ");
                    if (response != null && !TextUtils.isEmpty(response.msg)) {
                        msg = response.msg;
                    } else {
                        msg = PlatformUtil.getInstance().getString(R.string.data_error);
                    }
                    callback.onFailed(new CtException(msg));

                }
            }, favoriteMode.getId());
        }
    }


    public void requestLoadMore() {
        mFavoriteView.uiShowLoadMore();
        mFavoriteFetcher.getFavoriteListWithMore(mFavoriteView.getSize(), new ListFetchCallback<FavoriteMode>() {
            @Override
            public void onSuc(List<FavoriteMode> t) {
                LogHelper.e(TAG, "requestLoadMore onscu" + t.size());
                mFavoriteView.renderUIWithAppendData(t);
                mFavoriteView.uiHideLoadMore();
            }

            @Override
            public void onFailed(Throwable throwable) {

                MessageUtils.showToast(throwable.getMessage());
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
        mFavoriteFetcher.deleteFavorite(messageMode, new CtApiCallback() {
            @Override
            public void onSuc() {
                mFavoriteView.delete(messageMode);
            }

            @Override
            public void onFailed(CtException throwable) {
                MessageUtils.showToast(throwable.getMessage());
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
                MessageUtils.showToast(throwable.getMessage());
                mFavoriteView.uiHideRefreshLoading();
            }
        });
    }
}
