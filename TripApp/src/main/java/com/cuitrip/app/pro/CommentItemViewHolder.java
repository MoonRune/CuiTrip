package com.cuitrip.app.pro;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.cuitrip.service.R;
import com.lab.utils.ImageHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by baziii on 15/8/10.
 */
public class CommentItemViewHolder {
    public static final int RES = R.layout.ct_comment_part;
    @InjectView(R.id.ct_comment_author_name_tv)
    TextView mCommentAuthorNameTv;
    @InjectView(R.id.ct_comment_author_ava_im)
    ImageView mCommentAuthorAvaIm;
    @InjectView(R.id.ct_comment_stars_rt)
    RatingBar mCommentStarsRt;
    @InjectView(R.id.ct_comment_content_tv)
    TextView mCommentContentTv;

    public CommentItemViewHolder(View view) {
        ButterKnife.inject(this, view);

    }

    public void render(CommentPartRenderData data) {
        mCommentAuthorNameTv.setText(data.getAuthorName());
        ImageHelper.displayCtImage(data.getAuthorAva(), mCommentAuthorAvaIm, null);
        mCommentStarsRt.setMax(data.getStartsMax());
        mCommentStarsRt.setNumStars(data.getStarts());
        mCommentContentTv.setText(data.getCommentContent());


    }
}
