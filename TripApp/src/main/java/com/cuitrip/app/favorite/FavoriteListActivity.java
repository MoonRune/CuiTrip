package com.cuitrip.app.favorite;

import android.view.View;

import com.cuitrip.app.base.BaseVerticalListActivity;
import com.cuitrip.app.base.IRefreshLoadMoreJumpDeleteView;
import com.cuitrip.app.message.MessagePresent;
import com.cuitrip.service.R;

import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoriteListActivity extends BaseVerticalListActivity<FavoriteMode> implements IRefreshLoadMoreJumpDeleteView<FavoriteMode> {
    public static final String TAG = "FavoriteListActivity";
    FavoritePresent<FavoriteMode> mFavoritePresent = new MessagePresent(this);

    protected View.OnClickListener mOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ct_message_layout:
                    if (v.getTag()!=null &&v.getTag() instanceof FavoriteMode) {
                        FavoriteMode messageMode = (FavoriteMode) v.getTag();
                        mFavoritePresent.onClick(messageMode);
                    }
                    break;
                case R.id.ct_delete:
                    if (v.getTag()!=null &&v.getTag() instanceof FavoriteMode) {
                        FavoriteMode messageMode = (FavoriteMode) v.getTag();
                        mFavoritePresent.onMove(messageMode);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void requestPresentLoadMore() {
        uiShowLoadMore();
        mFavoritePresent.requestLoadMore();
    }

    @Override
    public void requestPresentRefresh() {
        uiHideLoadMore();
        mFavoritePresent.requestRefresh();

    }

    @Override
    public void renderUIWithAppendData(List items) {

    }

    @Override
    public void renderUIWithData(List items) {

    }

    @Override
    public void jump(FavoriteMode messageMode) {

    }

    @Override
    public void delete(FavoriteMode messageMode) {

    }
}
