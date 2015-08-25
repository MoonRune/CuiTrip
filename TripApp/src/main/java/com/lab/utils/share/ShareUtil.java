package com.lab.utils.share;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cuitrip.app.MainApplication;
import com.lab.utils.LogHelper;
import com.lab.utils.MessageUtils;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;
import com.umeng_social_sdk_res_lib.Config;
import com.umeng_social_sdk_res_lib.R;
import com.umeng_social_sdk_res_lib.SharePanelPopupWindow;

import java.lang.ref.SoftReference;

/**
 * Created by bqf on 7/15/14.
 */
public class ShareUtil {
    private static final String TAG = "ShareUtil";
    private static UMSocialService mController =
            UMServiceFactory.getUMSocialService("com.umeng.share", RequestType.SOCIAL);

    //可重用吗 重新负值context 和sharepane
    private static MyListener listener;

    public static void shareTo(final Activity context, Share share,
                               final SharePanelPopupWindow sharePanel) {
        if (context.isFinishing()) {
            return;
        }
        UMImage umImage = null;
        //关闭umeng组件的提示。
        mController.getConfig().closeToast();
        if (!TextUtils.isEmpty(share.imgUrl)) {
            umImage = new UMImage(context, share.imgUrl);
        } else {
            umImage = new UMImage(context, R.drawable.ct_ic_launcher);
        }

        switch (share.media) {
            case WEIXIN:
                UMWXHandler wxHandler =
                        new UMWXHandler(context, Config.WEIXIN_ID, Config.WEIXIN_SECRET_KEY);
                wxHandler.addToSocialSDK();
                if (!wxHandler.isClientInstalled()) {
                    MessageUtils.showToast(com.cuitrip.service.R.string.ct_wx_not_install);
                    return;
                }

                WeiXinShareContent shareContent = new WeiXinShareContent();
                shareContent.setShareContent(share.content);
                shareContent.setTitle(share.title);
                shareContent.setTargetUrl(share.detailUrl);
                shareContent.setShareImage(umImage);
                mController.setShareMedia(shareContent);
                mController.getConfig().supportAppPlatform(context, share.media, share.detailUrl, true);
                break;
            case WEIXIN_CIRCLE:
                UMWXHandler wxCircleHandler =
                        new UMWXHandler(context, Config.WEIXIN_ID, Config.WEIXIN_SECRET_KEY);
                wxCircleHandler.setToCircle(true);
                wxCircleHandler.addToSocialSDK();

                if (!wxCircleHandler.isClientInstalled()) {
                    MessageUtils.showToast(com.cuitrip.service.R.string.ct_wx_not_install);
                    return;
                }

                CircleShareContent circleShareContent = new CircleShareContent();
                circleShareContent.setTitle(share.title);
                circleShareContent.setShareContent(share.content);
                circleShareContent.setShareImage(umImage);
                circleShareContent.setTargetUrl(share.detailUrl);
                mController.getConfig().supportAppPlatform(context, share.media, share.detailUrl, true);
                mController.setShareMedia(circleShareContent);
                mController.setShareContent(share.content);
                break;

            case SINA:
                SinaShareContent sinaShareContent = new SinaShareContent();
                sinaShareContent.setShareContent(share.title + " - " + share.detailUrl);
                sinaShareContent.setShareImage(umImage);
                mController.setShareMedia(sinaShareContent);
                break;

            case QQ:
                UMQQSsoHandler qqSsoHandler =
                        new UMQQSsoHandler(context, Config.QQ_ID, Config.QQ_SECRET_KEY);
                qqSsoHandler.addToSocialSDK();

                if (!qqSsoHandler.isClientInstalled()) {
                    MessageUtils.showToast(com.cuitrip.service.R.string.ct_qq_not_install);
                    return;
                }

                QQShareContent qqShareContent = new QQShareContent();
                qqShareContent.setTitle(share.title);
                qqShareContent.setShareContent(share.content);
                qqShareContent.setShareImage(umImage);
                qqShareContent.setTargetUrl(share.detailUrl);
                mController.setShareMedia(qqShareContent);
                mController.setShareImage(umImage);
                break;

            default:
                return;
        }
        if (listener == null) {
            listener = new MyListener(context, sharePanel);
        } else {
            mController.unregisterListener(listener);
            listener = new MyListener(context, sharePanel);
        }

        //share service 之前版本出错过所以现在try catch下
        try {
            mController.postShare(context, share.media, listener);
        } catch (Exception e) {
            LogHelper.e(TAG, e.getMessage());
        }
    }

    public static SharePanelPopupWindow openShareWindow(Activity context, View target,
                                                        View.OnClickListener listener) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_panel, null);
        final SharePanelPopupWindow mShareWindow =
                new SharePanelPopupWindow(context, view, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true, R.style.share_panel_anim);
        view.findViewById(R.id.whole_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareWindow.dismiss();
            }
        });
        view.findViewById(R.id.wechat).setOnClickListener(listener);
        view.findViewById(R.id.wechatcircle).setOnClickListener(listener);
        mShareWindow.showAtLocation(target, Gravity.BOTTOM, 0, 0);
        return mShareWindow;
    }

    public static void share(final Activity context, View target, String title, String content, String url, String imageUrl) {

//        if (TextUtils.isEmpty(imageUrl)) {
//            imageUrl = "http://mmbiz.qpic.cn/mmbiz/CCOz0VqjicmxJpUWy6iaibsJ0FcIGaHDLo0TqBHVzyEJOmeaia8mW6jmBnsUrfSJNyd7vAf4sgc9U7ZJ4ydEicNpvZA/0?wx_fmt=gif&wxfrom=5&wx_lazy=1";
//        }
        final Share share = new Share(SHARE_MEDIA.WEIXIN, title, content, imageUrl, url);

        View view = LayoutInflater.from(context).inflate(R.layout.share_panel, null);
        final SharePanelPopupWindow mShareWindow =
                new SharePanelPopupWindow(context, view, LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT, true, R.style.share_panel_anim);
        view.findViewById(R.id.whole_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareWindow.dismiss();
            }
        });
        view.findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareWindow.dismiss();
                share.setMedia(SHARE_MEDIA.WEIXIN);
                ShareUtil.shareTo(context, share, null);

            }
        });
        view.findViewById(R.id.wechatcircle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareWindow.dismiss();
                share.setMedia(SHARE_MEDIA.WEIXIN_CIRCLE);
                ShareUtil.shareTo(context, share, null);

            }
        });
        mShareWindow.showAtLocation(target, Gravity.BOTTOM, 0, 0);
    }

    private static class MyListener implements SocializeListeners.SnsPostListener {
        final SoftReference<Activity> contextSoft;
        final SoftReference<SharePanelPopupWindow> sharePaneSoft;

        private MyListener(Activity context, SharePanelPopupWindow sharePane) {
            this.contextSoft = new SoftReference<Activity>(context);
            this.sharePaneSoft = new SoftReference<SharePanelPopupWindow>(sharePane);
        }

        @Override
        public void onStart() {
            SharePanelPopupWindow sharePane = sharePaneSoft.get();
            if (null != sharePane && sharePane.isShowing()) {
                sharePane.dismiss();
            }
        }

        @Override
        public void onComplete(SHARE_MEDIA shareMedia, int eCode, SocializeEntity socializeEntity) {
            if (eCode != 200) {
                String eMsg = "";
                if (eCode == -101) {
                    eMsg = MainApplication.getInstance().getString(com.cuitrip.service.R.string.ct_error_no_authorized);
                    Context context = contextSoft.get();
                    if (context != null) {
                        MessageUtils.showToast(com.cuitrip.service.R.string.ct_share_failed);
                    }
                }
                MessageUtils.showToast(com.cuitrip.service.R.string.ct_share_cancel);
            } else {
                MessageUtils.showToast(com.cuitrip.service.R.string.ct_share_suc);
            }

            // 分享成功后尝试取消监听的注册
            mController.unregisterListener(this);
        }
    }
}
