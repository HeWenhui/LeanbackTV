package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.browser.business.BrowserBll;
import com.xueersi.parentsmeeting.business.AppBll;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.business.ActivityChangeLand;
import com.xueersi.parentsmeeting.modules.livevideo.business.LecAdvertPagerClose;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LecAdvertEntity;
import com.xueersi.parentsmeeting.modules.loginregisters.business.UserBll;

/**
 * Created by linyuqiang on 2018/1/15.
 * 广告
 */
public class LecAdvertPager extends BasePager {
    private View step1;
    private View step2;
    private View step3;
    TextView tv_livelec_advert_step2_title;
    private LayoutInflater inflater;
    private LecAdvertPagerClose lecAdvertBll;
    private ViewGroup group;
    private LecAdvertPayPager lecAdvertPayPager;
    private int step = 1;
    LecAdvertEntity lecAdvertEntity;
    String liveid;

    public LecAdvertPager(Context context, LecAdvertEntity lecAdvertEntity, LecAdvertPagerClose lecAdvertBll, String liveid) {
        super(context);
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
                lecAdvertBll.close();
            }
        });
        TextView tv_livelec_advert_step1_name = (TextView) step1.findViewById(R.id.tv_livelec_advert_step1_name);
        TextView tv_livelec_advert_step1_remainder = (TextView) step1.findViewById(R.id.tv_livelec_advert_step1_remainder);
        tv_livelec_advert_step1_name.setText(lecAdvertEntity.saleName);
        tv_livelec_advert_step1_remainder.setText(lecAdvertEntity.limit);
        step1.findViewById(R.id.tv_livelec_advert_step1_enroll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContext instanceof ActivityChangeLand) {
                    ActivityChangeLand activityChangeLand = (ActivityChangeLand) mContext;
                    activityChangeLand.setAutoOrientation(false);
                    activityChangeLand.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                step = 2;
                step2 = inflater.inflate(R.layout.page_leclive_advert_step2, group, false);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                group.addView(step2, lp);
                initViewStep2();
            }
        });
    }

    @Override
    public void initData() {
        setCookie();
    }

    private void initViewStep2() {
        tv_livelec_advert_step2_title = (TextView) step2.findViewById(R.id.tv_livelec_advert_step2_title);
        String mEnStuId = UserBll.getInstance().getMyUserInfoEntity().getEnstuId(); // token
        String mAppChannel = AppBll.getInstance().getAppInfoEntity().getAppChannel(); // APP渠道
        String url = BrowserBll.getAutoLoginURL(mEnStuId, lecAdvertEntity.signUpUrl, mAppChannel, 0, false);
        lecAdvertPayPager = new LecAdvertPayPager(mContext, url, tv_livelec_advert_step2_title, new LecAdvertPayPager.OnPaySuccess() {
            @Override
            public void onPaySuccess() {
                clearCookie();
                step2.findViewById(R.id.rl_livelec_advert_step2_title).setBackgroundColor(mContext.getResources().getColor(R.color.COLOR_FFFFFF));
                tv_livelec_advert_step2_title.setText("购买成功");
                RelativeLayout relativeLayout = (RelativeLayout) step2.findViewById(R.id.rl_livelec_advert_step3_title);
                step3 = inflater.inflate(R.layout.page_leclive_advert_step3, relativeLayout, false);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                relativeLayout.addView(step3, lp);
                TextView tv_livelec_advert_step3_tip2 = (TextView) step3.findViewById(R.id.tv_livelec_advert_step3_tip2);
                tv_livelec_advert_step3_tip2.setText("稍后辅导老师会通过电话联系你\n请耐心等待");
                lecAdvertBll.onPaySuccess(lecAdvertEntity);
            }
        });
        ViewGroup group = (ViewGroup) step2.findViewById(R.id.rl_livelec_advert_step2_web);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        group.addView(lecAdvertPayPager.getRootView(), lp);
        step2.findViewById(R.id.iv_livelec_advert_step2_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCookie();
                lecAdvertBll.close();
            }
        });
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
