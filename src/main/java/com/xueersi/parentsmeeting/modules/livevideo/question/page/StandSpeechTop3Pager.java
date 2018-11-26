package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.Top3FrameAnim;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by linyuqiang on 2018/4/10.
 * 语音评测top3
 */
public class StandSpeechTop3Pager extends LiveBasePager {
    private ImageView iv_livevideo_speecteval_result_top3;
    private Top3FrameAnim top3FrameAnim;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    private HashMap<String, Bitmap> stuHeadBitmap = new HashMap<>();
    private GoldTeamStatus entity;
    private String id;

    public StandSpeechTop3Pager(Context context, GoldTeamStatus entity) {
        super(context);
        this.entity = entity;
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_live_stand_speecheval_top3, null);
        iv_livevideo_speecteval_result_top3 = mView.findViewById(R.id.iv_livevideo_speecteval_result_top3);
        LayoutParamsUtil.setViewFullScreen(iv_livevideo_speecteval_result_top3);
        return mView;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void initData() {
        mLogtf.d("initData:id=" + id + "," + entity.getStudents().size());
        top3FrameAnim = new Top3FrameAnim(mContext, iv_livevideo_speecteval_result_top3, stuHeadBitmap, frameAnimations);
        top3FrameAnim.setGold(false);
        top3FrameAnim.start(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                ViewGroup group = (ViewGroup) mView.getParent();
                if (group != null) {
                    group.removeView(mView);
                }
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    animation.destory();
                }
            }

            @Override
            public void onAnimationRepeat() {

            }
        }, entity.getStudents());
    }
}
