package com.lab.utils.share;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 用来存储分享的内容
 * Created by bqf on 7/16/14.
 */
public class Share {
  public SHARE_MEDIA media;
  String title;
  String content;
  String imgUrl;
  String detailUrl;

  public Share(SHARE_MEDIA media, String title, String content, String imgUrl, String detailUrl) {
    this.media = media;
    this.title = title;
    this.content = content;
    this.imgUrl = imgUrl;
    this.detailUrl = detailUrl;
  }

  public Share(String detailUrl, String imgUrl, String content, String title) {
    this.detailUrl = detailUrl;
    this.imgUrl = imgUrl;
    this.content = content;
    this.title = title;
  }

  public void setMedia(SHARE_MEDIA media) {
    this.media = media;
  }
}
