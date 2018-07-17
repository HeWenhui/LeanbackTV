package com.xueersi.parentsmeeting.modules.livevideo.understand.business;

import android.app.Activity;
import android.widget.RelativeLayout;

import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBaseBll;
import com.xueersi.parentsmeeting.modules.livevideo.business.XESCODE;
import com.xueersi.parentsmeeting.modules.livevideo.core.LiveBll2;
import com.xueersi.parentsmeeting.modules.livevideo.core.NoticeAction;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by linyuqiang on 2018/7/17.
 * 懂了吗？
 */
public class UnderstandIRCBll extends LiveBaseBll implements NoticeAction {
    private UnderstandAction understandAction;
    private AtomicBoolean mIsLand;

    public UnderstandIRCBll(Activity context, LiveBll2 liveBll, RelativeLayout rootView) {
        super(context, liveBll, rootView);
        mIsLand = liveBll.getmIsLand();
    }

    @Override
    public void onCreate(HashMap<String, Object> data) {
        super.onCreate(data);
    }

    @Override
    public void onNotice(final JSONObject object, int type) {
        switch (type) {
            case XESCODE.UNDERSTANDT:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (understandAction == null) {
                            UnderstandBll understandBll = new UnderstandBll(activity, mLiveBll);
                            understandBll.setGetInfo(mGetInfo);
                            understandBll.setUnderstandHttp(new UnderstandHttp() {
                                @Override
                                public void understand(boolean isUnderstand, String nonce) {
                                    if (mLiveBll.getMainTeacherStr() != null) {
                                        try {
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("type", "" + XESCODE.UNDERSTANDS);
                                            jsonObject.put("understand", isUnderstand);
                                            jsonObject.put("nonce", nonce);
                                            mLiveBll.sendNotice(mLiveBll.getMainTeacherStr(), jsonObject);
                                            mLogtf.d("understand ok");
                                        } catch (Exception e) {
                                            // Loger.e(TAG, "understand", e);
                                            mLogtf.e("understand", e);
                                        }
                                    } else {
                                        mLogtf.d("understand mMainTeacherStr=null");
                                    }
                                }
                            });
                            understandBll.initView(mRootView, mIsLand.get());
                            understandAction = understandBll;
                        }
                        String nonce = object.optString("nonce");
                        understandAction.understand(nonce);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public int[] getNoticeFilter() {
        return new int[]{XESCODE.UNDERSTANDT};
    }
}
