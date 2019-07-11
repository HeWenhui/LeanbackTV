package com.xueersi.parentsmeeting.modules.livevideo.praiselist.presenter;

import android.app.Activity;
import android.view.View;

import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;
import com.xueersi.parentsmeeting.modules.livevideo.core.TopicAction;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListView;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.MinimarketListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.PraiseListDanmakuEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseListPager;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.view.PraiseListBll;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/7/27.
 */

public class PraiseListIRCBll extends LiveBaseBll implements NoticeAction, TopicAction, PraiseListPresenter {
    /**
     * 表扬榜View层接口
     */
    private PraiseListView mPraiseListView;
    private LikeProbabilityEntity mLikeProbabilityEntity;

    public PraiseListIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mPraiseListView = new PraiseListBll(activity);
        mPraiseListView.setPresenter(PraiseListIRCBll.this);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        //模式切换为主讲，关闭表扬榜
        if (mPraiseListView != null && mode.equals(LiveTopic.MODE_CLASS))
            mPraiseListView.closePraiseList();
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mPraiseListView.initView(getLiveViewAction());
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            //开启和发布榜单
            case XESCODE.XCR_ROOM_PRAISELIST_OPEN: {
                String open = data.optString("open");
                int zanType = data.optInt("zanType");
                String nonce = data.optString("nonce");
                if ("on".equals(open)) {
                    mPraiseListView.onReceivePraiseList(zanType, nonce);
                    switch (zanType) {
                        case PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT:
                            getExcellentList();
                            break;
                        case PraiseListPager.PRAISE_LIST_TYPE_MINI_MARKET:
                            getMiniMarketList();
                            break;
                        case PraiseListPager.PRAISE_LIST_TYPE_LIKE:
                            getLikeList();
                            break;
                        default:
                            break;
                    }
                    getLikeProbability();
                } else if ("off".equals(open)) {
                    mPraiseListView.closePraiseList();
                }

            }
            //老师广播赞数，包含一键表扬 和 某某学生点了多少赞
            case XESCODE.XCR_ROOM_PRAISELIST_LIKE_STUTENT: {
                mLogtf.d("onNotice: like from student, data = " + data);
                int isTeacher = data.optInt("isTeacher");
                if (isTeacher == 1) {
                    // 只有 (isTeacher == 1) 时才有这个字段
                    String teacherName = data.optString("teacherName");
                    if (teacherName != null) {
                        mPraiseListView.showPraiseScroll(mGetInfo.getStuName(), teacherName);
                    }
                } else {
                    ArrayList<PraiseListDanmakuEntity> danmakuList = new ArrayList<>();
                    //学生名字列表，(isTeacher == 0) 时再解析这个字段
                    JSONArray agreeForms = data.optJSONArray("agreeFrom");
                    //学生点赞个数列表，(isTeacher == 0) 时再解析这个字段
                    JSONArray nums = data.optJSONArray("nums");
                    if (agreeForms != null && nums != null) {
                        int minLength = Math.min(agreeForms.length(), nums.length());
                        for (int i = 0; i < minLength; i++) {
                            try {
                                PraiseListDanmakuEntity danmakuEntity = new PraiseListDanmakuEntity();
                                danmakuEntity.setBarrageType(1);
                                danmakuEntity.setName(agreeForms.getString(i));
                                danmakuEntity.setNumber(nums.getInt(i));
                                danmakuList.add(danmakuEntity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        mLogtf.d("Parse data error: agreeFroms or nums is null, data = " + data);
                    }

                    if (danmakuList.size() != 0) {
                        mPraiseListView.receiveLikeNotice(danmakuList);
                    }

                }
            }
            //老师广播赞数，告诉学生 当前各个战队有多少赞
            case XESCODE.XCR_ROOM_PRAISELIST_LIKE_TEAM: {
                mLogtf.d("onNotice: like from team, data = " + data);
                ArrayList<PraiseListDanmakuEntity> danmakuList = new ArrayList<>();
                JSONArray teamList = data.optJSONArray("teamList");
                if (teamList != null) {
                    for (int i = 0; i < teamList.length(); i++) {
                        try {
                            PraiseListDanmakuEntity danmakuEntity = new PraiseListDanmakuEntity();
                            JSONObject team = teamList.getJSONObject(i);
                            danmakuEntity.setBarrageType(2);
                            danmakuEntity.setName(team.getString("teamName"));
                            danmakuEntity.setNumber(team.getInt("teamNum"));
                            danmakuList.add(danmakuEntity);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    mLogtf.d("Parse data error: teamList is null, data = " + data);
                }

                if (danmakuList.size() != 0) {
                    mPraiseListView.receiveLikeNotice(danmakuList);
                }
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.XCR_ROOM_PRAISELIST_OPEN,
                XESCODE.XCR_ROOM_PRAISELIST_LIKE_STUTENT,
                XESCODE.XCR_ROOM_PRAISELIST_LIKE_TEAM
        };
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
            if (coachRoomstatus.getListStatus() != 0 && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                if (mPraiseListView == null) {
                    mPraiseListView = new PraiseListBll(activity);
                    mPraiseListView.setPresenter(PraiseListIRCBll.this);
                    mPraiseListView.initView(getLiveViewAction());
                }
                if (mPraiseListView != null) {
                    if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT) {
                        getExcellentList();
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_MINI_MARKET) {
                        getMiniMarketList();
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_LIKE) {
                        getLikeList();
                    }
                }
            }
        }
    }

    /**
     * 获取优秀榜
     */
    @Override
    public synchronized void getExcellentList() {
        String stuId = mGetInfo.getStuId();
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
        getHttpManager().getExcellentList("0", stuId, mLiveId, classId, teamId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getExcellentList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                ExcellentListEntity excellentListEntity = getHttpResponseParser().parseExcellentList(responseEntity);
                if (excellentListEntity != null) {
                    mPraiseListView.onExcellentList(excellentListEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getExcellentList => onPmFailure: error = " + error + ", msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                vcDialog.showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getExcellentList();
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getExcellentList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取计算小超市榜
     */
    @Override
    public synchronized void getMiniMarketList() {
        String stuId = mGetInfo.getStuId();
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        String stuCouId = mGetInfo.getStuCouId();
        String couseId = mGetInfo.getStudentLiveInfo().getCourseId();
        String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
        getHttpManager().getMiniMarketList(stuId, mLiveId, classId, stuCouId, couseId, teamId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getMiniMarketList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                MinimarketListEntity minimarketListEntity = getHttpResponseParser().parseMiniMarketList(responseEntity);
                if (minimarketListEntity != null) {
                    mPraiseListView.onMiniMarketList(minimarketListEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getMiniMarketList => onPmFailure: error = " + error + ", msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！").showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getMiniMarketList();
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getMiniMarketList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞榜
     */

    @Override
    public synchronized void getLikeList() {
        String stuId = mGetInfo.getStuId();
        String classId = mGetInfo.getStudentLiveInfo().getClassId();
        String teamId = mGetInfo.getStudentLiveInfo().getTeamId();
        getHttpManager().getLikeList(stuId, mLiveId, classId, teamId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getLikeList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                LikeListEntity likeListEntity = getHttpResponseParser().parseLikeList(responseEntity);
                if (likeListEntity != null) {
                    mPraiseListView.onLikeList(likeListEntity);
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getLikeList => onPmFailure: error = " + error + ", msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！").showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getLikeList();
                    }
                });
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLikeList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞概率标识
     */
    @Override
    public synchronized void getLikeProbability() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getLikeProbability(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getLikeProbability => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                LikeProbabilityEntity likeProbabilityEntity = getHttpResponseParser().parseLikeProbability(responseEntity);
                mLikeProbabilityEntity = likeProbabilityEntity;
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getLikeProbability => onPmFailure: error = " + error + ", msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLikeProbability => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    @Override
    public int getProbability() {
        if (mLikeProbabilityEntity != null) {
            return mLikeProbabilityEntity.getProbability();
        } else {
            return 1;
        }
    }

    /**
     * 学生告诉教师点赞个数
     */
    @Override
    public void sendLikeNum(int likes, String teamId, int barrageType) {
        mLogtf.d("sendLikeNum: likes = " + likes + ", teamId = " + teamId + ", mCounTeacherStr = " + mLiveBll.getCounTeacherStr());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_PRAISELIST_SEND_LIKE);
            jsonObject.put("likes", likes);
            jsonObject.put("teamId", teamId);
            jsonObject.put("ownTeamId", mGetInfo.getStudentLiveInfo().getClassId() + "_" + mGetInfo.getStudentLiveInfo().getTeamId());
            jsonObject.put("barrageType", barrageType);
            jsonObject.put("stuId", mGetInfo.getStuId());
            jsonObject.put("stuName", mGetInfo.getStuName());
            sendNoticeToCoun(jsonObject);
        } catch (Exception e) {
            mLogtf.e("sendLikeNum", e);
        }
    }

    @Override
    public String getStuName() {
        return mLiveBll.getStuName();
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mPraiseListView != null) {
            mPraiseListView.setVideoLayout(liveVideoPoint);
        }
    }
}
