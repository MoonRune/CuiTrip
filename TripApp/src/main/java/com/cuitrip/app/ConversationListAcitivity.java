package com.cuitrip.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by baziii on 15/8/6.
 */
public class ConversationListAcitivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversationlist); /*加载您上面的 conversationlist */

        /* 创建 conversationlist 的Fragment */
        ConversationListFragment fragment =new ConversationListFragment();

        /* 给 IMKit 传递默认的参数，用于显示*/
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话采用聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true") //设置群组会话采用聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false") // 设置讨论组不采用聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true") //设置系统会话采用聚合显示
                .build();

        fragment.setUri(uri);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.conversationlist,fragment).commit();

    }
}
