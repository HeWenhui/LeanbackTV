package com.xueersi.parentsmeeting.modules.livevideoOldIJK.redpackage.pager;

import android.content.Context;
import android.support.constraint.Group;
import android.view.View;
import android.widget.ImageView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

/**
 * 小学语文红包
 */
public class SmallChineseRedPackagePager extends BasePager {
    /** 关闭按钮 */
    private ImageView ivClose;
    /** 赠送按钮 */
    private ImageView ivReceive;
    /** 红包北京 */
    private ImageView ivRedPackageBackground;
    /** 红包提示 */
    private ImageView ivRedPackageTip;
    /** 红包具体金额 */
    private FangZhengCuYuanTextView fzcytvMoney;
    /** 组别的金钱 */
    private Group groupMoney;

    public SmallChineseRedPackagePager(Context context) {
        super(context);
        initListener();
        updateView(false, 0);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_small_chinese_redpackage, null);
        ivClose = view.findViewById(R.id.iv_livevideo_small_chinese_red_package_close);
        ivReceive = view.findViewById(R.id.iv_livevideo_small_chinese_red_package_receive);
        ivRedPackageBackground = view.findViewById(R.id.iv_livevideo_small_chinese_red_package_background);
        ivRedPackageTip = view.findViewById(R.id.iv_livevideo_small_chinese_red_package_open_tip);
        fzcytvMoney = view.findViewById(R.id.fzcytv_livevideo_small_chinese_red_package_red_value);
        groupMoney = view.findViewById(R.id.group_livevideo_small_chinese_red_package_money);
        return view;
    }

    @Override
    public void initData() {

    }

    /**
     * 更新View
     *
     * @param isOpen
     * @param goldNum
     */
    public void updateView(boolean isOpen, int goldNum) {
        if (isOpen) {
            ivRedPackageBackground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_redpackage_open_background));
            ivRedPackageTip.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_redpackage_open_tip));
            ivReceive.setVisibility(View.GONE);
            fzcytvMoney.setVisibility(View.VISIBLE);
            fzcytvMoney.setText(String.valueOf(goldNum));
            groupMoney.setVisibility(View.VISIBLE);
        } else {
            ivRedPackageBackground.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_redpackage_not_open_background));
            ivRedPackageTip.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bg_livevideo_small_chinese_redpackage_not_open_tip));
            fzcytvMoney.setVisibility(View.GONE);
            ivReceive.setVisibility(View.VISIBLE);
            groupMoney.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.close();
                }
            }
        });
        ivReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.submit();
                }
            }
        });
    }

    public interface SmallChineseRedPackageListener {
        /** 关闭 */
        void close();

        /** 提交 */
        void submit();
    }

    private SmallChineseRedPackageListener listener;

    public void setListener(SmallChineseRedPackageListener listener) {
        this.listener = listener;
    }
}
