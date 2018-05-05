package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.util.Point;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by linyuqiang on 2018/5/1.
 * 站立直播红包组内战况布局
 */
public class RedPackageTeamPage extends BasePager {
    private ArrayList<GoldTeamStatus.Student> addStudents = new ArrayList<>();
    private HashMap<String, Bitmap> stuHeadBitmap = new HashMap<>();
    ArrayList<GoldTeamStatus.Student> students;
    GoldTeamStatus goldTeamStatus;
    private RedPackagePage.RedPackagePageAction redPackageAction;
    RelativeLayout rl_livevideo_redpackage_teams;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    String file10 = "live_stand/frame_anim/redpackage/10_team_mine";
    String file11 = "live_stand/frame_anim/redpackage/11_team_mine_loop";
    String file12 = "live_stand/frame_anim/redpackage/12_team_other";
    String file13 = "live_stand/frame_anim/redpackage/13_team_other_loop";
    private boolean isLive;
    private int operateId;
    private Bitmap headBitmap;

    public RedPackageTeamPage(Context context, int operateId, boolean isLive, GoldTeamStatus goldTeamStatus, RedPackagePage.RedPackagePageAction redPackageAction) {
        super(context);
        this.operateId = operateId;
        this.isLive = isLive;
        this.goldTeamStatus = goldTeamStatus;
        this.redPackageAction = redPackageAction;
        students = goldTeamStatus.getStudents();
    }

    public void setHeadBitmap(Bitmap headBitmap) {
        this.headBitmap = headBitmap;
    }

    public void setStuHeadBitmap(HashMap<String, Bitmap> stuHeadBitmap) {
        this.stuHeadBitmap = stuHeadBitmap;
    }

    @Override
    public View initView() {
        rl_livevideo_redpackage_teams = new RelativeLayout(mContext);
        return rl_livevideo_redpackage_teams;
    }

    @Override
    public void initData() {
//        initPos(1334, 750);
        initPos(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
        Random random = new Random();
        for (int i = 0; i < students.size(); i++) {
            GoldTeamStatus.Student student = students.get(i);
            if (addStudents.contains(student)) {
                continue;
            }
            addStudents.add(student);
            ImageView imageView = new ImageView(mContext);
            boolean center = student.isMe();
//            boolean center = rl_livevideo_redpackage_teams.getChildCount() == 0;
//            student.setMe(center);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            if (student.isMe()) {
//                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//            } else {
//                lp.leftMargin = random.nextInt(1800);
//                lp.topMargin = random.nextInt(900);
//            }
            if (center) {
                lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            } else {
                if (!teamLeftAndTops.isEmpty()) {
                    Point point = teamLeftAndTops.remove(0);
                    lp.leftMargin = (int) point.x;
                    lp.topMargin = (int) point.y;
                } else {
                    lp.leftMargin = random.nextInt(1700);
                    lp.topMargin = random.nextInt(850);
                }
            }
            final ArrayList<FrameAnimation> frameAnimations2;
            if (center) {
                frameAnimations2 = initCenterResult(student, imageView);
            } else {
                frameAnimations2 = initTeamResult(student, imageView);
            }
            imageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    Loger.d(TAG, "onGetTeamPackage:Attached");
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    Loger.d(TAG, "onGetTeamPackage:Detached:Animations=" + frameAnimations2.size());
                    for (int i = 0; i < frameAnimations2.size(); i++) {
                        FrameAnimation frameAnimation = frameAnimations2.get(i);
                        frameAnimation.destory();
                    }
                }
            });
            rl_livevideo_redpackage_teams.addView(imageView, lp);
        }
    }

    /**
     * 自己得到动画，放在中间
     *
     * @param entity
     * @param imageView
     */
    private ArrayList<FrameAnimation> initCenterResult(final GoldTeamStatus.Student entity, final ImageView imageView) {
        final ArrayList<FrameAnimation> frameAnimations2 = new ArrayList<>();
        final String path = file10;
        final FrameAnimation btframeAnimation1 =
                FrameAnimation.createFromAees(mContext, imageView, path, 50, false);
        frameAnimations.add(btframeAnimation1);
        frameAnimations2.add(btframeAnimation1);
        btframeAnimation1.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
                if (file.contains("HSchuchang_00169") || file.contains("HSchuchang_00170") || file.contains("HSchuchang_00171")) {
                    return null;
                }
                boolean havename = false;
                if (file.contains("HSchuchang_00178") || file.contains("HSchuchang_00179") || file.contains("HSchuchang_00180")
                        || file.contains("HSchuchang_00181") || file.contains("HSchuchang_00182")) {
                    havename = true;
                }
                return initTeamHeadAndGold(entity, file, havename, btframeAnimation1);
//                return null;
            }
        });
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                String path = file11;
                final FrameAnimation btframeAnimation2 =
                        FrameAnimation.createFromAees(mContext, imageView, path, 50, true);
                frameAnimations.add(btframeAnimation2);
                frameAnimations2.add(btframeAnimation2);
                btframeAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        return initTeamHeadAndGold(entity, file, true, btframeAnimation2);
                    }
                });
                if (!isLive) {
                    mView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            redPackageAction.onPackageClose(operateId);
                        }
                    }, 4000);
                }
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        return frameAnimations2;
    }

    /**
     * 小组得到金币动画
     *
     * @param entity
     * @param imageView
     */
    private ArrayList<FrameAnimation> initTeamResult(final GoldTeamStatus.Student entity, final ImageView imageView) {
        final ArrayList<FrameAnimation> frameAnimations2 = new ArrayList<>();
        final String path = file12;
        final FrameAnimation btframeAnimation1 =
                FrameAnimation.createFromAees(mContext, imageView, path, 50, false);
        frameAnimations.add(btframeAnimation1);
        frameAnimations2.add(btframeAnimation1);
        btframeAnimation1.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
                if (file.contains("LS15_00003") || file.contains("LS15_00004")) {
                    return null;
                }
                boolean havename = true;
                if (file.contains("LS15_00005") || file.contains("LS15_00006") || file.contains("LS15_00007")
                        || file.contains("LS15_00008") || file.contains("LS15_00001") || file.contains("LS15_00010")) {
                    havename = false;
                }
                return initTeamHeadAndGold(entity, file, havename, btframeAnimation1);
//                return null;
            }
        });
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                String path = file13;
                final FrameAnimation btframeAnimation2 =
                        FrameAnimation.createFromAees(mContext, imageView, path, 50, true);
                frameAnimations.add(btframeAnimation2);
                frameAnimations2.add(btframeAnimation2);
                btframeAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        return initTeamHeadAndGold(entity, file, true, btframeAnimation2);
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
        return frameAnimations2;
    }

    /**
     * 小组头像和金币
     *
     * @param entity
     * @param file
     * @param havename
     * @param upFrameAnimation
     * @return
     */
    private Bitmap initTeamHeadAndGold(final GoldTeamStatus.Student entity, final String file, boolean havename, final FrameAnimation upFrameAnimation) {
        InputStream inputStream = null;
        try {
            inputStream = FrameAnimation.getInputStream(mContext, file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            Bitmap head = null;
            final boolean isMe = entity.isMe();
            if (isMe) {
                head = headBitmap;
                if (head == null) {
                    head = stuHeadBitmap.get(entity.getStuId());
                }
            } else {
                head = stuHeadBitmap.get(entity.getStuId());
            }
            if (head != null && !head.isRecycled()) {
                float scaleWidth = 70f / head.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix, true);
                scalHeadBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
                float left = (bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                float top;
                if (isMe) {
                    left += 3f;
                    top = (bitmap.getHeight() - scalHeadBitmap.getHeight()) / 2;
                } else {
                    top = 120;
                }
                canvas.drawBitmap(scalHeadBitmap, left, top - 2, null);
                scalHeadBitmap.recycle();
            } else {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.with(mContext).load(entity.getAvatar_path()).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                                    if (isMe) {
                                        RedPackageTeamPage.this.headBitmap = headBitmap;
                                    }
                                    stuHeadBitmap.put(entity.getStuId(), headBitmap);
                                    upFrameAnimation.removeBitmapCache(file);
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        }
                    });
                }
            }
            bitmap.recycle();
            //画名字和金币数量
            if (havename) {
                String gold = "+" + entity.getGold();
                View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine2, null);
                TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                tv_livevideo_redpackage_name.setText("" + entity.getShowName());
                TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                tv_livevideo_redpackage_num.setText(gold);
                int width;
                int height;
                if (isMe) {
                    width = 66;
                    height = 44;
                } else {
                    width = 64;
                    height = 44;
                    tv_livevideo_redpackage_name.setTextColor(0xff096D62);
                    tv_livevideo_redpackage_num.setTextColor(0xff096D62);
                }
                tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.5f);
                tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 12.5f);

                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
                layout_live_stand_red_mine1.measure(widthMeasureSpec, heightMeasureSpec);
                layout_live_stand_red_mine1.layout(0, 0, width, height);

                canvas.save();
                int measuredWidth = layout_live_stand_red_mine1.getMeasuredWidth();
                int measuredHeight = layout_live_stand_red_mine1.getMeasuredHeight();
                if (isMe) {
                    canvas.translate((canvasBitmap.getWidth() - measuredWidth) / 2, 286);
                } else {
                    canvas.translate((canvasBitmap.getWidth() - measuredWidth) / 2, 193);
                }
//                Loger.d(TAG, "initTeamHeadAndGold:measuredWidth=" + measuredWidth + ",measuredHeight=" + measuredHeight);
                layout_live_stand_red_mine1.draw(canvas);
                canvas.restore();
            }
            return canvasBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private ArrayList<Point> teamLeftAndTops = new ArrayList<>();

    /** 组内成员领取红包位置 */
    private void initPos(int screenWidth, int screenHeight) {
        if (!teamLeftAndTops.isEmpty()) {
            return;
        }
        float width = 368;//切图上火箭宽度
        float height = 352;//切图上火箭高度
        float width3 = 134;//设计图上火箭宽度
        float height3 = 155;//设计图上火箭高度
        //切图标准宽1334，高750
        float scaleX = (float) screenHeight / 750.0f;
        float scaleY = (float) screenWidth / 1334.0f;
        Loger.d(TAG, "initPos:scaleX=" + scaleX + ",scaleY=" + scaleY);
        teamLeftAndTops.add(new Point(313, 263));//左
        teamLeftAndTops.add(new Point(904, 271));//右
        teamLeftAndTops.add(new Point(472, 72));//左上
        teamLeftAndTops.add(new Point(471, 488));//左下
        teamLeftAndTops.add(new Point(779, 30));//右上
        teamLeftAndTops.add(new Point(743, 481));//右下

        teamLeftAndTops.add(new Point(152, 63));//最左上
        teamLeftAndTops.add(new Point(52, 325));//最左中
        teamLeftAndTops.add(new Point(172, 507));//最左下

        teamLeftAndTops.add(new Point(1141, 128));//最右上
        teamLeftAndTops.add(new Point(1081, 456));//最右中

        float chax = (width - width3) * scaleX / 2;
        float chay = (height - height3) * scaleX / 2;

        Loger.d(TAG, "initPos:chax=" + chax + ",chay=" + chay);
        for (int i = 0; i < teamLeftAndTops.size(); i++) {
            Point point = teamLeftAndTops.get(i);
            point.x = (point.x * scaleX - chax);
            point.y = (point.y * scaleX - chay);
        }
    }

}
