package com.xueersi.parentsmeeting.modules.livevideoOldIJK.message.pager;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;

/**
 * 这个是小英回放，在聊天区加上MMD皮肤
 */
public class LiveBackVideoMessagePager extends BasePager {
    //board在外层所处的布局
    private RelativeLayout.LayoutParams boardParams;
    //聊天框中间部分的图片
    private ImageView img;
    //imgView所处的布局
    private RelativeLayout.LayoutParams imgParams;

    public LiveBackVideoMessagePager(Context activity) {
        super(activity);
        initData();
    }

    @Override
    public View initView() {
//        mView = new RelativeLayout(mContext);
//        mView.setBackgroundColor(Color.parseColor("#5AC8FF"));
//        mView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable
//                .bg_livevideo_small_english_playback_misslive_board));
//
//        //设置聊天区域中间图片
//        ImageView img = new ImageView(mContext);
//        img.setImageResource(R.drawable.bg_livevideo_small_english_playback_misslive_font);
//        imgParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
//                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        imgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        ((ViewGroup) mView).addView(img, imgParams);

        mView = View.inflate(mContext, R.layout.layout_back_video_smll_english_message, null);
        return mView;
    }

    public RelativeLayout.LayoutParams getBoardParams() {
        return boardParams;
    }

    @Override
    public void initData() {
        boardParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        LiveVideoPoint liveVideoPoint = LiveVideoPoint.getInstance();
        //设置聊天区域外层Board=
        int width = liveVideoPoint.x4 - liveVideoPoint.x3;
        int height = liveVideoPoint.screenHeight - liveVideoPoint.y3;
        boardParams.height = height;
        boardParams.width = width;
        boardParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        boardParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        boardParams.rightMargin = liveVideoPoint.screenWidth - liveVideoPoint.x4;
    }
}
