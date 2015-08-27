package com.cuitrip.app.message;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cuitrip.app.base.BaseItemDecoration;
import com.cuitrip.app.base.BaseVerticalListActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;
import com.lab.utils.Utils;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by baziii on 15/8/10.
 */
public class MessageListActivity extends BaseVerticalListActivity<MessageMode> implements IMessageListView {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showActionBar(getString(R.string.message_title));
        mRecyclerView.setAdapter(mAdapter);
        SlideInLeftAnimator animator =  new SlideInLeftAnimator();
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.addItemDecoration(new BaseItemDecoration(0,0,Utils.dp2pixel(1),0));
        requestPresentRefresh();
    }

    /** 最初refresh的时候 无法显示refresh状态
     *  TODO  GOOGLE ISSUE地址 :
     */



    @Override
    public void jumpMessage(MessageMode messageMode) {
        LogHelper.e(TAG,"jumpMessage :"+messageMode.toString());
//        BrowserActivity.startWithData(this,messageMode.content);
        MessageDetailActivity.start(this,messageMode);
    }

    @Override
    public void deleteMessage(MessageMode messageMode) {
            mAdapter.removeMessageMode(messageMode);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getSize() {
        return mAdapter.getItemCount();
    }

    @Override
    public void renderUIWithData(List<MessageMode> items) {
        mAdapter.setModeList(items);
//        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void renderUIWithAppendData(List<MessageMode> items) {
        mAdapter.appendModeList(items);
//        mAdapter.notifyDataSetChanged();

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
