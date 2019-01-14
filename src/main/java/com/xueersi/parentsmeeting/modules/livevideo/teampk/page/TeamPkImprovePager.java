package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;

/**
 * 战队pk 二期 进步榜
 *
 * @author chekun
 * created  at 2019/1/14 9:57
 */
public class TeamPkImprovePager extends BasePager {

    private final TeamPkBll mPkBll;
    private View bgMask;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private ImageView ivClostBtn;
    private int spanCount;
    private StarsAdapter mAdapter;

    public TeamPkImprovePager(Context context, TeamPkBll teamPkBll) {
        super(context);
        mPkBll = teamPkBll;
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_stars, null);
        bgMask = view.findViewById(R.id.iv_teampk_bg_mask);
        animationView = view.findViewById(R.id.lav_teampk_starts);
        recyclerView = view.findViewById(R.id.rcl_teampk_starts_list);
        ivClostBtn = view.findViewById(R.id.iv_teampk_open_btn_close);
        ivClostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        initRecycleView();

        return view;
    }

    private void initRecycleView() {
        spanCount = 2;
        recyclerView.setLayoutManager(new TeamMemberGridlayoutManager(mContext, 2,
                LinearLayoutManager.VERTICAL, false));
        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        recyclerView.setLayoutAnimation(animationController);
        mAdapter = new StarsAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.scheduleLayoutAnimation();
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition >= spanCount) {
                    top = SizeUtils.Dp2Px(mContext, 5);
                }
                outRect.set(left, top, right, bottom);
            }
        });
    }


    static class StarItemHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        ImageView ivStarIcon;
        TextView  tvName;
        TextView  tvEnergy;
        TextView  tvTeamName;

        public StarItemHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_stars_head);
            ivStarIcon = itemView.findViewById(R.id.iv_teampk_stars_super_star);
            tvName = itemView.findViewById(R.id.tv_teampk_stars_name);
            tvEnergy = itemView.findViewById(R.id.tv_teampk_stars_energy);
            tvTeamName = itemView.findViewById(R.id.tv_teampk_stars_teamname);
        }
    }


    static class StarsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StarItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_teampk_improve, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }


    @Override
    public void initData() {

    }

    public void close() {
        try {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    releaseSoundRes();
                    mPkBll.closeCurrentPager();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseSoundRes() {
        // TODO: 2019/1/14  释放音乐资源

    }

}
