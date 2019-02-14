package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.xueersi.common.business.AppBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.message.pager.HalfBodyLiveMessagePager;

import java.util.ArrayList;
import java.util.List;

/**
 * 战队pk 二期 点赞UI
 *
 * @author chekun
 * created  at 2019/1/30 15:57
 */
public class TeamPkPraiseLayout extends FrameLayout {

    private RecyclerView recyclerView;
    private ImageView ivPraise;
    private LottieAnimationView loopAnimationView;
    /**
     * lottie 资源根路径
     **/
    private static final String ANIM_RES_DIR = "team_pk/praise/";
    private LottieAnimationView clickAnimView;

    /**
     * 随机文案列表
     **/
    private List<String> mWrodList;

    /**
     * 随机名单列表
     **/
    private List<String> mNameList;
    /**
     * 点击时间间隔
     **/
    private static final long CLIK_AVAILABLE_TIME = 300;
    private long lastClickTime;
    /**
     * recycleView 数据源
     **/
    private List<Msg> mMsgList;
    /**
     * 消息缓存队列
     **/
    private List<Msg> mCacheMsgList;

    private int nameIndex;
    private int wrodsIndex;
    private LiveMsgAdapter mMsgAdapter;
    /**消息轮询间隔**/
    private static final long MSG_LOOP_DURATION = 1 *1000;

    public TeamPkPraiseLayout(@NonNull Context context) {
        this(context, null);
    }

    public TeamPkPraiseLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TeamPkPraiseLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.teampk_praise_layout, this);
        recyclerView = findViewById(R.id.rcl_teampk_praise);
        ivPraise = findViewById(R.id.iv_teampk_praise);
        loopAnimationView = findViewById(R.id.lav_teampk_praise);
        clickAnimView = findViewById(R.id.lav_teampk_praise_click);
        ivPraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((System.currentTimeMillis() - lastClickTime) > CLIK_AVAILABLE_TIME) {
                    playClickAnim();
                    generateMsg();
                    lastClickTime = System.currentTimeMillis();
                }
            }
        });

        test();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (getMeasuredWidth() > 0) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            playLoopAnim();
                        }
                    },1500);
                    startMsgLoop();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));
        mMsgAdapter = new LiveMsgAdapter(mMsgList);
        recyclerView.setAdapter(mMsgAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition < mMsgList.size()) {
                    top = SizeUtils.Dp2Px(getContext(), 9);
                }
                outRect.set(left, top, right, bottom);
            }
        });
    }



    private void generateMsg() {
        String name = UserBll.getInstance().getMyUserInfoEntity().getNickName();
        String wrods = mWrodList.get(wrodsIndex % mWrodList.size());
        Msg msg = new Msg(name, wrods, true);
        nameIndex++;
        wrodsIndex++;
        Msg msg2 = new Msg(mNameList.get(nameIndex % mNameList.size()), mWrodList.get(wrodsIndex % mWrodList.size()),
                false);
        wrodsIndex++;
        mCacheMsgList.add(msg);
        mCacheMsgList.add(msg2);
    }

    private void startMsgLoop(){
        this.post(runnable);
    }

    private void cancleMsgLoop(){
      this.removeCallbacks(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancleMsgLoop();
        if(mNameList != null){
            mNameList.clear();
        }
        if(mWrodList != null){
            mWrodList.clear();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mCacheMsgList != null && mCacheMsgList.size() > 0) {
                mMsgList.add(mCacheMsgList.remove(0));
                mMsgAdapter.notifyItemInserted(0);
            }
            postDelayed(this,MSG_LOOP_DURATION);
        }
    };

    public void setWrodList(List<String> wrodList) {
        this.mNameList = wrodList;
    }

    public void setNameList(List<String> nameList) {
        this.mNameList = nameList;
    }

    /**
     * 消息实体类
     **/
    private static class Msg {

        private String name;
        private String wrods;
        /**
         * 是否是自己发送的消息
         **/
        private boolean isMe;

        public Msg(String name, String words, boolean isMe) {
            this.name = name;
            this.wrods = words;
            this.isMe = isMe;
        }

        public String getName() {
            return name;
        }

        public String getWrods() {
            return wrods;
        }

        public void setIsMe(boolean isMe) {
            this.isMe = isMe;
        }

        public boolean isMe() {
            return isMe;
        }
    }

    private void test() {

        mWrodList = new ArrayList<String>();
        mWrodList.add("新");
        mWrodList.add("年");
        mWrodList.add("快");
        mWrodList.add("乐");
        mWrodList.add("!");

        mNameList = new ArrayList<String>();
        mNameList.add("A");
        mNameList.add("B");
        mNameList.add("C");
        mNameList.add("D");

        mMsgList = new ArrayList<Msg>();
        mCacheMsgList = new ArrayList<Msg>();
    }

    private void playClickAnim() {
        loopAnimationView.setVisibility(GONE);
        loopAnimationView.cancelAnimation();
        loopAnimationView.destroyDrawingCache();
        Log.e("TeamPkPraiseLayout", "======> playClickAnim:" + clickAnimView.getComposition());
        if (clickAnimView.getComposition() == null) {
            String lottieResPath = ANIM_RES_DIR + "click/images";
            String lottieJsonPath = ANIM_RES_DIR + "click/data.json";
            final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
            clickAnimView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(getContext()));
            clickAnimView.useHardwareAcceleration(true);
            clickAnimView.setRepeatCount(0);
            clickAnimView.setImageAssetDelegate(new ImageAssetDelegate() {
                @Override
                public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                    return effectInfo.fetchBitmapFromAssets(clickAnimView, lottieImageAsset.getFileName(),
                            lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                            getContext());
                }
            });
        }
        if (!clickAnimView.isAnimating()) {
            clickAnimView.playAnimation();
        }
    }

    private void playLoopAnim() {
        String lottieResPath = ANIM_RES_DIR + "loop/images";
        String lottieJsonPath = ANIM_RES_DIR + "loop/data.json";
        final LottieEffectInfo effectInfo = new LottieEffectInfo(lottieResPath, lottieJsonPath);
        loopAnimationView.setAnimationFromJson(effectInfo.getJsonStrFromAssets(getContext()));
        loopAnimationView.useHardwareAcceleration(true);
        loopAnimationView.setRepeatCount(-1);
        loopAnimationView.setImageAssetDelegate(new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return effectInfo.fetchBitmapFromAssets(loopAnimationView, lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(), lottieImageAsset.getWidth(), lottieImageAsset.getHeight(),
                        getContext());
            }
        });
        loopAnimationView.playAnimation();
    }


    private class MsgItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        public MsgItemHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_live_halfbody_msg);
        }
        public void bindData(Msg data) {
            if (data.isMe()) {
                tvMsg.setTextColor(Color.parseColor("#FFDB5C"));
            } else {
                tvMsg.setTextColor(Color.parseColor("#ffffff"));
            }
            tvMsg.setText(data.getName() + "：");
            tvMsg.append(data.getWrods());
        }
    }


    private class LiveMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Msg> mData;

        public LiveMsgAdapter(List<Msg> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MsgItemHolder(View.inflate(parent.getContext(), R.layout
                    .item_livevideo_teampk_praise_msg, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int dataIndex = (mData.size() - 1) - position;
            ((MsgItemHolder) holder).bindData(mData.get(dataIndex));
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size();
        }
    }


}
