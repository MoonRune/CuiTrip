package com.cuitrip.app.conversation;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.app.conversation.rong.ConversationFetcherRong;
import com.cuitrip.login.LoginInstance;

import java.util.List;

/**
 * Created by baziii on 15/8/7.
 * 不分页
 */
public class ConversationsPresent {
    IConversationsView iConversationView;
    IConversationsFetcher iConversationsFetcher = new ConversationFetcherRong();

    //            new IConversationsFetcher() {
//        @Override
//        public void getConversations(ListFetchCallback<ConversationItem> itemListFetchCallback) {
//            List<ConversationItem> result =new ArrayList<>();
//            for (int x=0;x<5;x++){
//                result.add(new ConversationItem(String.valueOf("x"),"name"+x,"last"+x,"time"+x));
//            }
//            itemListFetchCallback.onSuc(result);
//        }
//    };
    public ConversationsPresent(IConversationsView iConversationView) {
        this.iConversationView = iConversationView;
    }

    public void onConversationClick(ConversationItem item) {
        iConversationView.jumpConversation(item);

    }

    public void onConversationRemoved(ConversationItem item) {

    }

    public void onCallRefresh() {
        if (LoginInstance.isLogin(MainApplication.getInstance())) {
            iConversationView.uiHidenNoLogin();
            iConversationView.showRefreshLoading();
            iConversationsFetcher.getConversations(new ListFetchCallback<ConversationItem>() {
                @Override
                public void onSuc(List<ConversationItem> t) {
                    iConversationView.refreshMessage(t);
                    iConversationView.hideRefreshLoading();
                }

                @Override
                public void onFailed(Throwable throwable) {
                    iConversationView.hideRefreshLoading();

                }
            });
        } else {
            iConversationView.uiShowNoLogin();
        }

    }


    public void loadMore() {
        if (LoginInstance.isLogin(MainApplication.getInstance())) {
            iConversationView.uiHidenNoLogin();
            iConversationView.showRefreshLoading();
            iConversationsFetcher.getConversationsMore(new ListFetchCallback<ConversationItem>() {
                @Override
                public void onSuc(List<ConversationItem> t) {
                    iConversationView.appendMessages(t);
                    iConversationView.hideRefreshLoading();
                }

                @Override
                public void onFailed(Throwable throwable) {
                    iConversationView.hideRefreshLoading();

                }
            }, iConversationView.getSize());
        } else {
            iConversationView.uiShowNoLogin();
        }

    }
}
