package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LineEvaluator;
import com.xueersi.parentsmeeting.modules.livevideo.util.Point;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2017/11/16.
 */

public class StartProgress extends RelativeLayout {
    String TAG = "StartProgress";
    ArrayList<Point> points = new ArrayList<>();
    ArrayList<Point> dynamics = new ArrayList<>();
    int px[] = {40, 20, 0, -20, -40};
    Paint paint;
    Runnable runnable;
    ViewGroup childGroup;
    SpeechStrokeTextView tv_live_star_result_sorce;
    /** 星星 */
    TextView tv_live_star_result_count;
    /** 流畅度 */
    TextView tv_live_star_result_fluent;
    /** 准确 */
    TextView tv_live_star_result_accuracy;

    public StartProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.RED);
        Log.i(TAG, "StartProgress:light=" + findViewById(R.id.sp_live_star_result));
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                childGroup = (ViewGroup) findViewById(R.id.ll_live_star_progress_content);
                Log.i(TAG, "StartProgress:group=" + childGroup.getTop());
                for (int i = 0; i < childGroup.getChildCount(); i++) {
                    ImageView imageView = (ImageView) childGroup.getChildAt(i);
                    Point point = new Point();
                    point.width = imageView.getWidth();
                    point.x = imageView.getX() + childGroup.getX();
                    point.y = imageView.getY() + childGroup.getY();
                    points.add(point);
                    Log.i(TAG, "onFinishInflate:imageView=" + imageView.getTop() + "," + imageView.getX() + "," + imageView.getY());
                }
                {
                    View rl_live_star_result_tip = findViewById(R.id.rl_live_star_result_tip);
                    LayoutParams lp = (LayoutParams) rl_live_star_result_tip.getLayoutParams();
                    lp.topMargin = childGroup.getTop() + childGroup.getHeight();
                    Log.i(TAG, "onPreDraw:topMargin=" + lp.topMargin);
                    LayoutParamsUtil.setViewLayoutParams(rl_live_star_result_tip, lp);
                }
                {
                    LayoutParams lp = (LayoutParams) tv_live_star_result_sorce.getLayoutParams();
                    lp.topMargin = (int) (50 * ScreenUtils.getScreenDensity());
                    LayoutParamsUtil.setViewLayoutParams(tv_live_star_result_sorce, lp);
                }
//                invalidate();
                if (runnable != null) {
                    runnable.run();
                    runnable = null;
                }
                ImageView light = (ImageView) findViewById(R.id.iv_live_star_result_light);
                Animation mStarLightAnimRotate = AnimationUtils.loadAnimation(getContext(), R.anim.anim_livevideo_speech_light_rotate);
                mStarLightAnimRotate.setInterpolator(new LinearInterpolator());
                light.startAnimation(mStarLightAnimRotate);
                mStarLightAnimRotate.setFillAfter(true);
                return false;
            }
        });
    }

    private void initDynamics() {
        float scale = (float) ScreenUtils.getScreenWidth() / 692f;
//        scale=1;
        Log.d(TAG, "initDynamics:" + getWidth() + "," + scale + "," + ((float) ScreenUtils.getScreenHeight() / 447f));
        {
            Animation mStarLightAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_livevideo_star_light_alpha);
            ImageView iv_live_star_result_point_dynamic1 = (ImageView) findViewById(R.id.iv_live_star_result_point_dynamic1);
//            iv_live_star_result_point_dynamic1.setColorFilter(Color.BLUE);
            LayoutParams lp = (LayoutParams) iv_live_star_result_point_dynamic1.getLayoutParams();
            lp.leftMargin = (int) ((175) * scale);
            lp.topMargin = (int) ((272 - 18) * scale);
//            lp.leftMargin = (int) (155 * scale - iv_live_star_result_point_dynamic1.getWidth());
//            lp.topMargin = (int) (252 * scale - iv_live_star_result_point_dynamic1.getWidth());
            iv_live_star_result_point_dynamic1.setLayoutParams(lp);
            iv_live_star_result_point_dynamic1.startAnimation(mStarLightAnimAlpha);
        }
        {
            Animation mStarLightAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_livevideo_star_light_alpha2);
            ImageView iv_live_star_result_point_dynamic1 = (ImageView) findViewById(R.id.iv_live_star_result_point_dynamic2);
            LayoutParams lp = (LayoutParams) iv_live_star_result_point_dynamic1.getLayoutParams();
            lp.leftMargin = (int) ((226) * scale);
            lp.topMargin = (int) ((273) * scale);
            iv_live_star_result_point_dynamic1.setLayoutParams(lp);
            iv_live_star_result_point_dynamic1.startAnimation(mStarLightAnimAlpha);
        }
        {
            Animation mStarLightAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_livevideo_star_light_alpha);
            ImageView iv_live_star_result_point_dynamic1 = (ImageView) findViewById(R.id.iv_live_star_result_point_dynamic3);
//            iv_live_star_result_point_dynamic1.setColorFilter(Color.BLUE);
            LayoutParams lp = (LayoutParams) iv_live_star_result_point_dynamic1.getLayoutParams();
            lp.leftMargin = (int) ((459) * scale);
            lp.topMargin = (int) ((198) * scale);
            iv_live_star_result_point_dynamic1.setLayoutParams(lp);
            iv_live_star_result_point_dynamic1.startAnimation(mStarLightAnimAlpha);
        }
        {
            Animation mStarLightAnimAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.anim_livevideo_star_light_alpha2);
            ImageView iv_live_star_result_point_dynamic1 = (ImageView) findViewById(R.id.iv_live_star_result_point_dynamic4);
//            iv_live_star_result_point_dynamic1.setColorFilter(Color.BLUE);
            LayoutParams lp = (LayoutParams) iv_live_star_result_point_dynamic1.getLayoutParams();
            lp.leftMargin = (int) ((457) * scale);
            lp.topMargin = (int) ((245) * scale);
            iv_live_star_result_point_dynamic1.setLayoutParams(lp);
            iv_live_star_result_point_dynamic1.startAnimation(mStarLightAnimAlpha);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Bitmap bitmap = DrawableHelper.bitmapFromResource(getResources(), R.drawable.bg_live_star_result_bg);
//        LayoutParams lp = (LayoutParams) getLayoutParams();
//        lp.width = bitmap.getWidth();
//        lp.height = bitmap.getHeight();
//        setLayoutParams(lp);
        tv_live_star_result_sorce = (SpeechStrokeTextView) findViewById(R.id.tv_live_star_result_sorce);
        tv_live_star_result_count = (TextView) findViewById(R.id.tv_live_star_result_count);
        tv_live_star_result_fluent = (TextView) findViewById(R.id.tv_live_star_result_fluent);
        tv_live_star_result_accuracy = (TextView) findViewById(R.id.tv_live_star_result_accuracy);
//        Typeface fontFace = Typeface.createFromAsset(getContext().getAssets(),
//                "fangzhengcuyuan.ttf");
//        tv_live_star_result_sorce.setTypeface(fontFace);
//        tv_live_star_result_count.setTypeface(fontFace);
//        tv_live_star_result_fluent.setTypeface(fontFace);
//        tv_live_star_result_accuracy.setTypeface(fontFace);
        TextView borderText = tv_live_star_result_sorce.getBorderText();
//        borderText.setTypeface(fontFace);
        tv_live_star_result_sorce.invalidate();
        initDynamics();
    }

//    @Override
//    public void setVisibility(int visibility) {
//        super.setVisibility(visibility);
//        if (visibility == VISIBLE) {
//            initDynamics();
//        }
//    }
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (points.size() == 5) {
//            for (int i = 0; i < points.size(); i++) {
//                if (i % 2 != 0) {
//                    continue;
//                }
//                Point point = points.get(i);
//                canvas.drawRect(point.x, point.y, point.x + point.width, point.y + point.width, paint);
//            }
//        }
//    }

    public void setIsWord() {
        tv_live_star_result_fluent.setVisibility(GONE);
        tv_live_star_result_accuracy.setVisibility(GONE);
        LinearLayout ll_live_star_result_count = (LinearLayout) findViewById(R.id.ll_live_star_result_count);
        LayoutParams lp = (LayoutParams) ll_live_star_result_count.getLayoutParams();
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ll_live_star_result_count.setLayoutParams(lp);
    }

    public void setAnswered() {
        ViewGroup rl_live_star_result_tip = (ViewGroup) findViewById(R.id.rl_live_star_result_tip);
        rl_live_star_result_tip.removeAllViews();
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView(tv_live_star_result_fluent);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) (15 * ScreenUtils.getScreenDensity());
        linearLayout.addView(tv_live_star_result_accuracy, lp);
        LayoutParams lp2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rl_live_star_result_tip.addView(linearLayout, lp2);
    }

    public void setProgress(final int progress) {
        Log.i(TAG, "setProgress:progress=" + progress + ",points=" + points.size());
        if (points.size() != 5) {
            runnable = new Runnable() {
                @Override
                public void run() {
                    setProgress(progress);
                }
            };
            return;
        }
        for (int i = 0; i < childGroup.getChildCount(); i++) {
            ImageView imageView = (ImageView) childGroup.getChildAt(i);
            imageView.setImageResource(R.drawable.bg_live_star_grey);
        }
        startProgress(0, progress);
    }

    public void setSorce(int sorce) {
        tv_live_star_result_sorce.setText(sorce + "分");
    }

    public void setStarCount(int count) {
        tv_live_star_result_count.setText("+" + count + "枚");
    }

    public void setFluent(int fluent) {
        tv_live_star_result_fluent.setText("流畅性:" + fluent);
    }

    public void setAccuracy(int accuracy) {
        tv_live_star_result_accuracy.setText("准确性:" + accuracy);
    }

    private void startProgress(final int index, final int progress) {
        if (index == progress) {
            View rl_live_star_result_tip = findViewById(R.id.rl_live_star_result_tip);
            View ll_live_star_result_count = findViewById(R.id.ll_live_star_result_count);
            ImageView iv_live_star_result_count = (ImageView) findViewById(R.id.iv_live_star_result_count);
            if (iv_live_star_result_count == null) {
                return;
            }
            Log.d(TAG, "startProgress:iv_live_star_result_count=" + rl_live_star_result_tip.getTop() + "," + ll_live_star_result_count.getTop() + "," + iv_live_star_result_count.getTop());
            final ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.bg_live_star_result_count);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.leftMargin = iv_live_star_result_count.getLeft() + ll_live_star_result_count.getLeft();
            addView(imageView, lp);
            Point startPoint = new Point(lp.leftMargin, 0);
            /**
             * 右侧星星结束位置
             */
            Point endStarPoint = new Point(lp.leftMargin, rl_live_star_result_tip.getTop() + ll_live_star_result_count.getTop() + iv_live_star_result_count.getTop());
            ValueAnimator translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator.PointAndFloat(startPoint), new LineEvaluator.PointAndFloat(endStarPoint));
            translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            translateValueAnimator.setDuration(600);
            translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation.getAnimatedValue();
                    LayoutParams params = (LayoutParams) imageView.getLayoutParams();
                    params.topMargin = (int) currentPoint.point.getY();
                    params.leftMargin = (int) currentPoint.point.getX();
                    imageView.setLayoutParams(params);
//                Log.i(TAG, "onAnimationUpdate:fraction=" + currentPoint.fraction);
                }
            });
            translateValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    removeView(imageView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            translateValueAnimator.start();
            return;
        }
        final ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.bg_live_star_yellow);
        Point point = points.get(index);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.leftMargin = getWidth() / 2 - point.width / 2;
//        lp.leftMargin = (int) (point.x + px[index]);
        addView(imageView, lp);
        Point startPoint = new Point(lp.leftMargin, 0);
        /**
         * 右侧星星结束位置
         */
        Point endStarPoint = new Point(point.x, point.y);
        ValueAnimator translateValueAnimator = ValueAnimator.ofObject(new LineEvaluator(), new LineEvaluator.PointAndFloat(startPoint), new LineEvaluator.PointAndFloat(endStarPoint));
        translateValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        translateValueAnimator.setDuration(200);
        translateValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LineEvaluator.PointAndFloat currentPoint = (LineEvaluator.PointAndFloat) animation.getAnimatedValue();
                LayoutParams params = (LayoutParams) imageView.getLayoutParams();
                params.topMargin = (int) currentPoint.point.getY();
                params.leftMargin = (int) currentPoint.point.getX();
                imageView.setLayoutParams(params);
//                Log.i(TAG, "onAnimationUpdate:fraction=" + currentPoint.fraction);
            }
        });
        translateValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ImageView imageView1 = (ImageView) childGroup.getChildAt(index);
                imageView1.setImageResource(R.drawable.bg_live_star_yellow);
                removeView(imageView);
                startProgress(index + 1, progress);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translateValueAnimator.start();
    }
}
