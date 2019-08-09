package com.xueersi.parentsmeeting.modules.livevideoOldIJK.page;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.xueersi.common.event.MiniEvent;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.lecadvert.business.LecAdvertPagerClose;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by linyuqiang on 2018/1/15.
 * 广告
 */
public class LecAdvertPager extends LiveBasePager {
    private View step1;
    private View step2;
    private View step3;
    TextView tv_livelec_advert_step2_title;
    private LayoutInflater inflater;
    private LecAdvertPagerClose lecAdvertBll;
    private ViewGroup group;
    private int step = 1;
    LecAdvertEntity lecAdvertEntity;
    String liveid;
    private Activity mActivity;

    public LecAdvertPager(Context context, LecAdvertEntity lecAdvertEntity, LecAdvertPagerClose lecAdvertBll, String liveid) {
        super(context);
        this.mActivity = (Activity) context;
        this.lecAdvertBll = lecAdvertBll;
        this.lecAdvertEntity = lecAdvertEntity;
        this.liveid = liveid;
        initData();
    }

    @Override
    public View initView() {
        inflater = LayoutInflater.from(mContext);
        group = (ViewGroup) View.inflate(mContext, R.layout.page_leclive_advert, null);
        return group;
    }

    public void initStep1() {
        step1 = inflater.inflate(R.layout.page_leclive_advert_step1, group, false);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        group.addView(step1, lp);
        step1.findViewById(R.id.iv_livelec_advert_step1_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lecAdvertBll.close(true);
                EventBus.getDefault().post(new MiniEvent("Advertisement","","",""));
            }
        });
        TextView tv_livelec_advert_step1_name = (TextView) step1.findViewById(R.id.tv_livelec_advert_step1_name);
        TextView tv_livelec_advert_step1_remainder = (TextView) step1.findViewById(R.id.tv_livelec_advert_step1_remainder);
        tv_livelec_advert_step1_name.setText(lecAdvertEntity.saleName);
        tv_livelec_advert_step1_remainder.setText(lecAdvertEntity.limit);
        step1.findViewById(R.id.tv_livelec_advert_step1_enroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mContext instanceof ActivityChangeLand) {
//                    ActivityChangeLand activityChangeLand = (ActivityChangeLand) mContext;
//                    activityChangeLand.setAutoOrientation(false);
//                    activityChangeLand.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                }
//                step = 2;
//                step2 = inflater.inflate(R.layout.page_leclive_advert_step2, group, false);
//                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                group.addView(step2, lp);
//                initViewStep2();
//                LecAdvertLog.sno5(lecAdvertEntity, liveAndBackDebug);
                // 04.09 直接跳转到订单支付页面
                EventBus.getDefault().post(new MiniEvent("Order",lecAdvertEntity.courseId,lecAdvertEntity.classId,lecAdvertEntity.id));
                lecAdvertBll.close(false);
            }
        });
    }

    @Override
    public void initData() {
        setCookie();
    }

    public int getStep() {
        return step;
    }

    private void setCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        广告id & 直播id & 直播类型 & 端类型  base64编码
        String cookie = lecAdvertEntity.id + "&" + liveid + "&2&3";
        byte[] buffer = cookie.getBytes();
        String value = Base64.encodeToString(buffer, 0, buffer.length, 0);
        cookieManager.setCookie(".xueersi.com", "lecture_ads=" + value);
    }

    private void clearCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(".xueersi.com", "lecture_ads=");
    }
}
