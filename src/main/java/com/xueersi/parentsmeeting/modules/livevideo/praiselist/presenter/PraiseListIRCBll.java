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
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ExcellentListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveGetInfo;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveTopic;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LiveVideoPoint;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeProbabilityEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.ProgressListEntity;
import com.xueersi.parentsmeeting.modules.livevideo.praiselist.entity.LikeListEntity;
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
    LikeProbabilityEntity mLikeProbabilityEntity;
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
                            case PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT:
                                getExcellentList(0);
                                break;
                            case PraiseListPager.PRAISE_LIST_TYPE_PROGRESS:
                                getProgressList(0);
                                break;
                            case PraiseListPager.PRAISE_LIST_TYPE_Like:
                                getLikeList();
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
                        if (mLikeProbabilityEntity == null) {
                            mView.receiveLikeNotice(list, mLikeProbabilityEntity);
                        } else {
                            getLikeProbability(list);
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
                    if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT) {
                        getExcellentList(0);
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_PROGRESS) {
                        getProgressList(0);
                    } else if (coachRoomstatus.getListStatus() == PraiseListPager.PRAISE_LIST_TYPE_Like) {
                        getLikeList();
                    }
                }
            }
        }
    }

    /**
     * 获取光荣榜
     */
    @Override
    public synchronized void getExcellentList(final int status) {
        if (status == 0) {
            if (mListType == PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT) {
                //如果当前榜单类型和新开启榜单类型相同，则退出。
                return;
            } else {
                //设置当前榜单类型
                mListType = PraiseListPager.PRAISE_LIST_TYPE_EXECELLENT;
            }
        }

        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getExcellentList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getExcellentList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                ExcellentListEntity excellentListEntity = getHttpResponseParser().parseExcellentList(responseEntity);
                if (mView != null && excellentListEntity != null) {
                    if (status == 0) {
                        mView.onExcellentList(excellentListEntity);
                    } else if (status == 1) {
                        if (excellentListEntity.getPraiseStatus() == 1)
                            mView.showLikeToast();
                        else
                            mView.setLikeBtnEnabled(true);
                    }
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getExcellentList => onPmFailure: error = " + error + ", msg=" + msg);
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, true,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！");
                    vcDialog.showDialog();
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getExcellentList(0);
                        }
                    });
                    if (mView != null)
                        mListType = 0;
                } else if (status == 1 && mView != null) {
                    mView.setLikeBtnEnabled(true);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getExcellentList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞榜
     */
    @Override
    public synchronized void getLikeList() {
        if (mListType == PraiseListPager.PRAISE_LIST_TYPE_Like) {
            //如果当前榜单类型和新开启榜单类型相同，则退出。
            return;
        } else {
            //设置当前榜单类型
            mListType = PraiseListPager.PRAISE_LIST_TYPE_Like;
        }

        String enstuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId();
        String classId = "";
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getLikeList(classId, enstuId, new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getLikeList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                LikeListEntity likeListEntity = getHttpResponseParser().parseLikeList(responseEntity);
                if (mView != null && likeListEntity != null) {
                    mView.onLikeList(likeListEntity);
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
                mListType = 0;
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getLikeList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
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
        if (mGetInfo.getStudentLiveInfo() != null) {
            classId = mGetInfo.getStudentLiveInfo().getClassId();
        }
        getHttpManager().getProgressList(classId, enstuId, mLiveId, status + "", new HttpCallBack(false) {

            @Override
            public void onPmSuccess(ResponseEntity responseEntity) {
                mLogtf.d("getProgressList => onPmSuccess:  + jsonObject = " + responseEntity.getJsonObject());
                ProgressListEntity progressListEntity = getHttpResponseParser().parseProgressList(responseEntity);
                if (mView != null && progressListEntity != null) {
                    if (status == 0) {
                        mView.onProgressList(progressListEntity);
                    } else if (status == 1) {
                        if (progressListEntity.getPraiseStatus() == 1)
                            mView.showLikeToast();
                        else
                            mView.setLikeBtnEnabled(true);
                    }
                }
            }

            @Override
            public void onPmFailure(Throwable error, String msg) {
                mLogtf.d("getProgressList => onPmFailure: error = " + error + ", msg=" + msg);
                if (status == 0) {
                    VerifyCancelAlertDialog vcDialog = new VerifyCancelAlertDialog(mContext, mBaseApplication, false,
                            VerifyCancelAlertDialog.MESSAGE_VERIFY_CANCEL_TYPE);
                    vcDialog.initInfo("当前网络不佳，请刷新获取榜单！").showDialog();
                    ;
                    vcDialog.setVerifyBtnListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getProgressList(0);
                        }
                    });
                    mListType = 0;
                } else if (status == 1 && mView != null) {
                    mView.setLikeBtnEnabled(true);
                }
            }

            @Override
            public void onPmError(ResponseEntity responseEntity) {
                mLogtf.d("getProgressList => onPmError: errorMsg = " + responseEntity.getErrorMsg());
                showToast("" + responseEntity.getErrorMsg());
            }
        });
    }

    /**
     * 获取点赞概率标识
     */
    @Override
    public synchronized void getLikeProbability(final ArrayList<String> list) {
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
                if (mLikeProbabilityEntity != null) {
                    mLikeProbabilityEntity = likeProbabilityEntity;
                    if (mView != null) {
                        mView.receiveLikeNotice(list, likeProbabilityEntity);
                    }
                }
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

    /**
     * 学生私聊老师点赞
     */
    @Override
    public void sendLike() {
        mLogtf.d("sendLike");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_SEND_S);
            jsonObject.put("agreeFrom", "" + mGetInfo.getStuName());
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
        } catch (Exception e) {
            mLogtf.e("sendLike", e);
        }
    }

    /**
     * 学生计算赞数后私发老师
     */
    @Override
    public void sendLikeNum(int agreeNum) {
        mLogtf.d("sendLikeNum: agreeNum = " + agreeNum + ", mCounTeacherStr = " + mLiveBll.getCounTeacherStr());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "" + XESCODE.XCR_ROOM_AGREE_NUM_S);
            jsonObject.put("agreeNum", agreeNum);
            sendNotice(jsonObject, mLiveBll.getCounTeacherStr());
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
        if (mView != null) {
            mView.setVideoLayout(liveVideoPoint);
        }
    }
}
