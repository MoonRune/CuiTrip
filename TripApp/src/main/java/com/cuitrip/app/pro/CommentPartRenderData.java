package com.cuitrip.app.pro;

/**
 * Created by baziii on 15/8/10.
 */
public class CommentPartRenderData {
    //favorite name
    protected String authorName;
    protected String authorAva;
    protected int starts;
    protected int startsMax;

    protected String commentContent;

    public CommentPartRenderData(String authorName, String authorAva, int starts, int startsMax, String commentContent) {
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

    public int getStarts() {
        return starts;
    }

    public void setStarts(int starts) {
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
