package com.xueersi.parentsmeeting.modules.livevideo.dialog;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xueersi.common.base.BaseApplication;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.module.videoplayer.entity.VideoLivePlayBackEntity;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.http.LivePlayBackHttpManager;
import com.xueersi.ui.dialog.BaseAlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by：WangDe on 2018/11/24 12:07
 */
public class ExperienceQuitFeedbackDialog extends BaseAlertDialog {


    private CheckBox cbItem1;
    private CheckBox cbItem2;
    private CheckBox cbItem3;
    private CheckBox cbItem4;
    private CheckBox cbItem5;
    private Button btnBack;
    private Button btnLeave;
    private Map<String,Boolean> data = new HashMap<>();
    private List<String> datalist = new ArrayList<>();
    private String content = "";
    private ILeaveClassCallback leaveClassCallback;
    private IBackClassCallback backClassCallback;
    VideoLivePlayBackEntity mVideoEntity;
    LivePlayBackHttpManager livePlayBackHttpManager;
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public ExperienceQuitFeedbackDialog(Context context){
        super(context, (Application) BaseApplication.getContext(), false);
        livePlayBackHttpManager =  new LivePlayBackHttpManager(context);
    }

    @Override
    protected View initDialogLayout(int type) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_experience_quit_feedback, null);
        cbItem1 = view.findViewById(R.id.cb_item_1);
        cbItem2 = view.findViewById(R.id.cb_item_2);
        cbItem3 = view.findViewById(R.id.cb_item_3);
        cbItem4 = view.findViewById(R.id.cb_item_4);
        cbItem5 = view.findViewById(R.id.cb_item_5);
        btnBack = view.findViewById(R.id.btn_back_class);
        btnLeave = view.findViewById(R.id.btn_leave_class);
        btnLeave.setEnabled(false);
        initListener();
        return view;
    }
    private void initListener(){
        cbItem1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem1.setChecked(isChecked);
                data.put("1",isChecked);
                if (isChecked){
                    cbItem2.setChecked(false);
                    data.put("2",false);
                }
                hasChecked();
            }
        });
        cbItem2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem2.setChecked(isChecked);
                data.put("2",isChecked);
                if (isChecked){
                    cbItem1.setChecked(false);
                    data.put("1",false);
                }
                hasChecked();
            }
        });
        cbItem3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem3.setChecked(isChecked);
                data.put("3",isChecked);
                hasChecked();
            }
        });
        cbItem4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem4.setChecked(isChecked);
                data.put("4",isChecked);
                hasChecked();
            }
        });
        cbItem5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                cbItem5.setChecked(isChecked);
                data.put("5",isChecked);
                hasChecked();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelDialog();
                backClassCallback.backClass();
            }
        });
        btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (String key : data.keySet()){
                    if (data.get(key)){
                        content = content + key + ",";
                    }
                }
                content = content.substring(0,content.length()-1);
                livePlayBackHttpManager.sendExperienceQuitFeedback(UserBll.getInstance().getMyUserInfoEntity()
                        .getStuId(), mVideoEntity.getChapterId(), content, new HttpCallBack() {
                    @Override
                    public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                        logger.i("success"+ responseEntity.toString());
                    }

                    @Override
                    public void onPmFailure(Throwable error, String msg) {
                        super.onPmFailure(error, msg);
                        logger.i("failure"+ msg);
                    }

                });
                leaveClassCallback.leaveClass();
            }
        });
    }

    public void showDialog() {
        super.showDialog(true, false);
    }

    public void setOnClickConfirmlListener(ILeaveClassCallback leaveClassCallback){
       this.leaveClassCallback = leaveClassCallback;
    }

    public void setOnClickBackListener(IBackClassCallback backClassCallback){
        this.backClassCallback = backClassCallback;
    }

    public void setParam(VideoLivePlayBackEntity videoEntity){
        mVideoEntity = videoEntity;
    }

    public interface ILeaveClassCallback {
        void leaveClass();
    }

    public interface IBackClassCallback {
        void backClass();
    }

    private void hasChecked(){
        for(boolean haschecked : data.values()){
            if (haschecked){
                btnLeave.setTextColor(0xFFF13232);
                btnLeave.setEnabled(true);
                return;
            }
        }
        btnLeave.setTextColor(0xFF999999);
        btnLeave.setEnabled(false);
    }


}
