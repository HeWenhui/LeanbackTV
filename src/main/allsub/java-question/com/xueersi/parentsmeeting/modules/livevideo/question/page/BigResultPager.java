package com.xueersi.parentsmeeting.modules.livevideo.question.page;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveViewAction;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.VideoQuestionLiveEntity;
import com.xueersi.parentsmeeting.modules.livevideo.event.LiveRoomH5CloseEvent;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.parentsmeeting.modules.livevideo.question.config.LiveQueConfig;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.entity.BigResultItemEntity;
import com.xueersi.parentsmeeting.modules.livevideo.question.item.BigResultAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by linyuqiang on 2019/4/15.  大题互动结果页
 */
public class BigResultPager extends LiveBasePager {
    private BigResultEntity bigResultEntitie;
    private ArrayList<BigResultItemEntity> bigResultItemEntities = new ArrayList<>();
    /** 结果页上边金币标题 */
    private ImageView ivBigqueResultTitle;
    /** 结果页上边金币数量标题 */
    private TextView tvBigqueResultTitle;
    /** 结果页答题列表 */
    private RecyclerView rvBigqueResultList;
    /** 结果页关闭 */
    private ImageView ivBigqueResultClose;
    private LiveViewAction liveViewAction;
    private ImageView ivResultTitleLight;

    public BigResultPager(Context context, LiveViewAction liveViewAction, BigResultEntity bigResultEntitie) {
        super(context, false);
        this.liveViewAction = liveViewAction;
        mView = initView();
        this.bigResultEntitie = bigResultEntitie;
        bigResultItemEntities = bigResultEntitie.getBigResultItemEntityArrayList();
        initData();
        initListener();
    }

    @Override
    public View initView() {
        mView = liveViewAction.inflateView(R.layout.page_livevideo_bigques_result);
        ivBigqueResultTitle = mView.findViewById(R.id.iv_livevideo_bigque_result_title);
        tvBigqueResultTitle = mView.findViewById(R.id.tv_livevideo_bigque_result_title);
        rvBigqueResultList = mView.findViewById(R.id.rv_livevideo_bigque_result_list);
        ivBigqueResultClose = mView.findViewById(R.id.iv_livevideo_bigque_result_close);
        ivResultTitleLight = mView.findViewById(R.id.iv_livevideo_bigque_result_title_light);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvBigqueResultList.setLayoutManager(layoutManager);
        return mView;
    }

    @Override
    public void initData() {
        super.initData();
        int isRight = bigResultEntitie.getIsRight();
        if (isRight == LiveQueConfig.DOTTYPE_RESULT_WRONG) {
            tvBigqueResultTitle.setText("很遗憾答错了");
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_bigque_result_wrong_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light_grey);
        } else if (isRight == LiveQueConfig.DOTTYPE_ITEM_RIGHT) {
            tvBigqueResultTitle.setText("恭喜你答对了    金币+" + bigResultEntitie.getGold());
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light);
        } else if (isRight == LiveQueConfig.DOTTYPE_ITEM_PART_RIGHT) {
            tvBigqueResultTitle.setText("部分正确    金币+" + bigResultEntitie.getGold());
            ivBigqueResultTitle.setImageResource(R.drawable.bg_livevideo_bigque_result_part_title);
            ivResultTitleLight.setImageResource(R.drawable.bg_livevideo_bigque_result_right_title_light);
        }
        BigResultAdapter bigResultAdapter = new BigResultAdapter(bigResultItemEntities);
        rvBigqueResultList.setAdapter(bigResultAdapter);
    }

    @Override
    public void initListener() {
        super.initListener();
        ivBigqueResultClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPagerClose != null) {
                    onPagerClose.onClose(BigResultPager.this);
                }
            }
        });
    }

    public void flyGoldAndUpdate(VideoQuestionLiveEntity videoQuestionLiveEntity1) {
        if (LiveVideoConfig.isPrimary) {
            String testId = videoQuestionLiveEntity1.getvQuestionID();
            LiveRoomH5CloseEvent event = new LiveRoomH5CloseEvent(bigResultEntitie.getGold(), 0, LiveRoomH5CloseEvent.H5_TYPE_INTERACTION, testId);
            event.setBasePager(this);
            event.setCloseByTeahcer(false);
            event.setQuestionType(1); //判断大题互动
            EventBus.getDefault().post(event);
        }
    }
}
