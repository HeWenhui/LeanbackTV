package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RedPackageStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.Point;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.Top3FrameAnim;
import com.xueersi.xesalib.utils.log.Loger;
import com.xueersi.xesalib.utils.uikit.ScreenUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.utils.uikit.imageloader.SingleConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lyqai on 2018/3/21.
 */
public class RedPackagePage extends BasePager {
    private int operateId;
    private RedPackagePageAction redPackageAction;
    private ArrayList<GoldTeamStatus.Student> addStudents = new ArrayList<>();
    private View rl_livevideo_redpackage_bg;
    private ImageView iv_livevideo_redpackage_bg;
    private RelativeLayout rl_livevideo_redpackage_teams;
    GoldTeamStatus goldTeamStatus;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    private HashMap<String, Bitmap> stuHeadBitmap = new HashMap<>();
    private String userName;
    private String headUrl;
    private Bitmap headBitmap;
    /** 获取金币，点击的位置，1.在中间，2.在右边 */
    private int clickPackage = 1;
    private boolean isLive;
    LiveAndBackDebug liveAndBackDebug;
    Top3FrameAnim top3FrameAnim;
    boolean viewAttached = true;
    String file1 = "live_stand/frame_anim/redpackage/1_enter";
    String file2 = "live_stand/frame_anim/redpackage/2_loop";
    String file3 = "live_stand/frame_anim/redpackage/3_fly_up";
    String file4 = "live_stand/frame_anim/redpackage/4_ship_loop";
    String file5 = "live_stand/frame_anim/redpackage/5_shapeshift";
    String file6 = "live_stand/frame_anim/redpackage/6_narrow";
    String file7 = "live_stand/frame_anim/redpackage/7_right_loop";
    String file8 = "live_stand/frame_anim/redpackage/8_transition";
    String file9 = "live_stand/frame_anim/redpackage/9_teams_bg";
    String file10 = "live_stand/frame_anim/redpackage/10_team_mine";
    String file11 = "live_stand/frame_anim/redpackage/11_team_mine_loop";
    String file12 = "live_stand/frame_anim/redpackage/12_team_other";
    String file13 = "live_stand/frame_anim/redpackage/13_team_other_loop";
    String file14 = "live_stand/frame_anim/redpackage/14_transition";

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction, String userName, String headUrl, boolean isLive, LiveAndBackDebug liveAndBackDebug) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        this.headUrl = headUrl;
        this.userName = userName;
        this.isLive = isLive;
        if (isLive) {
            top3FrameAnim = new Top3FrameAnim(context, rl_livevideo_redpackage_bg, stuHeadBitmap, frameAnimations);
        }
        this.liveAndBackDebug = liveAndBackDebug;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_live_stand_red_packet_view, null);
        rl_livevideo_redpackage_bg = mView.findViewById(R.id.rl_livevideo_redpackage_bg);
        iv_livevideo_redpackage_bg = mView.findViewById(R.id.iv_livevideo_redpackage_bg);
        rl_livevideo_redpackage_teams = mView.findViewById(R.id.rl_livevideo_redpackage_teams);
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                Loger.d(TAG, "onViewAttachedToWindow");
                viewAttached = true;
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                viewAttached = false;
                Loger.d(TAG, "onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    int destory = animation.destory();
                    Loger.d(TAG, "onViewDetachedFromWindow:animation=" + animation.path + ",destory=" + destory);
                }
            }
        });
        return mView;
    }

    @Override
    public void initData() {
//        Button btnRedPacket = mView.findViewById(R.id.bt_livevideo_redpackage_cofirm);
//        btnRedPacket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redPackageAction.onPackageClick(operateId);
//            }
//        });
//        mView.findViewById(R.id.iv_livevideo_redpackage_close).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                redPackageAction.onPackageClose(operateId);
//            }
//        });
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                return;
            }
        }
        ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
            @Override
            public void onSuccess(Drawable drawable) {
                headBitmap = ((BitmapDrawable) drawable).getBitmap();
                if (top3FrameAnim != null) {
                    top3FrameAnim.setHeadBitmap(headBitmap);
                }
            }

            @Override
            public void onFail() {

            }
        });
    }

    /**
     * 收到金币命令的开场动画
     */
    public void initEnter() {
        final FrameAnimation btframeAnimation1 = createFromAees(file1, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation btframeAnimation2 = createFromAees(file2, true);
                frameAnimations.add(btframeAnimation2);
//                        btMesOpenAnimation.setAnimationListener(null);
                iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btframeAnimation1.destory();
                        frameAnimations.remove(btframeAnimation1);
                    }
                }, 60);
                final AtomicBoolean click = new AtomicBoolean(false);
                final FrameAnimation finalBtframeAnimation = btframeAnimation2;
                iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (click.get()) {
                            return;
                        }
                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                        }
                        final FrameAnimation btframeAnimation1 = createFromAees(file6, false);
                        frameAnimations.add(btframeAnimation1);
                        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
                            FrameAnimation btframeAnimation1;

                            @Override
                            public void onAnimationStart() {
                                redPackageAction.onPackageRight(operateId);
                                iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Loger.d(TAG, "onPackageClick:operateId=" + operateId + "," + view.getTop() + "," + view);
                                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                                        if (btframeAnimation1 != null) {
                                            btframeAnimation1.pauseAnimation();
                                        }
                                        clickPackage = 2;
                                        redPackageAction.onPackageClick(operateId, clickPackage);
                                        RedPackageStandLog.sno2_2(liveAndBackDebug, "" + operateId);
                                    }
                                });
                            }

                            @Override
                            public void onAnimationEnd() {
                                rl_livevideo_redpackage_bg.setVisibility(View.GONE);
//                                        initResult2();
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_redpackage_bg.getLayoutParams();
                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                lp.rightMargin = 40;
                                iv_livevideo_redpackage_bg.setLayoutParams(lp);
                                btframeAnimation1 = createFromAees(file7, true);
                                frameAnimations.add(btframeAnimation1);
                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                    }
                }, 3400);
                iv_livevideo_redpackage_bg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click.set(true);
                        RedPackageStandLog.sno2(liveAndBackDebug, "" + operateId);
                        iv_livevideo_redpackage_bg.setOnClickListener(null);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                            iv_livevideo_redpackage_bg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finalBtframeAnimation.destory();
                                }
                            }, 60);
                        }
                        FrameAnimation btframeAnimation3 = createFromAees(file3, false);
                        frameAnimations.add(btframeAnimation3);
                        btframeAnimation3.setAnimationListener(new FrameAnimation.AnimationListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                clickPackage = 1;
                                redPackageAction.onPackageClick(operateId, clickPackage);
                                rl_livevideo_redpackage_bg.setBackgroundColor(Color.TRANSPARENT);
//                                redPackageAction.onPackageClose(operateId);
                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    /**
     * 获取金币
     *
     * @param entity
     */
    public void onGetPackage(VideoResultEntity entity) {
        if (clickPackage == 2) {
            initRightResult(entity);
        } else {
            ViewParent parent = rl_livevideo_redpackage_teams.getParent();
            if (parent == null) {
                return;
            }
            initCenterResult(entity);
        }
    }

    /**
     * 在中间获得金币
     *
     * @param entity
     */
    private void initCenterResult(final VideoResultEntity entity) {
        final FrameAnimation btframeAnimation1 = FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg,
                file8, 50, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation btframeAnimation2 = FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg,
                        file9, 50, false);
                frameAnimations.add(btframeAnimation2);
                btframeAnimation2.setAnimationListener(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        rl_livevideo_redpackage_teams.setVisibility(View.VISIBLE);
//                        rl_livevideo_redpackage_teams.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ViewGroup group = (ViewGroup) rl_livevideo_redpackage_teams.getParent();
//                                if (group == null) {
//                                    return;
//                                }
//                                group.removeView(rl_livevideo_redpackage_teams);
//                            }
//                        }, 4000);
                        if (goldTeamStatus != null) {
                            onGetTeamPackage(goldTeamStatus);
                        }
                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    /**
     * 在右侧获得金币
     *
     * @param entity
     */
    private void initRightResult(final VideoResultEntity entity) {
        final FrameAnimation btframeAnimation1 = createFromAees(file5, false);
//        final FrameAnimation btframeAnimation1 = FrameAnimation.createFromAees(mContext, iv_livevideo_redpackage_bg, file5, 650, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setBitmapCreate(new FrameAnimation.BitmapCreate() {
            @Override
            public Bitmap onAnimationCreate(String file) {
//                "package_shapeshift_00152"
                int index = file.lastIndexOf("/");
                String name = "";
                int nameInt = 0;
                if (index != -1) {
                    name = file.substring(index + 1, file.length() - 4);
                    String split[] = name.split("_");
                    try {
                        nameInt = Integer.parseInt(split[split.length - 1]);
                    } catch (Exception e) {

                    }
                }
                if (nameInt > 163 && nameInt < 170) {
                    return initHeadAndGold(entity, file, btframeAnimation1, false);
                } else if (nameInt > 169) {
                    return initHeadAndGold(entity, file, btframeAnimation1, true);
                }
                return null;
            }
        });
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                final FrameAnimation btframeAnimation2 = createFromAees(file4, true);
                frameAnimations.add(btframeAnimation2);
                btframeAnimation2.setBitmapCreate(new FrameAnimation.BitmapCreate() {
                    @Override
                    public Bitmap onAnimationCreate(String file) {
                        Loger.d(TAG, "onAnimationCreate:file=" + file);
//                        return headBitmap;
                        return initHeadAndGold(entity, file, btframeAnimation2, true);
                    }
                });
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        redPackageAction.onPackageClose(operateId);
                    }
                }, 3000);
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    /**
     * 在右侧获得金币,设置头像和金币
     *
     * @param entity
     * @param file
     * @param btframeAnimation1
     * @param drawName
     * @return
     */
    private Bitmap initHeadAndGold(final VideoResultEntity entity, final String file, final FrameAnimation btframeAnimation1, boolean drawName) {
        InputStream inputStream = null;
        try {
            inputStream = FrameAnimation.getInputStream(mContext, file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
            Canvas canvas = new Canvas(canvasBitmap);

            if (headBitmap != null && !headBitmap.isRecycled()) {
                float scaleWidth = 61f / headBitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true);
                scalHeadBitmap.setDensity(160);
                float left = (bitmap.getWidth() - scalHeadBitmap.getWidth()) / 2;
                float top = (bitmap.getHeight() - scalHeadBitmap.getHeight()) / 2;
                canvas.drawBitmap(scalHeadBitmap, left + 3f, top + 4, null);
                scalHeadBitmap.recycle();
            } else {
                Activity activity = (Activity) mContext;
                if (!activity.isFinishing()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImageLoader.with(mContext).load(headUrl).asCircle().asBitmap(new SingleConfig.BitmapListener() {
                                @Override
                                public void onSuccess(Drawable drawable) {
                                    Bitmap headBitmap = ((BitmapDrawable) drawable).getBitmap();
                                    RedPackagePage.this.headBitmap = headBitmap;
                                    btframeAnimation1.removeBitmapCache(file);
                                }

                                @Override
                                public void onFail() {

                                }
                            });
                        }
                    });
                }
            }
            canvas.drawBitmap(bitmap, 0, 0, null);
            bitmap.recycle();
            //画名字和金币数量
            if (drawName) {
                String gold = "+" + entity.getGoldNum();
                View layout_live_stand_red_mine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine1, null);
                TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                tv_livevideo_redpackage_name.setText("" + userName);
                TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                tv_livevideo_redpackage_num.setText(gold);
                layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());
                tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14.5f);
                tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.5f);
                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 160);
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

    /**
     * 请求到小组金币列表
     *
     * @param entity
     */
    public void onGetTeamPackage(GoldTeamStatus entity) {
        ViewParent parent = rl_livevideo_redpackage_teams.getParent();
        if (parent == null) {
            return;
        }
        if (rl_livevideo_redpackage_teams.getVisibility() != View.VISIBLE) {
            goldTeamStatus = entity;
            return;
        }
        initPos(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight());
        RedPackageStandLog.sno3(liveAndBackDebug, "" + operateId);
        goldTeamStatus = entity;
        ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
        if (rl_livevideo_redpackage_teams.getChildCount() == students.size()) {//没有新数据了
            return;
        }
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
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
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
                scalHeadBitmap.setDensity(160);
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
                                        RedPackagePage.this.headBitmap = headBitmap;
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
                if (!isMe) {
                    tv_livevideo_redpackage_name.setTextColor(0xff096D62);
                    tv_livevideo_redpackage_num.setTextColor(0xff096D62);
                }
                tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 14.5f);
                tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 13.5f);
                layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());

                canvas.save();
                if (isMe) {
                    canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 286);
                } else {
                    canvas.translate((canvasBitmap.getWidth() - layout_live_stand_red_mine1.getMeasuredWidth()) / 2, 194);
                }
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

    /**
     * 得到幸运儿动画
     *
     * @param entity
     */
    public void onGetTeamRank(final GoldTeamStatus entity) {
        ViewGroup group = (ViewGroup) rl_livevideo_redpackage_teams.getParent();
        if (group != null) {
            group.removeView(rl_livevideo_redpackage_teams);
            rl_livevideo_redpackage_teams.removeAllViews();
        }
        final ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
        while (students.size() > 3) {
            students.remove(students.size() - 1);
        }
//        entity.getStudents().add(entity.getStudents().get(0));
//        entity.getStudents().add(entity.getStudents().get(0));
        String path = file14;
        final FrameAnimation btframeAnimation1 =
                FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg, path, 50, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                top3FrameAnim.start(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {
                        btframeAnimation1.destory();
                    }

                    @Override
                    public void onAnimationEnd() {
                        redPackageAction.onPackageClose(operateId);
                    }

                    @Override
                    public void onAnimationRepeat() {

                    }
                }, students);
                RedPackageStandLog.sno4(liveAndBackDebug, "" + operateId);
//                String path = file15;
//                FrameAnimation btframeAnimation2 =
//                        FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg, path, 50, false);
//                frameAnimations.add(btframeAnimation2);
//                btframeAnimation2.setAnimationListener(new FrameAnimation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart() {
//                        btframeAnimation1.destory();
//                    }
//
//                    @Override
//                    public void onAnimationEnd() {
//                        String path = file16;
//                        final FrameAnimation btframeAnimation3 =
//                                FrameAnimation.createFromAees(mContext, rl_livevideo_redpackage_bg, path, 50, true);
//                        frameAnimations.add(btframeAnimation3);
//                        btframeAnimation3.setBitmapCreate(new FrameAnimation.BitmapCreate() {
//                            @Override
//                            public Bitmap onAnimationCreate(String file) {
//                                if (students.isEmpty()) {
//                                    return null;
//                                }
//                                return initTeamRankHeadAndGold(students, file, true, btframeAnimation3);
//                            }
//                        });
//                        rl_livevideo_redpackage_bg.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                btframeAnimation3.destory();
//                                redPackageAction.onPackageClose(operateId);
//                            }
//                        }, 3000);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat() {
//
//                    }
//                });
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    private Bitmap initTeamRankHeadAndGold(ArrayList<GoldTeamStatus.Student> students, final String file, boolean havename, final FrameAnimation upFrameAnimation) {
        InputStream inputStream = null;
        try {
            inputStream = FrameAnimation.getInputStream(mContext, file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.setDensity(160);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(160);
            Canvas canvas = new Canvas(canvasBitmap);
            canvas.drawBitmap(bitmap, 0, 0, null);
            float[] headWidth = {108f, 96f, 96f};
            int mid = bitmap.getWidth() / 2;
            float[][] headLeftAndRights = {{mid - 20, 295}, {mid - 200, 382}, {mid + 170, 398}};
            float[] textTops = {408, 478, 492};
            int[] textColors = {0xffD45F19, 0xff0C719B, 0xffD04715};
            for (int i = 0; i < students.size(); i++) {
                Bitmap head;
                final GoldTeamStatus.Student entity = students.get(i);
                final boolean isMe = entity.isMe();
                if (isMe) {
                    head = headBitmap;
                    if (head == null) {
                        head = stuHeadBitmap.get(entity.getStuId());
                    }
                } else {
                    head = stuHeadBitmap.get(entity.getStuId());
                }
                float[] leftAndRight = headLeftAndRights[i];
                float left = leftAndRight[0];
                int scalHeadWidth = -1;
                if (head != null && !head.isRecycled()) {
                    float scaleWidth = headWidth[i] / head.getHeight();
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleWidth);
                    Bitmap scalHeadBitmap = Bitmap.createBitmap(head, 0, 0, head.getWidth(), head.getHeight(), matrix, true);
                    scalHeadBitmap.setDensity(160);
                    if (i == 0) {
                        left = mid - scalHeadBitmap.getWidth() / 2;
                    } else if (i == 1) {
                        left = mid - scalHeadBitmap.getWidth() - 206;
                    } else {
                        left = mid + scalHeadBitmap.getWidth() / 2 + 150;
                    }
                    float top = leftAndRight[1];
                    canvas.drawBitmap(scalHeadBitmap, left, top, null);
                    scalHeadWidth = scalHeadBitmap.getWidth();
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
                                            RedPackagePage.this.headBitmap = headBitmap;
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
                    ImageView iv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.iv_livevideo_redpackage_num);
                    iv_livevideo_redpackage_num.setImageResource(R.drawable.bg_live_stand_red_gold_big);
                    TextView tv_livevideo_redpackage_name = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_name);
                    tv_livevideo_redpackage_name.setText("" + entity.getShowName());
                    TextView tv_livevideo_redpackage_num = layout_live_stand_red_mine1.findViewById(R.id.tv_livevideo_redpackage_num);
                    tv_livevideo_redpackage_num.setText(gold);
//                    if (i == 0) {
//                        tv_livevideo_redpackage_name.setTextColor(textColors[i]);
//                        tv_livevideo_redpackage_num.setTextColor(textColors[i]);
//                    } else if (i == 1) {
//                        tv_livevideo_redpackage_name.setTextColor(0xff0C719B);
//                        tv_livevideo_redpackage_num.setTextColor(0xff0C719B);
//                    } else if (i == 2) {
//                        tv_livevideo_redpackage_name.setTextColor(0xffD04715);
//                        tv_livevideo_redpackage_num.setTextColor(0xffD04715);
//                    }
                    tv_livevideo_redpackage_name.setTextColor(textColors[i]);
                    tv_livevideo_redpackage_num.setTextColor(textColors[i]);
                    tv_livevideo_redpackage_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, 23);
                    tv_livevideo_redpackage_num.setTextSize(TypedValue.COMPLEX_UNIT_PX, 22);
                    layout_live_stand_red_mine1.measure(canvasBitmap.getWidth(), canvasBitmap.getHeight());
                    layout_live_stand_red_mine1.layout(0, 0, canvasBitmap.getWidth(), canvasBitmap.getHeight());
                    canvas.save();
                    float top = textTops[i];
                    float textLeft = left;
                    if (scalHeadWidth != -1) {
                        textLeft = left + scalHeadWidth / 2 - layout_live_stand_red_mine1.getMeasuredWidth() / 2;
                    }
                    canvas.translate(textLeft, top);
                    layout_live_stand_red_mine1.draw(canvas);
                    canvas.restore();
                }
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

    /**
     * 第二个红包到了
     */
    public void onOtherPackage() {
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_redpackage_bg.getLayoutParams();
//        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lp.bottomMargin = 100;
//        iv_livevideo_redpackage_bg.setLayoutParams(lp);
        redPackageAction.onPackageClose(operateId);
    }


    private FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, iv_livevideo_redpackage_bg, path, 50, isRepeat);
    }

    public interface RedPackagePageAction {
        void onPackageClick(int operateId, int clickPackage);

        void onPackageClose(int operateId);

        void onPackageRight(int operateId);
    }
}
