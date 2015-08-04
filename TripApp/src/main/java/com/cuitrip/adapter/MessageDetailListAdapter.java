package com.cuitrip.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cuitrip.business.MessageBusiness;
import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.login.LoginInstance;
import com.cuitrip.model.DialogItem;
import com.cuitrip.model.DialogList;
import com.cuitrip.model.RecommendItem;
import com.cuitrip.model.RecommendOutData;
import com.cuitrip.service.R;
import com.lab.adapter.PagedEndlessAdapter;
import com.lab.adapter.ViewHolder;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.loopj.android.http.SyncHttpClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 7/19.
 */
public class MessageDetailListAdapter extends PagedEndlessAdapter<DialogItem> {
    public static final int PAGE_SIZE = 100;

    private SyncHttpClient client = new SyncHttpClient();
    private String mOrderId;
    private ListView mList;

    @Override
    protected IncomingData<DialogItem> workForNewItems(int requestPageNum) throws Exception {
        final IncomingData<DialogItem> incoming = new IncomingData<DialogItem>();
        MessageBusiness.getDialogList(getContext(), client, new LabAsyncHttpResponseHandler(DialogList.class) {

            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null) {
                    DialogList outData = (DialogList) data;
                    incoming.itemList = outData.getDialog();
                    incoming.reachEnd = outData.getDialog() == null ?
                            true : (outData.getDialog().size() < PAGE_SIZE);
                } else {
                    incoming.reachEnd = true;
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                incoming.error = true;
            }
        }, mOrderId, PAGE_SIZE, requestPageNum * PAGE_SIZE);
        return incoming;
    }

    public void onDataReady() {
        super.onDataReady();
        mList.setSelection(mList.getCount() - 1);
    }

    public MessageDetailListAdapter(Context context, List<DialogItem> initList, int pendingResource,
                                    boolean keepOnAppending, String orderId, ListView listView) {
        super(context, new MessageWrapperAdapter(context, initList), pendingResource, keepOnAppending);
        mOrderId = orderId;
        mList = listView;
    }

    public static class MessageWrapperAdapter extends WrappedAdapter<DialogItem> {

        private static final int TYPE_SYSTEM = 0;
        private static final int TYPE_MY = 1;
        private static final int TYPE_TA = 2;

        private static final int TYPE_COUNT = 3;

        public MessageWrapperAdapter(Context context, List<DialogItem> initItems) {
            super(context, initItems);
        }

        public int getItemViewType(int position) {
            DialogItem dialogItem = getItem(position);
            if ("4".equals(dialogItem.getType())) {
                //对话消息
                if (LoginInstance.getInstance(getContext()).getUserInfo().getUid()
                        .equals(dialogItem.getFrom())) {
                    //自己发送
                    return TYPE_MY;
                }else{
                    //他人发送
                    return TYPE_TA;
                }
            } else {
                //系统消息
                return TYPE_SYSTEM;
            }
        }

        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)){
                case TYPE_MY:
                    MyHolder myHolder = new MyHolder();
                    return myHolder.bindView(getContext(), getItem(position), convertView);
                case TYPE_TA:
                    TaHolder taHolder = new TaHolder();
                    return taHolder.bindView(getContext(), getItem(position), convertView);
                case TYPE_SYSTEM:
                    SystemHolder systemHolder = new SystemHolder();
                    return systemHolder.bindView(getContext(), getItem(position), convertView);
            }
            return convertView;
        }
    }

    static class SystemHolder{
        public TextView mTime;
        public TextView mContent;
        public static View bindView(Context context, DialogItem item, View convertView){
            SystemHolder systemHolder;
            if(convertView == null){
                convertView = View.inflate(context, R.layout.ct_message_system_item, null);
                systemHolder = new SystemHolder();
                systemHolder.mContent = (TextView)convertView.findViewById(R.id.content);
                systemHolder.mTime = (TextView)convertView.findViewById(R.id.time);
                convertView.setTag(systemHolder);
            }else{
                systemHolder = (SystemHolder)convertView.getTag();
            }
            systemHolder.mTime.setText(item.getGmtCreated());
            systemHolder.mContent.setText(item.getContent());
            return convertView;
        }
    }

    static class MyHolder{
        public ImageView mImage;
        public TextView mContent;
        public static View bindView(Context context, DialogItem item, View convertView){
            MyHolder holder;
            if(convertView == null){
                convertView = View.inflate(context, R.layout.ct_message_my, null);
                holder = new MyHolder();
                holder.mContent = (TextView)convertView.findViewById(R.id.content);
                holder.mImage = (ImageView)convertView.findViewById(R.id.author_img);
                convertView.setTag(holder);
            }else{
                holder = (MyHolder)convertView.getTag();
            }
            ImageHelper.displayPersonImage(item.getHeadPic(), holder.mImage, null);
            holder.mContent.setText(item.getContent());
            return convertView;
        }
    }

    static class TaHolder{
        public ImageView mImage;
        public TextView mContent;
        public static View bindView(Context context, DialogItem item, View convertView){
            TaHolder holder;
            if(convertView == null){
                convertView = View.inflate(context, R.layout.ct_message_ta, null);
                holder = new TaHolder();
                holder.mContent = (TextView)convertView.findViewById(R.id.content);
                holder.mImage = (ImageView)convertView.findViewById(R.id.author_img);
                convertView.setTag(holder);
            }else{
                holder = (TaHolder)convertView.getTag();
            }
            ImageHelper.displayPersonImage(item.getHeadPic(), holder.mImage, null);
            holder.mContent.setText(item.getContent());
            return convertView;
        }
    }
}
