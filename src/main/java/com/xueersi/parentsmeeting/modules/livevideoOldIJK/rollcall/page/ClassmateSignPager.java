package com.xueersi.parentsmeeting.modules.livevideoOldIJK.rollcall.page;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xueersi.common.base.BasePager;
import com.xueersi.lib.framework.utils.ScreenUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item.ClassmateItem;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.business.LogToFile;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.parentsmeeting.modules.livevideo.widget.ScrollLinearLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author linyuqiang 签到
 */
public class ClassmateSignPager extends BasePager {
    private static final String TAG = "ClassmateSignPager";
    ScrollLinearLayout llClassmateSign;
    ArrayList<ClassmateEntity> allClassmateEntities = new ArrayList<ClassmateEntity>();
    private Handler mHandler = new Handler();
    private LayoutInflater mInflater;
    private int topMargin;
    private int mDuration = 900;
    private long delay = 1000;
    private long hidedelay = 5000;
    private Runnable hideRunnable = new HideRunnable();
    private LogToFile logToFile;
    private ClassSignStop classSignStop;

    /**
     * 最大 展示条目数
     */
    private static final int MAX_DISPLAY_COUNT = 3;

    public ClassmateSignPager(Context context) {
        super(context);
        initData();
        mInflater = LayoutInflater.from(context);
        float density = ScreenUtils.getScreenDensity();
        topMargin = (int) (density * 15 + 0.5f);
        logToFile = new LogToFile(context,TAG);
    }

    public void setClassSignStop(ClassSignStop classSignStop) {
        this.classSignStop = classSignStop;
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevodeo_classmatesign, null);
        llClassmateSign = (ScrollLinearLayout) mView.findViewById(R.id.ll_livevideo_classmate_sign);
        mView.setVisibility(View.INVISIBLE);

        mView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                stop();
                if (mHandler != null) {
                    mHandler.removeCallbacksAndMessages(null);
                }
            }
        });
        return mView;
    }

    @Override
    public void initData() {
        llClassmateSign.setDuration(mDuration);
    }

    int showCount = 0;
    int ignoreCount = 0;
    long before = System.currentTimeMillis();

    /**
     * 5秒没消息，自动隐藏
     */
    class HideRunnable implements Runnable {

        @Override
        public void run() {
            logToFile.d("HideRunnable:INVISIBLE:show=" + showCount + ",ignore=" + ignoreCount);
            mView.setVisibility(View.INVISIBLE);
            llClassmateSign.removeAllViews();
//            hideRunnable = null;
        }
    }

    /**
     * 1秒取一次数据
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(runnable);
            if (classSignStop.getStopSign() || !classSignStop.isTraning()) {
                stop();
                return;
            }
            if (!allClassmateEntities.isEmpty()) {
                if (hideRunnable != null) {
                    mHandler.removeCallbacks(hideRunnable);
                    hideRunnable = null;
                }
                logToFile.d("runnable.add" + (System.currentTimeMillis() - before));
                before = System.currentTimeMillis();
                ClassmateEntity classmateEntity = allClassmateEntities.remove(0);
                {
                    ClassmateItem classmateItem = new ClassmateItem(mContext);
                    View convertView = mInflater.inflate(classmateItem.getLayoutResId(), llClassmateSign, false);
                    classmateItem.initViews(convertView);
                    llClassmateSign.addView(convertView);
                    if (mView.getVisibility() != View.VISIBLE) {
                        mView.setVisibility(View.VISIBLE);
                    }
                    classmateItem.updateViews(classmateEntity, 0, null);
                }
                final int height = llClassmateSign.getChildAt(0).getMeasuredHeight();
                ViewGroup group = (ViewGroup) llClassmateSign.getParent();
                ViewGroup.LayoutParams params2 = group.getLayoutParams();
                if (llClassmateSign.getChildCount() < MAX_DISPLAY_COUNT) {
                    params2.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                } else {
                    params2.height = (height + topMargin) * MAX_DISPLAY_COUNT;
                }
                if (llClassmateSign.getChildCount() > MAX_DISPLAY_COUNT) {
                    llClassmateSign.smoothScrollBy(0, (height + topMargin));
                    llClassmateSign.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            llClassmateSign.stop();
                        }
                    }, mDuration);
                }
            } else {
                if (hideRunnable == null) {
                    hideRunnable = new HideRunnable();
                    mHandler.postDelayed(hideRunnable, hidedelay);
                    logToFile.d("runnable:hideRunnable");
                }
            }
            mHandler.postDelayed(runnable, delay);
        }
    };

    public void start() {
        mHandler.postDelayed(runnable, delay);
    }

    public void stop() {
        mView.post(new Runnable() {
            @Override
            public void run() {
                mView.setVisibility(View.INVISIBLE);
                llClassmateSign.removeAllViews();
                mHandler.removeCallbacks(runnable);
                allClassmateEntities.clear();
            }
        });
    }

    public void addClassmage(ClassmateEntity classmateEntity) {
        if (classSignStop.getStopSign() || !classSignStop.isTraning()) {
            stop();
            ignoreCount++;
            return;
        }
        showCount++;
        if (hideRunnable != null) {
            mHandler.removeCallbacks(hideRunnable);
            hideRunnable = null;
        }
        allClassmateEntities.add(classmateEntity);
        logToFile.d("addClassmage:name=" + classmateEntity.getName() + ",getChildCount=" + llClassmateSign
                .getChildCount());
        if (llClassmateSign.getChildCount() < MAX_DISPLAY_COUNT) {
            mHandler.post(runnable);
        }
    }

    public static class ClassSignStop {
        /**
         * 停止点名
         */
        private AtomicBoolean stopSign = new AtomicBoolean(false);
        /**
         * 是不是辅导状态
         */
        private boolean isTraning = true;

        /**
         * 是否是自动签到
         */
        private boolean atuoSign = false;

        public boolean getStopSign() {
            return stopSign.get();
        }

        public void setStopSign(boolean stopSign) {
            this.stopSign = new AtomicBoolean(stopSign);
        }

        public boolean isTraning() {
            return isTraning;
        }

        public void setTraning(boolean traning) {
            isTraning = traning;
        }

        public void setAtuoSign(boolean atuoSign) {
            this.atuoSign = atuoSign;
        }

        public boolean isAtuoSign() {
            return atuoSign;
        }
    }
}
