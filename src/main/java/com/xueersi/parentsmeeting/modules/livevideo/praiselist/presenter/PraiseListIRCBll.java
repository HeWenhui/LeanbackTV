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
import com.xueersi.parentsmeeting.modules.livevideo.entity.HonorListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ThumbsUpProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListPresenter;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.contract.PraiseListView;
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
    private PraiseListView mView;
    ThumbsUpProbabilityEntity mThumbsUpProbabilityEntity;
    int mListType = 0;

    public PraiseListIRCBll(Activity context, LiveBll2 liveBll) {
        super(context, liveBll);
        mView = new PraiseListBll(activity);
        mView.setPresenter(PraiseListIRCBll.this);
    }

    @Override
    public void onModeChange(String oldMode, String mode, boolean isPresent) {
        //模式切换为主讲，关闭表扬榜
        if (mView != null && mode.equals(LiveTopic.MODE_CLASS))
            mView.closePraiseList();
    }

    @Override
    public void onLiveInited(LiveGetInfo getInfo) {
        super.onLiveInited(getInfo);
        mView.initView(mRootView);
    }

    @Override
    public void onNotice(String sourceNick, String target, JSONObject data, int type) {
        switch (type) {
            case XESCODE.XCR_ROOM_AGREE_OPEN: {
                if (mView == null) {
                    mView = new PraiseListBll(activity);
                    mView.setPresenter(PraiseListIRCBll.this);
                    mView.initView(mRootView);
                }
                if (mView != null) {
                    String open = data.optString("open");
                    int zanType = data.optInt("zanType");
                    String nonce = data.optString("nonce");
                    if ("on".equals(open)) {
                        mView.onReceivePraiseList(zanType, nonce);
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
                        if (mView != null) {
                            mView.closePraiseList();
                        }
                    }
                }

            }
            case XESCODE.XCR_ROOM_AGREE_SEND_T: {
                if (mView == null) {
                    mView = new PraiseListBll(activity);
                    mView.setPresenter(PraiseListIRCBll.this);
                    mView.initView(mRootView);
                }
                JSONArray agreeForms = data.optJSONArray("agreeFroms");
                boolean isTeacher = data.optBoolean("isTeacher");
                mLogtf.d("agreeFroms is null，data = " + data);
                if (agreeForms == null) {
                    return;
                }
                logger.i("agreeForms=" + agreeForms.toString() + ", isTeacher=" + isTeacher);
                if (isTeacher) {
                    if (mView != null && agreeForms.length() != 0) {
                        try {
                            mView.showPraiseScroll(mGetInfo.getStuName(), agreeForms.getString(0));
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
                    if (mView != null && list.size() != 0) {
                        if (mThumbsUpProbabilityEntity == null) {
                            mView.receiveThumbsUpNotice(list, mThumbsUpProbabilityEntity);
                        } else {
                            getThumbsUpProbability(list);
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
                if (mView == null) {
                    mView = new PraiseListBll(activity);
                    mView.setPresenter(PraiseListIRCBll.this);
                    mView.initView(mRootView);
                }
                if (mView != null) {
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
    @Override
    public synchronized void getHonorList(final int status) {
        if (status == 0) {
            if (mListType == PraiseListPager.PRAISE_LIST_TYPE_HONOR) {
                //如果当前榜单类型和新开启榜单类型相同，则退出。
                return;
            } else {
                //设置当前榜单类型
                mListType = PraiseListPager.PRAISE_LIST_TYPE_HONOR;
            }
        }

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
                if (mView != null && honorListEntity != null) {
                    if (status == 0) {
                        mView.onHonerList(honorListEntity);
                    } else if (status == 1) {
                        if (honorListEntity.getPraiseStatus() == 1)
                            mView.showThumbsUpToast();
                        else
                            mView.setThumbsUpBtnEnabled(true);
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
                    if (mView != null)
                        mListType = 0;
                } else if (status == 1 && mView != null) {
                    mView.setThumbsUpBtnEnabled(true);
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
    @Override
    public synchronized void getThumbsUpList() {
        if (mListType == PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP) {
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        } else {
            //设置当前榜单类型
            mListType = PraiseListPager.PRAISE_LIST_TYPE_THUMBS_UP;
        }

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
                if (mView != null && thumbsUpListEntity != null) {
                    mView.onThumbsUpList(thumbsUpListEntity);
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
                mListType = 0;
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
    @Override
    public synchronized void getProgressList(final int status) {
        if (status == 0) {
            if (mListType == PraiseListPager.PRAISE_LIST_TYPE_PROGRESS) {
                //如果当前榜单类型和新开启榜单类型相同，则退出。
                return;
            } else {
                //设置当前榜单类型
                mListType = PraiseListPager.PRAISE_LIST_TYPE_PROGRESS;
            }
        }

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
                if (mView != null && progressListEntity != null) {
                    if (status == 0) {

                        mView.onProgressList(progressListEntity);
                    } else if (status == 1) {
                        if (progressListEntity.getPraiseStatus() == 1)
                            mView.showThumbsUpToast();
                        else
                            mView.setThumbsUpBtnEnabled(true);
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
                    mListType = 0;
                } else if (status == 1 && mView != null) {
                    mView.setThumbsUpBtnEnabled(true);
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
    @Override
    public synchronized void getThumbsUpProbability(final ArrayList<String> list) {
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
                if (mThumbsUpProbabilityEntity != null) {
                    mThumbsUpProbabilityEntity = thumbsUpProbabilityEntity;
                    if (mView != null) {
                        mView.receiveThumbsUpNotice(list, thumbsUpProbabilityEntity);
                    }
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
    @Override
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
    @Override
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

    @Override
    public String getStuName() {
        return mLiveBll.getStuName();
    }

    @Override
    public void setVideoLayout(LiveVideoPoint liveVideoPoint) {
        if (mView != null) {
            mView.setVideoLayout(liveVideoPoint);
        }
    }
}
