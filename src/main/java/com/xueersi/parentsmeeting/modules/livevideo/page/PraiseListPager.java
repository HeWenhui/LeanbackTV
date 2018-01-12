package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.os.SystemClock;
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
import com.xueersi.parentsmeeting.modules.livevideo.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.AutoVerticalScrollTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Zhang Yuansun on 2018/1/2.
 */

public class PraiseListPager extends BasePager {

    public static final String TAG = "PraiseListPager";

    private HonorListEntity honorListEntity;
    private LikeListEntity likeListEntity;
    private ProgressListEntity progressListEntity;
    private LiveVideoActivity videoActivity;
    private LiveBll liveBll;
    private PraiseListBll mPraiseListBll;
    private WeakHandler weakHandler;
    private BaseAdapter myAdapter;

    /** 表扬榜单 */
    private GridView gvPraiseList;
    /** 点赞轮播消息 */
    private AutoVerticalScrollTextView tvMessage;
    /** 点赞 */
    private Button btnLike;
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

    /** 当前表扬榜类型*/
    public final int mPraiseListType;
    public final static int PRAISE_LIST_TYPE_HONOR = 1;
    public final static int PRAISE_LIST_TYPE_LIKE = 3;
    public final static int PRAISE_LIST_TYPE_PROGRESS = 2;

    /** 测试*/
/*    private TextView tv1;
    private TextView tv2;
    private TextView tv3;
    private TextView tv4;
    private TextView tv5;
    private TextView tv6;*/

    private int number =0;
    private boolean isRunning=false;

    public void setRunning(boolean running) {
        isRunning = running;
    }

    private LogToFile logToFile;

    private String mStuName;
    private ArrayList<String> stuNames = new ArrayList<>();
    private ArrayList<Integer> likeNums = new ArrayList<>();
    public String[] likeShowInfo;

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

    public PraiseListPager(Context context, LikeListEntity likeListEntity, LiveBll liveBll, PraiseListBll mPraiseListBll, WeakHandler mVPlayVideoControlHandler) {
        super(context);
        mPraiseListType = PRAISE_LIST_TYPE_LIKE;
        videoActivity = (LiveVideoActivity) context;
        this.likeListEntity = likeListEntity;
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
        gvPraiseList = (GridView) mView.findViewById(R.id.gv_livevideo_praiselist);
        tvMessage = (AutoVerticalScrollTextView) mView.findViewById(R.id.tv_livevideo_praiselist_message);
        btnLike = (Button) mView.findViewById(R.id.btn_livevideo_praiselist_like);
        ivTitle = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_title);
        ivLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_light);
        ivScrollLight = (ImageView) mView.findViewById(R.id.iv_livevideo_praiselist_scroll_light);
        rlLight = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_light);
        rlMessage = (RelativeLayout) mView.findViewById(R.id.rv_livevideo_praiselist_message);
        rlScroll = (RelativeLayout) mView.findViewById(R.id.rl_livevideo_praiselist_scroll);
        tvScroll = (TextView) mView.findViewById(R.id.tv_livevideo_praiselist_scroll);

/*        tv1=(TextView) mView.findViewById(R.id.text_1);
        tv2=(TextView) mView.findViewById(R.id.text_2);
        tv3=(TextView) mView.findViewById(R.id.text_3);
        tv4=(TextView) mView.findViewById(R.id.text_4);
        tv5=(TextView) mView.findViewById(R.id.text_5);
        tv6=(TextView) mView.findViewById(R.id.text_6);*/
        return mView;
    }

    @Override
    public void initData() {

        mStuName = liveBll.getStuName();
        likeShowInfo = new String[]{
                "为你点赞，" + mStuName + "学神~下次榜单再相见！",
                "为你点赞，再接再厉哦，小学霸~",
                "为你点赞，" + mStuName + "好厉害，向你学习！",
                "为你点赞，一起学习，一起进步",
                "为你点赞，下次一定赶超你~",
                "为你点赞，好羡慕能上榜~",
                "为你点赞，" + mStuName + "学神请接收我的膜拜",
                "为你点赞，运气不错，额外获得1颗赞哦~",
                "为你点赞，运气爆棚，额外获得2颗赞哦！"};

        //设置表扬榜榜头的上边距，使其居中显示
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTitle.getLayoutParams();
        int top = (int )(mPraiseListBll.getDisplayHeight()/2-dip2px(videoActivity,89.5f));
        lp.setMargins(0, top,0, 0);

        //表扬榜榜头动画
        startTitleAnimation();

        switch (mPraiseListType){
            case PRAISE_LIST_TYPE_HONOR:
                myAdapter = new HonorAdapter(videoActivity,honorListEntity.getHonorEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(3);
                if(honorListEntity.getPraiseStatus()!=0)
                    btnLike.setVisibility(View.GONE);
                break;
            case PRAISE_LIST_TYPE_LIKE:
                myAdapter = new LikeAdapter(videoActivity,likeListEntity.getLikeEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(3);
                rlMessage.setVisibility(View.GONE);
                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                myAdapter = new ProgressAdapter(videoActivity,progressListEntity.getProgressEntities());
                gvPraiseList.setAdapter(myAdapter);
                gvPraiseList.setNumColumns(2);
                if(progressListEntity.getPraiseStatus()!=0)
                    btnLike.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        gvPraiseList.setSelector(new ColorDrawable(Color.TRANSPARENT));


        //监听点赞按钮点击事件
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"onClick(): btnLike");
                Toast.makeText(videoActivity,"你真棒！谢谢你的点赞",Toast.LENGTH_SHORT).show();
                liveBll.sendLike();
                btnLike.setVisibility(View.GONE);
            }
        });

/*        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"nonce\":\"125746-11448-1515569367333\",\"open\":\"on\",\"type\":\"224\",\"zanType\":\"1\"}");
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"nonce\":\"125746-11448-1515569477420\",\"open\":\"on\",\"type\":\"224\",\"zanType\":\"2\"}");
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"nonce\":\"125746-11448-1515569477420\",\"open\":\"on\",\"type\":\"224\",\"zanType\":\"3\"}");
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"agreeFroms\":[\"赵鹏飞\"],\"isTeacher\":true,\"type\":\"221\"}");
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"agreeFroms\":[\"梁二十八\",\"梁二\",\"梁十四\"],\"isTeacher\":false,\"type\":\"221\"}");
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                liveBll.mIRCcallback.onNotice("","","","",
                        "{\"nonce\":\"125746-11448-1515569367333\",\"open\":\"off\",\"type\":\"224\",\"zanType\":\"1\"}");
            }
        });*/
    }

    /** 计算点赞数量的规则 */
    public int calculateLikeNum(int size){
        int totalNum=0;
        int likeNum=1;
        int probability = mPraiseListBll.getLikeProbability();
        Random random = new Random();
        int i;
        for(int j=0;j<size;j++){
            if(probability==1){
                //1：表示概率不加倍
                i = random.nextInt(9);
                if(i==0){
                    likeNum=2;
                }
                else{
                    i = random.nextInt(19);
                    if(i==0)
                        likeNum=3;
                    else
                        likeNum=1;
                }
            }else if(probability==2){
                //2：表示概率加倍
                i = random.nextInt(4);
                if(i==0){
                    likeNum=2;
                }
                else{
                    i = random.nextInt(9);
                    if(i==0)
                        likeNum=3;
                    else
                        likeNum=1;
                }
            }
            this.likeNums.add(likeNum);
            totalNum+=likeNum;
        }
        return totalNum;

    }

    /** 收到给我点赞的消息 */
    public void receiveLikeMessage(ArrayList<String> stuNames){
        for(int i=0;i<stuNames.size();i++){
            this.stuNames.add(stuNames.get(i));
        }
        //计算点赞总数，发送至教师端
        liveBll.sendLikeNum(calculateLikeNum(stuNames.size()));
        if(!isRunning){
            isRunning=true;
            startDanmaku();
        }

    }


    /** 开始榜单头部动画 */
    public void startTitleAnimation() {
        ivTitle.setVisibility(View.VISIBLE);
        ivLight.setVisibility(View.VISIBLE);

        switch (mPraiseListType){
            case PRAISE_LIST_TYPE_HONOR:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_honor);
                break;
            case PRAISE_LIST_TYPE_LIKE:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_like);
                break;
            case PRAISE_LIST_TYPE_PROGRESS:
                ivTitle.setImageResource(R.drawable.bg_livevideo_praiselist_title_progress);
                break;
            default:
                break;
        }
        ivLight.setImageResource(R.drawable.bg_livevideo_praiselist_light);

        //平移距离 = 平移前中心MarginTop - 平移后中心MarginTop
        float moveUpDistance = (float)(mPraiseListBll.getDisplayHeight())/2-dip2px(videoActivity, 74.5f);

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

        AnimatorSet animSetTitle = new AnimatorSet();
        animSetTitle.play(fadeInTitle).with(expandXTitle).with(expandYTitle);
        animSetTitle.start();

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


    /** 开始滚动点赞弹幕 */
    public void startDanmaku(){
        new Thread(){
            @Override
            public void run() {
                while (stuNames.size()>number&&isRunning){
                    weakHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(stuNames.get(number).equals(mStuName)){
                                //过滤自己给自己点赞的情况
                                number++;
                                if(stuNames.size()>number){
                                    tvMessage.next();
                                    if(likeNums.get(number)==1){
                                        int random = new Random().nextInt(6);
                                        tvMessage.setText(Html.fromHtml(
                                                "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[random]
                                        ));
                                    }
                                    else if(likeNums.get(number)==2)
                                        tvMessage.setText(Html.fromHtml(
                                                "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[7]
                                        ));
                                    else if(likeNums.get(number)==3)
                                        tvMessage.setText(Html.fromHtml(
                                                "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[8]
                                        ));
                                    number++;
                                }
                            }
                            else{
                                tvMessage.next();
                                if(likeNums.get(number)==1){
                                    int random = new Random().nextInt(6);
                                    tvMessage.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[random]
                                    ));
                                }
                                else if(likeNums.get(number)==2)
                                    tvMessage.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[7]
                                    ));
                                else if(likeNums.get(number)==3)
                                    tvMessage.setText(Html.fromHtml(
                                            "<font color='#F13232'>"+stuNames.get(number)+"</font>"+likeShowInfo[8]
                                    ));
                                number++;

                            }

                        }
                    });
                    SystemClock.sleep(2000);
                }
                isRunning=false;
            }
        }.start();

    }

    /** Honor榜 适配器 */
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
            if (honorEntity != null) {
                if(honorEntity.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
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

    /** Like榜 适配器 */
    private class LikeAdapter extends BaseAdapter{

        ArrayList<LikeListEntity.LikeEntity> likeEntities;
        private LayoutInflater layoutInflater;

        public LikeAdapter(Context context, ArrayList<LikeListEntity.LikeEntity> likeEntities) {
            this.likeEntities = likeEntities;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return likeEntities.size();
        }

        @Override
        public Object getItem(int i) {
            return likeEntities.get(i);
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

            LikeListEntity.LikeEntity likeEntitie = (LikeListEntity.LikeEntity)getItem(i);
            if (likeEntitie != null) {
                if(likeEntitie.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                }
                holder.tvName.setText(likeEntitie.getStuName());
                holder.tvCounts.setText("×"+likeEntitie.getStuPraiseNum());
            }
            return convertView;
        }

        class ViewHolder{
            ImageView ivCrown;
            TextView tvName;
            TextView tvCounts;
        }
    }

    /** Progress榜 适配器 */
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
                convertView = layoutInflater.inflate(R.layout.item_livevideo_praiselist_honor,null);
                holder = new ViewHolder();
                holder.ivCrown = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_honor_crown);
                holder.tvName = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_name);
                holder.tvCounts = (TextView)convertView.findViewById(R.id.tv_livevideo_praiselist_honor_counts);
                holder.ivArrow = (ImageView)convertView.findViewById(R.id.iv_livevideo_praiselist_honor_arrow);
                //设置姓名和分数之间的间距
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)  holder.tvCounts.getLayoutParams();
                int left = (int )(dip2px(videoActivity,34));
                lp.setMargins(left, 0,0, 0);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }

            ProgressListEntity.ProgressEntity progressEntity = (ProgressListEntity.ProgressEntity)getItem(i);
            if (progressEntity != null) {
                if(progressEntity.getIsMy()== 1) {
                    holder.ivCrown.setImageResource(R.drawable.ic_livevideo_praiselist_crown);
                    holder.tvName.setTextColor(videoActivity.getResources().getColor(R.color.COLOR_F13232));
                }
                holder.tvName.setText(progressEntity.getStuName());
                holder.tvCounts.setText(progressEntity.getProgressScore()+"分");
                holder.ivArrow.setVisibility(View.VISIBLE);
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }
}
