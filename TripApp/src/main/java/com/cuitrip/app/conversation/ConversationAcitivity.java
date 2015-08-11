package com.cuitrip.app.conversation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.cuitrip.service.R;
import com.lab.app.BaseActivity;

import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by baziii on 15/8/6.
 */
public class ConversationAcitivity extends BaseActivity {

    public static final String TARGET_ID = "TARGET_ID";
    public static final String TITLE = "TITLE";

    public static void startActivity(Context context, String target, String title) {
        Intent intent = new Intent(context, ConversationAcitivity.class);
        intent.putExtra(TARGET_ID, target);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation_detail); /*加载您上面的 conversationlist */

        /* 创建 conversationlist 的Fragment */
        ConversationFragment fragment = new ConversationFragment();

        String target = getIntent().getStringExtra(TARGET_ID);
        String title = getIntent().getStringExtra(TITLE);
        /* 传入私聊会话 PRIVATE 的参数*/
        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                .appendPath(Conversation.ConversationType.DISCUSSION.getName().toLowerCase())
                .appendQueryParameter("targetId", target).appendQueryParameter("title", title).build();

//rong://com.cuitrip.service/conversation/discussion?targetId=21026d2b-7063-4d5b-bc3a-1d4600e3f935&title=ct_test_for_send_2_1
        fragment.setUri(uri);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.conversationlist, fragment).commit();

    }
}
