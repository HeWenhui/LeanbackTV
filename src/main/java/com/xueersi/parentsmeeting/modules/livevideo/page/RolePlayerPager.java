package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.tal.speech.speechrecognizer.SpeechEvaluatorInter;
import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerOtherItem;
import com.xueersi.parentsmeeting.modules.livevideo.activity.item.RolePlayerSelfItem;
import com.xueersi.parentsmeeting.modules.livevideo.entity.RolePlayerEntity;
import com.xueersi.parentsmeeting.speech.SpeechEvaluatorUtils;
import com.xueersi.xesalib.adapter.AdapterItemInterface;
import com.xueersi.xesalib.adapter.CommonAdapter;
import com.xueersi.xesalib.utils.app.ContextManager;
import com.xueersi.xesalib.utils.app.XESToastUtils;
import com.xueersi.xesalib.utils.uikit.imageloader.ImageLoader;
import com.xueersi.xesalib.view.image.CircleImageView;

import java.io.File;
import java.util.List;


/**
 * RolePlayer
 * Created by zouhao on 2018/3/29.
 */

public class RolePlayerPager extends BasePager<RolePlayerEntity> {

    /** 匹配页默认停留时间 */
    private final int MATCH_WAIT_SECOND = 1000;
    /** 角色确认页停留时间 */
    private final int WAIT_ROLE_HEAD_SHOW = 2000;

    /** 匹配页 */
    private RelativeLayout rlMatchPager;
    /** 准备开始朗读前的提示文案 */
    private TextView tvBeginTipMsg;
    /** 倒计时器 */
    private TextView tvCountTime;
    /** 角色展示列表 */
    private GridView gvRoleHeadShow;
    /** 角色展示区 */
    private RelativeLayout rlRoleHeadMain;
    /** 朗读区 */
    private RelativeLayout rlRoleReadMain;

    /** 角色展示区适配器 */
    private RolePlayerHeadShowAdapter mHeadShowAdapter;

    /** 对话区 */
    private ListView lvReadList;

    /** 对话数据适配器 */
    private CommonAdapter<RolePlayerEntity.RolePlayerMessage> mRolePlayerAdapter;

    /** 当前正在朗读的索引 */
    private int mCurrentReadIndex;

    /** 语音评测 */
    protected SpeechEvaluatorUtils mIse;
    private SpeechEvaluatorInter speechEvaluatorInter;
    private File saveVideoFile, dir;

    public RolePlayerPager(Context context, RolePlayerEntity obj, boolean isNewView) {
        super(context, obj, isNewView);
        dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/liveSpeech/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.pager_roleplayer, null);
        rlMatchPager = view.findViewById(R.id.rl_live_roleplayer_matchpager);
        rlMatchPager.setVisibility(View.VISIBLE);
        tvCountTime = view.findViewById(R.id.tv_live_roleplayer_countdown);
        gvRoleHeadShow = view.findViewById(R.id.gv_live_roleplayer_headshow);
        rlRoleHeadMain = view.findViewById(R.id.rl_live_roleplayer_rolelist_main);
        rlRoleReadMain = view.findViewById(R.id.rl_live_roleplayer_read_main);
        tvBeginTipMsg = view.findViewById(R.id.tv_live_roleplayer_countdown_tip);
        lvReadList = view.findViewById(R.id.lv_live_roleplayer_read_list);
        return view;
    }

    @Override
    public void initData() {
        //默认MATCH_WAIT_SECOND 后，匹配页消失
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEntity.getLstRoleInfo().size() > 0 && mEntity.getLstRolePlayerMessage().size() > 0) {
                    rlMatchPager.setVisibility(View.GONE);
                    roleConfirmPage(); //确定角色开始RolePlayer
                } else {
                    XESToastUtils.showToast(mContext, "无朗读数据");
                }
            }
        }, MATCH_WAIT_SECOND);

    }


    /**
     * 确定角色准备开始RolePlayer
     */
    private void roleConfirmPage() {
        tvCountTime.setText(getCountDownTime());
        mHeadShowAdapter = new RolePlayerHeadShowAdapter(mContext, mEntity.getLstRoleInfo());
        gvRoleHeadShow.setAdapter(mHeadShowAdapter);
        tvBeginTipMsg.postDelayed(new Runnable() {
            @Override
            public void run() {
                waitRolePlayer();
            }
        }, WAIT_ROLE_HEAD_SHOW);

    }

    /**
     * 准备好朗读数据显示，3秒倒计时准备RolePlayer
     */
    private void waitRolePlayer() {
        rlRoleHeadMain.setVisibility(View.GONE);
        rlRoleReadMain.setVisibility(View.VISIBLE);
        if (mEntity.getLstRolePlayerMessage().get(0).getRolePlayer().isSelfRole()) {
            tvBeginTipMsg.setText("You go first. Are you ready?");
            tvBeginTipMsg.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvBeginTipMsg.setText("go");
                }
            }, 2000);
        } else {
            tvBeginTipMsg.setText("Don't hurry. Not your turn yet.");
        }
        //开始倒计时，1秒更新一次
        tvCountTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mEntity.getCountDownSecond() == 0) {
                    //倒计时结束，进入结束环节
                    endRolePlayer();
                    return;
                }
                mEntity.setCountDownSecond(mEntity.getCountDownSecond() - 1);
                tvCountTime.setText(getCountDownTime());
                tvCountTime.postDelayed(this, 1000);
            }
        }, 1000);

        //填充对话内容
        mRolePlayerAdapter = new CommonAdapter<RolePlayerEntity.RolePlayerMessage>(mEntity.getLstRolePlayerMessage(), 2) {
            @Override
            public AdapterItemInterface<RolePlayerEntity.RolePlayerMessage> getItemView(Object type) {
                if ((boolean) type) {
                    //自己朗读的
                    return new RolePlayerSelfItem(mContext);
                } else {
                    //他人朗读的
                    return new RolePlayerOtherItem(mContext);
                }
            }

            @Override
            public Object getItemViewType(RolePlayerEntity.RolePlayerMessage t) {
                return t.getRolePlayer().isSelfRole();
            }
        };
        lvReadList.setAdapter(mRolePlayerAdapter);


        //整个前奏3秒后开始
        tvBeginTipMsg.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvBeginTipMsg.setVisibility(View.GONE);
                beginRolePlayer();
            }
        }, 3000);


    }

    private final static int READ_MESSAGE = 100;

    /** 用来自动朗读 */
    Handler mReadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == READ_MESSAGE) {
                //恢复上一条的状态
                if (mCurrentReadIndex > 0) {
                    RolePlayerEntity.RolePlayerMessage upMessage = mEntity.getLstRolePlayerMessage().get(mCurrentReadIndex - 1);
                    upMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.END_ROLEPLAY);
                    mRolePlayerAdapter.updataSingleRow(lvReadList, upMessage);
                    mIse.stop();
                }

                if (mCurrentReadIndex == (mEntity.getLstRolePlayerMessage().size())) {
                    //已经对话完毕
                    endRolePlayer();
                    return;
                }

                //取出当前这条的延时时间
                RolePlayerEntity.RolePlayerMessage currentMessage = mEntity.getLstRolePlayerMessage().get(mCurrentReadIndex);
                currentMessage.setMsgStatus(RolePlayerEntity.RolePlayerMessageStatus.BEGIN_ROLEPLAY);
                mRolePlayerAdapter.updataSingleRow(lvReadList, currentMessage);
                speechReadMessage(currentMessage);
                lvReadList.smoothScrollToPosition(mCurrentReadIndex);
                mCurrentReadIndex++;
                Message temp = mReadHandler.obtainMessage();
                temp.what = READ_MESSAGE;
                mReadHandler.sendMessageDelayed(temp, currentMessage.getMaxReadTime() * 1000);
            }
        }
    };

    /**
     * 开始进入RolePlayer对话
     */
    private void beginRolePlayer() {
        mReadHandler.sendEmptyMessage(READ_MESSAGE);
    }

    /**
     * 结束RolePlayer
     */
    private void endRolePlayer() {
        XESToastUtils.showToast(mContext, "结束");
    }


    /**
     * 进入自己朗读评测
     *
     * @param message
     */
    private void speechReadMessage(RolePlayerEntity.RolePlayerMessage message) {
        String spechMsg = message.getReadMsg().replace("\n", "");
        if (mIse == null) {
            mIse = new SpeechEvaluatorUtils(true);
            saveVideoFile = new File(dir, "roleplayer.mp3");
        }
        mIse.cancel();
        speechEvaluatorInter = mIse.startEnglishEvaluatorOffline(spechMsg, saveVideoFile.getAbsolutePath(), false,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            XESToastUtils.showToast(mContext, resultEntity.getScore() + "");
                            //提前开始下一条
                            nextReadMessage();
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            XESToastUtils.showToast(mContext, "失败");
                            //提前开始下一条
                            nextReadMessage();
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                        }

                    }

                    @Override
                    public void onVolumeUpdate(int i) {
                    }
                });
    }

    /**
     * 提前开始下一条
     */
    private void nextReadMessage() {
        mReadHandler.removeMessages(READ_MESSAGE);
        mReadHandler.sendEmptyMessage(READ_MESSAGE);
    }

    /**
     * 角色扮演列表适配器
     */
    private class RolePlayerHeadShowAdapter extends BaseAdapter {

        List<RolePlayerEntity.RolePlayerHead> lstRolePlayerHead;

        public RolePlayerHeadShowAdapter(Context context, List<RolePlayerEntity.RolePlayerHead>
                lstHead) {
            this.lstRolePlayerHead = lstHead;
        }

        @Override
        public int getCount() {
            return lstRolePlayerHead.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = View.inflate(mContext, R.layout.item_live_roleplayer_rolehead, null);
                holder.tvNickName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolehead_nickname);
                holder.civHeadImg = convertView.findViewById(R.id.civ_roleplayer_item_rolehead_img);
                holder.tvRoleName = convertView.findViewById(R.id.tv_live_roleplayer_item_rolename);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            setData(lstRolePlayerHead.get(position), holder);
            return convertView;
        }

        private void setData(RolePlayerEntity.RolePlayerHead entity, Holder holder) {
            holder.tvNickName.setText(entity.getNickName());
            holder.tvRoleName.setText(entity.getRoleName());
            ImageLoader.with(ContextManager.getApplication()).load(entity.getHeadImg()).into(holder.civHeadImg);
            if (entity.isSelfRole()) {
                holder.tvNickName.setTextColor(Color.RED);
                holder.civHeadImg.setBorderColor(Color.RED);
                holder.civHeadImg.setBorderWidth(7);
                holder.tvRoleName.setTextColor(Color.RED);
//
//                holder.civHeadImg.setFinishBorderColor(Color.GRAY);
//                holder.civHeadImg.setUnFinishBorderColor(Color.RED);
//                holder.civHeadImg.startCountDown(10);
            } else {
                holder.civHeadImg.setBorderColor(Color.WHITE);
            }
        }


        class Holder {
            /** 昵称 */
            private TextView tvNickName;
            /** 头像 */
            private CircleImageView civHeadImg;
            /** 角色名 */
            private TextView tvRoleName;
        }
    }

    /**
     * 返回当前的倒计时
     */
    private String getCountDownTime() {
        long min = mEntity.getCountDownSecond() / 60;
        long sec = mEntity.getCountDownSecond() % 60;
        long hour = min / 60;
        min %= 60;
        if (hour == 0) {
            return "时间: " + min + "分" + sec + "秒";
        } else {
            return "时间:" + hour + "时" + min + "分" + sec + "秒";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mReadHandler.removeMessages(READ_MESSAGE);
    }
}
