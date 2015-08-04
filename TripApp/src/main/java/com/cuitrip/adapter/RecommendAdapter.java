package com.cuitrip.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cuitrip.business.ServiceBusiness;
import com.cuitrip.model.RecommendItem;
import com.cuitrip.model.RecommendOutData;
import com.cuitrip.service.R;
import com.lab.adapter.PagedEndlessAdapter;
import com.lab.adapter.ViewHolder;
import com.lab.network.LabAsyncHttpResponseHandler;
import com.lab.network.LabResponse;
import com.lab.utils.ImageHelper;
import com.loopj.android.http.SyncHttpClient;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends PagedEndlessAdapter<RecommendItem> {
    public static final int PAGE_SIZE = 20;

    private SyncHttpClient client = new SyncHttpClient();

    @Override
    protected IncomingData<RecommendItem> workForNewItems(int requestPageNum) throws Exception {
        final IncomingData<RecommendItem> incoming = new IncomingData<RecommendItem>();
        ServiceBusiness.getRecommendList(getContext(), client, new LabAsyncHttpResponseHandler(RecommendOutData.class) {

            @Override
            public void onSuccess(LabResponse response, Object data) {
                if (data != null) {
                    RecommendOutData outData = (RecommendOutData) data;
                    incoming.itemList = outData.getLists();
                    incoming.reachEnd = outData.getNum() < PAGE_SIZE;
                } else {
                    incoming.reachEnd = true;
                }
            }

            @Override
            public void onFailure(LabResponse response, Object data) {
                incoming.error = true;
            }
        }, "TW", null, requestPageNum * PAGE_SIZE, PAGE_SIZE);
        return incoming;
    }

    public RecommendAdapter(Context context, List<RecommendItem> initList, int pendingResource, boolean keepOnAppending) {
        super(context, new RecommenWrapperAdapter(context, initList), pendingResource, keepOnAppending);
//        if (getWrappedAdapter() != null && getWrappedAdapter().getCount() > 0) {
//            initStartPage(1);
//        }
    }

    public static class RecommenWrapperAdapter extends WrappedAdapter<RecommendItem> {

        public RecommenWrapperAdapter(Context context, List<RecommendItem> initItems) {
            super(context, initItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RecommendViewHolder viewHolder;
            if (null == convertView) {
                convertView = View.inflate(getContext(), R.layout.ct_recommend_list_item, null);
                viewHolder = new RecommendViewHolder();
                viewHolder.authorName = (TextView) convertView.findViewById(R.id.author_name);
                viewHolder.serviceName = (TextView) convertView.findViewById(R.id.service_name);
                viewHolder.servicePic = (ImageView) convertView.findViewById(R.id.service_img);
                viewHolder.authorPic = (CircleImageView) convertView.findViewById(R.id.author_img);
                viewHolder.authorAddress = (TextView) convertView.findViewById(R.id.author_address);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (RecommendViewHolder) convertView.getTag();
            }
            RecommendItem item = getItem(position);
            if (!TextUtils.isEmpty(item.getServiceAddress())) {
                viewHolder.authorAddress.setText("  -  " + item.getServiceAddress());
            }
            viewHolder.authorName.setText(item.getUserNick());
            ImageHelper.displayPersonImage(item.getHeadPic(), viewHolder.authorPic, null);
            ImageHelper.displayCtImage(item.getServicePicUrl(), viewHolder.servicePic, null);
            viewHolder.serviceName.setText(item.getServiceName());
            return convertView;
        }
    }

    public static class RecommendViewHolder extends ViewHolder {
        public ImageView servicePic;
        public CircleImageView authorPic;
        public TextView serviceName;
        public TextView authorName;
        public TextView authorAddress;

    }

}
