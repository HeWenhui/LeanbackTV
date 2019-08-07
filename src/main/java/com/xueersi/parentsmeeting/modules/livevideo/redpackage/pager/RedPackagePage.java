package com.xueersi.parentsmeeting.modules.livevideo.redpackage.pager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveAndBackDebug;
import com.xueersi.parentsmeeting.modules.livevideo.entity.GoldTeamStatus;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.RedPackageStandLog;
import com.xueersi.parentsmeeting.modules.livevideo.util.GlideDrawableUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LayoutParamsUtil;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveSoundPool;
import com.xueersi.parentsmeeting.modules.livevideo.util.StandLiveMethod;
import com.xueersi.parentsmeeting.modules.livevideo.widget.FrameAnimation;
import com.xueersi.parentsmeeting.modules.livevideo.widget.StandLiveTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.Top3FrameAnim;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 站立直播红包页面
 *
 * @author linyuqiang
 * @date 2018/3/21
 */
public class RedPackagePage extends LiveBasePager {
    private int operateId;
    private RedPackagePageAction redPackageAction;
    private ArrayList<GoldTeamStatus.Student> addStudents = new ArrayList<>();
    private ImageView rlLivevideoRedpackageBg;
    private ImageView ivLivevideoRedpackageBg;
    private RelativeLayout rlLivevideoRedpackageTeams;
    GoldTeamStatus goldTeamStatus;
    private ArrayList<FrameAnimation> frameAnimations = new ArrayList<>();
    private HashMap<String, Bitmap> stuHeadBitmap = new HashMap<>();
    private String userName;
    private String headUrl;
    private Bitmap headBitmap;
    /** 获取金币，点击的位置，1.在中间，2.在右边 */
    public static final int CLICK_PACKAGE_1 = 1;
    public static final int CLICK_PACKAGE_2 = 2;
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
    LiveSoundPool soundPool;

    public RedPackagePage(Context context, int operateId, RedPackagePageAction redPackageAction, String userName, String headUrl, boolean isLive, LiveAndBackDebug liveAndBackDebug) {
        super(context);
        this.operateId = operateId;
        this.redPackageAction = redPackageAction;
        this.headUrl = headUrl;
        this.userName = StandLiveTextView.getShortName(userName);
        this.isLive = isLive;
        if (isLive) {
            top3FrameAnim = new Top3FrameAnim(context, rlLivevideoRedpackageBg, stuHeadBitmap, frameAnimations);
        }
        this.liveAndBackDebug = liveAndBackDebug;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.dialog_live_stand_red_packet_view, null);
        rlLivevideoRedpackageBg = mView.findViewById(R.id.rl_livevideo_redpackage_bg);
        LayoutParamsUtil.setViewFullScreen(rlLivevideoRedpackageBg);
        ivLivevideoRedpackageBg = mView.findViewById(R.id.iv_livevideo_redpackage_bg);
        rlLivevideoRedpackageTeams = mView.findViewById(R.id.rl_livevideo_redpackage_teams);
        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                logger.d("onViewAttachedToWindow");
                viewAttached = true;
            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                viewAttached = false;
                logger.d("onViewDetachedFromWindow:frameAnimations=" + frameAnimations.size());
                for (int i = 0; i < frameAnimations.size(); i++) {
                    FrameAnimation animation = frameAnimations.get(i);
                    int destory = animation.destory();
                    logger.d("onViewDetachedFromWindow:animation=" + animation.path + ",destory=" + destory);
                }
                if (soundPool != null) {
                    soundPool.release();
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
                headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "initData", headUrl);
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
     * 得到红包id
     *
     * @return
     */
    public int getOperateId() {
        return operateId;
    }

    /**
     * 创建语音播放
     */
    public void createSoundPool() {
        if (soundPool == null) {
            soundPool = LiveSoundPool.createSoundPool();
        }
    }

    /**
     * 收到金币命令的开场动画
     */
    public void initEnter() {
        createSoundPool();
        final FrameAnimation btframeAnimation1 = createFromAees(file1, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                StandLiveMethod.redPocket(soundPool);
            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation btframeAnimation2 = createFromAees(file2, true);
                frameAnimations.add(btframeAnimation2);
//                        btMesOpenAnimation.setAnimationListener(null);
                ivLivevideoRedpackageBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btframeAnimation1.destory();
                        frameAnimations.remove(btframeAnimation1);
                    }
                }, 60);
                final AtomicBoolean click = new AtomicBoolean(false);
                final FrameAnimation finalBtframeAnimation = btframeAnimation2;
                ivLivevideoRedpackageBg.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (click.get()) {
                            return;
                        }
                        logger.d("onPackageClick(timeout):operateId=" + operateId);
                        ivLivevideoRedpackageBg.setOnClickListener(null);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                        }
                        final FrameAnimation btframeAnimationFile6 = createFromAees(file6, false);
                        frameAnimations.add(btframeAnimationFile6);
                        btframeAnimationFile6.setAnimationListener(new FrameAnimation.AnimationListener() {
                            FrameAnimation btframeAnimationFile7;

                            @Override
                            public void onAnimationStart() {
                                StandLiveMethod.voiceSiu(soundPool);
                                redPackageAction.onPackageRight(operateId);
                                ivLivevideoRedpackageBg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        logger.d("onPackageClick2:operateId=" + operateId + "," + view.getTop());
                                        ivLivevideoRedpackageBg.setOnClickListener(null);
                                        StandLiveMethod.onClickVoice(soundPool);
                                        btframeAnimationFile6.pauseAnimation();
                                        if (btframeAnimationFile7 != null) {
                                            btframeAnimationFile7.pauseAnimation();
                                        }
                                        clickPackage = CLICK_PACKAGE_2;
                                        redPackageAction.onPackageClick(operateId, clickPackage, new AbstractBusinessDataCallBack() {
                                            @Override
                                            public void onDataSucess(Object... objData) {
                                                VideoResultEntity entity = (VideoResultEntity) objData[0];
                                                RedPackageStandLog.sno3_2(liveAndBackDebug, "" + operateId, entity.getHttpUrl(), "" + entity.getHttpRes());
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onAnimationEnd() {
                                rlLivevideoRedpackageBg.setVisibility(View.GONE);
//                                        initResult2();
                                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivLivevideoRedpackageBg.getLayoutParams();
                                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                lp.rightMargin = 40;
                                LayoutParamsUtil.setViewLayoutParams(ivLivevideoRedpackageBg, lp);
                                btframeAnimationFile7 = createFromAees(file7, true);
                                frameAnimations.add(btframeAnimationFile7);
                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                    }
                }, 3400);
                ivLivevideoRedpackageBg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        click.set(true);
                        ivLivevideoRedpackageBg.setOnClickListener(null);
                        logger.d("onPackageClick:operateId=" + operateId);
                        if (finalBtframeAnimation != null) {
                            finalBtframeAnimation.pauseAnimation();
                            ivLivevideoRedpackageBg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finalBtframeAnimation.destory();
                                }
                            }, 60);
                        }
                        final LiveSoundPool.SoundPlayTask playTask = StandLiveMethod.redFly(soundPool);
                        FrameAnimation btframeAnimation3 = createFromAees(file3, false);
                        frameAnimations.add(btframeAnimation3);
                        btframeAnimation3.setAnimationListener(new FrameAnimation.AnimationListener() {

                            @Override
                            public void onAnimationStart() {
                            }

                            @Override
                            public void onAnimationEnd() {
                                clickPackage = CLICK_PACKAGE_1;
                                soundPool.stop(playTask);
                                redPackageAction.onPackageClick(operateId, clickPackage, new AbstractBusinessDataCallBack() {
                                    @Override
                                    public void onDataSucess(Object... objData) {
                                        VideoResultEntity entity = (VideoResultEntity) objData[0];
                                        RedPackageStandLog.sno3(liveAndBackDebug, "" + operateId, entity.getHttpUrl(), "" + entity.getHttpRes());
                                    }
                                });
                                rlLivevideoRedpackageBg.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        if (clickPackage == CLICK_PACKAGE_2) {
            initRightResult(entity);
        } else {
            ViewParent parent = rlLivevideoRedpackageTeams.getParent();
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
        final FrameAnimation btframeAnimation1 = FrameAnimation.createFromAees(mContext, rlLivevideoRedpackageBg,
                file8, 50, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                StandLiveMethod.changeScene(soundPool);
            }

            @Override
            public void onAnimationEnd() {
                FrameAnimation btframeAnimation2 = FrameAnimation.createFromAees(mContext, rlLivevideoRedpackageBg,
                        file9, 50, false);
                frameAnimations.add(btframeAnimation2);
                btframeAnimation2.setAnimationListener(new FrameAnimation.AnimationListener() {
                    @Override
                    public void onAnimationStart() {

                    }

                    @Override
                    public void onAnimationEnd() {
                        rlLivevideoRedpackageTeams.setVisibility(View.VISIBLE);
//                        rlLivevideoRedpackageTeams.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                ViewGroup group = (ViewGroup) rlLivevideoRedpackageTeams.getParent();
//                                if (group == null) {
//                                    return;
//                                }
//                                group.removeView(rlLivevideoRedpackageTeams);
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
//        final FrameAnimation btframeAnimation1 = FrameAnimation.createFromAees(mContext, ivLivevideoRedpackageBg, file5, 650, false);
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
                    String[] split = name.split("_");
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
//                        logger.d( "onAnimationCreate:file=" + file);
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
            bitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            canvasBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
            Canvas canvas = new Canvas(canvasBitmap);

            if (headBitmap != null && !headBitmap.isRecycled()) {
                float scaleWidth = 61f / headBitmap.getHeight();
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleWidth);
                Bitmap scalHeadBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true);
                scalHeadBitmap.setDensity(FrameAnimation.DEFAULT_DENSITY);
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
                                    headBitmap = GlideDrawableUtil.getBitmap(drawable, mLogtf, "initData", headUrl);
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
                View layoutLiveStandRedMine1 = LayoutInflater.from(mContext).inflate(R.layout.layout_live_stand_red_mine1, null);
                TextView tvLivevideoRedpackageName = layoutLiveStandRedMine1.findViewById(R.id.tv_livevideo_redpackage_name);
                tvLivevideoRedpackageName.setText(userName + "");
                TextView tvLivevideoRedpackageNum = layoutLiveStandRedMine1.findViewById(R.id.tv_livevideo_redpackage_num);
                tvLivevideoRedpackageNum.setText(gold);
                tvLivevideoRedpackageName.setTextSize(TypedValue.COMPLEX_UNIT_PX, 11f);
                tvLivevideoRedpackageNum.setTextSize(TypedValue.COMPLEX_UNIT_PX, 10f);
                int width = 52;
                int height = 32;
                int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                layoutLiveStandRedMine1.measure(widthMeasureSpec, heightMeasureSpec);
                layoutLiveStandRedMine1.layout(0, 0, width, height);
                canvas.save();
                canvas.translate((canvasBitmap.getWidth() - layoutLiveStandRedMine1.getMeasuredWidth()) / 2, 162);
                layoutLiveStandRedMine1.draw(canvas);
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
     * 请求到小组金币列表
     *
     * @param entity
     */
    public void onGetTeamPackage(GoldTeamStatus entity) {
        ViewParent parent = rlLivevideoRedpackageTeams.getParent();
        if (parent == null) {
            return;
        }
        if (rlLivevideoRedpackageTeams.getVisibility() != View.VISIBLE) {
            goldTeamStatus = entity;
            return;
        }
        RedPackageStandLog.sno4(liveAndBackDebug, "" + operateId, entity.getHttpUrl(), entity.getHttpRes());
        goldTeamStatus = entity;
        ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
        //当添加的数据和返回的数据一致，没有新数据了
        if (rlLivevideoRedpackageTeams.getChildCount() == students.size()) {
            return;
        }
        RedPackageTeamPage redPackageTeamPage = new RedPackageTeamPage(mContext, operateId, isLive, entity, redPackageAction);
        rlLivevideoRedpackageTeams.addView(redPackageTeamPage.getRootView(), new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        redPackageTeamPage.setHeadBitmap(headBitmap);
        redPackageTeamPage.setStuHeadBitmap(stuHeadBitmap);
        redPackageTeamPage.initData();
    }

    /**
     * 得到幸运儿动画
     *
     * @param entity
     */
    public void onGetTeamRank(final GoldTeamStatus entity) {
        ViewGroup group = (ViewGroup) rlLivevideoRedpackageTeams.getParent();
        if (group != null) {
            group.removeView(rlLivevideoRedpackageTeams);
            rlLivevideoRedpackageTeams.removeAllViews();
        }
        final ArrayList<GoldTeamStatus.Student> students = entity.getStudents();
        while (students.size() > 3) {
            students.remove(students.size() - 1);
        }
//        entity.getStudents().add(entity.getStudents().get(0));
//        entity.getStudents().add(entity.getStudents().get(0));
        String path = file14;
        final FrameAnimation btframeAnimation1 =
                FrameAnimation.createFromAees(mContext, rlLivevideoRedpackageBg, path, 50, false);
        frameAnimations.add(btframeAnimation1);
        btframeAnimation1.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                StandLiveMethod.changeScene(soundPool);
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
                RedPackageStandLog.sno5(liveAndBackDebug, "" + operateId, entity.getHttpUrl(), entity.getHttpRes());
            }

            @Override
            public void onAnimationRepeat() {

            }
        });
    }

    /**
     * 第二个红包到了
     */
    public void onOtherPackage() {
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivLivevideoRedpackageBg.getLayoutParams();
//        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        lp.bottomMargin = 100;
//        ivLivevideoRedpackageBg.setLayoutParams(lp);
        redPackageAction.onPackageClose(operateId);
    }


    private FrameAnimation createFromAees(String path, boolean isRepeat) {
        return FrameAnimation.createFromAees(mContext, ivLivevideoRedpackageBg, path, 50, isRepeat);
    }

    /**
     * 红包领取关闭的一些事件
     */
    public interface RedPackagePageAction {
        /**
         * 红包点击领取
         *
         * @param operateId
         * @param clickPackage 在中间点击还是在右侧
         * @param callBack
         */
        void onPackageClick(int operateId, int clickPackage, AbstractBusinessDataCallBack callBack);

        /**
         * 当红包关闭
         *
         * @param operateId
         */
        void onPackageClose(int operateId);

        /**
         * 当红包往右移动动画。
         *
         * @param operateId
         */
        @Deprecated
        void onPackageRight(int operateId);
    }
}
