package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.string.RegexUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveBackMsgEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveMessageEntity;
import com.xueersi.parentsmeeting.modules.livevideo.message.business.LiveMessageEmojiParser;
import com.xueersi.parentsmeeting.modules.livevideo.message.config.LiveMessageConfig;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveMainHandler;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CenterAlignImageSpan;
import com.xueersi.parentsmeeting.modules.livevideo.widget.HalfBodyLiveMsgRecycelView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.message.pager
 * @ClassName: LightLiveBackMsgLandPager
 * @Description: 轻直播回放横屏页面
 * @Author: WangDe
 * @CreateDate: 2019/12/26 10:49
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/12/26 10:49
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveBackMsgLandPager extends BasePager implements IBackMsgpager {

    private HalfBodyLiveMsgRecycelView msgListView;
    /**
     * 消息集合
     **/
    ArrayList<LiveBackMsgEntity> liveMessageEntities;
    ArrayList<LiveBackMsgEntity> teacherMessageEntities;
    private boolean isTouch = false;
    /**
     * 聊天字体大小，最多13个汉字
     */
    private int messageSize = 0;
    private int[] nameColors;

    LiveMsgAdapter mMsgAdapter;
    LiveMsgAdapter mTeacherMsgAdapter;
    private Drawable dwSysIcon;
    private Drawable dwTeacherIcon;

    public LightLiveBackMsgLandPager(Context context) {
        super(context);
        liveMessageEntities = new ArrayList<>();
        teacherMessageEntities = new ArrayList<>();
        initData();
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pager_liveback_message_halfbody_lightlive, null);
        msgListView = mView.findViewById(R.id.rcl_live_halfbody_msg);
        msgListView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, true));
        return mView;
    }

    @Override
    public void initData() {
        initMsgRcyclView();
    }

    /**
     * 初始化 联通信息
     */
    private void initMsgRcyclView() {
        int screenWidth = ScreenUtils.getScreenWidth();
        int wradio = (int) (LiveVideoConfig.VIDEO_HEAD_WIDTH * screenWidth / LiveVideoConfig.VIDEO_WIDTH);
        int minisize = wradio / 13;
        messageSize = Math.max((int) (ScreenUtils.getScreenDensity() * 12), minisize);

        mMsgAdapter = new LiveMsgAdapter(liveMessageEntities);
        msgListView.setAdapter(mMsgAdapter);
        mTeacherMsgAdapter = new LiveMsgAdapter(teacherMessageEntities);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) msgListView.getLayoutParams();
        Point point = new Point();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getSize(point);
        int screenHeight = Math.min(point.x, point.y);
        int height = (int) (screenHeight * 0.573);
        int width = (int) (screenWidth * 0.45f);
        params.height = height;
        params.width = width;
        params.bottomMargin = (int) (screenHeight * 0.14f);
        msgListView.setLayoutParams(params);

        msgListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int itemPosition = parent.getChildAdapterPosition(view);
                int left = 0;
                int right = 0;
                int top = 0;
                int bottom = 0;
                if (itemPosition < liveMessageEntities.size()) {
                    top = SizeUtils.Dp2Px(mContext, 9);
                }
                outRect.set(left, top, right, bottom);
            }
        });

        //监听 item淡出动画  动画结束后 清空数据源
        msgListView.setItemFadeAnimListener(new HalfBodyLiveMsgRecycelView.ItemFadeAnimListener() {
            @Override
            public void onAllItemFadeOut() {
                liveMessageEntities.clear();
                mMsgAdapter.notifyDataSetChanged();
            }
        });

        dwSysIcon = mView.getResources().getDrawable(R.drawable.icon_live_sys_msg);
        dwTeacherIcon = mView.getResources().getDrawable(R.drawable.icon_live_teacher_msg);
    }

    @Override
    public void addMsg(LiveBackMsgEntity entity) {
        SpannableStringBuilder sBuilder = LiveMessageEmojiParser.convertToHtml(RegexUtils
                .chatSendContentDeal(entity.getText().toString()), mContext, SizeUtils.Dp2Px(mContext,12));
        entity.setText(sBuilder);
        if (liveMessageEntities.size() > 29) {
            liveMessageEntities.remove(0);
        }
        liveMessageEntities.add(entity);
        if (LiveBackMsgEntity.MESSAGE_TEACHER == entity.getFrom() || LiveBackMsgEntity.MESSAGE_MINE == entity.getFrom()){
            teacherMessageEntities.add(entity);
        }
        if (mMsgAdapter != null) {
            LiveMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mMsgAdapter.notifyDataSetChanged();
                    mTeacherMsgAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public void removeAllMsg() {
        liveMessageEntities.clear();
        teacherMessageEntities.clear();
        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mMsgAdapter.notifyDataSetChanged();
                mTeacherMsgAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void removeOverMsg(long pos) {
        Iterator<LiveBackMsgEntity> iterator = liveMessageEntities.iterator();
        while (iterator.hasNext()){
            LiveBackMsgEntity entity = iterator.next();
            if(entity.getId() > pos){
                iterator.remove();
            }
        }
        Iterator<LiveBackMsgEntity> itTeacher = teacherMessageEntities.iterator();
        while (itTeacher.hasNext()) {
            LiveBackMsgEntity entity = itTeacher.next();
            if (entity.getId() > pos) {
                itTeacher.remove();
            }
        }
        LiveMainHandler.post(new Runnable() {
            @Override
            public void run() {
                mMsgAdapter.notifyDataSetChanged();
                mTeacherMsgAdapter.notifyDataSetChanged();
            }
        });
    }


    private class MsgItemHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        /**
         * 展示带图片的消息
         */
        private TextView tvSysMsg;

        public MsgItemHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tv_live_halfbody_msg);
            tvSysMsg = itemView.findViewById(R.id.tv_live_halfbody_sys_msg);
        }

        public void bindData(LiveBackMsgEntity data) {
            Drawable drawable = null;
            if (data.getFrom() == LiveMessageEntity.MESSAGE_MINE) {
                tvMsg.setTextColor(Color.parseColor("#FFDB5C"));
                tvSysMsg.setTextColor(Color.parseColor("#FFDB5C"));
            } else {
                tvMsg.setTextColor(Color.parseColor("#ffffff"));
                tvSysMsg.setTextColor(Color.parseColor("#ffffff"));
            }
            if (LiveMessageEntity.MESSAGE_TIP == data.getFrom()) {
                drawable = dwSysIcon;
            } else if (LiveMessageEntity.MESSAGE_TEACHER == data.getFrom() && data.getSender().startsWith(LiveMessageConfig.TEACHER_PREFIX)) {
                drawable = dwTeacherIcon;
            }
            if (drawable != null) {
                tvMsg.setVisibility(View.INVISIBLE);
                SpannableStringBuilder ssb = new SpannableStringBuilder("# ");
                drawable.setBounds(0, 0, SizeUtils.Dp2Px(tvMsg.getContext(), 40), SizeUtils.Dp2Px(tvMsg.getContext(),
                        18));
                CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable);
                ssb.setSpan(imageSpan, 0, 1, ImageSpan.ALIGN_BASELINE);
                tvSysMsg.setVisibility(View.VISIBLE);
                tvSysMsg.setAutoLinkMask(0);
                tvSysMsg.setText(ssb);
                tvSysMsg.append(data.getText());

            } else {
                tvSysMsg.setVisibility(View.INVISIBLE);
                tvMsg.setVisibility(View.VISIBLE);
                tvMsg.setText(data.getSender() + "：");
                tvMsg.append(data.getText());
            }
        }
    }


    private class LiveMsgAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<LiveBackMsgEntity> mData;

        public LiveMsgAdapter(List<LiveBackMsgEntity> data) {
            mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MsgItemHolder(View.inflate(parent.getContext(), R.layout.item_livevideo_halfbody_msg, null));
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

   public void justShowTeacher(boolean isShow){
        if (isShow){
            msgListView.setAdapter(mTeacherMsgAdapter);
        }else {
            msgListView.setAdapter(mMsgAdapter);
        }
   }
}
