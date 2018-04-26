package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SoundInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SpringScaleInterpolator;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPKStateLayout;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.xesalib.utils.uikit.SizeUtils;

import java.util.HashMap;

/**
 * 战队pk 实时答题
 */
public class TeamPKAQResultPager extends BasePager {

    private RelativeLayout rlRootView;
    private ImageView ivEnergy;
    private ImageView ivCoin;
    private TextView tvEnergy;
    private TextView tvCoin;
    private TeamPKStateLayout teamPKStateLayout;
    private ViewGroup decorView;
    private TeamPkProgressBar pkProgressBar;
    private final int FLY_ANIM_DURATION = 700; // 飞行动画时间
    private int controloffsetX;
    private int controloffsetY;
    private ScaleAnimation scaleAnimation;
    private SoundPool soundPool;
    private static final int SOUND_TYPE_COIN_GET = 1; //获得金币音效
    private HashMap<Integer,SoundInfo> mSoundInfoMap;
    private static final int DEFAULT_VOLUME = 5;  //默认音量大小

    public TeamPKAQResultPager(Context context){
        super(context);
    }

    @Override
    public View initView() {

        controloffsetX = SizeUtils.Dp2Px(mContext,70);
        controloffsetY = SizeUtils.Dp2Px(mContext,120);

        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_aq_result, null);
        rlRootView = view.findViewById(R.id.rl_answer_question_award_root);
        ivEnergy = view.findViewById(R.id.iv_answer_question_energy);
        ivCoin = view.findViewById(R.id.iv_answer_question_coin);
        tvEnergy = view.findViewById(R.id.tv_answer_question_energy);
        tvCoin = view.findViewById(R.id.tv_answer_question_coin);
        view.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAwardAnim();
            }
        });

        return  view;
    }


    /**
     *
     * @param soundType
     * @param volume
     * @param loop
     */
    private void playMusic(final int soundType, final int volume, final boolean loop){
        if (soundPool == null) {
            soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC, 0);
        }
        if (mSoundInfoMap == null) {
            mSoundInfoMap = new HashMap<Integer, SoundInfo>();
        }
        soundPool.load(mContext,R.raw.coin_get,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                int streamId = soundPool.play(sampleId,volume,volume,0,loop?-1:0,1);
                SoundInfo soundInfo = mSoundInfoMap.get(soundType);
                if(soundInfo == null){
                    soundInfo = new SoundInfo(sampleId,streamId);
                    mSoundInfoMap.put(soundType,soundInfo);
                }else{
                    soundInfo.setStreamId(streamId);
                }
            }
        });
    }

    /**
     * 展示  答题奖励动画
     */
    private void showAwardAnim() {
        scaleAnimation = (ScaleAnimation) AnimationUtils.
                loadAnimation(mContext, R.anim.anim_livevido_teampk_aq_award);
        scaleAnimation.setInterpolator(new SpringScaleInterpolator(0.23f));
        rlRootView.setVisibility(View.VISIBLE);
        rlRootView.startAnimation(scaleAnimation);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                rlRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startFlyAnim();
                    }
                },500);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    @Override
    public void initData() {

    }

    /**
     * 执行飞星 动画
     */
    private void startFlyAnim() {
        decorView = (ViewGroup) ((Activity)mContext).getWindow().getDecorView();
        teamPKStateLayout = decorView.findViewById(R.id.tpkL_teampk_pkstate_root);

        if(teamPKStateLayout == null){
            return;
        }
        // 能量图标动画
        pkProgressBar = teamPKStateLayout.findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        Rect endRect = pkProgressBar.getSliderDrawRect();
        if(endRect != null){
            playFlayAnim(ivEnergy,endRect);
        }

        //金币图标动画
        ImageView ivTargetCoin = teamPKStateLayout.findViewById(R.id.iv_teampk_pkstate_coin);
        Rect coinEndRect = new Rect();
        int[] location = new int[2];
        ivTargetCoin.getLocationInWindow(location);
        coinEndRect.left = location[0];
        coinEndRect.top = location[1];
        coinEndRect.right =  coinEndRect.left + ivTargetCoin.getLayoutParams().width;
        coinEndRect.bottom =  coinEndRect.top+ ivTargetCoin.getLayoutParams().height;
        playFlayAnim(ivCoin,coinEndRect);

    }

    /**
     *
     * @param anchorView
     * @param targetRect  目标view 的绘制区域
     */
    private void playFlayAnim(ImageView anchorView, Rect targetRect) {

        // 把view 添加到 docerview中
        final ImageView flyView = new ImageView(mContext);
        flyView.setImageDrawable(anchorView.getDrawable());
        flyView.setScaleType(ImageView.ScaleType.FIT_XY);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(anchorView.getLayoutParams()
                .width,anchorView.getLayoutParams().height);
        decorView.addView(flyView,lp);

        int [] des = new int[2];
        des[0] = targetRect.left;
        des[1] = targetRect.top;

        int []startPosition = new int[2];
        anchorView.getLocationInWindow(startPosition);
        Point startPoint = new Point(startPosition[0],startPosition[1]);
        int offsetX = 0;
        int offsetY = 0;
        offsetX = (targetRect.width() -flyView.getLayoutParams().width)/2;
        offsetY = (targetRect.height() -flyView.getLayoutParams().height)/2;
        final float endScale =  targetRect.width() /(float)flyView.getLayoutParams().width;

        Point endPoint = new Point( des[0] +offsetX,des[1]+offsetY);

       int controlX = (startPoint.x + endPoint.x)/2 -controloffsetX;
       int controlY = endPoint.y - controloffsetY;
       Point controlPoint = new Point(controlX,controlY);

        ValueAnimator valueAnimator = ValueAnimator.ofObject(new BezierEvaluator(controlPoint)
                ,startPoint,endPoint);
        valueAnimator.setDuration(FLY_ANIM_DURATION);
        valueAnimator.start();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                flyView.setX(point.x);
                flyView.setY(point.y);
                float scale = 1-(1-endScale)*animation.getAnimatedFraction();
                flyView.setScaleX(scale);
                flyView.setScaleY(scale);
            }
        });


        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                doAnimEnd();
                decorView.post(new Runnable() {
                    @Override
                    public void run() {
                        decorView.removeView(flyView);
                    }
                });
            }
        } );
    }


    private void doAnimEnd() {
        // 0 播发音效
          playMusic(SOUND_TYPE_COIN_GET, DEFAULT_VOLUME,false);
        // 1 聊天区域状态更新
         if(teamPKStateLayout != null){
             teamPKStateLayout.updateData(15,10);
         }
        // 2 隐藏 UI/ 移除UI ？
        rlRootView.setVisibility(View.GONE);

    }


    /**
     * 清除资源
     */
    private void releaseRes() {
        if(soundPool != null){
            soundPool.release();
            soundPool =null;
        }
    }

    /**
     * 贝塞尔曲线（二阶抛物线）
     * controlPoint 是中间的转折点
     * startValue 是起始的位置
     * endValue 是结束的位置
     */
    public class BezierEvaluator implements TypeEvaluator<Point>{
        private Point controlPoint;
        BezierEvaluator(Point controlPoint){
            this.controlPoint = controlPoint;
        }
        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            int x = (int) ((1 - fraction) * (1 - fraction) * startValue.x + 2 * fraction * (1 - fraction) * controlPoint.x + fraction * fraction * endValue.x);
            int y = (int) ((1 - fraction) * (1 - fraction) * startValue.y + 2 * fraction * (1 - fraction) * controlPoint.y + fraction * fraction * endValue.y);
            return new Point(x, y);
        }
    }

}
