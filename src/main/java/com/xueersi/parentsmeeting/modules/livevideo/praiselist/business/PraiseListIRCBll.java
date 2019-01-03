package com.xueersi.parentsmeeting.modules.livevideo.praiselist.business;

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
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.page.PraiseListPager;
import com.xueersi.ui.dialog.VerifyCancelAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Zhang Yuansun on 2018/7/27.
 */

public class PraiseListIRCBll extends LiveBaseBll implements NoticeAction, TopicAction {
    /**
     * 表扬榜事件
     */
    private PraiseListAction mPraiseListAction;

    public PraiseListIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        //模式切换为主讲，关闭表扬榜
        if (mPraiseListAction != null && mode.equals(LiveTopic.MODE_CLASS))
            mPraiseListAction.closePraiseList();
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_AGREE_OPEN: {
                if (mPraiseListAction == null) {
                    PraiseListBll praiseListBll = new PraiseListBll(activity);
                    praiseListBll.initView(mRootView);
                    praiseListBll.setLiveBll(PraiseListIRCBll.this);
                    mPraiseListAction = praiseListBll;
                }
                if (mPraiseListAction != null) {
                    String open = data.optString("open");
                    int zanType = data.optInt("zanType");
                    String nonce = data.optString("nonce");
                    if ("on".equals(open)) {
                        mPraiseListAction.onReceivePraiseList(zanType, nonce);
                        switch (zanType) {
                            case PraiseListPager.PRAISE_LIST_TYPE_HONOR:
                                getHonorList(0);
                                break;
                            case PraiseListPager.PRAISE_LIST_TYPE_PROGRESS:
                                getProgressList(0);
                                break;
                            case PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP:
                                getThumbsUpList();
                                break;
                            default:
                                break;
                        }
                    } else if ("off".equals(open)) {
                        if (mPraiseListAction != null) {
                            mPraiseListAction.closePraiseList();
                        }
                    }
                }

            }
            case XESCODE.XCR_ROOM_AGREE_SEND_T: {
                if (mPraiseListAction == null) {
                    PraiseListBll praiseListBll = new PraiseListBll(activity);
                    praiseListBll.initView(mRootView);
                    praiseListBll.setLiveBll(PraiseListIRCBll.this);
                    mPraiseListAction = praiseListBll;
                }
                if (mPraiseListAction != null) {
                    if (mPraiseListAction.getThumbsUpProbability() == 0) {
                        getThumbsUpProbability();
                    }
                    JSONArray agreeForms = data.optJSONArray("agreeFroms");
                    boolean isTeacher = data.optBoolean("isTeacher");
                    mLogtf.d("agreeFroms is null，data = " + data);
                    if (agreeForms == null) {
                        return;
                    }
                    logger.i("agreeForms=" + agreeForms.toString());
                    logger.i("isTeacher=" + isTeacher);
                    if (isTeacher) {
                        if (mPraiseListAction != null && agreeForms.length() != 0) {
                            try {
                                mPraiseListAction.showPraiseScroll(mGetInfo.getStuName(), agreeForms.getString(0));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < agreeForms.length(); i++) {
                            String stuName = null;
                            try {
                                stuName = agreeForms.getString(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            logger.i("stuName=" + stuName);
                            list.add(stuName);
                        }
                        if (mPraiseListAction != null && list.size() != 0) {
                            mPraiseListAction.receiveThumbsUpNotice(list);
                        }
                    }
                }
            }
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{
                XESCODE.XCR_ROOM_AGREE_SEND_T,
                XESCODE.XCR_ROOM_AGREE_OPEN
        };
    }

    @Override
    public void onTopic(LiveTopic liveTopic, JSONObject jsonObject, boolean modeChange) {
        if (mLiveType == LiveVideoConfig.LIVE_TYPE_LIVE) {
            LiveTopic.RoomStatusEntity coachRoomstatus = liveTopic.getCoachRoomstatus();
            if (coachRoomstatus.getListStatus() != 0 && LiveTopic.MODE_TRANING.equals(mLiveBll.getMode())) {
                if (mPraiseListAction == null) {
                    PraiseListBll praiseListBll = new PraiseListBll(activity);
                    praiseListBll.initView(mRootView);
                    praiseListBll.setLiveBll(PraiseListIRCBll.this);
                    mPraiseListAction = praiseListBll;
                }
                if (mPraiseListAction != null) {
                    if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_HONOR) {
                        getHonorList(0);
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_PROGRESS) {
                        getProgressList(0);
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP) {
                        getThumbsUpList();
                    }
                }
            }
        }
    }

    /**
     * 获取光荣榜
     */
    public synchronized void getHonorList(final int status) {
        if (mPraiseListAction != null && status == 0 && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_HONOR)
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        if (mPraiseListAction != null && status == 0)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_HONOR);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getHonorList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getHonorList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                HonorListEntity honorListEntity = getHttpResponseParser().parseHonorList(responseEntity);
                if (mPraiseListAction != null && honorListEntity != null) {
                    if (status == 0) {

                        mPraiseListAction.onHonerList(honorListEntity);
                    } else if (status == 1) {
                        if (honorListEntity.getPraiseStatus() == 1)
                            mPraiseListAction.showThumbsUpToast();
                        else
                            mPraiseListAction.setThumbsUpBtnEnabled(true);
                    }

                }
                mLogtf.d("getHonorList:onPmSuccess:honorListEntity=" + (honorListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                    vcDialog.showDialog();
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getHonorList(0);
                        }
                    });
                    if (mPraiseListAction != null)
                        mPraiseListAction.setCurrentListType(0);
                } else if (status == 1 && mPraiseListAction != null) {
                    mPraiseListAction.setThumbsUpBtnEnabled(true);
                }
                mLogtf.d("getHonorList:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                showToast("" + responseEntity.getErrorMsg());
                mLogtf.d("getHonorList:onPmError=" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞榜
     */
    public synchronized void getThumbsUpList() {
        if (mPraiseListAction != null && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_THUMBS_UP)
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        if (mPraiseListAction != null)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getThumbsUpList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getThumbsUpList(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ThumbsUpListEntity thumbsUpListEntity = getHttpResponseParser().parseThumbsUpList(responseEntity);
                if (mPraiseListAction != null && thumbsUpListEntity != null) {
                    mPraiseListAction.onThumbsUpList(thumbsUpListEntity);
                }
                mLogtf.d("getThumbsUpList:onPmSuccess:thumbsUpListEntity=" + (thumbsUpListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getThumbsUpList:onPmFailure=" + error + ",msg=" + msg);
                VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                        VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                vcDialog.showDialog();
                vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getThumbsUpList();
                    }
                });
                if (mPraiseListAction != null)
                    mPraiseListAction.setCurrentListType(0);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getThumbsUpList:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取进步榜
     */
    public synchronized void getProgressList(final int status) {
        if (mPraiseListAction != null && status == 0 && mPraiseListAction.getCurrentListType() == PraiseListPager
                .PRAISE_LIST_TYPE_PROGRESS)
            //如果当前榜单类型和新开启榜单类型相同，则退出
            return;
        if (mPraiseListAction != null)
            //设置当前榜单类型
            mPraiseListAction.setCurrentListType(PraiseListPager.PRAISE_LIST_TYPE_PROGRESS);
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        mLogtf.d("getProgressList:enstuId=" + enstuId + ",liveId=" + mLiveId);
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getProgressList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ProgressListEntity progressListEntity = getHttpResponseParser().parseProgressList(responseEntity);
                if (mPraiseListAction != null && progressListEntity != null) {
                    if (status == 0) {

                        mPraiseListAction.onProgressList(progressListEntity);
                    } else if (status == 1) {
                        if (progressListEntity.getPraiseStatus() == 1)
                            mPraiseListAction.showThumbsUpToast();
                        else
                            mPraiseListAction.setThumbsUpBtnEnabled(true);
                    }

                }
                mLogtf.d("getProgressList:onPmSuccess:progressListEntity=" + (progressListEntity == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                    vcDialog.showDialog();
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getProgressList(0);
                        }
                    });
                    if (mPraiseListAction != null)
                        mPraiseListAction.setCurrentListType(0);
                } else if (status == 1 && mPraiseListAction != null) {
                    mPraiseListAction.setThumbsUpBtnEnabled(true);
                }
                mLogtf.d("getProgressList:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                showToast("" + responseEntity.getErrorMsg());
                mLogtf.d("getProgressList:onPmError=" + responseEntity.getErrorMsg());
            }

        });
    }

    /**
     * 获取点赞概率标识
     */
    public synchronized void getThumbsUpProbability() {
        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        mLogtf.d("getThumbsUpProbability:enstuId=" + enstuId + ",liveId=" + mLiveId);
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getThumbsUpProbability(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                ThumbsUpProbabilityEntity thumbsUpProbabilityEntity = getHttpResponseParser().parseThumbsUpProbability
                        (responseEntity);
                if (mPraiseListAction != null && thumbsUpProbabilityEntity != null) {
                    mPraiseListAction.setThumbsUpProbability(thumbsUpProbabilityEntity);
                }
                mLogtf.d("getThumbsUpProbability:onPmSuccess:thumbsUpProbabilityEntity=" + (thumbsUpProbabilityEntity
                        == null) + "," +
                        "JsonObject=" + responseEntity.getJsonObject());
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getThumbsUpProbability:onPmFailure=" + error + ",msg=" + msg);
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getThumbsUpProbability:onPmError=" + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 学生私聊老师点赞
     */
    public void sendThumbsUp() {
        mLogtf.i("sendThumbsUp");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_SEND_S);
            jsonObject.put("agreeFrom", "" + mGetInfo.getStuName());
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
        } catch (Exception e) {
            mLogtf.e("sendThumbsUp", e);
        }
    }

    /**
     * 学生计算赞数后私发老师
     */
    public void sendThumbsUpNum(int agreeNum) {
        mLogtf.i("sendThumbsUpNum:agreeNum=" + agreeNum + ",mCounTeacherStr=" + mLiveBll.getCounTeacherStr());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_NUM_S);
            jsonObject.put("agreeNum", agreeNum);
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
        } catch (Exception e) {
            mLogtf.e("sendThumbsUpNum", e);
        }
    }

    public String getStuName() {
        return mLiveBll.getStuName();
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mPraiseListAction != null) {
            mPraiseListAction.setVideoLayout(liveVideoPoint);
        }
    }
}
