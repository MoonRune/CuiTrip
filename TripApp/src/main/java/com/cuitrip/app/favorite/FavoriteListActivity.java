package com.cuitrip.app.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.app.base.BaseVerticalListActivity;
import com.cuitrip.service.R;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoriteListActivity extends BaseVerticalListActivity<FavoriteMode> implements IFavoriteListView {
    public static final String TAG = "FavoriteListActivity";
    FavoritePresent<FavoriteMode> mFavoritePresent = new FavoritePresent<>(this);
    public static void start(Context context) {
        if (context != null) {
            context.startActivity(new Intent(context, FavoriteListActivity.class));
        }
    }
    protected View.OnClickListener mOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ct_service:
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

    FavoriteAdapter mAdapter = new FavoriteAdapter(mOnClickListener);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView.setAdapter(mAdapter);
        SlideInLeftAnimator animator =  new SlideInLeftAnimator();
        mRecyclerView.setItemAnimator(animator);
        requestPresentRefresh();
    }
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
        mAdapter.appendModeList(items);
    }

    @Override
    public void renderUIWithData(List items) {
        mAdapter.appendModeList(items);
    }

    @Override
    public void jump(FavoriteMode messageMode) {

    }

    @Override
    public void delete(FavoriteMode messageMode) {
        mAdapter.remove(messageMode);

    }

    @Override
    public void jumpUnvaliable(FavoriteMode mode) {
        FavorityUnvaliableActivity.start(FavoriteListActivity.this);

    }
}
