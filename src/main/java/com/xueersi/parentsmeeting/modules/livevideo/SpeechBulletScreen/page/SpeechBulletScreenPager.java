package com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tal.speech.speechrecognizer.EvaluatorListener;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.tal.speech.speechrecognizer.ResultCode;
import com.tal.speech.speechrecognizer.ResultEntity;
import com.xueersi.common.speech.SpeechEvaluatorUtils;
import com.xueersi.lib.framework.utils.NetWorkHelper;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.SpeechBulletScreen.page.BaseSpeechBulletScreenPager;
import com.xueersi.parentsmeeting.modules.livevideo.business.LiveBll;
import com.xueersi.parentsmeeting.modules.livevideo.util.Loger;
import com.xueersi.parentsmeeting.widget.VolumeWaveView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * 语音弹幕页面
 * Created by Zhang Yuansun on 2018/7/11.
 */

public class SpeechBulletScreenPager extends BaseSpeechBulletScreenPager {
    /** 语音录入标题 */
    TextView tvSpeechbulTitle;
    /** 关闭按钮 */
    ImageView ivSpeechbulClose;
    /** 音量波形 */
    VolumeWaveView vwvSpeechbulWave;
    /** 话语编辑框 */
    EditText etSpeechbulWords;
    /** 计数 */
    TextView tvSpeechbulCount;
    /** 重说按钮 */
    TextView tvSpeechbulRepeat;
    /** 发送按钮 */
    TextView tvSpeechbulSend;
    /** 语音评测结果-布局 */
    LinearLayout llSpeechbulContent;
    /** 语音评测工具类 */
    private SpeechEvaluatorUtils mSpeechEvaluatorUtils;
    /** 是不是评测失败 */
    private boolean isSpeechError = false;
    /** 是不是评测成功 */
    private boolean isSpeechSuccess = false;

    public SpeechBulletScreenPager(Context context) {
        super(context);
        initListener();
        initData();
    }

    @Override
    public View initView() {
        Log.d(TAG,"initView()");
        View view = View.inflate(mContext, R.layout.page_livevideo_speech_bullet_screen,null);
        tvSpeechbulTitle = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_title);
        ivSpeechbulClose = (ImageView) view.findViewById(R.id.tv_livevideo_speechbul_close);
        vwvSpeechbulWave = (VolumeWaveView) view.findViewById(R.id.vwv_livevideo_speechbul_wave);
        etSpeechbulWords = (EditText) view.findViewById(R.id.et_livevideo_speechbul_words);
        tvSpeechbulCount = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_count);
        tvSpeechbulRepeat = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_repeat);
        tvSpeechbulSend = (TextView) view.findViewById(R.id.tv_livevideo_speechbul_send);
        llSpeechbulContent = (LinearLayout) view.findViewById(R.id.ll_livevideo_speechbul_content);
        int colors[] = {0x19F13232, 0x32F13232, 0x64F13232, 0x96F13232, 0xFFF13232};
        vwvSpeechbulWave.setColors(colors);
        vwvSpeechbulWave.setBackColor(Color.TRANSPARENT);
        return view;
    }


    @Override
    public void initListener() {
        Log.d(TAG,"initListener()");
        super.initListener();
        ivSpeechbulClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: ivSpeechbulClose");

            }
        });
        etSpeechbulWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: etSpeechbulWords");
            }
        });
        //重新开启语音评测
        tvSpeechbulRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: tvSpeechbulRepeat");
                vwvSpeechbulWave.setVisibility(View.VISIBLE);
                tvSpeechbulTitle.setVisibility(View.VISIBLE);
                llSpeechbulContent.setVisibility(View.GONE);
                startEvaluator();
            }
        });
        //发送语音弹幕
        tvSpeechbulSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick: tvSpeechbulSend");
            }
        });
    }

    @Override
    public void initData() {
        Log.d(TAG,"initData()");
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                vwvSpeechbulWave.start();
                if (mSpeechEvaluatorUtils == null) {
                    mSpeechEvaluatorUtils = new SpeechEvaluatorUtils(false);
                }
                startEvaluator();
            }
        }, 1000);
    }

    @Override
    public void setSpeechEvaluatorUtils(SpeechEvaluatorUtils speechEvaluatorUtils) {
        Log.d(TAG,"setSpeechEvaluatorUtils()");
    }

    private void startEvaluator() {
        Log.d(TAG,"startEvaluator()");
        File dir = new File(Environment.getExternalStorageDirectory(), "parentsmeeting/voice/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String saveFile = dir + "/speechbul.mp3";
        mSpeechEvaluatorUtils.startOnlineRecognize(saveFile, SpeechEvaluatorUtils.RECOGNIZE_CHINESE,
                new EvaluatorListener() {
                    @Override
                    public void onBeginOfSpeech() {
                        Log.d(TAG, "onBeginOfSpeech");
                        isSpeechError = false;
                    }

                    @Override
                    public void onResult(ResultEntity resultEntity) {
                        Log.d(TAG, "onResult:status=" + resultEntity.getStatus() + ",errorNo=" + resultEntity.getErrorNo());
                        if (resultEntity.getStatus() == ResultEntity.SUCCESS) {
                            if (resultEntity.getErrorNo() > 0) {
                                onEvaluatorError(resultEntity);
                            } else {
                                onEvaluatorSuccess(resultEntity, true);
                            }
                        } else if (resultEntity.getStatus() == ResultEntity.ERROR) {
                            onEvaluatorError(resultEntity);
                        } else if (resultEntity.getStatus() == ResultEntity.EVALUATOR_ING) {
                            onEvaluatorSuccess(resultEntity, false);
                        }
                    }

                    @Override
                    public void onVolumeUpdate(int volume) {
                        Loger.d(TAG, "onVolumeUpdate:volume=" + volume);
                        vwvSpeechbulWave.setVolume(volume * 3);
                    }
                });
    }

    private void onEvaluatorSuccess(ResultEntity resultEntity, boolean isSpeechFinish) {
        Log.d(TAG,"onEvaluatorSuccess():isSpeechFinish=" + isSpeechFinish);
        String str = resultEntity.getCurString();
        try {
            JSONObject jsonObject = new JSONObject(str);
            String content = jsonObject.optString("nbest");
            JSONArray array = jsonObject.optJSONArray("sensitiveWords");
            if (array != null && array.length() > 0) {
                for (int i = array.length() - 1; i >= 0; i--) {
                    StringBuilder star = new StringBuilder();
                    for (int j = 0; j < array.getString(i).length(); j++) {
                        star.append("*");
                    }
                    content = content.replaceAll(array.getString(i), star.toString());
                }
            }
            content = content.replaceAll("。", "");
            if (!TextUtils.isEmpty(content)) {
                Log.d(TAG,"speech evaluate result:" + content);
            }
            if (isSpeechFinish) {
                mSpeechEvaluatorUtils.cancel();
                vwvSpeechbulWave.setVisibility(View.GONE);
                tvSpeechbulTitle.setVisibility(View.GONE);
                llSpeechbulContent.setVisibility(View.VISIBLE);
                etSpeechbulWords.setText(content);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onEvaluatorError(ResultEntity resultEntity) {
        Log.d(TAG,"onEvaluatorError()");
        isSpeechError = true;
        if (resultEntity.getErrorNo() == ResultCode.MUTE_AUDIO || resultEntity.getErrorNo() == ResultCode.MUTE) {
            Log.d(TAG,"声音有点小，再来一次哦！");
            mView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startEvaluator();
                }
            }, 300);
            return;
        }else if (resultEntity.getErrorNo() == ResultCode.NO_AUTHORITY) {
            Log.d(TAG,"麦克风不可用，快去检查一下！");
        }
        else if (resultEntity.getErrorNo() == ResultCode.WEBSOCKET_TIME_OUT || resultEntity.getErrorNo() == ResultCode.NETWORK_FAIL
                || resultEntity.getErrorNo() == ResultCode.WEBSOCKET_CONN_REFUSE) {
            int netWorkType = NetWorkHelper.getNetWorkState(mContext);
            if (netWorkType == NetWorkHelper.NO_NETWORK) {
                Log.d(TAG,"好像没网了，快检查一下");
            } else {
                Log.d(TAG,"服务器连接不上");
            }
        } else {
            Log.d(TAG,"语音输入有点小问题");
        }
    }

}
