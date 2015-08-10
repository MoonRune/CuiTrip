package com.cuitrip.app.favorite;

import android.view.View;
import android.widget.TextView;

import com.cuitrip.app.pro.ServiceNonViewHolder;
import com.cuitrip.app.base.RecycleViewHolder;
import com.cuitrip.service.R;
import com.lab.widget.CanClickSwipeLayout;
import com.malinskiy.superrecyclerview.swipe.SwipeLayout;

import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoriteViewHolder extends RecycleViewHolder<FavoriteMode> {
    public static int RES = R.layout.favorite_item;
    ServiceNonViewHolder serviceNonViewHolder;
    @InjectView(R.id.ct_delete)
    TextView mDelete;
    @InjectView(R.id.ct_swip_layout)
    CanClickSwipeLayout mSwipLayout;
    @InjectView(R.id.ct_service)
    View mFavoriteLayout;


    public FavoriteViewHolder(View itemView,View.OnClickListener mOnClickListener) {
        super(itemView);
        mSwipLayout.setDragEdge(SwipeLayout.DragEdge.Right);
        mSwipLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        mFavoriteLayout.setOnClickListener(mOnClickListener);
        mDelete.setOnClickListener(mOnClickListener);
        serviceNonViewHolder = new ServiceNonViewHolder(mFavoriteLayout);
    }

    @Override
    public void render(FavoriteMode favoriteMode) {
        serviceNonViewHolder.render(favoriteMode.buildRecommendRenderData());
        mFavoriteLayout.setTag(favoriteMode);
        mDelete.setTag(favoriteMode);

    }
}
