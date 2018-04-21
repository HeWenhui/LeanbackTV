package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * 战队pk  右侧状态栏
 *
 */
public class TeamPKStateLayout extends FrameLayout {

    private TeamPkProgressBar pkProgressBar;
    private SmoothAddNumTextView tvMyteamEnergy;
    private TextView tvOtherteamEnergy;
    private SmoothAddNumTextView tvCoin;

    private int mMyteamAnergy;
    private int mOtherTeamAnergy;
    private int mCoinNum;

    public TeamPKStateLayout(@NonNull Context context) {
        super(context);
        initView();
    }


    public TeamPKStateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TeamPKStateLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView( ) {
        LayoutInflater.from(getContext()).inflate(R.layout.team_pk_state_layout,this);
        pkProgressBar = findViewById(R.id.tpb_teampk_pkstate_energy_bar);
        tvMyteamEnergy = findViewById(R.id.tv_teampk_pkstate_myteam_energy);
        tvOtherteamEnergy = findViewById(R.id.tv_teampk_pkstate_otherteam_energy);
        tvCoin = findViewById(R.id.tv_teampk_pkstate_coin_num);
        pkProgressBar.setMaxProgress(100);
        //pkProgressBar.setProgress(10);
        // 测试
        bindData(98,50,50);
    }


    /**
     *  刷新 能量，金币
     * @param energrCrement
     * @param coinCrement
     */
    public void updateData(int energrCrement,int coinCrement ){
        mMyteamAnergy += energrCrement;
        mCoinNum += coinCrement;
        //tvMyteamEnergy.setText(mMyteamAnergy+"");
        tvMyteamEnergy.smoothAddNum(energrCrement);
        tvCoin.smoothAddNum(coinCrement);
       // tvCoin.setText(mCoinNum+"");

        float ratio = mMyteamAnergy /(float)(mMyteamAnergy+mOtherTeamAnergy);
        Log.e("PKstate","============> current progress:"+pkProgressBar.getProgress());
        int addProgress = (int) (ratio * 100+0.5f) - pkProgressBar.getProgress();
        pkProgressBar.smoothAddProgress(addProgress);
        Log.e("PKstate","============> current progress 2222:"+pkProgressBar.getProgress()+":"+addProgress);
    }


    /**
     * 绑定数据
     * @param coinNum       当前战队 金币总数
     * @param myTeamAnergy  当前战队 能量值
     * @param otherTeamAnergy 当前对手能量值
     */
    public void bindData(int coinNum ,int myTeamAnergy,int otherTeamAnergy){
        this.mCoinNum = coinNum;
        this.mMyteamAnergy = myTeamAnergy;
        this.mOtherTeamAnergy = otherTeamAnergy;
        tvCoin.setText(mCoinNum+"");
        tvMyteamEnergy.setText(mMyteamAnergy+"");
        tvOtherteamEnergy.setText(otherTeamAnergy+"");
        float ratio = mMyteamAnergy/(float)(mMyteamAnergy+mOtherTeamAnergy);
        int currentProgress = (int) (ratio*100);
        pkProgressBar.setProgress(currentProgress);
    }

}
