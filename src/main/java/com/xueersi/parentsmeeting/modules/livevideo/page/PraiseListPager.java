package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoActivity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.business.PraiseListBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.WeakHandler;
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.AutoVerticalScrollTextView;
import com.xueersi.ui.adapter.RCommonAdapter;
import com.xueersi.ui.adapter.RItemViewInterface;
import com.xueersi.ui.adapter.ViewHolder;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.lib.framework.utils.SizeUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListPager extends LiveBasePager {

    public static final String TAG = "PraiseListPager";

    private HonorListEntity honorListEntity;
    private ThumbsUpListEntity thumbsUpListEntity;
    private ProgressListEntity progressListEntity;
    private LiveVideoActivity videoActivity;
    private LiveBll liveBll;
    private PraiseListBll mPraiseListBll;
    private WeakHandler weakHandler;

    /** 表扬榜单 */
    private RecyclerView rvPraiseList;
    /** 备注 */
    private TextView tvTips;
    /** 点赞弹幕 */
    private AutoVerticalScrollTextView tvDanmaku;
    /** 点赞按钮 */
    private Button btnThumbsUp;
    /** 表扬榜图片 */
    private ImageView ivTitle;
    /** 光线图片 */
    private ImageView ivLight;
    /** 条幅光线图片 */
    private ImageView ivScrollLight;
    /** 条幅内容 */
    private TextView tvScroll;
    /** 条幅背景虚化图片 */
    private ImageView ivScrollBackground;

    private RelativeLayout rlMessage;
    private RelativeLayout rlLight;
    private RelativeLayout rlScroll;

    /** 测试按钮 */
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;

    /** 当前表扬榜类型 */
    private final int mPraiseListType;
    public final static int PRAISE_LIST_TYPE_HONOR = 1;
    public final static int PRAISE_LIST_TYPE_THUMBS_UP = 3;
    public final static int PRAISE_LIST_TYPE_PROGRESS = 2;

    /** 我的姓名 */
    private String stuName;
    /** 我是否在榜上 */
    private boolean isOnList = false;
    /** 我在榜上的位置索引 */
    private int onListIndex = 0;
    /** 给我点赞同学姓名 */
    private ArrayList<String> stuNames = new ArrayList<>();
    /** 给我点赞数量 */
    private ArrayList<Integer> thumbsUpNums = new ArrayList<>();
    /** 点赞文案 */
    public String[] thumbsUpCopywriting;
    private ArrayList<Integer> thumbsUpCopywritingIndex = new ArrayList<>();

    /** 点赞弹幕定时器 */
    private Timer mTimer = null;
    /** 点赞弹幕计数 */
    private int number = 0;
    /** 点赞弹幕线程是否停止 */
    private boolean isStop = true;

    /** 声音池 */
    private SoundPool soundPool;
    /** 榜单弹出声音 */
    private int soundPraiselistIn = 0;
    /** 点赞声音 */
    private int soundThumbsUp = 0;


    public PraiseListPager(Context context, HonorListEntity honorListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_HONOR;
        videoActivity = (LiveVideoActivity) context;
        this.honorListEntity = honorListEntity;
        this.liveBll = liveBll;
        this.mPraiseListBll = mPraiseListBll;
        this.weakHandler = mVPlayVideoControlHandler;
        initData();
    }

    public PraiseListPager(Context context, ThumbsUpListEntity thumbsUpListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_THUMBS_UP;
        videoActivity = (LiveVideoActivity) context;
        this.thumbsUpListEntity = thumbsUpListEntity;
        this.liveBll = liveBll;
        this.mPraiseListBll = mPraiseListBll;
        this.weakHandler = mVPlayVideoControlHandler;
        initData();
    }

    public PraiseListPager(Context context, ProgressListEntity progressListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_PROGRESS;
        videoActivity = (LiveVideoActivity) context;
        this.progressListEntity = progressListEntity;

/*        ProgressListEntity testEntity = new ProgressListEntity();
        testEntity.setPraiseStatus(0);
        for(int i=0;i<30;i++){
            ProgressListEntity.ProgressEntity test = testEntity.new ProgressEntity();
            test.setStuName("张远荪"+i);
            test.setProgressScore(i+"");
            if(i==16)
                test.setIsMy(1);
            testEntity.getProgressEntities().add(test);

        }
        this.progressListEntity = testEntity;*/

        this.liveBll = liveBll;
        this.mPraiseListBll = mPraiseListBll;
        this.weakHandler = mVPlayVideoControlHandler;
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praiselist, null);
        tvTips = (TextView) mView.findViewById(R.id.tv_livevideo_praiselist_tips);
        rvPraiseList = (RecyclerView) mView.findViewById(R.id.gv_livevideo_praiselist);
        tvDanmaku = (AutoVerticalScrollTextView) mView.findViewById(R.id.tv_livevideo_praiselist_danmaku);
        btnThumbsUp = (Button) mView.findViewById(R.id.btn_livevideo_praiselist_thumbs_up);
        ivTitle = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_title);
        ivLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_light);
        ivScrollLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_scroll_light);
        rlLight = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_light);
        rlMessage = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_message);
        rlScroll = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_praiselist_scroll);
        tvScroll = (TextView) mView.findViewById(R.id.tv_livevideo_praiselist_scroll);
        ivScrollBackground = (ImageView) mView.findViewById(R.id.rl_livevideo_praiselist_scroll_bg);

        tv1 = (TextView) mView.findViewById(R.id.text_1);
        tv2 = (TextView) mView.findViewById(R.id.text_2);
        tv3 = (TextView) mView.findViewById(R.id.text_3);
        tv4 = (TextView) mView.findViewById(R.id.text_4);
        tv5 = (TextView) mView.findViewById(R.id.text_5);
        tv6 = (TextView) mView.findViewById(R.id.text_6);
        return mView;
    }

    @Override
    public void initData() {
        //名字
        stuName = liveBll.getStuName();
        //名字缩写
        String abbStuName = stuName;
        if (stuName != null && stuName.length() > 4) {
            abbStuName = stuName.substring(0, 3) + "...";
        }
        thumbsUpCopywriting = new String[]{
                " 为你点赞，" + abbStuName + "学神~下次榜单再相见！",
                " 为你点赞，再接再厉哦，小学霸~",
                " 为你点赞，" + abbStuName + "好厉害，向你学习！",
                " 为你点赞，一起学习，一起进步",
                " 为你点赞，下次一定赶超你~",
                " 为你点赞，好羡慕能上榜~",
                " 为你点赞，" + abbStuName + "学神请接收我的膜拜",
                " 为你点赞，运气不错，额外获得<font color='#F13232'>1</font>颗赞哦~",
                " 为你点赞，运气爆棚，额外获得<font color='#F13232'>2</font>颗赞哦!"};

        //开始表扬榜榜头动画
        startTitleAnimation();

        //播放声音
        if (soundPool == null)
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        if (soundPraiselistIn == 0) {
            soundPraiselistIn = soundPool.load(videoActivity, R.raw.praise_list, 1);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                    // TODO Auto-generated method stub
                    soundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
                }
            });
        } else {
            soundPool.play(soundPraiselistIn, 1, 1, 0, 0, 1);
        }

        RCommonAdapter adapter = null;
        GridLayoutManager layoutManager = null;
        switch (mPraiseListType) {
            case PRAISE_LIST_TYPE_HONOR:
                tvTips.setText("全对或者订正到全对的同学可以上榜哦~");
                adapter = new RCommonAdapter(mContext, honorListEntity.getHonorEntities());
                adapter.addItemViewDelegate(new HonorItem());
                layoutManager = new GridLayoutManager(mContext, 3);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0 && honorListEntity.getHonorEntities().get(0).getIsMy() == 1) {
                            return 3;
                        } else {
                            return 1;
                        }
                    }
                });
                rvPraiseList.setLayoutManager(layoutManager);

                rvPraiseList.setAdapter(adapter);
                if (honorListEntity.getPraiseStatus() != 0)
                    btnThumbsUp.setVisibility(View.GONE);
                for (int i = 0; i < honorListEntity.getHonorEntities().size(); i++) {
                    if (honorListEntity.getHonorEntities().get(i).getIsMy() == 1) {
                        isOnList = true;
                        onListIndex = i;
                        break;
                    }
                }
//                if(isOnList)
//                    rvPraiseList.setSelection(onListIndex);
                break;
            case PRAISE_LIST_TYPE_THUMBS_UP:
                adapter = new RCommonAdapter(mContext, thumbsUpListEntity.getThumbsUpEntities());
                adapter.addItemViewDelegate(new ThunbsUpItem());
                layoutManager = new GridLayoutManager(mContext, 3);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0 && thumbsUpListEntity.getThumbsUpEntities().get(0).getIsMy() == 1) {
                            return 3;
                        } else {
                            return 1;
                        }
                    }
                });
                rvPraiseList.setLayoutManager(layoutManager);
                rvPraiseList.setAdapter(adapter);
                tvTips.setVisibility(View.GONE);
                rlMessage.setVisibility(View.GONE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) rvPraiseList.getLayoutParams();
                lp.setMargins(SizeUtils.Dp2Px(videoActivity, 20),
                        SizeUtils.Dp2Px(videoActivity, 53),
                        SizeUtils.Dp2Px(videoActivity, 20),
                        SizeUtils.Dp2Px(videoActivity, 24));
                for (int i = 0; i < thumbsUpListEntity.getThumbsUpEntities().size(); i++) {
                    if (thumbsUpListEntity.getThumbsUpEntities().get(i).getIsMy() == 1) {
                        isOnList = true;
                        onListIndex = i;
                        break;
                    }
                }
//                if(isOnList)
//                    rvPraiseList.setSelection(onListIndex);
                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                tvTips.setText("连续两次作业分数(百分制)有进步可以上榜哦~");
                adapter = new RCommonAdapter(mContext, progressListEntity.getProgressEntities());
                adapter.addItemViewDelegate(new ProgressItem());
                layoutManager = new GridLayoutManager(mContext, 2);
                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (position == 0 && progressListEntity.getProgressEntities().get(0).getIsMy() == 1) {
                            return 2;
                        } else {
                            return 1;
                        }
                    }
                });
                rvPraiseList.setLayoutManager(layoutManager);
                rvPraiseList.setAdapter(adapter);
                if (progressListEntity.getPraiseStatus() != 0)
                    btnThumbsUp.setVisibility(View.GONE);
                for (int i = 0; i < progressListEntity.getProgressEntities().size(); i++) {
                    if (progressListEntity.getProgressEntities().get(i).getIsMy() == 1) {
                        isOnList = true;
                        onListIndex = i;
                        break;
                    }
                }
//                if(isOnList)
//                    rvPraiseList.setSelection(onListIndex);
                break;
            default:
                break;
        }
        //屏蔽GridView的Item选中变色
        //rvPraiseList.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //监听点赞按钮点击事件
        btnThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (soundThumbsUp == 0) {
                    soundThumbsUp = soundPool.load(videoActivity, R.raw.thumbs_up, 1);
                    soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                            // TODO Auto-generated method stub
                            soundPool.play(soundThumbsUp, 1, 1, 0, 0, 1);
                        }
                    });
                } else {
                    soundPool.play(soundThumbsUp, 1, 1, 0, 0, 1);
                }
                if (mPraiseListType == PRAISE_LIST_TYPE_HONOR)
                    liveBll.getHonorList(1);
                if (mPraiseListType == PRAISE_LIST_TYPE_PROGRESS)
                    liveBll.getProgressList(1);
                btnThumbsUp.setEnabled(false);
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.getHonorList(0);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.getProgressList(0);
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.getThumbsUpList();
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPraiseListBll.showPraiseScroll("测试", "测试");
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("测试1");
                arrayList.add("测试22");
                arrayList.add("测试333");
                mPraiseListBll.receiveThumbsUpNotice(arrayList);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPraiseListBll.closePraiseList();
            }
        });
    }

    /** 计算点赞数量的规则 */
    public int calculateThumbsUpNum() {
        int thumbsUpNum = 1;
        int probability = mPraiseListBll.getThumbsUpProbability();
        Random random = new Random();
        int i;
        if (probability == 1) {
            //1：表示概率不加倍
            i = random.nextInt(9);
            if (i == 0) {
                thumbsUpNum = 2;
            } else {
                i = random.nextInt(19);
                if (i == 0)
                    thumbsUpNum = 3;
                else
                    thumbsUpNum = 1;
            }
        } else if (probability == 2) {
            //2：表示概率加倍
            i = random.nextInt(4);
            if (i == 0) {
                thumbsUpNum = 2;
            } else {
                i = random.nextInt(9);
                if (i == 0)
                    thumbsUpNum = 3;
                else
                    thumbsUpNum = 1;
            }
        }
        return thumbsUpNum;
    }

    /** 开始榜单头部动画 */
    public void startTitleAnimation() {

        //设置表扬榜榜头的上边距，使其居中显示
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTitle.getLayoutParams();
        int top = (int) (mPraiseListBll.getDisplayHeight() / 2 - SizeUtils.Dp2Px(videoActivity, 89.5f));
        lp.setMargins(0, top, 0, 0);

        ivTitle.setVisibility(View.VISIBLE);
        ivLight.setVisibility(View.VISIBLE);

        switch (mPraiseListType) {
            case PRAISE_LIST_TYPE_HONOR:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_excellent);
                break;
            case PRAISE_LIST_TYPE_THUMBS_UP:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_thumbs_up);
                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_progress);
                break;
            default:
                break;
        }
        ivLight.setImageResource(R.drawable.bg_livevideo_praiselist_light);

        //平移距离 = 平移前中心MarginTop - 平移后中心MarginTop
        float moveUpDistance = (float) (mPraiseListBll.getDisplayHeight()) / 2 - SizeUtils.Dp2Px(videoActivity, 74.5f);

        /** 动画表扬榜 */
        //渐现
        ObjectAnimator fadeInTitle = ObjectAnimator.ofFloat(ivTitle, "alpha", 0f, 1f);
        fadeInTitle.setDuration(1000);
        //放大
        ObjectAnimator expandXTitle = ObjectAnimator.ofFloat(ivTitle, "scaleX", 0.6f, 1f);
        expandXTitle.setDuration(1000);
        ObjectAnimator expandYTitle = ObjectAnimator.ofFloat(ivTitle, "scaleY", 0.6f, 1f);
        expandYTitle.setDuration(1000);
        //平移
        ObjectAnimator moveUpTitle = ObjectAnimator.ofFloat(ivTitle, "translationY", 0f, -moveUpDistance);
        moveUpTitle.setDuration(1000);
        //缩小
        ObjectAnimator ShrinkXTitle = ObjectAnimator.ofFloat(ivTitle, "scaleX", 1f, 0.6f);
        ShrinkXTitle.setDuration(1000);
        ObjectAnimator ShrinkYTitle = ObjectAnimator.ofFloat(ivTitle, "scaleY", 1f, 0.6f);
        ShrinkYTitle.setDuration(1000);
        AnimatorSet animSetTitle = new AnimatorSet();
        animSetTitle.play(fadeInTitle).with(expandXTitle).with(expandYTitle).before(moveUpTitle).before(ShrinkXTitle).before(ShrinkYTitle);
        animSetTitle.start();

        /** 动画光芒*/
        //旋转
        ObjectAnimator rotateLight = ObjectAnimator.ofFloat(ivLight, "rotation", 0f, 60f);
        rotateLight.setDuration(1000);
        //平移
        ObjectAnimator moveUpLight = ObjectAnimator.ofFloat(ivLight, "translationY", 0f, -moveUpDistance);
        moveUpLight.setDuration(1000);
        AnimatorSet animSetLight = new AnimatorSet();
        animSetLight.play(rotateLight).before(moveUpLight);
        animSetLight.start();

        moveUpLight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                //修改光芒所在的层级
                super.onAnimationStart(animation);
                ViewGroup viewGroup = (ViewGroup) ivLight.getParent();
                viewGroup.removeView(ivLight);
                rlLight.addView(ivLight);
            }
        });
    }

    /** 开始表扬条幅动画 */
    public void startScrollAnimation(String stuName, String tecName) {
        if (!isOnList)
            return;
        Loger.i(TAG, "startScrollAnimation");

        if (stuName != null && stuName.length() > 4) {
            stuName = stuName.substring(0, 3) + "...";
        }

        if (tecName != null && tecName.length() > 4) {
            tecName = tecName.substring(0, 3) + "...";
        }

        rlScroll.setVisibility(View.VISIBLE);
        ivScrollBackground.setVisibility(View.VISIBLE);
        //ivLight.setVisibility(View.INVISIBLE);

        //Html设置字体加粗效果
        tvScroll.setText(
                Html.fromHtml(
                        "<b><tt>" + stuName + "同学</tt></b> 获得 <b><tt>" + tecName + "老师</tt></b> 的重点表扬<br />要努力继续上榜哦!"));

        //渐现
        ObjectAnimator fadeInBackground = ObjectAnimator.ofFloat(ivScrollBackground, "alpha", 0f, 0.8f);
        fadeInBackground.setDuration(1000);
        fadeInBackground.start();
        //渐隐
        ObjectAnimator fadeOutBackground = ObjectAnimator.ofFloat(ivScrollBackground, "alpha", 0.8f, 0f);
        fadeOutBackground.setDuration(1000);
        fadeOutBackground.setStartDelay(4000);
        fadeOutBackground.start();

        //渐现
        ObjectAnimator fadeInTitle = ObjectAnimator.ofFloat(rlScroll, "alpha", 0f, 1f);
        fadeInTitle.setDuration(1000);
        //放大
        ObjectAnimator expandXTitle = ObjectAnimator.ofFloat(rlScroll, "scaleX", 0.6f, 1f);
        expandXTitle.setDuration(1000);
        ObjectAnimator expandYTitle = ObjectAnimator.ofFloat(rlScroll, "scaleY", 0.6f, 1f);
        expandYTitle.setDuration(1000);

        //渐隐
        ObjectAnimator fadeOutTitle = ObjectAnimator.ofFloat(rlScroll, "alpha", 1f, 0f);
        fadeOutTitle.setDuration(1000);
        //缩小
        ObjectAnimator shrinkXTitle = ObjectAnimator.ofFloat(rlScroll, "scaleX", 1f, 0.6f);
        shrinkXTitle.setDuration(1000);
        ObjectAnimator shrinkYTitle = ObjectAnimator.ofFloat(rlScroll, "scaleY", 1f, 0.6f);
        shrinkYTitle.setDuration(1000);

        AnimatorSet animSetTitleIn = new AnimatorSet();
        animSetTitleIn.play(fadeInTitle).with(expandXTitle).with(expandYTitle);
        animSetTitleIn.start();

        AnimatorSet animSetTitleOut = new AnimatorSet();
        animSetTitleOut.play(fadeOutTitle).with(shrinkXTitle).with(shrinkYTitle).after(4000);
        animSetTitleOut.start();

        //旋转
        ObjectAnimator rotateLight = ObjectAnimator.ofFloat(ivScrollLight, "rotation", 0f, 300f);
        rotateLight.setDuration(5000);

        rotateLight.start();
        rotateLight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rlScroll.setVisibility(View.GONE);
                ivScrollBackground.setVisibility(View.GONE);
                //ivLight.setVisibility(View.VISIBLE);

            }
        });
        liveBll.sendThumbsUpNum(1);
    }

    /** 收到给我点赞的消息 */
    public void receiveThumbsUpNotice(ArrayList<String> stuNames) {
        int stuNamesSize = this.stuNames.size();
        int random;
        if (!isOnList)
            return;
        if (number > this.stuNames.size())
            number = this.stuNames.size();
        int totalNums = 0;
        for (int i = 0; i < stuNames.size(); i++) {
            int thumbsUpNum = calculateThumbsUpNum();
            if (!stuNames.get(i).equals(stuName)) {
                //过滤掉自己和同名
                if (stuNames.get(i).length() > 4) {
                    this.stuNames.add(stuNames.get(i).substring(0, 3) + "...");
                } else {
                    this.stuNames.add(stuNames.get(i));
                }
                if (thumbsUpNum == 1) {
                    random = new Random().nextInt(6);
                    this.thumbsUpCopywritingIndex.add(random);
                } else if (thumbsUpNum == 2) {
                    this.thumbsUpCopywritingIndex.add(7);
                } else if (thumbsUpNum == 3) {
                    this.thumbsUpCopywritingIndex.add(8);
                }
                this.thumbsUpNums.add(thumbsUpNum);
            }
            totalNums += thumbsUpNum;
        }
        //计算点赞总数，发送至教师端
        liveBll.sendThumbsUpNum(totalNums);
        if (this.stuNames.size() != 0 && this.stuNames.size() > stuNamesSize)
            //如果给我点赞的同学的集合不为空，且数量增加，开启弹幕滚动
            startTimer();
    }

    class TanmakuTimerTask extends TimerTask {

        @Override
        public void run() {
            if (isStop)
                stopTimer();
            else {
                weakHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvDanmaku.next();
                        tvDanmaku.setText(Html.fromHtml(
                                "<font color='#F13232'>" + stuNames.get(number % stuNames.size()) + "</font>"
                                        + thumbsUpCopywriting[thumbsUpCopywritingIndex.get(number % stuNames.size())]
                        ));
                        number++;
                    }
                });
                if (stuNames.size() == 1)
                    stopTimer();
            }
        }
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimer != null && isStop) {
            isStop = false;
            mTimer.schedule(new TanmakuTimerTask(), 0, 2000);
        }
    }

    private void stopTimer() {
        isStop = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void showThumbsUpToast() {
        btnThumbsUp.setVisibility(View.GONE);
        Toast.makeText(videoActivity, "你真棒！谢谢你的点赞", Toast.LENGTH_SHORT).show();
        liveBll.sendThumbsUp();
        mPraiseListBll.umsAgentDebug2(mPraiseListType);
    }

    public void setThumbsUpBtnEnabled(boolean enabled) {
        btnThumbsUp.setEnabled(enabled);
    }

    /**
     * 光荣榜item
     */
    private class HonorItem implements RItemViewInterface<HonorListEntity.HonorEntity> {
        ImageView ivCrown;
        TextView tvName;
        TextView tvCounts;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_honor;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            ivCrown = viewHolder.getView(R.id.iv_livevideo_praiselist_honor_crown);
            tvName = viewHolder.getView(R.id.tv_livevideo_praiselist_honor_name);
            tvCounts = viewHolder.getView(R.id.tv_livevideo_praiselist_honor_counts);
        }

        @Override
        public boolean isShowView(HonorListEntity.HonorEntity honorEntity, int i) {
            return true;
        }

        @Override
        public void convert(ViewHolder viewHolder, HonorListEntity.HonorEntity honorEntity, int i) {
            if (honorEntity != null && !honorEntity.getStuName().equals("")) {
                if (honorEntity.getIsMy() == 1) {
                    ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                } else if (honorEntity.getIsMy() == 0) {
                    ivCrown.setImageResource(0);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }
                tvName.setText(honorEntity.getStuName());
//                tvCounts.setText("×" + honorEntity.getExcellentNum());
            }
        }
    }

    /**
     * 点赞榜item
     */
    private class ThunbsUpItem implements RItemViewInterface<ThumbsUpListEntity.ThumbsUpEntity> {
        ImageView ivCrown;
        TextView tvName;
        TextView tvCounts;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_honor;
        }

        @Override
        public boolean isShowView(ThumbsUpListEntity.ThumbsUpEntity thumbsUpEntity, int i) {
            return true;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            ivCrown = (ImageView) viewHolder.getView(R.id.iv_livevideo_praiselist_honor_crown);
            tvName = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_honor_name);
            tvCounts = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_honor_counts);
        }

        @Override
        public void convert(ViewHolder viewHolder, ThumbsUpListEntity.ThumbsUpEntity thumbsUpEntity, int i) {
            if (thumbsUpEntity != null && !thumbsUpEntity.getStuName().equals("")) {
                if (thumbsUpEntity.getIsMy() == 1) {
                    ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                } else if (thumbsUpEntity.getIsMy() == 0) {
                    ivCrown.setImageResource(0);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }

                tvName.setText(thumbsUpEntity.getStuName());
                tvCounts.setText("×" + thumbsUpEntity.getStuPraiseNum());
            }
        }
    }

    /**
     * 进步榜item
     */
    private class ProgressItem implements RItemViewInterface<ProgressListEntity.ProgressEntity> {
        ImageView ivCrown;
        TextView tvName;
        TextView tvCounts;
        ImageView ivArrow;

        @Override
        public int getItemLayoutId() {
            return R.layout.item_livevideo_praiselist_progress;
        }

        @Override
        public boolean isShowView(ProgressListEntity.ProgressEntity progressEntity, int i) {
            return true;
        }

        @Override
        public void initView(ViewHolder viewHolder, int i) {
            ivCrown = (ImageView) viewHolder.getView(R.id.iv_livevideo_praiselist_progress_crown);
            tvName = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_name);
            tvCounts = (TextView) viewHolder.getView(R.id.tv_livevideo_praiselist_progress_counts);
            ivArrow = (ImageView) viewHolder.getView(R.id.iv_livevideo_praiselist_progress_arrow);
        }

        @Override
        public void convert(ViewHolder viewHolder, ProgressListEntity.ProgressEntity progressEntity, int i) {
            if (progressEntity != null && !progressEntity.getStuName().equals("")) {
                if (progressEntity.getIsMy() == 1) {
                    ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                } else if (progressEntity.getIsMy() == 0) {
                    ivCrown.setImageResource(0);
                    tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                    tvCounts.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }

                tvName.setText(progressEntity.getStuName());
                tvCounts.setText(progressEntity.getProgressScore() + "分");
                ivArrow.setImageResource(R.drawable.ic_livevideo_praiselist_arrow);
            }
        }
    }

    public void setDanmakuStop(boolean isStop) {
        this.isStop = isStop;
    }

    public void releaseSoundPool() {
        if (soundPool != null)
            soundPool.release();
    }
}
