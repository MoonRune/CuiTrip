package com.cuitrip.app.message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cuitrip.app.base.BaseVerticalListActivity;
import com.cuitrip.service.R;
import com.lab.app.BrowserActivity;
import com.lab.utils.LogHelper;
import com.malinskiy.superrecyclerview.swipe.SwipeItemManagerInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageListActivity extends BaseVerticalListActivity implements IMessageView {
    public static final String TAG = "MessageListActivity";
    MessagePresent mMessagePresent = new MessagePresent(this);

    protected View.OnClickListener mOnClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ct_message_layout:
                    if (v.getTag()!=null &&v.getTag() instanceof MessageMode) {
                        MessageMode messageMode = (MessageMode) v.getTag();
                        mMessagePresent.onClickMessage(messageMode);
                    }
                    break;
                case R.id.ct_delete:
                    if (v.getTag()!=null &&v.getTag() instanceof MessageMode) {
                        MessageMode messageMode = (MessageMode) v.getTag();
                        mMessagePresent.onMove(messageMode);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    MessageAdapter mAdapter = new MessageAdapter(mOnClickListener);
    public static void start(Context context) {
        if (context != null) {
            context.startActivity(new Intent(context, MessageListActivity.class));
        }
    }
    RecyclerView.ItemAnimator mItemAnimator = new RecyclerView.ItemAnimator() {
        List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<RecyclerView.ViewHolder>();
        List<RecyclerView.ViewHolder> mAnimationRemoveViewHolders = new ArrayList<RecyclerView.ViewHolder>();
        //需要执行动画时会系统会调用，用户无需手动调用
        @Override
        public void runPendingAnimations() {
            if (!mAnimationAddViewHolders.isEmpty()) {

                AnimatorSet animator;
                View target;
                for (final RecyclerView.ViewHolder viewHolder : mAnimationAddViewHolders) {
                    target = viewHolder.itemView;
                    animator = new AnimatorSet();

                    animator.playTogether(
                            ObjectAnimator.ofFloat(target, "translationX", -target.getMeasuredWidth(), 0.0f),
                            ObjectAnimator.ofFloat(target, "alpha", target.getAlpha(), 1.0f)
                    );

                    animator.setTarget(target);
                    animator.setDuration(100);
                    animator.addListener(new AnimatorListenerAdapter() {

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mAnimationAddViewHolders.remove(viewHolder);
                            if (!isRunning()) {
                                dispatchAnimationsFinished();
                            }
                        }

                    });
                    animator.start();
                }
            }
            else if(!mAnimationRemoveViewHolders.isEmpty()){
            }
        }
        //remove时系统会调用，返回值表示是否需要执行动画
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
            return mAnimationRemoveViewHolders.add(viewHolder);
        }

        //viewholder添加时系统会调用
        @Override
        public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
            return mAnimationAddViewHolders.add(viewHolder);
        }

        @Override
        public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i2, int i3, int i4) {
            return false;
        }

        @Override
        public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
            return false;
        }

        @Override
        public void endAnimation(RecyclerView.ViewHolder viewHolder) {
        }

        @Override
        public void endAnimations() {
        }

        @Override
        public boolean isRunning() {
            return !(mAnimationAddViewHolders.isEmpty()&&mAnimationRemoveViewHolders.isEmpty());
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter.setMode(SwipeItemManagerInterface.Mode.Single);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(mItemAnimator);
        requestPresentRefresh();
    }

    /** 最初refresh的时候 无法显示refresh状态
     *  TODO  GOOGLE ISSUE地址 :
     */



    @Override
    public void jumpMessage(MessageMode messageMode) {
        LogHelper.e(TAG,"jumpMessage :"+messageMode.toString());
        BrowserActivity.startWithData(this,messageMode.content);
    }

    @Override
    public void deleteMessage(MessageMode messageMode) {
            mAdapter.removeMessageMode(messageMode);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void uiShowLoadMore() {
        onLoadStarted();
        //foot view show
    }

    @Override
    public void uiHideLoadMore() {
        onLoadFinished();
        //foot view hiden
    }

    @Override
    public void uiShowRefreshLoading() {
        super.uiShowRefreshLoading();
        onLoadStarted();
    }

    @Override
    public void uiHideRefreshLoading() {
        super.uiHideRefreshLoading();
        onLoadFinished();
    }

    @Override
    public void renderUIWithData(List<MessageMode> items) {
        mAdapter.setmModeList(items);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void renderUIWithAppendData(List<MessageMode> items) {
        mAdapter.appendModeList(items);
        mAdapter.notifyDataSetChanged();

    }
    @Override
    public void disableEvent() {
    }

    @Override
    public void enableEvent() {
    }

    @Override
    public void requestPresentLoadMore() {
        uiShowLoadMore();
        mMessagePresent.requestLoadMore();
    }

    @Override
    public void requestPresentRefresh() {
        uiHideLoadMore();
        mMessagePresent.requestRefresh();

    }
}
