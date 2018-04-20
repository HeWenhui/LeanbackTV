package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.TeamPKBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ContributionRankLayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkProgressBar;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by chenkun on 2018/4/12
 * 战队 pk 结果页
 */
public class TeamPkResultPager extends BasePager{
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
    private TextView tvMyTeamEnergy;
    private TextView tvOtherTeamEnergy;
    private TextView tvAddEnergy;
    private TeamPkProgressBar tpbEnergyBar;
    private RecyclerView rclContributionRank;

    private static  final int ADAPTER_TYPE_CONTRIBUTION_RANK = 1; // 贡献之星
    private static  final int ADAPTER_TYPE_ALL=      2; // 贡献之星


    public TeamPkResultPager(Context context, TeamPKBll pkBll){
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
                tpbEnergyBar.smoothAddProgress(25);
            }
        });

        view.findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpbEnergyBar.setProgress(0);
                tvMyTeamEnergy.setText("0");
            }
        });

        return view;
    }

    private void startAddEnergyEffect() {
        Animation animation = AnimationUtils.loadAnimation(mContext,R.anim.anim_livevideo_teampk_add_energy);
        animation.setFillAfter(true);
        tvAddEnergy.setVisibility(View.VISIBLE);
        tvAddEnergy.startAnimation(animation);
        smoothAddEnergy(20);
    }

    int duration = 1500;
    int maxAddCount = 30;
    int timeGap ;
    int addTimes;
    int increment;
    private int currentEnergy = 0;
    class IncrementTask implements Runnable{
        int startNum;
        int endNum;
        int addNum;
        long timeGap;
        int increment;
        int currentNum;
        IncrementTask(int startNum,int addNum,int increment ,long timeGap){
            this.startNum = startNum;
            this.addNum = addNum;
            this.timeGap = timeGap;
            this.increment = increment;
            this.endNum = startNum + addNum;
            this.currentNum = startNum;
        }
        @Override
        public void run() {
            if(currentNum <= endNum){
                tvMyTeamEnergy.setText(currentNum+"");
                currentNum += increment;
                tvMyTeamEnergy.postDelayed(this,timeGap);
            }
        }
    }


    private void smoothAddEnergy(int num) {

        if(!TextUtils.isEmpty(tvMyTeamEnergy.getText().toString())){
            currentEnergy = Integer.parseInt(tvMyTeamEnergy.getText().toString());
            addTimes = num > maxAddCount?maxAddCount:num;
            timeGap = duration/addTimes;
            increment = num/addTimes;
            IncrementTask task = new IncrementTask(currentEnergy,num,increment,timeGap);
            tvMyTeamEnergy.post(task);
        }

    }


    private void initRecycleView(RecyclerView rclContributionRank) {
        // TODO: 2018/4/20  动态判断函数
        rclContributionRank.setLayoutManager(new GridLayoutManager(mContext,5,
                LinearLayoutManager.VERTICAL,false));
        rclContributionRank.setAdapter(new PkResultAdapter(ADAPTER_TYPE_CONTRIBUTION_RANK));
    }
    



    static class ItemHolder extends RecyclerView.ViewHolder{

        public ItemHolder(View itemView) {
            super(itemView);
        }
    }

    static  class  PkResultAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{

        int mAdapterType;
        PkResultAdapter(int adapterType){
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
            return  mAdapterType == ADAPTER_TYPE_CONTRIBUTION_RANK?5:0;
        }
    }

    
    
    
    
    

    /**
     * 展示老师点赞
     */
    public void showTeacherRraise() {
        String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "teacher_praise/images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR +"teacher_praise/data.json";
        lottieAnimationView.setImageAssetsFolder(lottieResPath);
        LottieComposition.Factory.fromAssetFileName(mContext, lottieJsonPath, new OnCompositionLoadedListener() {
            @Override
            public void onCompositionLoaded(@Nullable LottieComposition lottieComposition) {
                lottieAnimationView.setComposition(lottieComposition);
                lottieAnimationView.resumeAnimation();
            }
        });

      //  lottieAnimationView.addAnimatorListener();
        lottieAnimationView.addAnimatorListener(new PkAnimListener(ANIM_TYPE_PRIASE));

    }

    private class PkAnimListener implements Animator.AnimatorListener{
        private int animType;
        PkAnimListener(int animType){
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
                case  ANIM_TYPE_PK_REUSLT:
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
        Loger.e(TAG,"======> initData called");
    }
}
