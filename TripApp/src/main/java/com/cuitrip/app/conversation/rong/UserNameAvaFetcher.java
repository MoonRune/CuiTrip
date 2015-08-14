package com.cuitrip.app.conversation.rong;

import android.os.AsyncTask;

import com.cuitrip.app.base.CtFetchCallback;
import com.cuitrip.model.UserInfo;

/**
 * Created by baziii on 15/8/14.
 */
public class UserNameAvaFetcher {
    public void fetchUseNameAva(final String id,final CtFetchCallback<UserInfo> fetchCallback){
        new AsyncTask() {
            UserInfo userInfo;
            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    userInfo = new UserInfo();
                    userInfo.setNick("name"+id);
                    userInfo.setHeadPic("ava"+id);
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                fetchCallback.onSuc(userInfo);
                super.onPostExecute(o);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
