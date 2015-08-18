package com.cuitrip.app.identity;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by baziii on 15/8/18.
 */
public class IdentityMode implements java.io.Serializable {
    private String failedReaon;
    private String country;
    private String identity;
    private String identityDate;
    private ArrayList<ImageBitmap> images = new ArrayList();

    public String getFailedReaon() {
        return failedReaon;
    }

    public void setFailedReaon(String failedReaon) {
        this.failedReaon = failedReaon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIdentityType() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIdentityDate() {
        return identityDate;
    }

    public void setIdentityDate(String identityDate) {
        this.identityDate = identityDate;
    }

    public ArrayList<ImageBitmap> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageBitmap> images) {
        this.images = images;
    }

    public IdentityMode(String failedReaon, String country, String identity, String identityDate, ArrayList<ImageBitmap> images) {
        this.failedReaon = failedReaon;
        this.country = country;
        this.identity = identity;
        this.identityDate = identityDate;
        if (images == null) {

            this.images = new ArrayList<>();
        } else {
            this.images = images;
        }
    }

    public boolean isImageUploaded() {
        return images.size() > 1 && !TextUtils.isEmpty(images.get(0).getUrl()) && !TextUtils.isEmpty(images.get(1).getUrl());
    }

    public String getImagesString() {
        if (isImageUploaded()) {
            return images.get(0).getUrl() + "," + images.get(1).getUrl();
        }
        return "";
    }
}
