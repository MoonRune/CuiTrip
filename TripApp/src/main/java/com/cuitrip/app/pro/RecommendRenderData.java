package com.cuitrip.app.pro;

/**
 * Created by baziii on 15/8/10.
 */
public class RecommendRenderData {
    protected String id;
    //favorite name
    protected String name;
    protected String headPic;
    protected String address;

    protected String authorName;
    protected String authorAva;
    protected String authorCarrer;

    public RecommendRenderData(String id, String name, String headPic, String address, String authorName, String authorAva, String authorCarrer) {
        this.id = id;
        this.name = name;
        this.headPic = headPic;
        this.address = address;
        this.authorName = authorName;
        this.authorAva = authorAva;
        this.authorCarrer = authorCarrer;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHeadPic() {
        return headPic;
    }

    public String getAddress() {
        return address;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorAva() {
        return authorAva;
    }

    public String getAuthorCarrer() {
        return authorCarrer;
    }
}
