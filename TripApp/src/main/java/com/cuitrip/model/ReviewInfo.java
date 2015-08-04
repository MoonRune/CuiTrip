package com.cuitrip.model;

public class ReviewInfo {
    //    "lastReview": {
//        "rid": "4",
//                "status": "0",
//                "type": "1",
//                "oid": "9_20150622135203_10",
//                "sid": "9",
//                "insiderId": "13",
//                "travellerId": "10",
//                "serviceName": "bobby带你看花莲老火车",
//                "serviceType": "1",
//                "serviceDate": "2015-08-18 00:00:00.0",
//                "serviceScore": "5",
//                "content": "你好，终于看到了《练习曲》中的那个场景，很真实，这才是真正的台湾，非常难忘，非常难忘。",
//                "extInfo": "",
//                "gmtCreated": "2015-06-27 21:04:34.0",
//                "gmtModified": "2015-06-27 21:04:34.0",
//                "headPic": null,
//                "nick": null
//    },
//            "reviewNum": "4"
    private ReviewInfoItem lastReview;
    private String reviewNum;

    public ReviewInfoItem getLastReview() {
        return lastReview;
    }

    public void setLastReview(ReviewInfoItem lastReview) {
        this.lastReview = lastReview;
    }

    public String getReviewNum() {
        return reviewNum;
    }

    public void setReviewNum(String reviewNum) {
        this.reviewNum = reviewNum;
    }
}
