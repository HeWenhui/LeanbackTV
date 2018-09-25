package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoQuestionEntity;
import com.xueersi.common.logerhelper.XesMobAgent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaController2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaControllerBottom2;
import com.xueersi.parentsmeeting.module.videoplayer.media.MediaPlayerControl;
import com.xueersi.common.business.sharebusiness.config.LocalCourseConfig;
import com.xueersi.lib.framework.utils.TimeUtils;
import com.xueersi.lib.framework.utils.ScreenUtils;

import java.util.List;

/**
 * 站立直播看课的控制栏
 *
 * @author linyuqiang
 */
public class LiveStandPlaybackMediaController extends MediaController2 {
    private String TAG = "LivePlaybackMediaController";
    protected Logger logger = LoggerFactory.getLogger(TAG);
    public MediaControllerBottom2 mediaControllerBottom;
    private RelativeLayout rlKeyPoints;
    private RelativeLayout rlKeytip;
    Activity activity;

    public LiveStandPlaybackMediaController(Context context, MediaPlayerControl player) {
        super(context, player);
        activity = (Activity) context;
        controllerBottom = new LiveBackStandMediaControllerBottom(getContext(), this, mPlayer);
        setControllerBottom(controllerBottom);
        mediaControllerBottom = (MediaControllerBottom2) controllerBottom;
        rlKeyPoints = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keypoints);
        rlKeytip = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keytip);
    }

    @Override
    protected View inflateLayout() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pop_livestand_mediacontroller, this);
        return view;
    }

    /**
     * 对控制栏布局进行初始化设置 将竖屏时上方系统栏只留小箭头，取消背景和标题栏
     */
    @Override
    protected void findViewItems() {
        super.findViewItems();
        if (!mPlayer.isLandSpace()) {
            mFileName.setVisibility(View.INVISIBLE); // 隐藏标题栏
//            mSystemInfoLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

//    @Override
//    public void setHaveBottom(boolean have) {
////        super.setHaveBottom(have);
//        if (have) {
//            controllerBottom = new LiveBackStandMediaControllerBottom(getContext(), this, mPlayer);
//            ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
//                    .MATCH_PARENT);
//            mControlsLayout.addView((View) controllerBottom, layoutParams);
//        } else {
//            mControlsLayout.removeAllViews();
//            controllerBottom = new EmptyControllerBottomInter();
//        }
//
//        mediaControllerBottom = (MediaControllerBottom) controllerBottom;
//        rlKeyPoints = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keypoints);
//        rlKeytip = (RelativeLayout) mediaControllerBottom.findViewById(R.id.rl_video_mediacontroller_keytip);
//    }

    /**
     * 添加互动题的点
     *
     * @param lstVideoQuestion
     * @param duration
     */
    public void setVideoQuestions(final String where, final List<VideoQuestionEntity> lstVideoQuestion, final long duration) {
        if (duration == 0) {
            XesMobAgent.videoPointClick(where + 0);
            return;
        }
        if (rlKeyPoints != null) {
            rlKeyPoints.removeAllViews();
            if (rlKeyPoints.getWidth() == 0) {//刚开始得不到宽度，通过回调再调一次方法，否则直接添加
                rlKeyPoints.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        rlKeyPoints.getViewTreeObserver().removeOnPreDrawListener(this);
                        setVideoQuestions(where, lstVideoQuestion, duration);
                        return true;
                    }
                });
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_scrubber_control_selector_holo_spjd);
            int pointWidth = rlKeyPoints.getWidth();
            float screenDensity = ScreenUtils.getScreenDensity();
            MarginLayoutParams rlKeytipLp = (MarginLayoutParams) rlKeytip.getLayoutParams();
            rlKeytip.setLayoutParams(rlKeytipLp);
            logger.i("setVideoQuestions:pointWidth=" + pointWidth + ",rlKeytip=" + rlKeytip.getWidth()
                    + ",duration=" + duration + ",bitmap=" + bitmap.getWidth());
            int index = 1;
            LayoutInflater inflater = activity.getLayoutInflater();
            for (int i = 0; i < lstVideoQuestion.size(); i++) {
                final VideoQuestionEntity videoQuestionEntity = lstVideoQuestion.get(i);
                String key = "";
                RelativeLayout rlClick = (RelativeLayout) inflater.inflate(R.layout.item_video_ques_point, rlKeyPoints, false);
                ImageView imageView = (ImageView) rlClick.findViewById(R.id.iv_video_ques_point);
                if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
                    if (!LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {
                        videoQuestionEntity.setQuestionIndex(index++);
                    }
                    if (!videoQuestionEntity.isAnswered()) {
                        imageView.setBackgroundResource(R.drawable.shape_liveplayback_point);
                        key = videoQuestionEntity.getvCategory() + "-";
                        if (LocalCourseConfig.QUESTION_TYPE_SELECT.equals(videoQuestionEntity.getvQuestionType())) {
                            key += "1-" + videoQuestionEntity.getChoiceType();
                        } else {
                            key += "2";
                        }
                    }
                } else {
                    if (!videoQuestionEntity.isAnswered()) {
                        key = "" + videoQuestionEntity.getvCategory();
                        imageView.setBackgroundResource(R.drawable.shape_liveplayback_point);
                    }
                }
                if (videoQuestionEntity.isAnswered()) {
                    continue;
                }
                //添加外面点击区域 和SeekBar的原点一样大
                int width = bitmap.getWidth();
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, RelativeLayout.LayoutParams.MATCH_PARENT);
                final int insretTime = videoQuestionEntity.getvQuestionInsretTime();
                long pos = 1000000L * insretTime / (duration);//按SeekBar的算法，算出比例
                lp.leftMargin = (int) ((pointWidth * pos / 1000) - width / 2 + 3 * screenDensity);//位置显示居中，3 * screenDensity是SeekBar居左
                logger.i("setVideoQuestions:" + videoQuestionEntity.getvCategory()
                        + ",insretTime=" + TimeUtils.generateTime(insretTime * 1000) + ",pos=" + pos
                        + ",leftMargin=" + lp.leftMargin);
                //rlClick.setBackgroundColor(0x99000000);
                rlKeyPoints.addView(rlClick, lp);
                final String finalKey = key;
                rlClick.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        XesMobAgent.videoPointClick(where + 1 + "$" + finalKey);
                        show();
//                        XESToastUtils.showToast(activity, "" + XesStringUtils.generateTime(insretTime));
                        showTip(where, finalKey, videoQuestionEntity, duration);
                    }
                });
            }
        }
    }


    /**
     * 点击显示提示
     *
     * @param videoQuestionEntity 互动题对象
     * @param duration            视频总时长
     */
    private void showTip(final String where, final String key, final VideoQuestionEntity videoQuestionEntity, final long duration) {
        if (rlKeytip.getChildCount() > 0) {//有提示显示
            View child = rlKeytip.getChildAt(0);
            VideoQuestionEntity entity = (VideoQuestionEntity) child.getTag();
            if (entity == videoQuestionEntity) {// 如果是当前的题，隐藏。其他的显示
                rlKeytip.removeAllViews();
                XesMobAgent.videoPointClick(where + 3 + "$" + key);
                return;
            }
        }
        rlKeytip.removeAllViews();
        final View contentView = activity.getLayoutInflater().inflate(R.layout.pop_liveplayback_point, rlKeytip, false);
        contentView.setTag(videoQuestionEntity);
        TextView textView = (TextView) contentView.findViewById(R.id.tv_liveplayback_point_name);
        if (LocalCourseConfig.CATEGORY_QUESTION == videoQuestionEntity.getvCategory()) {
            if (LocalCourseConfig.QUESTION_TYPE_SPEECH.equals(videoQuestionEntity.getvQuestionType())) {
                textView.setText("语音评测");
            } else {
                textView.setText("第" + videoQuestionEntity.getQuestionIndex() + "题");
            }
        } else if (LocalCourseConfig.CATEGORY_REDPACKET == videoQuestionEntity.getvCategory()) {
            textView.setText("红包");
        } else if (LocalCourseConfig.CATEGORY_EXAM == videoQuestionEntity.getvCategory()) {
            textView.setText("试卷");
        } else if (LocalCourseConfig.CATEGORY_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
            textView.setText("互动实验");
        } else if (LocalCourseConfig.CATEGORY_ENGLISH_H5COURSE_WARE == videoQuestionEntity.getvCategory()) {
            String coursewareType = videoQuestionEntity.getvQuestionType();
            if ("1".equals(coursewareType)) {
                textView.setText("游戏");
            } else if ("2".equals(coursewareType)) {
                textView.setText("互动题");
            } else if ("3".equals(coursewareType)) {
                textView.setText("入门测");
            } else if ("4".equals(coursewareType)) {
                textView.setText("出门考");
            } else {
                textView.setText("互动课件");
            }
        } else if (LocalCourseConfig.CATEGORY_LEC_ADVERT == videoQuestionEntity.getvCategory()) {
            textView.setText("购课");
        }
        final ImageView ivPlay = (ImageView) contentView.findViewById(R.id.iv_liveplayback_point_play);
        final int insretTime = videoQuestionEntity.getvQuestionInsretTime();
        rlKeytip.addView(contentView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                int loction[] = new int[2];
                rlKeyPoints.getLocationInWindow(loction);//rlKeytip的宽度是全屏，rlKeyPoints在中间
                contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                RelativeLayout rlPointTop = (RelativeLayout) contentView.findViewById(R.id.rl_liveplayback_point_top);//上面的提示
                ImageView ivArrow = (ImageView) contentView.findViewById(R.id.iv_liveplayback_point_arrow);//下面的箭头
                int pointWidth = rlKeyPoints.getWidth();
                float screenDensity = ScreenUtils.getScreenDensity();
                long pos = 1000000L * insretTime / (duration);//按SeekBar的算法，算出比例
                {
                    //下面箭头
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivArrow.getLayoutParams();
                    lp.leftMargin = (int) ((pointWidth * pos / 1000) - ivArrow.getWidth() / 2 + 3 * screenDensity + loction[0]);//位置显示居中，3 * screenDensity是SeekBar居左
                    logger.i("showTip:category=" + videoQuestionEntity.getvCategory()
                            + ",insretTime=" + TimeUtils.generateTime(insretTime * 1000)
                            + ",pointWidth=" + pointWidth + ",leftMargin=" + lp.leftMargin + ",getWidth=" + ivPlay.getWidth());
                    ivArrow.setLayoutParams(lp);
                }
                {//上面文字和播放
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rlPointTop.getLayoutParams();
                    int leftMargin = (int) ((pointWidth * pos / 1000) - rlPointTop.getWidth() / 2 + 3 * screenDensity + loction[0]);//位置显示居中，3 * screenDensity是SeekBar居左
                    logger.i("showTip:leftMargin=" + leftMargin + ",getWidth=" + rlPointTop.getWidth());
                    if (leftMargin <= 0) {//目前调整SeekBar，字数少，不会出现
                        leftMargin = 12;
                    } else {
                        int left = ScreenUtils.getScreenWidth() - leftMargin - rlPointTop.getWidth();
                        if (left <= 0) {//目前调整SeekBar，字数少，不会出现
                            leftMargin = leftMargin + left - 12;
                        }
                    }
                    lp.leftMargin = leftMargin;
                    rlPointTop.setLayoutParams(lp);
                }
                return false;
            }
        });
        contentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                XesMobAgent.videoPointClick(where + 2 + "$" + key);
//                XESToastUtils.showToast(activity, "" + XesStringUtils.generateTime(insretTime * 1000));
                rlKeytip.removeAllViews();
                hide();
                Context context = getContext();
                if (context instanceof OnPointClick) {
                    OnPointClick onPointClick = (OnPointClick) context;
                    onPointClick.onOnPointClick(videoQuestionEntity, insretTime * 1000);
                }
                mPlayer.seekTo(insretTime * 1000);
            }
        });
    }

    /** 互动题点点击事件 */
    public interface OnPointClick {
        void onOnPointClick(VideoQuestionEntity videoQuestionEntity, long position);
    }
}
