package com.xueersi.parentsmeeting.modules.livevideo.teampk.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamPkStuProgress;
import com.xueersi.parentsmeeting.modules.livevideo.stablelog.TeamPkLog;
import com.xueersi.parentsmeeting.modules.livevideo.teampk.business.TeamPkBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveCutImage;
import com.xueersi.parentsmeeting.modules.livevideo.util.SoundPoolHelper;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamMemberGridlayoutManager;
import com.xueersi.parentsmeeting.modules.livevideo.widget.TeamPkPraiseLayout;

import java.util.List;

/**
 * 战队pk 二期 进步榜
 *
 * @author chekun
 * created  at 2019/1/14 9:57
 */
public class TeamPkImprovePager extends TeamPkBasePager {

    private final TeamPkBll mPkBll;
    private View bgMask;
    private LottieAnimationView animationView;
    private RecyclerView recyclerView;
    private ImageView ivClostBtn;
    private int spanCount;
    private RankAdapter mAdapter;
    private final List<TeamPkStuProgress> mData;
    private static final String LOTTIE_RES_ASSETS_ROOTDIR = "team_pk/student_improver/";
    private final float ANIM_DISPATCH_FRACTION = 0.10f;
    private TeamPkPraiseLayout teamPkPraiseLayout;

    public TeamPkImprovePager(Context context, List<TeamPkStuProgress> data, TeamPkBll teamPkBll) {
        super(context);
        mPkBll = teamPkBll;
        mData = data;
    }


    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_improve, null);
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
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mView.getMeasuredWidth() > 0) {
                    showAnim();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        teamPkPraiseLayout = view.findViewById(R.id.pk_praise_layout);
        teamPkPraiseLayout.setPriaseStateListener(new TeamPkPraiseLayout.PraiseStateListener() {
            @Override
            public void onFinish(int clickCount) {
                TeamPkLog.sendPkStarThumbCount(mPkBll.getLiveAndBackDebug(),"1",mPkBll.getNonce(),clickCount);

            }
        });
        return view;
    }


    private void showAnim() {
        bgMask.setVisibility(View.VISIBLE);
        playMusic(R.raw.war_bg, DEFAULT_BG_VOLUME, true);
        animationView.useHardwareAcceleration(true);
        final String lottieResPath = LOTTIE_RES_ASSETS_ROOTDIR + "images";
        String lottieJsonPath = LOTTIE_RES_ASSETS_ROOTDIR + "data.json";

        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        animationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(mContext));
        animationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(animationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        mContext);
            }
        });
        animationView.playAnimation();
        animationView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            boolean animDispatched = false;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() > ANIM_DISPATCH_FRACTION && !animDispatched) {
                    animDispatched = true;
                    showStuProgressList();
                    ivClostBtn.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void showStuProgressList() {
        spanCount = 2;
        recyclerView.setLayoutManager(new TeamMemberGridlayoutManager(mContext, 2,
                LinearLayoutManager.VERTICAL, false));
        GridLayoutAnimationController animationController = (GridLayoutAnimationController)
                AnimationUtils.loadLayoutAnimation(mContext, R.anim.anim_livevido_teampk_teammember_list);
        recyclerView.setLayoutAnimation(animationController);
        mAdapter = new RankAdapter(mData);
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
                    top = SizeUtils.Dp2Px(mContext, 22);
                }
                outRect.set(left, top, right, bottom);
            }
        });

        teamPkPraiseLayout.setOnLineTeammates(mPkBll.getOnlineTeamMates());
        teamPkPraiseLayout.setWrodList(mPkBll.getPraiseText());
    }



    static class StarItemHolder extends RecyclerView.ViewHolder {
        ImageView ivHead;
        ImageView ivStarIcon;
        TextView tvName;
        TextView tvProgressScope;
        TextView tvTeamName;

        public StarItemHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_teampk_stars_head);
            ivStarIcon = itemView.findViewById(R.id.iv_teampk_stars_super_star);
            tvName = itemView.findViewById(R.id.tv_teampk_stars_name);
            tvProgressScope = itemView.findViewById(R.id.tv_teampk_stars_energy);
            tvTeamName = itemView.findViewById(R.id.tv_teampk_stars_teamname);
        }

        public void bindData(TeamPkStuProgress data, int postion) {
            ivStarIcon.setVisibility(data.isSuper() ? View.VISIBLE : View.INVISIBLE);
            tvName.setText(data.getName());
            tvTeamName.setText(data.getTeamName());
            tvProgressScope.setText("排名+" + data.getProgressScope());
            ImageLoader.with(ContextManager.getContext()).load(data.getAvatarPath())
                    .asBitmap(new SingleConfig.BitmapListener() {
                        @Override
                        public void onSuccess(Drawable drawable) {
                            Bitmap resultBitmap = null;
                            if (drawable instanceof GifDrawable) {
                                resultBitmap = ((GifDrawable) drawable).getFirstFrame();
                            }
                            else {
                                resultBitmap = DrawableHelper.drawable2bitmap(drawable);
                            }
                            if (resultBitmap != null) {
                                Bitmap circleBitmap = LiveCutImage.scaleBitmap(resultBitmap, Math.min(resultBitmap.getWidth(),
                                        resultBitmap.getHeight()) / 2);
                                ivHead.setImageBitmap(circleBitmap);
                            }
                        }

                        @Override
                        public void onFail() {
                        }
                    });
        }

    }


    static class RankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<TeamPkStuProgress> mData;

        RankAdapter(List<TeamPkStuProgress> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StarItemHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.item_teampk_improve, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((StarItemHolder)holder).bindData(mData.get(position),position);
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }


    @Override
    public void initData() {

    }

    @Override
    public void onStop() {
        super.onStop();
        pauseMusic();
    }

    int[] soundResArray = {
            R.raw.war_bg
    };
    private SoundPoolHelper soundPoolHelper;

    /**
     * 暂停音效
     * 注 此处的暂停  只是将音量设置为0  （因为 动画和音效是 同步的）
     */
    private void pauseMusic() {
        if (soundPoolHelper != null) {
            for (int i = 0; i < soundResArray.length; i++) {
                soundPoolHelper.setVolume(soundResArray[i], 0);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeMusic();
    }

    /**
     * 默认背景音效大小
     */
    private static final float DEFAULT_BG_VOLUME = 0.4f;
    /**
     * 默认前景音效大小
     */
    private static final float DEFAULT_FRONT_VOLUME = 0.6f;

    /**
     * 恢复音乐播放
     * 注释  将音量恢复为暂停之前的状态
     */
    private void resumeMusic() {
        if (soundPoolHelper != null) {
            for (int i = 0; i < soundResArray.length; i++) {
                if (soundResArray[i] == R.raw.war_bg) {
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_BG_VOLUME);
                } else {
                    soundPoolHelper.setVolume(soundResArray[i], DEFAULT_FRONT_VOLUME);
                }
            }
        }
    }

    /**
     * @param resId
     * @param volume
     * @param loop
     */
    private void playMusic(int resId, final float volume, final boolean loop) {
        if (soundPoolHelper == null) {
            soundPoolHelper = new SoundPoolHelper(mContext, 2, AudioManager.STREAM_MUSIC);
        }
        soundPoolHelper.playMusic(resId, volume, loop);
    }

    private void releaseRes() {
        if (soundPoolHelper != null) {
            soundPoolHelper.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseRes();
    }

    public void close() {
        try {
            mView.post(new Runnable() {
                @Override
                public void run() {
                    releaseRes();
                    mPkBll.closeCurrentPager();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
