package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.SoundInfo;
import com.xueersi.parentsmeeting.modules.livevideo.widget.SmoothAddNumTextView;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TimeCountDowTextView;
import com.xueersi.xesalib.utils.log.Loger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chenkun on 2018/4/12
 * 战队 pk 结果页
 */
public class TeamPkResultPager extends BasePager {
    private static final String TAG = "TeamPkResultPager";
    private LottieAnimationView lottieAnimationView;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/pkresult/";
    private final TeamPKBll mTeamPkBll;
    private static final int ANIM_TYPE_PRIASE = 1;       //老师点赞动画
    private static final int ANIM_TYPE_PK_ADVERSARY = 2; //pk 对手动画
    private static final int ANIM_TYPE_PK_REUSLT = 3;    //pk 结果
    private RelativeLayout rlResultRootView;  //pk 结果 信息 根节点
    private ImageView ivMyteamState;
    private ImageView ivOtherTeamState;
    private ImageView ivMyTeamLogo;
    private ImageView ivMyOtherTeamLogo;
    private ImageView ivMyTeacherHead;
    private ImageView ivOtherTeacherHead;
    private TextView tvMyTeacherName;
    private TextView tvOtherTeacherName;
    private TextView tvMyTeamSlogan;
    private TextView tvOtherTeamSlogan;
    private SmoothAddNumTextView tvMyTeamEnergy;
    private TextView tvOtherTeamEnergy;
    private TextView tvAddEnergy;
    private TeamPkProgressBar tpbEnergyBar;
    private RecyclerView rclContributionRank;

    private static final int ADAPTER_TYPE_CONTRIBUTION_RANK = 1; // 贡献之星
    private static final int ADAPTER_TYPE_ALL = 2; // 贡献之星
    private static final int TEXT_SIZE_NAME = 30;
    private static final int TEXT_SIZE_SLOGAN = 40;
    private EffectInfo effectInfo;
    private SoundPool soundPool;
    private HashMap<Integer, SoundInfo> mSoundInfoMap;

    private static final int SOUND_TYPE_BG = 1;       //背景音乐
    private static final int SOUND_TYPE_ADVERSARY = 2;// pk 对手音乐
    private static final int SOUND_TYPE_LOSE = 3;     //  失败音效
    private static final int SOUND_TYPE_WIN = 4;      // 胜利音效

    private static final float SOUND_VOLUME_BG = 0.3f;  //背景音效大小
    private static final float SOUND_VOLUME_FRONT = 0.6f;  //前景音效大小
    private TimeCountDowTextView timeCountDowTextView;

    private static final int TIME_DELAY_AUTO_CLOSE = 8;  // 自动关闭延时时间


    public TeamPkResultPager(Context context, TeamPKBll pkBll) {
        super(context);
        mTeamPkBll = pkBll;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_pkresult, null);
        lottieAnimationView = view.findViewById(R.id.lav_teampk_pkresult);
        rlResultRootView = view.findViewById(R.id.rl_teampk_pkresult_root);

        ivMyteamState = view.findViewById(R.id.iv_teampk_pkresult_myteam_state);
        ivOtherTeamState = view.findViewById(R.id.iv_teampk_pkresult_otherteam_state);
        ivMyTeamLogo = view.findViewById(R.id.iv_teampk_pkresult_myteam_logo);
        ivMyOtherTeamLogo = view.findViewById(R.id.iv_teampk_pkresult_otherteam_logo);

        ivMyTeacherHead = view.findViewById(R.id.iv_teampk_pkresult_myteam_teacher_head);
        ivOtherTeacherHead = view.findViewById(R.id.iv_teampk_pkresult_otherteam_teacher_head);

        tvMyTeacherName = view.findViewById(R.id.tv_teampk_pkresult_myteacher_name);
        tvOtherTeacherName = view.findViewById(R.id.tv_teampk_pkresult_otherteacher_name);

        tvMyTeamSlogan = view.findViewById(R.id.iv_teampk_pkresult_myteam_slogan);
        tvOtherTeamSlogan = view.findViewById(R.id.iv_teampk_pkresult_otherteam_slogan);

        tvMyTeamEnergy = view.findViewById(R.id.tv_teampk_myteam_energy);
        tvOtherTeamEnergy = view.findViewById(R.id.tv_teampk_otherteam_energy);
        tvAddEnergy = view.findViewById(R.id.tv_teampk_myteam_add_energy);

        tpbEnergyBar = view.findViewById(R.id.tpb_teampk_pkresult_pbbar);
        tpbEnergyBar.setMaxProgress(100);
        timeCountDowTextView = view.findViewById(R.id.tv_teampk_pkresult_time_countdow);


        //测试
        tpbEnergyBar.setProgress(20);
        rclContributionRank = view.findViewById(R.id.rcl_teampk_pkresult_contribution_rank);
        initRecycleView(rclContributionRank);

        //测试
        Button btnTest = view.findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddEnergyEffect();
                tpbEnergyBar.smoothAddProgress(20);
            }
        });

        view.findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  tpbEnergyBar.setProgress(0);
                //   tvMyTeamEnergy.setText("0");
                //showPkAdversary();
                //showWin();
                //showLose();
                  showDraw();
            }
        });

        return view;
    }

    /**
     * 显示平局动画
     */
    private void showDraw() {
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "draw/data.json";
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.resumeAnimation();
            }
        });
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PRIASE));
    }

    //显示 失败动画
    private void showLose() {

    }

    /**
     * 显示胜利动画
     */
    private void showWin() {
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        ///LottieEffectInfo effectInfo = new LottieEffectInfo();
    }

    private void startAddEnergyEffect() {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_livevideo_teampk_add_energy);
        animation.setFillAfter(true);
        tvAddEnergy.setVisibility(View.VISIBLE);
        tvAddEnergy.startAnimation(animation);
        tvMyTeamEnergy.smoothAddNum(15);
    }


    private void initRecycleView(RecyclerView rclContributionRank) {
        // TODO: 2018/4/20  动态判断函数
        rclContributionRank.setLayoutManager(new GridLayoutManager(mContext, 5,
                LinearLayoutManager.VERTICAL, false));
        rclContributionRank.setAdapter(new PkResultAdapter(ADAPTER_TYPE_CONTRIBUTION_RANK));
    }


    static class ItemHolder extends RecyclerView.ViewHolder {

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }

    static class PkResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        int mAdapterType;

        PkResultAdapter(int adapterType) {
            this.mAdapterType = adapterType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_teampk_contribution, parent, false));

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            // TODO: 2018/4/20  设置不同数据源
            return mAdapterType == ADAPTER_TYPE_CONTRIBUTION_RANK ? 5 : 0;
        }
    }


    /**
     * 展示老师点赞
     */
    public void showTeacherRraise() {
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/data.json";
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.resumeAnimation();
            }
        });
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PRIASE));
    }


    /**
     * @param
     * @param resId
     * @param volume
     * @param loop
     */
    private void playMusic(final int soundType, int resId, final float volume, final boolean loop) {
        if (soundPool == null) {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }
        if (mSoundInfoMap == null) {
            mSoundInfoMap = new HashMap<Integer, SoundInfo>();
        }
        soundPool.load(mContext, resId, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                int streamId = soundPool.play(sampleId, volume, volume, 0, loop ? -1 : 0, 1);
                SoundInfo soundInfo = mSoundInfoMap.get(soundType);
                if (soundInfo == null) {
                    soundInfo = new SoundInfo(sampleId, streamId);
                    mSoundInfoMap.put(soundType, soundInfo);
                } else {
                    soundInfo.setStreamId(streamId);
                }
            }
        });
    }


    /**
     * 展示pk 对手动画
     */
    public void showPkAdversary() {

        // 播放背景音乐
        playMusic(SOUND_TYPE_BG, R.raw.war_bg, SOUND_VOLUME_BG, true);
        //播放动效
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "pk_adversary/data.json";
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.playAnimation();
            }
        });

        int color = Color.parseColor("#73510A");
        effectInfo = new EffectInfo(null, null, "郭启铭老师",
                "哈哈哈哈哈哈哈哈哈" +
                        "哈哈哈哈哈", 30, color, 40);

        lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                Bitmap reusltBitmap = null;
                if (lottieImageAsset.getFileName().equals("img_3.png")) {
                    reusltBitmap = effectInfo.getTeacherName(lottieImageAsset.getWidth(), lottieImageAsset.getHeight());
                } else if (lottieImageAsset.getFileName().equals("img_4.png")) {
                    reusltBitmap = effectInfo.getSlogan(lottieImageAsset.getWidth(), lottieImageAsset.getHeight());
                } else {
                    InputStream in = null;
                    try {
                        in = mContext.getAssets().open(lottieResPath + "/" + lottieImageAsset.getFileName());
                        reusltBitmap = BitmapFactory.decodeStream(in);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                return reusltBitmap;
            }
        });

        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                startTimeCountDow();
            }
        });

        lottieAnimationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean soundPlayed;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!soundPlayed && animation.getAnimatedFraction() > 0.11f) {
                    soundPlayed = true;
                    playMusic(SOUND_TYPE_ADVERSARY, R.raw.pk_adversary, SOUND_VOLUME_FRONT, false);
                }
            }
        });

    }

    private void startTimeCountDow() {
        timeCountDowTextView.setTimeDuration(TIME_DELAY_AUTO_CLOSE);
        timeCountDowTextView.setTimeSuffix("秒后关闭");
        timeCountDowTextView.startCountDow();
        timeCountDowTextView.setTimeCountDowListener(new TimeCountDowTextView.TimeCountDowListener() {
            @Override
            public void onFinish() {
                closePkResultPager();
            }
        });
    }

    /**
     * 关闭页面释放资源
     */
    private void closePkResultPager() {
        releaseSoundRes();
        if (getRootView().getParent() != null) {
            ((ViewGroup) getRootView().getParent()).removeView(getRootView());
        }
    }

    private void releaseSoundRes() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (mSoundInfoMap != null) {
            mSoundInfoMap.clear();
            mSoundInfoMap = null;
        }
    }


    private static class LottieImageDelegate implements ImageAssetDelegate {
        EffectInfo effectInfo;

        LottieImageDelegate(EffectInfo effectInfo) {
            this.effectInfo = effectInfo;
        }


        @Override
        public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
            return null;
        }
    }


    static class EffectInfo {

        Bitmap mLogo; // 队徽图片
        Bitmap mTeacherHead;// 老师头像
        String mTeacherName; //老师昵称
        String mSlogan;  // 队伍口号

        String logoFileName;         // lottie 资源中 队徽 文件名
        String headFileName;         // lottie 资源中 老师头像 文件名
        String teacherNameFileName; // lottie 中 老师昵称 文件名称
        String sloganFileName;      // lottie 中 口号 文件名
        String mResPath;           // 资源文件 磁盘路径
        int mNameTextSize;
        int mTextColor;
        int mSloganTextSize;

        public EffectInfo(Bitmap mLogo, Bitmap mTeacherHead, String mTeacherName,
                          String mSlogan, int mTextSize, int mTextColor, int sloganTextSize) {
            this.mLogo = mLogo;
            this.mTeacherHead = mTeacherHead;
            this.mTeacherName = mTeacherName;
            this.mSlogan = mSlogan;
            this.mNameTextSize = mTextSize;
            this.mTextColor = mTextColor;
            this.mSloganTextSize = sloganTextSize;
        }

        public void setLogoFileName(String logoFileName) {
            this.logoFileName = logoFileName;
        }

        public String getLogoFileName() {
            return logoFileName;
        }

        public void setHeadFileName(String headFileName) {
            this.headFileName = headFileName;
        }

        public String getHeadFileName() {
            return headFileName;
        }

        public void setTeacherNameFileName(String teacherNameFileName) {
            this.teacherNameFileName = teacherNameFileName;
        }

        public String getTeacherNameFileName() {
            return teacherNameFileName;
        }

        public void setSloganFileName(String sloganFileName) {
            this.sloganFileName = sloganFileName;
        }

        public String getSloganFileName() {
            return sloganFileName;
        }

        public void setTextColor(int textColor) {
            this.mTextColor = textColor;
        }

        public void setTextSize(int textSize) {
            this.mNameTextSize = textSize;
        }

        public void setTeacherName(String name) {

        }

        public void setSlogan(String slogan) {

        }

        public void setLogo(Bitmap logo) {
            this.mLogo = logo;
        }

        public void setTeacherHead(Bitmap teacherHead) {
            this.mTeacherHead = teacherHead;
        }

        public Bitmap getLogo(int width, int height) {
            return mLogo;
        }

        public Bitmap getTeacherHead(int width, int height) {

            return null;
        }


        public Bitmap getTeacherName(int width, int height) {
            return createMsgBitmap(width, height, mTeacherName, mNameTextSize, mTextColor);
        }

        private Bitmap createMsgBitmap(int width, int height, String msg, int textSize, int textColor) {
            Bitmap resultBitmap = null;
            if (TextUtils.isEmpty(msg)) {
                return resultBitmap;
            }
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            // measuer
            String singleCharacter = msg.substring(0, 1);
            float characterWidth = paint.measureText(singleCharacter);
            // 一行能放几个字儿
            int lineNum = (int) (width / characterWidth);

            Rect fontRect;
            List<String> stringList = getStrList(msg, lineNum);
            int size = height / stringList.size();
            //paint.setTextSize(size);
            Log.e("cksdd", "====>setTextSize:" + size + ":" + stringList.size() + ":" + height);
            resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Rect drawRect = null;
            Canvas canvas = new Canvas(resultBitmap);
            for (int i = 0; i < stringList.size(); i++) {
                fontRect = new Rect();
                paint.getTextBounds(stringList.get(i), 0, stringList.get(i).length(), fontRect);
                int textWidth = fontRect.width();
                int textHeight = fontRect.height();
                // int offsetX = (width - textWidth)/2;//无需居中显示
                if (drawRect == null) {
                    drawRect = new Rect(0, 0, width, height / stringList.size() - textHeight / 2);
                } else {
                    drawRect.left = 0;
                    drawRect.top = drawRect.bottom;
                    drawRect.bottom = drawRect.top + height / stringList.size();
                }
                Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
                int baseLine = (drawRect.bottom + drawRect.bottom - fontMetricsInt.bottom - fontMetricsInt.top) / 2;
                canvas.drawText(stringList.get(i), 0, baseLine, paint);
            }
            return resultBitmap;
        }


        public Bitmap getSlogan(int width, int height) {
            return createMsgBitmap(width, height, mSlogan, mSloganTextSize, mTextColor);
        }

        private List<String> getStrList(String text, int length) {
            int size = text.length() / length;
            if (text.length() % length != 0) {
                size += 1;
            }
            return getStrList(text, length, size);
        }

        private List<String> getStrList(String text, int length, int size) {
            List<String> list = new ArrayList<String>();
            for (int index = 0; index < size; index++) {
                String childStr = substring(text, index * length,
                        (index + 1) * length);
                list.add(childStr);
            }
            return list;
        }

        private String substring(String text, int f, int t) {
            if (f > text.length())
                return null;
            if (t > text.length()) {
                return text.substring(f, text.length());
            } else {
                return text.substring(f, t);
            }
        }
    }

    private class PkAnimListener implements Animator.AnimatorListener {
        private int animType;

        PkAnimListener(int animType) {
            this.animType = animType;
        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animType) {
                case ANIM_TYPE_PRIASE:
                    removeLottieView();
                    break;
                case ANIM_TYPE_PK_ADVERSARY:
                    break;
                case ANIM_TYPE_PK_REUSLT:
                    break;
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private void removeLottieView() {
        lottieAnimationView.cancelAnimation();
        lottieAnimationView.removeAllAnimatorListeners();
        lottieAnimationView.setVisibility(View.GONE);
    }


    @Override
    public void initData() {
        Loger.e(TAG, "======> initData called");
    }
}
