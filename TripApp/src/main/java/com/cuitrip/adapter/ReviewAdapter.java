package com.cuitrip.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cuitrip.model.ReviewListItem;
import com.cuitrip.service.R;
import com.lab.adapter.BaseListAdapter;
import com.lab.adapter.ViewHolder;
import com.lab.utils.ImageHelper;
import com.lab.utils.Utils;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 7/17.
 */
public class ReviewAdapter extends BaseListAdapter<ReviewListItem> {

    public ReviewAdapter(Context context, int resourceId) {
        super(context, resourceId);
    }

    @Override
    protected ViewHolder view2Holder(View view) {
        ReViewHolder holder = new ReViewHolder();
        holder.name = (TextView) view.findViewById(R.id.name);
        holder.time = (TextView) view.findViewById(R.id.time);
        holder.content = (TextView) view.findViewById(R.id.content);
        holder.author_img = (CircleImageView) view.findViewById(R.id.author_img);
        return holder;
    }

    @Override
    protected void bindView(ViewHolder holder, ReviewListItem item, int position) {
        ReViewHolder reViewHolder = (ReViewHolder) holder;
        reViewHolder.name.setText(item.getNick());
        reViewHolder.time.setText(Utils.getDateFormat(item.getGmtCreated(), Utils.DATE_FORMAT_SECOND));
        reViewHolder.content.setText(item.getContent());
        ImageHelper.displayPersonImage(item.getHeadPic(), reViewHolder.author_img, null);
    }

    public static class ReViewHolder extends ViewHolder {
        public CircleImageView author_img;
        public TextView content;
        public TextView time;
        public TextView name;

    }
}
