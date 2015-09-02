package com.cuitrip.app.conversation;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cuitrip.app.OldConversatoinActivity;
import com.cuitrip.service.R;
import com.lab.utils.LogHelper;

import io.rong.imkit.fragment.ConversationFragment;

/**
 * Created by baziii on 15/9/2.
 */
public class CtConversationFragment extends ConversationFragment {
    public static final String ORDER_ID="CtConversationFragment.ORDER_ID";
    public static final String OLD_VERSION_MSG_FLAG="CtConversationFragment.OLD_VERSION_MSG_FLAG";
    public static CtConversationFragment newInstance(String orderId,boolean hasOldVersionMessage) {

        Bundle args = new Bundle();
        args.putString(ORDER_ID,orderId);
        args.putBoolean(OLD_VERSION_MSG_FLAG,hasOldVersionMessage);
        CtConversationFragment fragment = new CtConversationFragment();
        fragment.setArguments(args);
        LogHelper.e("omg", "newInstance  " + hasOldVersionMessage);
        return fragment;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ct_conversation,menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        LogHelper.e("omg", "onPrepareOptionsMenu  " + getArguments().getBoolean(OLD_VERSION_MSG_FLAG,false));
        menu.findItem(R.id.action_old_versation).setVisible(getArguments().getBoolean(OLD_VERSION_MSG_FLAG,false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_old_versation:
                OldConversatoinActivity.start(getActivity(), getArguments().getString(ORDER_ID));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
