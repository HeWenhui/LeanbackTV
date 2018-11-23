package com.xueersi.parentsmeeting.modules.livevideo.enteampk.pager;

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
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.config.EnTeamPkConfig;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.EnTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.entity.PkTeamEntity;
import com.xueersi.parentsmeeting.modules.livevideo.enteampk.item.EnTeamItem;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;
import com.xueersi.ui.adapter.AdapterItemInterface;
import com.xueersi.ui.adapter.CommonAdapter;

import java.util.ArrayList;

public class TeamPkRankPager extends LiveBasePager {
    private GridView gv_livevideo_en_teampk_rank;
    private ImageView iv_livevideo_en_teampk_rank_select;
    CommonAdapter<EnTeamEntity> teamAdapter;
    ArrayList<EnTeamEntity> teamEntitys = new ArrayList<>();
    Handler handler = new Handler(Looper.getMainLooper());
    int index = 0;
    boolean isFinish = false;
    PkTeamEntity pkTeamEntity;
    OnTeamSelect onTeamSelect;

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
        gv_livevideo_en_teampk_rank = view.findViewById(R.id.gv_livevideo_en_teampk_rank);
        iv_livevideo_en_teampk_rank_select = view.findViewById(R.id.iv_livevideo_en_teampk_rank_select);
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
        gv_livevideo_en_teampk_rank.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_livevideo_en_teampk_rank.setAdapter(teamAdapter);
        gv_livevideo_en_teampk_rank.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (gv_livevideo_en_teampk_rank.getChildCount() == res.length) {
                    gv_livevideo_en_teampk_rank.getViewTreeObserver().removeOnPreDrawListener(this);
                    iv_livevideo_en_teampk_rank_select.setVisibility(View.VISIBLE);
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
                View childView = gv_livevideo_en_teampk_rank.getChildAt(index % gv_livevideo_en_teampk_rank.getChildCount());
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_livevideo_en_teampk_rank_select.getLayoutParams();
                int x = (iv_livevideo_en_teampk_rank_select.getWidth() - childView.getWidth()) / 2;
                int y = (iv_livevideo_en_teampk_rank_select.getHeight() - childView.getHeight()) / 2;
                lp.topMargin = gv_livevideo_en_teampk_rank.getTop() + childView.getTop() - x;
                lp.leftMargin = gv_livevideo_en_teampk_rank.getLeft() + childView.getLeft() - y;
                iv_livevideo_en_teampk_rank_select.setLayoutParams(lp);
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
