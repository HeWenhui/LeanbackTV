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
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xueersi.parentsmeeting.base.BasePager;
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
import com.xueersi.xesalib.utils.uikit.SizeUtils;
import com.xueersi.xesalib.view.alertdialog.VerifyCancelAlertDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListPager extends BasePager {

    public static final String TAG = "PraiseListPager";

    private HonorListEntity honorListEntity;
    private ThumbsUpListEntity thumbsUpListEntity;
    private ProgressListEntity progressListEntity;
    private LiveVideoActivity videoActivity;
    private LiveBll liveBll;
    private PraiseListBll mPraiseListBll;
    private WeakHandler weakHandler;
    private BaseAdapter myAdapter;
    private SoundPool soundPool;

    /** 表扬榜单 */
    private GridView gvPraiseList;
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
    private RelativeLayout rlMessage;
    private RelativeLayout rlLight;
    private RelativeLayout rlScroll;

    /** 测试按钮*/
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;

    /** 当前表扬榜类型*/
    public final int mPraiseListType;
    public final static int PRAISE_LIST_TYPE_HONOR = 1;
    public final static int PRAISE_LIST_TYPE_THUMBS_UP = 3;
    public final static int PRAISE_LIST_TYPE_PROGRESS = 2;

    /** 我的姓名*/
    private String stuName;
    /** 我是否在榜上*/
    private boolean isOnList = false;
    /** 给我点赞同学姓名*/
    private ArrayList<String> stuNames = new ArrayList<>();
    /** 给我点赞数量*/
    private ArrayList<Integer> thumbsUpNums = new ArrayList<>();
    /** 点赞文案*/
    public String[] thumbsUpCopywriting;

    /** 点赞弹幕定时器*/
    private Timer mTimer = null;
    /** 点赞弹幕定时任务*/
    private TimerTask mTimerTask = null;
    /** 点赞弹幕计数*/
    private int number = 0;
    /** 点赞弹幕线程是否停止*/
    private boolean isStop = true;

    private LogToFile logToFile;

    public PraiseListPager(Context context, HonorListEntity honorListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_HONOR;
        videoActivity = (LiveVideoActivity) context;
        this.honorListEntity = honorListEntity;
        this.liveBll = liveBll;
        this.mPraiseListBll = mPraiseListBll;
        this.weakHandler = mVPlayVideoControlHandler;
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
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
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        initData();
    }

    public PraiseListPager(Context context, ProgressListEntity progressListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_PROGRESS;
        videoActivity = (LiveVideoActivity) context;
        this.progressListEntity = progressListEntity;
        this.liveBll = liveBll;
        this.mPraiseListBll = mPraiseListBll;
        this.weakHandler = mVPlayVideoControlHandler;
        logToFile = new LogToFile(TAG, new File(Environment.getExternalStorageDirectory(), "parentsmeeting/log/" + TAG
                + ".txt"));
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praiselist, null);
        tvTips = (TextView) mView.findViewById(R.id.tv_livevideo_praiselist_tips);
        gvPraiseList = (GridView) mView.findViewById(R.id.gv_livevideo_praiselist);
        tvDanmaku = (AutoVerticalScrollTextView) mView.findViewById(R.id.tv_livevideo_praiselist_danmaku);
        btnThumbsUp = (Button) mView.findViewById(R.id.btn_livevideo_praiselist_thumbs_up);
        ivTitle = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_title);
        ivLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_light);
        ivScrollLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_scroll_light);
        rlLight = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_light);
        rlMessage = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_message);
        rlScroll = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_praiselist_scroll);
        tvScroll = (TextView) mView.findViewById(R.id.tv_livevideo_praiselist_scroll);

        tv1=(TextView) mView.findViewById(R.id.text_1);
        tv2=(TextView) mView.findViewById(R.id.text_2);
        tv3=(TextView) mView.findViewById(R.id.text_3);
        tv4=(TextView) mView.findViewById(R.id.text_4);
        tv5=(TextView) mView.findViewById(R.id.text_5);
        tv6=(TextView) mView.findViewById(R.id.text_6);
        return mView;
    }

    @Override
    public void initData() {
        stuName = liveBll.getStuName();
        thumbsUpCopywriting = new String[]{
                " 为你点赞，" + stuName + "学神~下次榜单再相见！",
                " 为你点赞，再接再厉哦，小学霸~",
                " 为你点赞，" + stuName + "好厉害，向你学习！",
                " 为你点赞，一起学习，一起进步",
                " 为你点赞，下次一定赶超你~",
                " 为你点赞，好羡慕能上榜~",
                " 为你点赞，" + stuName + "学神请接收我的膜拜",
                " 为你点赞，运气不错，额外获得1颗赞哦~",
                " 为你点赞，运气爆棚，额外获得2颗赞哦!"};

        //设置表扬榜榜头的上边距，使其居中显示
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTitle.getLayoutParams();
        int top = (int )(mPraiseListBll.getDisplayHeight()/2-SizeUtils.Dp2Px(videoActivity,89.5f));
        lp.setMargins(0, top,0, 0);

        //开始表扬榜榜头动画
        startTitleAnimation();

        //播放声音
        soundPool= new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundPool.load(videoActivity,R.raw.praise_list,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // TODO Auto-generated method stub
                soundPool.play(1,1, 1, 0, 0, 1);
            }
        });

        switch (mPraiseListType){
            case PRAISE_LIST_TYPE_HONOR:
                tvTips.setText("全对或者订正到全对的同学可以上榜哦~");
                myAdapter = new HonorAdapter(videoActivity,honorListEntity.getHonorEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(3);
                if(honorListEntity.getPraiseStatus()!=0)
                    btnThumbsUp.setVisibility(View.GONE);
                for(int i=0;i<honorListEntity.getHonorEntities().size();i++){
                    if(honorListEntity.getHonorEntities().get(i).getIsMy()==1){
                        isOnList=true;
                        break;
                    }
                }
                break;
            case PRAISE_LIST_TYPE_THUMBS_UP:
                myAdapter = new ThumbsUpAdapter(videoActivity,thumbsUpListEntity.getThumbsUpEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(3);
                tvTips.setVisibility(View.GONE);
                rlMessage.setVisibility(View.GONE);

                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                tvTips.setText("连续两次作业分数(百分制)有进步可以上榜哦~");
                myAdapter = new ProgressAdapter(videoActivity,progressListEntity.getProgressEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(2);
                if(progressListEntity.getPraiseStatus()!=0)
                    btnThumbsUp.setVisibility(View.GONE);
                for(int i=0;i<progressListEntity.getProgressEntities().size();i++){
                    if(progressListEntity.getProgressEntities().get(i).getIsMy()==1){
                        isOnList=true;
                        break;
                    }

                }
                break;
            default:
                break;
        }
        //屏蔽GridView的Item选中变色
        gvPraiseList.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //监听点赞按钮点击事件
        btnThumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPraiseListType==PRAISE_LIST_TYPE_HONOR)
                    liveBll.getHonorList(1);
                if(mPraiseListType==PRAISE_LIST_TYPE_PROGRESS)
                    liveBll.getProgressList(1);
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
                mPraiseListBll.showPraiseScroll("测试","测试");
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("测试一");
                arrayList.add("测试二");
                arrayList.add("测试三");
                mPraiseListBll.receiveThumbsUpNotice(arrayList);
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请稍后重试");
                vcDialog.showDialog();
            }
        });
    }

    /** 计算点赞数量的规则 */
    public int calculateThumbsUpNum(){
        int thumbsUpNum=1;
        int probability = mPraiseListBll.getThumbsUpProbability();
        Random random = new Random();
        int i;
        if(probability==1){
            //1：表示概率不加倍
            i = random.nextInt(9);
            if(i==0){
                thumbsUpNum=2;
            }
            else{
                i = random.nextInt(19);
                if(i==0)
                    thumbsUpNum=3;
                else
                    thumbsUpNum=1;
            }
        }else if(probability==2){
            //2：表示概率加倍
            i = random.nextInt(4);
            if(i==0){
                thumbsUpNum=2;
            }
            else{
                i = random.nextInt(9);
                if(i==0)
                    thumbsUpNum=3;
                else
                    thumbsUpNum=1;
            }
        }
        return thumbsUpNum;
    }

    /** 收到给我点赞的消息 */
    public void receiveThumbsUpNotice(ArrayList<String> stuNames){
        if(!isOnList)
            return;
        int totalNums = 0;
        for(int i=0;i<stuNames.size();i++){
            int thumbsUpNum = calculateThumbsUpNum();
            if(!stuNames.get(i).equals(stuName)){
                //过滤掉自己和同名
                this.stuNames.add(stuNames.get(i));
                this.thumbsUpNums.add(thumbsUpNum);
            }
            totalNums+=thumbsUpNum;
        }
        //计算点赞总数，发送至教师端
        liveBll.sendThumbsUpNum(totalNums);
        number = stuNames.size();
        startTimer();
    }

    /** 开始榜单头部动画 */
    public void startTitleAnimation() {
        ivTitle.setVisibility(View.VISIBLE);
        ivLight.setVisibility(View.VISIBLE);

        switch (mPraiseListType){
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
        float moveUpDistance = (float)(mPraiseListBll.getDisplayHeight())/2 - SizeUtils.Dp2Px(videoActivity,74.5f);

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
    public void startScrollAnimation(String stuName,String tecName) {
        if(!isOnList)
            return;
        Log.i(TAG,"startScrollAnimation");

        rlScroll.setVisibility(View.VISIBLE);
        ivLight.setVisibility(View.INVISIBLE);

        //Html设置字体加粗效果
        tvScroll.setText(
                Html.fromHtml(
                        "<b><tt>"+stuName+"同学</tt></b> 获得 <b><tt>"+tecName+"老师</tt></b> 的重点表扬<br />要努力继续上榜哦!"));

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

        AnimatorSet animSetLight = new AnimatorSet();
        animSetLight.play(rotateLight);
        animSetLight.start();
        rotateLight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rlScroll.setVisibility(View.GONE);
                ivLight.setVisibility(View.VISIBLE);

            }
        });
    }

    private void startTimer(){
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(isStop)
                        stopTimer();
                    else{
                        weakHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvDanmaku.next();
                                if(thumbsUpNums.get(number%thumbsUpNums.size())==1){
                                    int random = new Random().nextInt(6);
                                    tvDanmaku.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number%stuNames.size())+"</font>"+thumbsUpCopywriting[random]
                                    ));
                                }
                                else if(thumbsUpNums.get(number%thumbsUpNums.size())==2)
                                    tvDanmaku.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number%stuNames.size())+"</font>"+thumbsUpCopywriting[7]
                                    ));
                                else if(thumbsUpNums.get(number%thumbsUpNums.size())==3)
                                    tvDanmaku.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number%stuNames.size())+"</font>"+thumbsUpCopywriting[8]
                                    ));
                                number++;
                            }
                        });
                        if(stuNames.size()==1)
                            stopTimer();
                    }
                }
            };
        }

        if(mTimer != null && mTimerTask != null & isStop){
            isStop = false;
            mTimer.schedule(mTimerTask, 0, 2000);
        }

    }

    private void stopTimer(){
        isStop = true;
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public void showThumbsUpToast(){
        btnThumbsUp.setVisibility(View.GONE);
        Toast.makeText(videoActivity,"你真棒！谢谢你的点赞",Toast.LENGTH_SHORT).show();
        soundPool.load(videoActivity,R.raw.thumbs_up,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // TODO Auto-generated method stub
                soundPool.play(2,1, 1, 0, 0, 1);
            }
        });
        liveBll.sendThumbsUp();

    }

    /** 优秀榜适配器 */
    private class HonorAdapter extends BaseAdapter{

        ArrayList<HonorListEntity.HonorEntity> honorEntities;
        private LayoutInflater layoutInflater;

        public HonorAdapter(Context context, ArrayList<HonorListEntity.HonorEntity> honorEntities) {
            this.honorEntities = honorEntities;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return honorEntities.size();
        }

        @Override
        public Object getItem(int i) {
            return honorEntities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.item_livevideo_praiselist_honor,null);
                holder = new ViewHolder();
                holder.ivCrown = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_honor_crown);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_name);
                holder.tvCounts = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_counts);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            HonorListEntity.HonorEntity honorEntity = (HonorListEntity.HonorEntity)getItem(i);
            if (honorEntity != null  && !honorEntity.getStuName().equals("")) {
                if(honorEntity.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                }
                else if(honorEntity.getIsMy()== 0){
                    holder.ivCrown.setImageResource(0);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }
                holder.tvName.setText(honorEntity.getStuName());
                holder.tvCounts.setText("×"+honorEntity.getExcellentNum());
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ivCrown;
            TextView tvName;
            TextView tvCounts;
        }
    }

    /** 点赞榜适配器 */
    private class ThumbsUpAdapter extends BaseAdapter{

        ArrayList<ThumbsUpListEntity.ThumbsUpEntity> thumbsUpEntities;
        private LayoutInflater layoutInflater;

        public ThumbsUpAdapter(Context context, ArrayList<ThumbsUpListEntity.ThumbsUpEntity> thumbsUpEntities) {
            this.thumbsUpEntities = thumbsUpEntities;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return thumbsUpEntities.size();
        }

        @Override
        public Object getItem(int i) {
            return thumbsUpEntities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.item_livevideo_praiselist_honor,null);
                holder = new ViewHolder();
                holder.ivCrown = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_honor_crown);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_name);
                holder.tvCounts = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_counts);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            ThumbsUpListEntity.ThumbsUpEntity thumbsUpEntity = (ThumbsUpListEntity.ThumbsUpEntity)getItem(i);
            if (thumbsUpEntity != null && !thumbsUpEntity.getStuName().equals("")) {
                if(thumbsUpEntity.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                }
                else if(thumbsUpEntity.getIsMy()== 0){
                    holder.ivCrown.setImageResource(0);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }

                holder.tvName.setText(thumbsUpEntity.getStuName());
                holder.tvCounts.setText("×"+thumbsUpEntity.getStuPraiseNum());
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ivCrown;
            TextView tvName;
            TextView tvCounts;
        }
    }

    /** 进步榜适配器 */
    private class ProgressAdapter extends BaseAdapter{

        ArrayList<ProgressListEntity.ProgressEntity> progressEntities;
        private LayoutInflater layoutInflater;

        public ProgressAdapter(Context context, ArrayList<ProgressListEntity.ProgressEntity> progressEntities) {
            this.progressEntities = progressEntities;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return progressEntities.size();
        }

        @Override
        public Object getItem(int i) {
            return progressEntities.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.item_livevideo_praiselist_progress,null);
                holder = new ViewHolder();
                holder.ivCrown = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_progress_crown);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_progress_name);
                holder.tvCounts = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_progress_counts);
                holder.ivArrow = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_progress_arrow);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            ProgressListEntity.ProgressEntity progressEntity = (ProgressListEntity.ProgressEntity)getItem(i);
            if (progressEntity != null && !progressEntity.getStuName().equals("")) {
                if(progressEntity.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                }
                else if(progressEntity.getIsMy()== 0){
                    holder.ivCrown.setImageResource(0);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_666666));
                }

                holder.tvName.setText(progressEntity.getStuName());
                holder.tvCounts.setText(progressEntity.getProgressScore()+"分");
                holder.ivArrow.setImageResource(R.drawable.ic_livevideo_praiselist_arrow);
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ivCrown;
            TextView tvName;
            TextView tvCounts;
            ImageView ivArrow;
        }
    }

    public void setDanmakuStop(boolean isStop) {
        this.isStop = isStop;
    }

    public void releaseSoundPool(){
        soundPool.release();
    }
}
