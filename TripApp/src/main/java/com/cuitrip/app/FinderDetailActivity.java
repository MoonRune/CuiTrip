package com.cuitrip.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;

import com.cuitrip.model.UserInfo;
import com.cuitrip.service.R;
import com.lab.app.BaseActivity;
import com.lab.utils.Constants;
import com.lab.utils.ImageHelper;
import com.lab.utils.MessageUtils;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 8/1.
 */
public class FinderDetailActivity extends BaseActivity {
    public static final String USER_INFO = "user_info";

//    public static final String TEST = "这是\n杭州的\n夜晚，也是一\n个测试，手机打字还\n是很痛苦的<div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/177_1438347822741\" width=\"100%\"/></div>呵\n呵<div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/177_1438347813868\" width=\"100%\"/></div><div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/177_1438349348821\" width=\"100%\"/></div><div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/177_1438349626449\" width=\"100%\"/></div><div><img src=\"http://cuitrip.oss-cn-shenzhen.aliyuncs.com/177_1438349630623\" width=\"100%\"/></div>";

    private UserInfo mUserInfo;
    private WebView mWebView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        mUserInfo = (UserInfo) intent.getSerializableExtra(USER_INFO);
        if (mUserInfo == null) {
            MessageUtils.showToast(R.string.parameter_error);
            finish();
            return;
        }
        showActionBar(mUserInfo.getNick());
        setContentView(R.layout.ct_finder_detail);
        mWebView = (WebView) findViewById(R.id.author_introduce);
        updateUserInfo();
    }

    private void updateUserInfo() {
        CircleImageView img = (CircleImageView) findViewById(R.id.author_img);
        ImageHelper.displayPersonImage(mUserInfo.getHeadPic(), img, null);
        setViewText(R.id.author_name, mUserInfo.getNick());
        setViewText(R.id.author_sign, mUserInfo.getSign());
        setViewText(R.id.author_register_time_value, mUserInfo.getGmtModified());
        if ("0".equals(mUserInfo.getStatus())) {
            setViewText(R.id.author_identification_value, getString(R.string.ct_cuitrip_renzheng));
        } else {
            setViewText(R.id.author_identification_value, getString(R.string.ct_cuitrip_renzheng_no));
        }

        setViewText(R.id.author_career_value, mUserInfo.getCareer());
        setViewText(R.id.author_interest_value, mUserInfo.getInterests());
        setViewText(R.id.author_language_value, mUserInfo.getLanguage());

        if (mUserInfo.getExtInfo() != null) {
            try {
                com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(mUserInfo.getExtInfo());
                String desc = json.getString("introduce");
                Integer status = json.getInteger("introduceAuditStatus");
                if (status != null && status == 1 && !TextUtils.isEmpty(desc)) { //审核通过才展示
                    mWebView.loadDataWithBaseURL(null, Constants.CT_HTML_HEAD
                            + Constants.dealHtmlLine(desc) + Constants.CT_HTML_END, "text/html", "utf-8", null);
                } else {
                    mWebView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                mWebView.setVisibility(View.GONE);
            }
        } else {
            mWebView.setVisibility(View.GONE);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }
}
