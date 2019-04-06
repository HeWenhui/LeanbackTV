package com.xueersi.parentsmeeting.modules.livevideo.switchflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.analytics.umsagent.UmsAgentManager;
import com.xueersi.lib.framework.utils.XESToastUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.widget.FangZhengCuYuanTextView;

public class SwitchFlowView extends FrameLayout {

    private Button btnSwitchFlow;

    private ConstraintLayout layoutSwitchFlow;

    private ImageView ivSwitchFlowArrow;

    private FangZhengCuYuanTextView fzcyReload;

    private FangZhengCuYuanTextView fzcySwitch;

    private TextView tvReload;

    private TextView tvSwitch;
    private int isPopWindowShow = 0;

    private int clickColor = 0;

    private int normalColor = 0;
    /** 举麦是否打开 */
    private boolean isVoiceOn = false;

    public SwitchFlowView(@NonNull Context context) {
        super(context);
        init();
    }

    public SwitchFlowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchFlowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwitchFlowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        initView();
        initData();
        initListener();
        setSwitchFlowWholeVisible(false);
    }

    public void initData() {
        isSmallEnglish = ((Activity) getContext()).getIntent().getBooleanExtra("isSmallEnglish", false);
        Drawable drawable = getContext().getResources().getDrawable(R.drawable.bg_livevideo_normal_swich_flow);
        if (isSmallEnglish) {
            clickColor = getContext().getResources().getColor(R.color.COLOR_61B2F1);
            normalColor = getContext().getResources().getColor(R.color.COLOR_FFFFFF);
        } else if (LiveVideoConfig.isSmallChinese) {
            clickColor = getContext().getResources().getColor(R.color.COLOR_F0A61B);
            normalColor = getContext().getResources().getColor(R.color.COLOR_FFFFFF);
        } else if (LiveVideoConfig.isPrimary) {
            getContext().getResources().getDrawable(R.drawable.bg_livevideo_ps_swich_flow);
            clickColor = getContext().getResources().getColor(R.color.COLOR_FF6326);
            normalColor = getContext().getResources().getColor(R.color.COLOR_FFFFFF);
        } else {
            clickColor = getContext().getResources().getColor(R.color.COLOR_99FFFFFF);
            normalColor = getContext().getResources().getColor(R.color.COLOR_FFFFFF);
        }
        btnSwitchFlow.setBackground(drawable);
    }

    private boolean isSmallEnglish;
    private int pattern;

    private void initView() {
        isSmallEnglish = ((Activity) getContext()).getIntent().getBooleanExtra("isSmallEnglish", false);
        pattern = ((Activity) getContext()).getIntent().getIntExtra("pattern", 2);
        if (isSmallEnglish || LiveVideoConfig.isPrimary || LiveVideoConfig.isSmallChinese) {
            View view = View.inflate(getContext(), R.layout.page_livevideo_triple_screen_switch_flow, this);
            btnSwitchFlow = view.findViewById(R.id.bt_switch_flow);
            layoutSwitchFlow = view.findViewById(R.id.layout_livevideo_switch_flow_pop_window);
            ivSwitchFlowArrow = view.findViewById(R.id.iv_livevideo_common_switch_flow_arrow);
            tvSwitch = view.findViewById(R.id.fzcytv_livevideo_switch_flow_switch);
            tvReload = view.findViewById(R.id.fzcytv_livevideo_switch_flow_reload);
        } else {
            View view = View.inflate(getContext(), R.layout.page_livevideo_triple_screen_normal_switch_flow, this);
            btnSwitchFlow = view.findViewById(R.id.bt_switch_flow);
            layoutSwitchFlow = view.findViewById(R.id.layout_livevideo_switch_flow_pop_window);
            ivSwitchFlowArrow = view.findViewById(R.id.iv_livevideo_common_switch_flow_arrow);
            tvSwitch = view.findViewById(R.id.fzcytv_livevideo_switch_flow_switch);
            tvReload = view.findViewById(R.id.fzcytv_livevideo_switch_flow_reload);
        }
    }


    public View getLayoutSwitchFlow() {
        return layoutSwitchFlow;
    }

    private void initListener() {
        btnSwitchFlow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVoiceOn) {
                    XESToastUtils.showToast(getContext(), "当前正在举麦，请稍后重试");
                    return;
                }
                UmsAgentManager.umsAgentCustomerBusiness(getContext(), getContext().getResources().getString(R.string
                        .livevideo_switch_flow_17078));
                if (isPopWindowShow == 0) {
                    setSwitchFlowPopWindowVisible(true);

                } else {
                    setSwitchFlowPopWindowVisible(false);

                }
                if (clickListener != null) {
                    clickListener.click(v);
                }
            }
        });

//        if (fzcySwitch != null) {
//            fzcySwitch.setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        fzcySwitch.setTextColor(clickColor);
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        fzcySwitch.setTextColor(normalColor);
//                    }
//                    return false;
//                }
//            });
//            fzcySwitch.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (iSwitchFlow != null) {
//                        UmsAgentManager.umsAgentCustomerBusiness(getContext(), getContext().getResources().getString(R.string
//                                .livevideo_switch_flow_17078));
//                        iSwitchFlow.switchRoute();
//                    }
//                }
//            });
//        }
        if (tvSwitch != null) {
            tvSwitch.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        tvSwitch.setTextColor(clickColor);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        tvSwitch.setTextColor(normalColor);
                    }
                    return false;
                }
            });
            tvSwitch.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iSwitchFlow != null) {
                        UmsAgentManager.umsAgentCustomerBusiness(getContext(), getContext().getResources().getString(R.string
                                .livevideo_1773));
                        iSwitchFlow.switchRoute();
                    }
                }
            });
        }
//        if (fzcyReload != null) {
//            fzcyReload.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (iSwitchFlow != null) {
//                        setSwitchFlowPopWindowVisible(false);
//                        iSwitchFlow.reLoad();
//                    }
//                }
//            });
//            fzcyReload.setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                        fzcyReload.setTextColor(clickColor);
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        fzcyReload.setTextColor(normalColor);
//                    }
//                    return false;
//                }
//            });
//        }
        if (tvReload != null) {
            tvReload.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (iSwitchFlow != null) {
                        setSwitchFlowPopWindowVisible(false);
                        iSwitchFlow.reLoad();
                    }
                }
            });
            tvReload.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        tvReload.setTextColor(clickColor);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        tvReload.setTextColor(normalColor);
                    }
                    return false;
                }
            });
        }

    }

    /** 整个布局切流显示或着隐藏 */
    public final void setSwitchFlowWholeVisible(boolean isShow) {
        setVisibility(isShow ? VISIBLE : GONE);
//        btnSwitchFlow.setVisibility(isShow ? VISIBLE : GONE);
        setSwitchFlowPopWindowVisible(false);
    }

    /** 切流的弹窗隐藏或者显示 */
    public final void setSwitchFlowPopWindowVisible(boolean isShow) {
        isPopWindowShow = isShow ? 1 : 0;
        layoutSwitchFlow.setVisibility(isShow ? VISIBLE : GONE);
        ivSwitchFlowArrow.setVisibility(isShow ? VISIBLE : GONE);
    }

    /** 弹窗是否处于Visible状态 */
    private final boolean getSwitchFlowPopWindowVisible() {
        return layoutSwitchFlow.getVisibility() == VISIBLE && ivSwitchFlowArrow.getVisibility() == VISIBLE;
    }

    public void setIsVoiceOn(boolean isShow) {
        this.isVoiceOn = isShow;
    }

    public interface ISwitchFlow extends IReLoad {


        void switchRoute();
    }

    public interface IReLoad {
        /**
         * 1.重新加载
         * 2.自动切流
         */
        void reLoad();
    }

    public interface ISwitchFlowClickListener {
        void click(View v);
    }

    private ISwitchFlowClickListener clickListener;

    public void setClickListener(ISwitchFlowClickListener clickListener) {
        this.clickListener = clickListener;
    }
//    public interface ISwitchRoute {
//        /** 切换线路中 */
//        void switchRouteing();
//    }

    private ISwitchFlow iSwitchFlow;

//    public ISwitchFlow getiSwitchFlow() {
//        return iSwitchFlow;
//    }


    public void setiSwitchFlow(ISwitchFlow iSwitchFlow) {
        this.iSwitchFlow = iSwitchFlow;
    }
}
