package com.xueersi.parentsmeeting.modules.livevideo.page;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;

/**
 * Created by chenkun on 2018/4/12
 * 分队进行中
 */
public class TeamPkTeamSelectingPager extends BasePager implements View.OnClickListener {
    private ImageView ivEnter;
    private final TeamPKBll mTeamPkBll;
    /**呼吸动画持续时间*/
    private static final int ANIM_DURATION = 1500;

    public TeamPkTeamSelectingPager(Context context, TeamPKBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_teamselecting, null);
        ivEnter = view.findViewById(R.id.iv_teampk_enter_teamselect);
        ivEnter.setOnClickListener(this);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.12f, 1.0f, 1.12f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        scaleAnimation.setRepeatCount(-1);
        scaleAnimation.setDuration(ANIM_DURATION);
        ivEnter.startAnimation(scaleAnimation);

        ImageView ivImg = view.findViewById(R.id.iv_teampk_enter_teamselect_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) ivImg.getDrawable();
        animationDrawable.start();

        return view;
    }


    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        mTeamPkBll.enterTeamSelectScene();
    }

    /**
     * 关闭当前页面
     */
    public void closeTeamSelectPager(){
       mTeamPkBll.closeCurrentPager();
    }

}
