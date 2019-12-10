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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.internal.RtcEngineMessage;

public class EnglishNameBusiness extends BaseBll {

    SettingEnglishNameHttpManager mSettingEnglishNameHttpManager;
    EnlishNameParser mEnlishNameParser;
    File fileDir;
    public EnglishNameBusiness (Context mContext){
        super(mContext);
        mEnlishNameParser = new EnlishNameParser();
        mSettingEnglishNameHttpManager = new SettingEnglishNameHttpManager(mContext);
        if (fileDir == null) {
            fileDir = new File(getLocalPath());
        }
        getFilePath(mContext);
    }

    public String getLocalPath(){
        return  EnglishNameConfig.LIVE_UNITI_NET_PATH_L;

    }

    public void getFilePath(final Context context){
        mSettingEnglishNameHttpManager.getDownLoadPath(new HttpCallBack(false) {
            @Override
            public void onPmSuccess(ResponseEntity responseEntity) throws Exception {
                String url = mEnlishNameParser.parseDownLoadUrl(responseEntity);
                downloadResource(context,url);
            }
        });
    }

    /**
     * 下载资源
     *
     * @param context
     * @param resourceCallback
     */
    public void downloadResource(final Context context ,String url) {
        String mathGamePath = getLocalPath()+"/generate.txt";
        try {
            if (!TextUtils.isEmpty(mathGamePath)) {
                File file = new File(mathGamePath);
                if (file.exists()) {
                    return;
                }
            }

            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            // 最终文件
            final File fileUpload = new File(fileDir, "generate.txt");


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

    public void getNameList(final List<EngLishNameEntity> indexList,final int sexType, final AbstractBusinessDataCallBack cityCallBack){
        File fileUpload = new File(fileDir, "generate.txt");
        if(fileUpload.exists()) {
            String names = getStringFromFile(fileUpload.getAbsolutePath(),false);
            List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(names,sexType,indexList);
            cityCallBack.onDataSucess(nameList);
            if(nameList==null || nameList.size()==0) {
                getDefaultName(indexList,sexType,cityCallBack);
            }
        } else {
            getDefaultName(indexList,sexType,cityCallBack);
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
                String areaText = getStringFromFile("generate.txt",true);
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
        File fileUpload = new File(fileDir, "generate.txt");
        String nameString = "";
        if(fileUpload.exists()) {
             nameString = getStringFromFile(fileUpload.getAbsolutePath(), false);
        } else {
            nameString = getStringFromFile("generate.txt", true);
        }
        int indexPosition = 0;
        for (int j = 0; j < word.length; j++) {
            index = new EngLishNameEntity();
            index.setWordIndex(word[j]);
            listIndex.add(index);
        }
        List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(nameString,1,listIndex);
        List<EngLishNameEntity> nameList2 = mEnlishNameParser.pareseEnglishName(nameString,2,listIndex);
        nameList.addAll(nameList2);

        for (int i = 0; i < nameList.size(); i++) {
            if(TextUtils.equals(nameList.get(i).getName(),userName)) {
                UserBll.getInstance().saveUserNameAudio(nameList.get(i).getAudioPath());
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
    private String getStringFromFile(String fileName,boolean isAsset) {
        try {
            InputStream fileStream = null;
            if(isAsset) {
                fileStream = mContext.getResources().getAssets().open(fileName);
            } else {
                fileStream = new FileInputStream(new File(fileName));
            }
            InputStreamReader inputReader = new InputStreamReader(fileStream);
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
