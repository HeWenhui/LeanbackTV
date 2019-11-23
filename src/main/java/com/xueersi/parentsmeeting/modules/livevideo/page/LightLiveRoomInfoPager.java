package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;

/**
 * @ProjectName: xueersiwangxiao
 * @Package: com.xueersi.parentsmeeting.modules.livevideo.page
 * @ClassName: LightLiveRoomInfoPager
 * @Description: 竖屏下 直播间人数和公告
 * @Author: WangDe
 * @CreateDate: 2019/11/23 17:34
 * @UpdateUser: 更新者
 * @UpdateDate: 2019/11/23 17:34
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class LightLiveRoomInfoPager extends LiveBasePager {

    TextView tvCount;
    TextView tvNotice;
    ImageView ivClose;
    View vGap;
    public LightLiveRoomInfoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.pager_livevideo_lightlive_roominfo, null);
        tvCount = mView.findViewById(R.id.tv_livevideo_message_count);
        tvNotice = mView.findViewById(R.id.iv_livevideo_lightlive_roominfo_notice);
        ivClose = mView.findViewById(R.id.iv_livevideo_lightlive_roominfo_close);
        vGap = mView.findViewById(R.id.v_livevideo_message_gap);
        initListener();
        return mView;
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNotice.setVisibility(View.GONE);
                vGap.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
            }
        });
    }

    public void setTvCount(final String count){
        post(new Runnable() {
            @Override
            public void run() {
                tvCount.setVisibility(View.VISIBLE);
                tvCount.setText(count);
                if (tvNotice.getVisibility() == View.VISIBLE){
                    vGap.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void setTvNotice(final String message){
        post(new Runnable() {
            @Override
            public void run() {
                if (tvCount.getVisibility() == View.GONE){
                    vGap.setVisibility(View.INVISIBLE);
                }else {
                    vGap.setVisibility(View.VISIBLE);
                }
                ivClose.setVisibility(View.VISIBLE);
                tvNotice.setVisibility(View.VISIBLE);
                tvNotice.setText(message);
            }
        });
    }

}
