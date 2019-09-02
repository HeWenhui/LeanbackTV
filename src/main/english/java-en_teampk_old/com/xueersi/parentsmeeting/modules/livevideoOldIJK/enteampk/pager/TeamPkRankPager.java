package com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.pager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.enteampk.item.EnTeamItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankPager extends LiveBasePager {
    private GridView gvTeampkRank;
    private ImageView ivTeampkRankSelect;
    private CommonAdapter<EnTeamEntity> teamAdapter;
    private ArrayList<EnTeamEntity> teamEntitys = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private int index = 0;
    private boolean isFinish = false;
    private PkTeamEntity pkTeamEntity;
    private OnTeamSelect onTeamSelect;

    public TeamPkRankPager(Context context) {
        super(context);
        initData();
        initListener();
    }

    public void setPkTeamEntity(PkTeamEntity pkTeamEntity) {
        this.pkTeamEntity = pkTeamEntity;
    }

    public PkTeamEntity getPkTeamEntity() {
        return pkTeamEntity;
    }

    public void setOnTeamSelect(OnTeamSelect onTeamSelect) {
        this.onTeamSelect = onTeamSelect;
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_en_team_rank, null);
        gvTeampkRank = view.findViewById(R.id.gv_livevideo_en_teampk_rank);
        ivTeampkRankSelect = view.findViewById(R.id.iv_livevideo_en_teampk_rank_select);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        final int[] res = EnTeamPkConfig.TEAM_RES;
        for (int i = 0; i < res.length; i++) {
            EnTeamEntity teamEntity = new EnTeamEntity();
            teamEntity.resId = res[i];
            teamEntitys.add(teamEntity);
        }
        teamAdapter = new CommonAdapter<EnTeamEntity>(teamEntitys) {
            @Override
            public AdapterItemInterface<EnTeamEntity> getItemView(Object type) {
                return new EnTeamItem();
            }
        };
        gvTeampkRank.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gvTeampkRank.setAdapter(teamAdapter);
        gvTeampkRank.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (gvTeampkRank.getChildCount() == res.length) {
                    gvTeampkRank.getViewTreeObserver().removeOnPreDrawListener(this);
                    ivTeampkRankSelect.setVisibility(View.VISIBLE);
                    select();
                }
                return false;
            }
        });
    }

    private void select() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isFinish || mView.getParent() == null || ((Activity) mContext).isFinishing()) {
                    isFinish = true;
                    return;
                }
                View childView = gvTeampkRank.getChildAt(index % gvTeampkRank.getChildCount());
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivTeampkRankSelect.getLayoutParams();
                int x = (ivTeampkRankSelect.getWidth() - childView.getWidth()) / 2;
                int y = (ivTeampkRankSelect.getHeight() - childView.getHeight()) / 2;
                lp.topMargin = gvTeampkRank.getTop() + childView.getTop() - x;
                lp.leftMargin = gvTeampkRank.getLeft() + childView.getLeft() - y;
                ivTeampkRankSelect.setLayoutParams(lp);
                logger.d("select:top=" + childView.getTop() + ",left=" + childView.getLeft()
                        + ",width=" + childView.getWidth() + ",height=" + childView.getHeight() + ",x=" + x + ",y=" + y);
                if (pkTeamEntity != null) {
                    if ((index % 8) == pkTeamEntity.getMyTeam()) {
                        logger.d("select:index=" + index);
                        if (onTeamSelect != null) {
                            isFinish = true;
                            onTeamSelect.onTeamSelect(pkTeamEntity);
                        }
                        return;
                    }
                }
                index++;
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void initListener() {
        super.initListener();
    }

    public interface OnTeamSelect {
        void onTeamSelect(PkTeamEntity pkTeamEntity);
    }
}
