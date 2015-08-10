package com.cuitrip.app.pro;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baziii on 15/8/10.
 */
public class ServiceNonViewHolder {
    public static final int RES = R.layout.ct_recommend_list_item;
    @InjectView(R.id.service_img)
    RoundedImageView serviceImg;
    @InjectView(R.id.author_name)
    TextView authorName;
    @InjectView(R.id.author_address)
    TextView authorAddress;
    @InjectView(R.id.service_name)
    TextView serviceName;
    @InjectView(R.id.author_img)
    CircleImageView authorImg;
    @InjectView(R.id.author_block)
    LinearLayout authorBlock;

    public RoundedImageView getServiceImg() {
        return serviceImg;
    }

    public TextView getAuthorName() {
        return authorName;
    }

    public TextView getAuthorAddress() {
        return authorAddress;
    }

    public TextView getServiceName() {
        return serviceName;
    }

    public CircleImageView getAuthorImg() {
        return authorImg;
    }

    public LinearLayout getAuthorBlock() {
        return authorBlock;
    }

    public ServiceNonViewHolder(View view){
        ButterKnife.inject(this,view);

    }
    public void render(RecommendRenderData data){
        if (!TextUtils.isEmpty(data.getAddress())) {
            authorAddress.setText("  -  " + data.getAddress());
        }
        authorName.setText(data.getAuthorName());
        ImageHelper.displayPersonImage(data.getAuthorAva(), authorImg, null);
        ImageHelper.displayCtImage(data.getHeadPic(), serviceImg, null);
        serviceName.setText(data.getName());


    }
}
