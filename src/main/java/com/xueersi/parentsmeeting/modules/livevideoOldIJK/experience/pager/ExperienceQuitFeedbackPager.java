package com.xueersi.parentsmeeting.modules.livevideoOldIJK.experience.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.fragment.se.IExperiencePresenter;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by：WangDe on 2018/12/4 20:46
 */
public class ExperienceQuitFeedbackPager extends LiveBasePager {
    private CheckBox cbItem1;
    private CheckBox cbItem2;
    private CheckBox cbItem3;
    private CheckBox cbItem4;
    private CheckBox cbItem5;
    private Button btnBack;
    private Button btnLeave;
    private Map<String, Boolean> data = new HashMap<>();
    private List<String> datalist = new ArrayList<>();
    private String content = "";
    private IButtonClickListener mButtonListener;
    VideoLivePlayBackEntity mVideoEntity;
    LivePlayBackHttpManager livePlayBackHttpManager;
    private LinearLayout llGradingPaper;
    private IExperiencePresenter mExpPresenter;
    private TextView tvGradingPaper;
    private boolean isShow = false;
    private boolean gradingPaperShow = false;

    public ExperienceQuitFeedbackPager(Context context) {
        super(context);
        livePlayBackHttpManager = new LivePlayBackHttpManager(context);
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_experience_quit_feedback, null);
        cbItem1 = view.findViewById(R.id.cb_item_1);
        cbItem2 = view.findViewById(R.id.cb_item_2);
        cbItem3 = view.findViewById(R.id.cb_item_3);
        cbItem4 = view.findViewById(R.id.cb_item_4);
        cbItem5 = view.findViewById(R.id.cb_item_5);
        btnBack = view.findViewById(R.id.btn_back_class);
        btnLeave = view.findViewById(R.id.btn_leave_class);
        llGradingPaper = view.findViewById(R.id.ll_experience_grading_papers);
        tvGradingPaper = view.findViewById(R.id.tv_experience_grading_papers);
        btnLeave.setEnabled(false);
        initListener();
        return view;
    }

    @Override
    public void initListener() {
        cbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem1.setChecked(isChecked);
                data.put("1", isChecked);
                if (isChecked) {
                    cbItem2.setChecked(false);
                    data.put("2", false);
                }
                hasChecked();
            }
        });
        cbItem2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem2.setChecked(isChecked);
                data.put("2", isChecked);
                if (isChecked) {
                    cbItem1.setChecked(false);
                    data.put("1", false);
                }
                hasChecked();
            }
        });
        cbItem3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem3.setChecked(isChecked);
                data.put("3", isChecked);
                hasChecked();
            }
        });
        cbItem4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem4.setChecked(isChecked);
                data.put("4", isChecked);
                hasChecked();
            }
        });
        cbItem5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem5.setChecked(isChecked);
                data.put("5", isChecked);
                hasChecked();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isShow = mButtonListener.removePager();
            }
        });
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mButtonListener.leaveClass(data);
            }
        });
        tvGradingPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mExpPresenter.showWindow();
                gradingPaperShow = true;
            }
        });
        super.initListener();
    }

    public void setButtonListener(IButtonClickListener listener){
        mButtonListener = listener;
    }

    public void setIExperiencePresenter(IExperiencePresenter mExpPresenter){
        this.mExpPresenter = mExpPresenter;
    }

    private void hasChecked() {
        for (boolean haschecked : data.values()) {
            if (haschecked) {
                btnLeave.setTextColor(0xFFF13232);
                btnLeave.setEnabled(true);
                return;
            }
        }
        btnLeave.setTextColor(0xFF999999);
        btnLeave.setEnabled(false);
    }

    public void showGradingPaper(boolean isShow){
        if (isShow){
            llGradingPaper.setVisibility(View.VISIBLE);
        }else{
            gradingPaperShow = false;
        }

    }

    public void removeAllCheck(){
        cbItem5.setChecked(false);
        cbItem4.setChecked(false);
        cbItem3.setChecked(false);
        cbItem2.setChecked(false);
        cbItem1.setChecked(false);
        data.clear();
    }

    @Override
    public boolean onUserBackPressed() {
        //显示定级卷时点击返回直接关闭
        if (!gradingPaperShow){
            if(!isShow){
                logger.i("show pager");
                isShow = mButtonListener.showPager();
                return isShow;
            }else{
                logger.i("remove pager");
                isShow = mButtonListener.removePager();
            }
            return true;
        }else {
            logger.i("quit video");
            return false;
        }
    }

    public interface IButtonClickListener {
        boolean showPager();

        boolean removePager();

        void leaveClass(Map<String, Boolean> data);

    }



}
