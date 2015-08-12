package com.cuitrip.app.pro;

/**
 * Created by baziii on 15/8/10.
 */
public class CommentPartRenderData {
    public static final int DEFAULT_MAX_SCORE=5;
    //favorite name
    protected String authorName;
    protected String authorAva;
    protected float starts;
    protected int startsMax;

    protected String commentContent;

    public CommentPartRenderData(String authorName, String authorAva, float starts, int startsMax, String commentContent) {
        this.authorName = authorName;
        this.authorAva = authorAva;
        this.starts = starts;
        this.startsMax = startsMax;
        this.commentContent = commentContent;
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

    public float getStarts() {
        return starts;
    }

    public void setStarts(float starts) {
        this.starts = starts;
    }

    public int getStartsMax() {
        return startsMax;
    }

    public void setStartsMax(int startsMax) {
        this.startsMax = startsMax;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
