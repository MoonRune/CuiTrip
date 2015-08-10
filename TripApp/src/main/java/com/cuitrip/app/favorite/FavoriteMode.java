package com.cuitrip.app.favorite;

import com.cuitrip.app.pro.RecommendRenderData;

/**
 * Created by baziii on 15/8/10.
 */
public class FavoriteMode {
    //service id
    protected String id;
    //favorite name
    protected String name;
    protected String headPic;
    protected String address;

    protected String authorName;
    protected String authorAva;
    protected String authorCarrer;
    protected boolean avaliable;

    public FavoriteMode(String id, String name, String headPic, String address, String authorName, String authorAva, String authorCarrer, boolean avaliable) {
        this.id = id;
        this.name = name;
        this.headPic = headPic;
        this.address = address;
        this.authorName = authorName;
        this.authorAva = authorAva;
        this.authorCarrer = authorCarrer;
        this.avaliable = avaliable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAva() {
        return authorAva;
    }

    public void setAuthorAva(String authorAva) {
        this.authorAva = authorAva;
    }

    public String getAuthorCarrer() {
        return authorCarrer;
    }

    public void setAuthorCarrer(String authorCarrer) {
        this.authorCarrer = authorCarrer;
    }

    public boolean isAvaliable() {
        return avaliable;
    }

    public void setAvaliable(boolean avaliable) {
        this.avaliable = avaliable;
    }
    
    public RecommendRenderData buildRecommendRenderData(){
        return  new RecommendRenderData( id,  name,  headPic, address, authorName,  authorAva,  authorCarrer);
    }
}
