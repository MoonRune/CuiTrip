package com.example.service.app;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.cuitrip.app.MainApplication;
import com.cuitrip.app.base.ListFetchCallback;
import com.cuitrip.conversation.ConversationItem;
import com.cuitrip.conversation.rong.ConversationFetcherRong;
import com.lab.utils.LogHelper;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<MainApplication> {
    public ApplicationTest() {
        super(MainApplication.class);
    }
    MainApplication app;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        app=getApplication();
    }

    @SmallTest
    public void testRong(){
        final CountDownLatch initCountDownLatch = new CountDownLatch(1);
        final CountDownLatch fetchCountDownLatch = new CountDownLatch(1);

        RongIM.init(app);
        RongIMClient.init(app);
        RongContext.init(app);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RongIM.connect("+vxFW/YC81BKdar9uHKMxmsPNHhQWfeATmnDz5/QSg/BmrE+6qTjXbWPfZThUMDBGk0Cq7/9GRk=", new RongIMClient.ConnectCallback() {
            @Override
            public void onSuccess(String userId) {

//                LogHelper.e( "onResume", "current connect status is:" + RongIM.getInstance().getRongIMClient().getCurrentConnectionStatus());
                LogHelper.e("ron suc", "" + userId);
                initCountDownLatch.countDown();

/* 连接成功 */
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                LogHelper.e("ron failed", "");
/* 连接失败，注意并不需要您做重连 */
                initCountDownLatch.countDown();
            }

            @Override
            public void onTokenIncorrect() {
                LogHelper.e("ron token error", "");
                initCountDownLatch.countDown();
/* Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token */
            }

        });
        try {
            initCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new ConversationFetcherRong().getConversations(new ListFetchCallback<ConversationItem>() {
            @Override
            public void onSuc(List<ConversationItem> t) {
                if (t == null){

                    LogHelper.e("onSuc", "null");
                }else {

                    LogHelper.e("onSuc", "---");
                    for (ConversationItem item:t){

                        LogHelper.e("onSuc", ""+t.toString());
                    }
                }
                fetchCountDownLatch.countDown();

            }

            @Override
            public void onFailed(Throwable throwable) {

                LogHelper.e("onfailed", ""+throwable.getMessage());
                fetchCountDownLatch.countDown();
            }
        });

        try {
            fetchCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}