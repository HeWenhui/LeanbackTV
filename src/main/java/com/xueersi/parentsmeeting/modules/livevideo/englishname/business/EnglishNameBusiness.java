package com.xueersi.parentsmeeting.modules.livevideo.englishname.business;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.http.DownloadCallBack;
import com.xueersi.common.http.HttpCallBack;
import com.xueersi.common.http.ResponseEntity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XSAsykTask;
import com.xueersi.lib.framework.utils.file.FileUtils;
import com.xueersi.lib.log.Loger;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.config.EnglishNameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.internal.RtcEngineMessage;

public class EnglishNameBusiness extends BaseBll {

    Context mContext;
    SettingEnglishNameHttpManager mSettingEnglishNameHttpManager;
    EnlishNameParser mEnlishNameParser;
    File fileDir;
    public EnglishNameBusiness (Context mContext){
        super(mContext);
    this.mContext = mContext;
        mEnlishNameParser = new EnlishNameParser();
        mSettingEnglishNameHttpManager = new SettingEnglishNameHttpManager(mContext);
        getFilePath();
    }

    public void getFilePath(){
        mSettingEnglishNameHttpManager.getDownLoadPath(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                String url = mEnlishNameParser.parseDownLoadUrl(responseEntity);
            }
        });
    }

    /**
     * 下载资源
     *
     * @param context
     * @param resourceCallback
     */
    public void downloadResource(final Context context, final AbstractBusinessDataCallBack resourceCallback, String url) {
        String mathGamePath = mShareDataManager.getString(EnglishNameConfig.GROUP_CLASS_SUB_NAME_LIST_TXT, "", ShareDataManager.SHAREDATA_NOT_CLEAR);
        try {
            if (!TextUtils.isEmpty(mathGamePath)) {
                File file = new File(mathGamePath);
                if (file.exists()) {
                    return;
                } else {
                    mShareDataManager.put(EnglishNameConfig.GROUP_CLASS_SUB_NAME_LIST_TXT, "", ShareDataManager.SHAREDATA_NOT_CLEAR);
                }
            }

            if (fileDir == null) {
                fileDir = new File(EnglishNameConfig.LIVE_UNITI_NET_PATH_L);
            }
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            // 最终文件
            final File fileUpload = new File(fileDir, "generate.text");


            mSettingEnglishNameHttpManager.downloadRenew(url, fileUpload, new DownloadCallBack() {
                @Override
                protected void onDownloadSuccess() {

                }

                @Override
                protected void onDownloadFailed() {

                }

                @Override
                protected void onDownloading(int progress) {
                    super.onDownloading(progress);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    /**
     * 读取地区信息
     *
     * @return
     */
    public void getDefaultName(final List<EngLishNameEntity> indexList,final int sexType, final AbstractBusinessDataCallBack cityCallBack) {
        new XSAsykTask() {
            @Override
            public void preTask() {
            }

            @Override
            public void postTask() {
                String areaText = getStringFromAssets("generate.txt");
               // CityEntity entity = mPsswordSettingHttpResponseParser.getCityLst(areaText);
                List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(areaText,sexType,indexList);
                cityCallBack.onDataSucess(nameList);
            }

            @Override
            public void doInBack() {

            }
        }.execute(true);
    }

    public void checkName(){
        mShareDataManager.put(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK,false,ShareDataManager.SHAREDATA_USER);
        String userName = UserBll.getInstance().getMyUserInfoEntity().getEnglishName();
        if(TextUtils.isEmpty(userName)) {
            return;
        }
        List<EngLishNameEntity>  listName = new ArrayList<>();
        List<EngLishNameEntity> listIndex = new ArrayList<>();
        EngLishNameEntity entity = null;
        String[] word = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        EngLishNameEntity index = null;
        String areaText = getStringFromAssets("generate.txt");
        int indexPosition = 0;
        for (int j = 0; j < word.length; j++) {
            index = new EngLishNameEntity();
            index.setWordIndex(word[j]);
            listIndex.add(index);
        }
        List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(areaText,1,listIndex);
        List<EngLishNameEntity> nameList2 = mEnlishNameParser.pareseEnglishName(areaText,2,listIndex);
        nameList.addAll(nameList2);

        for (int i = 0; i < nameList.size(); i++) {
            if(TextUtils.equals(nameList.get(i).getName(),userName)) {
                mShareDataManager.put(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK,true,ShareDataManager.SHAREDATA_USER);
                return;
            }
        }
    }



    /***
     * 读取XML
     *
     * @param fileName
     * @return
     */
    private String getStringFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
